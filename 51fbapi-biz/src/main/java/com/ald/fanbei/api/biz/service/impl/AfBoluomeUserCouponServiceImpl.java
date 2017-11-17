
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
import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.biz.service.AfBoluomeUserCouponService;

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

	@Override
	public BaseDao<AfBoluomeUserCouponDo, Long> getDao() {
		return afBoluomeUserCouponDao;
	}

	@Override
	public List<AfBoluomeUserCouponDo> getUserCouponListByUerIdAndChannel(AfBoluomeUserCouponDo queryUserCoupon) {

		return afBoluomeUserCouponDao.getUserCouponListByUerIdAndChannel(queryUserCoupon);
	}
	@Override
	public AfBoluomeUserCouponDo getByCouponIdAndUserIdAndChannel(AfBoluomeUserCouponDo userCoupon) {
		    // TODO Auto-generated method stub
		 return afBoluomeUserCouponDao.getByCouponIdAndUserIdAndChannel(userCoupon);
	}
	@Override
	public boolean sendCoupon(Long userId) {
		boolean result = false;
		int isHave = afBoluomeUserCouponDao.checkIfHaveCoupon(userId);
		if (isHave == 0) {
			// have never sent coupon before , right now send it .
			Long refUserIdTemp = afBoluomeActivityUserLoginDao.findRefUserId(userId);

			if (refUserIdTemp != null) {
				AfBoluomeUserCouponDo afBoluomeUserCouponDo = new AfBoluomeUserCouponDo();
				afBoluomeUserCouponDo.setGmtCreate(new Date());
				afBoluomeUserCouponDo.setGmtModified(new Date());
				afBoluomeUserCouponDo.setRefId(userId);
				afBoluomeUserCouponDo.setUserId(refUserIdTemp);
				afBoluomeUserCouponDo.setStatus(1);
				afBoluomeUserCouponDo.setChannel("RECOMMEND");

				AfResourceDo resourceDo = afResourceDao.getConfigByTypesAndSecType("GGACTIVITY", "BOLUOMECOUPON");
				if (resourceDo != null) {
					String couponIdStr = resourceDo.getValue();
					Long couponId = Long.parseLong(couponIdStr);
					afBoluomeUserCouponDo.setCouponId(couponId);

					afBoluomeUserCouponDao.saveRecord(afBoluomeUserCouponDo);
					result = true;
					//call Jpush for rebate
					
				}

			}
		}

		return result;

	}

	@Override
	public AfBoluomeUserCouponDo getLastUserCouponByUserId(Long userId) {
	    // TODO Auto-generated method stub
	    return afBoluomeUserCouponDao.getLastUserCouponByUserId(userId);
	}

}