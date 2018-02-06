
package com.ald.fanbei.api.biz.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfBoluomeUserCouponService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.H5GgActivity;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserLoginDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.alibaba.fastjson.JSONObject;

/**
 * 点亮活动新版ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:33 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afBoluomeUserCouponService")
public class AfBoluomeUserCouponServiceImpl extends ParentServiceImpl<AfBoluomeUserCouponDo, Long>
		implements AfBoluomeUserCouponService {

	private static final Logger logger = LoggerFactory.getLogger(AfBoluomeUserCouponServiceImpl.class);

	@Resource
	private AfBoluomeUserCouponDao afBoluomeUserCouponDao;
	@Resource
	AfBoluomeActivityUserLoginDao afBoluomeActivityUserLoginDao;
	@Resource
	AfResourceDao afResourceDao;
	@Resource
	JpushService jpushService;
	@Resource
	AfUserDao afUserDao;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfRecommendUserService afRecommendUserService;
	@Resource
	AfBoluomeUserCouponService afBoluomeUserCouponService;

	@Override
	public BaseDao<AfBoluomeUserCouponDo, Long> getDao() {
		return afBoluomeUserCouponDao;
	}


	@Override
	public boolean sendCoupon(Long userId) {
		boolean result = false;
		String log = String.format("sendCoupon || params: userId = %s", userId);
		logger.info(log);

		String key = Constants.GG_COUPON_LOCK + ":" + userId;
		boolean lock = bizCacheUtil.getLockTryTimes(key, "1", 100);

		try {
			if (lock) {
			    
				int isHave = afBoluomeUserCouponDao.checkIfHaveCoupon(userId);
				
				log = log + String.format("middle business params isHave =  %s", isHave);
				logger.info(log);
				
				if (isHave == 0) {
					// have never sent coupon before , right now send it .
					//Long refUserIdTemp = afBoluomeActivityUserLoginDao.findRefUserId(userId);
				        //绑定记录必须在活动开始时间之后
				    	AfResourceDo startTime = new  AfResourceDo();
				    	startTime = afResourceDao.getConfigByTypesAndSecType("GG_ACTIVITY", "ACTIVITY_TIME");
				    	   if(startTime != null){
        				        AfRecommendUserDo queryRecommendUser = new AfRecommendUserDo();
        					queryRecommendUser.setUser_id(userId);
        					queryRecommendUser.setType(1);
 				    	        SimpleDateFormat parser = new SimpleDateFormat(DateUtil.DATE_TIME_SHORT);
 				    	        Date gmtCreate =  parser.parse(startTime.getValue());
 				    	        queryRecommendUser.setGmt_create(gmtCreate);
        					Long refUserIdTemp = afRecommendUserService.findRefUserId(queryRecommendUser);
        				   //   Long refUserIdTemp = afBoluomeActivityUserLoginDao.findRefUserId(userId);
        					log = log + String.format("refUserIdTemp =  %s", refUserIdTemp);
        					logger.info(log);
        					
        					if (refUserIdTemp != null) {
        					         //查询该邀请者今日获得的优惠券,若大于限制不发放
        					         int couponNum =  afBoluomeUserCouponService.getTodayNumByUserId(refUserIdTemp);
                				        if(getRecommendRecourceForStrongWind()!=null){
                				            int limitNum = Integer.valueOf(getRecommendRecourceForStrongWind().getValue3()) ;
                						   if(limitNum != 0 && limitNum < couponNum){
                						       return false;
                						   }
                					 }
        						AfBoluomeUserCouponDo afBoluomeUserCouponDo = new AfBoluomeUserCouponDo();
        						afBoluomeUserCouponDo.setGmtCreate(new Date());
        						afBoluomeUserCouponDo.setGmtModified(new Date());
        						afBoluomeUserCouponDo.setRefId(userId);
        						afBoluomeUserCouponDo.setUserId(refUserIdTemp);
        						afBoluomeUserCouponDo.setStatus(1);
        						afBoluomeUserCouponDo.setChannel("RECOMMEND");
        
        						AfResourceDo resourceDo = afResourceDao.getConfigByTypesAndSecType("GG_ACTIVITY",
        								"BOLUOME_COUPON");
        
        						log = log + String.format("AfBoluomeUserCouponDo = %s , AfResourceDo = %s ",
        								afBoluomeUserCouponDo.toString(), resourceDo.toString());
        						logger.info(log);
        
        						if (resourceDo != null) {
        							String couponIdStr = resourceDo.getValue1();
        							Long couponId = Long.parseLong(couponIdStr);
        							afBoluomeUserCouponDo.setCouponId(couponId);
        
        							log = log + String.format("couponId =  %s ", couponId);
        							logger.info(log);
        
        							// send coupon
        							AfResourceDo temCoupon = afResourceDao.getResourceByResourceId(couponId);
        							if (temCoupon != null) {
        								PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
        								bo.setUser_id(refUserIdTemp + StringUtil.EMPTY);
        								String resultString = HttpUtil.doHttpPostJsonParam(temCoupon.getValue(),
        										JSONObject.toJSONString(bo));
        								
        								log = log + String.format("sendBoluomeCoupon  bo =  %s, resultString =  %s",
        										JSONObject.toJSONString(bo), resultString);
        								logger.info(log);
        								
        								JSONObject resultJson = JSONObject.parseObject(resultString);
        								String code = resultJson.getString("code");
        								
        								// 发券成功，保存记录，推送极光
        								if ("0".equals(code)) {
        									afBoluomeUserCouponDao.saveRecord(afBoluomeUserCouponDo);
        									result = true;
        									// call Jpush for rebate
        									String userName = convertToUserName(refUserIdTemp);
        									log = log + String.format("userName = %s ", userName);
        									logger.info(log);
        									if (userName != null) {
        									     //jpushService.send15Coupon(userName);
        									     jpushService.boluomeActivityMsg(userName, H5GgActivity.GG_ACTIVITY.getCode(), H5GgActivity.GG_SMS_INVITER.getCode());
        									}
        								}
        
        							}
        
        						}
        					}

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			bizCacheUtil.delCache(key);
		}
		return result;

	}
	private AfResourceDo getRecommendRecourceForStrongWind(){
		List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_STROLL");
		return list.get(0);
	}

	private String convertToUserName(Long userId) {
		AfUserDo userDo = afUserDao.getUserById(userId);
		String userName = "";
		if (userDo != null) {
			userName = userDo.getUserName();
		}
		return userName;
	}

	@Override
	public int isHasCouponInDb(Long userId, Long couponId) {
		return afBoluomeUserCouponDao.isHasCouponInDb(userId, couponId);

	}
	// @Override
	// public AfBoluomeUserCouponDo getLastUserCouponByUserId(Long userId) {
	// // TODO Auto-generated method stub
	// return afBoluomeUserCouponDao.getLastUserCouponByUserId(userId);
	// }

	@Override
	public AfBoluomeUserCouponDo getUserCouponByUerIdAndRefIdAndChannel(AfBoluomeUserCouponDo userCoupon) {
		// TODO Auto-generated method stub
		return afBoluomeUserCouponDao.getUserCouponByUerIdAndRefIdAndChannel(userCoupon);
	}

	@Override
	public int getByCouponIdAndUserIdAndChannel(AfBoluomeUserCouponDo userCoupon) {
		// TODO Auto-generated method stub
		return afBoluomeUserCouponDao.getByCouponIdAndUserIdAndChannel(userCoupon);
	}

	@Override
	public AfBoluomeUserCouponDo getLastUserCouponByUserIdSentCouponId(Long userId, Long newUser, Long inviter) {
		// TODO Auto-generated method stub
		return afBoluomeUserCouponDao.getLastUserCouponByUserIdSentCouponId(userId, newUser, inviter);
	}


	@Override
	public int getTodayNumByUserId(Long userId) {
	    // TODO Auto-generated method stub
	    	return afBoluomeUserCouponDao.getTodayNumByUserId(userId);
	}

}