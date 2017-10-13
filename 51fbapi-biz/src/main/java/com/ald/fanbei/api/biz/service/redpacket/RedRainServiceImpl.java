package com.ald.fanbei.api.biz.service.redpacket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.service.redpacket.RedPacketPoolService.Redpacket;
import com.ald.fanbei.api.common.enums.UserCouponSource;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfRedRainPoolDao;
import com.ald.fanbei.api.dal.dao.AfRedRainRoundDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfRedRainPoolDo;
import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo;
import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo.AfRedRainRoundStatusEnum;
import com.ald.fanbei.api.dal.domain.AfUserDo;

@Service("redpacketService")
public class RedRainServiceImpl implements IRedRainService{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final int MAX_NUM_HIT_REDPACKET = 9;
	
	private ScheduledExecutorService scheduler ;
	private Map<String,AtomicInteger> counters;
	
	@Resource
	private RedPacketPoolService redPacketPoolService;
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
	
	@PostConstruct
	public void init() {
		this.counters = new ConcurrentHashMap<>(8096);
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		
		//每5分钟启动扫描
		this.scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				scanAndInjected();
			}
		}, 0, 5, TimeUnit.MINUTES);
		
		//每天0点清空计数器
		this.scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				counters.clear();
			}
		}, DateUtil.getIntervalFromNowInSec(DateUtil.getEndOfDate(new Date())), 24*60*60, TimeUnit.SECONDS);
	}
	
	@Override
	public AfCouponDo apply(String userName) {
		Assert.hasText(userName);
		
		AtomicInteger counter;
		synchronized (userName.intern()) { //防刷
			counter = this.counters.get(userName);
			if(counter == null) this.counters.put(userName, counter = new AtomicInteger());
			if(counter.incrementAndGet() > MAX_NUM_HIT_REDPACKET) { //假设用户一定可以得到一个红包
				return null;
			}
		}
		
		Redpacket rp = this.redPacketPoolService.apply();
		if(rp != null) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if("BOLUOMI".equals(rp.getType())) {
				boluomeUtil.grantCoupon(rp.getCoupon_id(), new HashMap<String,Object>(), user.getRid());
			}else {
				afUserCouponService.grantCoupon(user.getRid(), rp.getCoupon_id(), UserCouponSource.RED_RAIN.name(), rp.getRedRainRoundId().toString());
			}
		}
		
		counter.decrementAndGet(); //未获取，回滚计数器-1
		return null;
	}

	@Override
	public List<AfRedRainRoundDo> fetchTodayRounds() {
		return null;
	}


	@Override
	public void clearAndStatisic(Integer roundId) {
		try {
			Map<String, Integer> info = this.redPacketPoolService.informationPacket();
			
			AfRedRainRoundDo roundForMod = new AfRedRainRoundDo();
			roundForMod.setStatus(AfRedRainRoundStatusEnum.OVER.name());
			Integer count = info.get("count");
			roundForMod.setSum(count);
			roundForMod.setSumGrabed(count - info.get("listNumber"));
			
			roundForMod.setId(roundId);
			afRedRainRoundDao.update(roundForMod);
			
			this.redPacketPoolService.emptyPacket();
		}catch (Exception e) {
			this.logger.error(e.getMessage(),e);
		}
	}
	
	@Override
	public void scanAndInjected() {
		try {
			//扫描
			AfRedRainRoundDo paramRound = new AfRedRainRoundDo();
			paramRound.setGmtStart(DateUtil.addMins(new Date(), 10));
			paramRound.setStatus(AfRedRainRoundStatusEnum.PREPARE.name());
			final AfRedRainRoundDo round = afRedRainRoundDao.fetch(paramRound);
			
			if(round == null) return;
			
			//注入
			List<AfRedRainPoolDo> pools = afRedRainPoolDao.queryAll();
			for(AfRedRainPoolDo pool : pools) {
				int num = pool.getNum();
				BlockingQueue<Redpacket> queue = new ArrayBlockingQueue<>(num);
				String couponType = pool.getCouponType();
				String couponName = pool.getCouponName();
				Long couponId = pool.getCouponId();
				Integer roundId = round.getId();
				for(int i = 0; i<num; i++) {
					queue.offer(new Redpacket(couponType, couponName, couponId, roundId));
				}
				redPacketPoolService.inject(queue);
			}
			
			//更新数据库状态
			round.setStatus(AfRedRainRoundStatusEnum.INJECTED.name());
			afRedRainRoundDao.update(round);
			
			//场次开始时间5分钟后清场
			this.scheduler.schedule(new Runnable() {
				public void run() {
					clearAndStatisic(round.getId());
				}
			}, DateUtil.getIntervalFromNowInSec(round.getGmtStart())+300, TimeUnit.SECONDS);
		}catch (Exception e) {
			this.logger.error(e.getMessage(),e);
			this.redPacketPoolService.emptyPacket();
		}
	}
	
}
