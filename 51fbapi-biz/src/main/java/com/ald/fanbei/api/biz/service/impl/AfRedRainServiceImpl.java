package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.ald.fanbei.api.biz.service.AfRedPacketPoolService;
import com.ald.fanbei.api.biz.service.AfRedPacketPoolService.Redpacket;
import com.ald.fanbei.api.biz.service.AfRedRainService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.UserCouponSource;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfRedRainPoolDao;
import com.ald.fanbei.api.dal.dao.AfRedRainRoundDao;
import com.ald.fanbei.api.dal.domain.AfRedRainPoolDo;
import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo;
import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo.AfRedRainRoundStatusEnum;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

@Service("redRainService")
public class AfRedRainServiceImpl implements AfRedRainService{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final int MAX_NUM_HIT_REDPACKET = 9;
	private static final int INTERVAL_SCAN = 60;	//扫描af_red_rain_round间隔, 300s
	private static final int INTERVAL_CLEAR_COUNTER = 120;//清空用户红包计数器间隔, 24*60*60
	private static final int DELAY_OF_ACTIVATE_CLEAR = 300;//激活清空红包池任务的延时, 300s
	
	private ScheduledExecutorService scheduler ;
	
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	private AfRedPacketPoolService redPacketRedisPoolService;
	@Resource 
	private AfRedRainPoolDao afRedRainPoolDao;
	@Resource 
	private AfRedRainRoundDao afRedRainRoundDao;
	
	@Resource
    private AfUserService afUserService;
	@Resource
	private AfUserCouponService afUserCouponService;
	@Resource
	private BoluomeUtil boluomeUtil;
	@Resource
	TransactionTemplate transactionTemplate;
	
	@PostConstruct
	public void init() {
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		
		//每5分钟启动扫描
		this.scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				scanAndInjected();
			}
		}, 0, INTERVAL_SCAN, TimeUnit.SECONDS);
		
		//每天0点清空计数器
		this.scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				bizCacheUtil.delCache(Constants.CACHEKEY_REDRAIN_COUNTERS);
			}
		}, DateUtil.getIntervalFromNowInSec(DateUtil.getEndOfDate(new Date())), INTERVAL_CLEAR_COUNTER, TimeUnit.SECONDS);
	}
	
	@Override
	public Redpacket apply(String userName) {
		Assert.hasText(userName);
		
		Integer counter;
		synchronized (userName.intern()) { //防刷
			String val = this.bizCacheUtil.hget(Constants.CACHEKEY_REDRAIN_COUNTERS, userName);
			if(val == null) this.bizCacheUtil.hset(Constants.CACHEKEY_REDRAIN_COUNTERS, userName, String.valueOf(counter = 0));
			else counter = Integer.valueOf(val);
			logger.info("redRainService.apply,userName={},raw counter={}", userName, counter);
			
			if(++counter > MAX_NUM_HIT_REDPACKET) { 
				return null;
			}
		
			try {
				Redpacket rp = this.redPacketRedisPoolService.apply();
				if(rp != null) {
					AfUserDo user = afUserService.getUserByUserName(userName);
					if("BOLUOMI".equals(rp.getType())) {
						boluomeUtil.grantCoupon(rp.getCouponId(), new HashMap<String,Object>(), user.getRid());
					}else {
						afUserCouponService.grantCouponForRedRain(user.getRid(), rp.getCouponId(), UserCouponSource.RED_RAIN.name(), rp.getRedRainRoundId().toString());
					}
					this.bizCacheUtil.hincrBy(Constants.CACHEKEY_REDRAIN_COUNTERS, userName, 1L);
					return rp;
				}
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		return null;
	}

	@Override
	public List<AfRedRainRoundDo> fetchTodayRounds() {
		Map<String, Object> params = new HashMap<>();
		params.put("status", AfRedRainRoundStatusEnum.PREPARE.name());
		Date cur = new Date();
		params.put("gmtMin", DateUtil.getStartOfDate(cur));
		params.put("gmtMax", DateUtil.getEndOfDate(cur));
		return afRedRainRoundDao.queryByGmtStartInToday(params);
	}


	@Override
	public void clearAndStatisic(Integer roundId) {
		try {
			Map<String, Integer> info = this.redPacketRedisPoolService.informationPacket();
			int sum = info.get("count");
			int sumGrabed = sum - info.get("listNumber");
			logger.info("redRainService.clearAndStatisic,roundId={},sum={},sumGrabed={}", roundId, sum, sumGrabed);
			
			AfRedRainRoundDo roundForMod = new AfRedRainRoundDo();
			roundForMod.setStatus(AfRedRainRoundStatusEnum.OVER.name());
			
			roundForMod.setSum(sum);
			roundForMod.setSumGrabed(sum - sumGrabed);
			
			roundForMod.setId(roundId);
			afRedRainRoundDao.update(roundForMod);
			
			this.redPacketRedisPoolService.emptyPacket();
		}catch (Exception e) {
			this.logger.error(e.getMessage(),e);
		}
	}
	
	@Override
	public void scanAndInjected() {
		// 同步风控数据
		transactionTemplate.execute(new TransactionCallback<Integer>() { public Integer doInTransaction(TransactionStatus status) {
			try {
				//扫描
				AfRedRainRoundDo paramRound = new AfRedRainRoundDo();
				Date gmtStart = DateUtil.addMins(new Date(), 10);
				paramRound.setGmtStart(gmtStart);
				paramRound.setStatus(AfRedRainRoundStatusEnum.PREPARE.name());
				final AfRedRainRoundDo round = afRedRainRoundDao.fetch(paramRound);
				
				logger.info("redRainService.scanAndInjected, query gmtStart={}, fetchRound={}", gmtStart, JSON.toJSONString(round, SerializerFeature.UseISO8601DateFormat));
				
				if(round == null) return 1;
				
				//注入
				List<AfRedRainPoolDo> pools = afRedRainPoolDao.queryAll();
				for(AfRedRainPoolDo pool : pools) {
					int num = pool.getNum();
					BlockingQueue<String> queue = new ArrayBlockingQueue<>(num);
					for(int i = 0; i<num; i++) {
						queue.offer(JSON.toJSONString(new Redpacket(pool.getCouponType(), pool.getCouponName(), pool.getCouponId(), round.getId(), pool.getAmount())));
					}
					redPacketRedisPoolService.inject(queue);
				}
				
				//更新数据库状态
				round.setStatus(AfRedRainRoundStatusEnum.INJECTED.name());
				afRedRainRoundDao.update(round);
				
				//场次开始时间5分钟后清场
				scheduler.schedule(new Runnable() {
					public void run() {
						clearAndStatisic(round.getId());
					}
				}, DateUtil.getIntervalFromNowInSec(round.getGmtStart())+DELAY_OF_ACTIVATE_CLEAR, TimeUnit.SECONDS);
			}catch (Exception e) {
				logger.error(e.getMessage(),e);
				redPacketRedisPoolService.emptyPacket();
			}
			
			return 1;
		}});
		
	}
	
}
