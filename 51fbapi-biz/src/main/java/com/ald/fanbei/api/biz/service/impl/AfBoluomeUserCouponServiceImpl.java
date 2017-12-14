
package com.ald.fanbei.api.biz.service.impl;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserLoginDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.alibaba.fastjson.JSONObject;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfBoluomeUserCouponService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;

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

	@Override
	public BaseDao<AfBoluomeUserCouponDo, Long> getDao() {
		return afBoluomeUserCouponDao;
	}

//	@Override
//	public List<AfBoluomeUserCouponDo> getUserCouponListByUerIdAndChannel(AfBoluomeUserCouponDo queryUserCoupon) {
//
//		return afBoluomeUserCouponDao.getUserCouponListByUerIdAndChannel(queryUserCoupon);
//	}
	
	@Override
	public boolean sendCoupon(Long userId) {
		boolean result = false;
		String log = String.format("sendCoupon || params: userId = %s", userId);
		logger.info(log);
		int isHave = afBoluomeUserCouponDao.checkIfHaveCoupon(userId);
		log = log + String.format("middle business params isHave =  %s", isHave);
		logger.info(log);
		if (isHave == 0) {
			// have never sent coupon before , right now send it .
			Long refUserIdTemp = afBoluomeActivityUserLoginDao.findRefUserId(userId);
			log = log + String.format("refUserIdTemp =  %s", refUserIdTemp);
			logger.info(log);
			
			if (refUserIdTemp != null) {
				AfBoluomeUserCouponDo afBoluomeUserCouponDo = new AfBoluomeUserCouponDo();
				afBoluomeUserCouponDo.setGmtCreate(new Date());
				afBoluomeUserCouponDo.setGmtModified(new Date());
				afBoluomeUserCouponDo.setRefId(userId);
				afBoluomeUserCouponDo.setUserId(refUserIdTemp);
				afBoluomeUserCouponDo.setStatus(1);
				afBoluomeUserCouponDo.setChannel("RECOMMEND");
				
				AfResourceDo resourceDo = afResourceDao.getConfigByTypesAndSecType("GG_ACTIVITY", "BOLUOME_COUPON");
				
				log = log + String.format("AfBoluomeUserCouponDo = %s , AfResourceDo = %s ", afBoluomeUserCouponDo.toString(), resourceDo.toString());
				logger.info(log);
				
				if (resourceDo != null) {
					String couponIdStr = resourceDo.getValue1();
					Long couponId = Long.parseLong(couponIdStr);
					afBoluomeUserCouponDo.setCouponId(couponId);

					log = log + String.format("couponId =  %s ", couponId);
					logger.info(log);
					
					afBoluomeUserCouponDao.saveRecord(afBoluomeUserCouponDo);
					
					//send coupon
					AfResourceDo temCoupon = afResourceDao.getResourceByResourceId(couponId);
					if (temCoupon != null) {
						PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
						bo.setUser_id(refUserIdTemp + StringUtil.EMPTY);
						String resultString = HttpUtil.doHttpPostJsonParam(temCoupon.getValue(), JSONObject.toJSONString(bo));
						log = log + String.format("sendBoluomeCoupon  bo =  %s, resultString =  %s", JSONObject.toJSONString(bo),
								resultString);
						logger.info(log);
					
					}
					
					result = true;
					//call Jpush for rebate
					String userName = convertToUserName(refUserIdTemp);
					log = log + String.format("userName = %s ", userName);
					logger.info(log);
					if (userName != null) {
						jpushService.send15Coupon(userName);
					}
					
				}

			}
		}

		return result;

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
		return afBoluomeUserCouponDao.isHasCouponInDb(userId,couponId);

	}
//	@Override
//	public AfBoluomeUserCouponDo getLastUserCouponByUserId(Long userId) {
//	    // TODO Auto-generated method stub
//	    return afBoluomeUserCouponDao.getLastUserCouponByUserId(userId);
//	}
	
	@Override
	public AfBoluomeUserCouponDo getUserCouponByUerIdAndRefIdAndChannel(AfBoluomeUserCouponDo userCoupon) {
		    // TODO Auto-generated method stub
		 return afBoluomeUserCouponDao.getUserCouponByUerIdAndRefIdAndChannel(userCoupon);
	}

	@Override
	public AfBoluomeUserCouponDo getByCouponIdAndUserIdAndChannel(AfBoluomeUserCouponDo userCoupon) {
	    // TODO Auto-generated method stub
	         return afBoluomeUserCouponDao.getByCouponIdAndUserIdAndChannel(userCoupon);
	}

	@Override
	public AfBoluomeUserCouponDo getLastUserCouponByUserIdSentCouponId(Long userId, Long newUser, Long inviter) {
	    // TODO Auto-generated method stub
	         return afBoluomeUserCouponDao.getLastUserCouponByUserIdSentCouponId(userId,newUser,inviter);
	}

	

}