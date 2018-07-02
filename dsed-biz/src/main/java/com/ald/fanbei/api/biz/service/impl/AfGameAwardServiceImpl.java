/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfGameAwardService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.dto.AfGameConfCompleteDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *@类现描述：
 *@author chenjinhu 2017年6月4日 上午11:36:54
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afGameAwardService")
public class AfGameAwardServiceImpl implements AfGameAwardService {
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfGameAwardDao afGameAwardDao;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfUserCouponDao afUserCouponDao;
	@Resource
	AfCouponDao afCouponDao;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfTaskUserService afTaskUserService;
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Override
	public int addGameAward(AfGameAwardDo afGameAwardDo) {
		return afGameAwardDao.addGameAward(afGameAwardDo);
	}

	@Override
	public AfGameAwardDo getByUserId(Long userId) {
		return afGameAwardDao.getByUserId(userId);
	}

	@Override
	public List<AfGameAwardDo> getLatestAwards() {
		String key = Constants.CACHEKEY_LATESTAWARD_LIST;
		List<AfGameAwardDo> awardList = bizCacheUtil.getObjectList(key);
		if(awardList != null){
			return awardList;
		}
		
		awardList = afGameAwardDao.getLatestAwards();
		if(awardList != null){
			bizCacheUtil.saveObjectList(key, awardList);
		}
		return awardList;
	}
	
	@Override
	public int updateContact(Long userId, String contacts) {
		return afGameAwardDao.updateContact(userId, contacts);
	}

	/**
	 * 
	 */
	@Override
	public AfGameAwardDo getLoanSignAward(Long userId, Long gameId) {
		return afGameAwardDao.getLoanSignAward(userId,gameId);
	}

	@Override
	public void receiveSignAward(final AfUserDo user,final AfGameConfDo confDo,final AfGameDo gameDo) {
		 transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
		String key = Constants.CACHEKEY_LOAN_SUPERMARKET_SIGN_AWARD_LOCK+user.getRid();
		boolean lock = bizCacheUtil.getLockAFewMinutes(key, "1", 4);
		try{
			if(lock){
				//再次检查是否领取过，避免重复领取
				AfGameAwardDo award = getLoanSignAward(user.getRid(), confDo.getGameId());
				if(award!=null){
					return null;
				}
				List<AfUserCouponDo> userCouponList = new ArrayList<AfUserCouponDo>();
				String rule = confDo.getRule();
				AfGameConfCompleteDto dto = JSON.parseObject(rule,AfGameConfCompleteDto.class);
				for(AfGameConfCompleteDto.AfGameConifPrize prize:dto.getPrize()){
					if("BOLUOMI".equals(prize.getType())){
						Map<String,Object> data = new HashMap<String, Object>();
						grantBoluomiCoupon(Long.valueOf(prize.getPrizeId()),data,user.getRid());
					}else if(CouponType.CASH.getCode().equals(prize.getType())){
						Long couponId = Long.valueOf(prize.getPrizeId());
						AfCouponDo couponDo = afCouponDao.getCouponById(couponId);
						AfUserAccountDo accountDo = new AfUserAccountDo();
						accountDo.setUserId(user.getRid());
						accountDo.setRebateAmount(couponDo.getAmount());
						int count = afUserAccountDao.updateUserAccount(accountDo);
						if(count > 0){
							AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
							accountLog.setAmount(couponDo.getAmount());
							accountLog.setType(prize.getType());
							accountLog.setRefId(gameDo.getRid().toString());
							accountLog.setUserId(user.getRid());
							afUserAccountLogDao.addUserAccountLog(accountLog);

							// add by luoxiao for 边逛边赚，增加零钱明细
							afTaskUserService.addTaskUser(user.getRid(), UserAccountLogType.SIGN.getName(), couponDo.getAmount());
							// end by luoxiao
						}
					}else{
						Long couponId = Long.valueOf(prize.getPrizeId());
						AfCouponDo couponDo = afCouponDao.getCouponById(couponId);
						if(couponDo==null){
							continue;
						}
						AfUserCouponDo userCoupon = new AfUserCouponDo();
						userCoupon.setCouponId(NumberUtil.objToLongDefault(couponId, 1l));
						userCoupon.setGmtCreate(new Date());
						userCoupon.setGmtStart(couponDo.getGmtStart());
						userCoupon.setGmtEnd(couponDo.getGmtEnd());
						userCoupon.setUserId(user.getRid());
						userCoupon.setStatus("NOUSE");
						userCoupon.setSourceType("LOANSIGN");
						userCouponList.add(userCoupon);
					}
				}
				//发普通优惠券
				if(userCouponList.size()>0)
					afUserCouponDao.batchAddUserCoupon(userCouponList);
				//添加领奖记录
				AfGameAwardDo gameAward = new AfGameAwardDo();
				gameAward.setGameId(confDo.getGameId());
				gameAward.setUserId(String.valueOf(user.getRid()));
				gameAward.setUserName(user.getUserName());
				gameAward.setAwardId(confDo.getRid());
				gameAward.setAwardName("借贷超市签到"+dto.getName()+"奖励");
				gameAward.setUserAvata("");
				gameAward.setAwardIcon(gameDo.getIcon());
				gameAward.setAwardType("C");
				afGameAwardDao.addGameAward(gameAward);
			}
		}finally{
            if(lock){
				bizCacheUtil.delCache(key);
			}
		}
		return null;
		}
			});
	}

	/**
	 * 发菠萝觅优惠券，不抛出异常，忽略掉
	 * @param sceneId
	 * @param data
	 * @param userId
	 */
	private void grantBoluomiCoupon(Long sceneId, Map<String, Object> data, Long userId ) {
		if (sceneId == null) {
			return;
		}
		AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
		if (resourceInfo == null) {
			return;
		}
		data.put("prizeName", resourceInfo.getName());
		data.put("prizeType", "BOLUOMI");
		
		PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
		bo.setUser_id(userId + StringUtil.EMPTY);
		
		Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
		Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);
		
		if (DateUtil.beforeDay(new Date(), gmtStart)) {
			return;
		}
		if (DateUtil.afterDay(new Date(), gmtEnd)) {
			return;
		}
		String url = resourceInfo.getValue();
		if(url != null) {
			url = url.replace(" ", "");
		}
		String resultString = HttpUtil.doHttpPostJsonParam(url, JSONObject.toJSONString(bo));
		JSONObject resultJson = JSONObject.parseObject(resultString);
		if (!"0".equals(resultJson.getString("code"))) {
			return;
		} else if (JSONArray.parseArray(resultJson.getString("data")).size() == 0){
			return;
		}
	}
}
