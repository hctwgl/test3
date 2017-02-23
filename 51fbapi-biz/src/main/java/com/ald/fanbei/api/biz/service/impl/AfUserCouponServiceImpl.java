package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;

/**
 * 
 *@类描述：AfUserCouponServiceImpl
 *@author 何鑫 2017年1月20日  13:07:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserCouponService")
public class AfUserCouponServiceImpl implements AfUserCouponService{

	@Resource
	private AfUserCouponDao afUserCouponDao;
	
	@Override
	public List<AfUserCouponDto> getUserCouponByUser(AfUserCouponQuery query) {
		return afUserCouponDao.getUserCouponByUser(query);
	}

	@Override
	public int getUserCouponByUserNouse(Long userId) {
		return afUserCouponDao.getUserCouponByUserNouse(userId);
	}


	@Override
	public int getUserCouponByUserIdAndCouponId(Long userId, Long couponId) {
		return afUserCouponDao.getUserCouponByUserIdAndCouponId(userId, couponId);
	}


	@Override
	public int addUserCoupon(AfUserCouponDo afUserCouponDo) {
		return afUserCouponDao.addUserCoupon(afUserCouponDo);
	}

	
	@Override
	public BigDecimal getUserCouponByInvite(Long userId) {
		return afUserCouponDao.getUserCouponByInvite(userId);
	}
	@Override
	public AfUserCouponDto getUserCouponById(Long rid) {
		return afUserCouponDao.getUserCouponById(rid);
	}

	@Override
	public int updateUserCouponSatusUsedById(Long rid) {
		return afUserCouponDao.updateUserCouponSatusUsedById(rid);
	}

	@Override
	public List<AfUserCouponDto> getUserCouponByUserIdAndType(Long userId,String type,BigDecimal amount) {
		return null;
	}

}
