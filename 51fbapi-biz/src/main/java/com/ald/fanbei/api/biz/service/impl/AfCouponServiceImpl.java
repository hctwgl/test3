/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.AfCouponDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午4:29:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afCouponService")
public class AfCouponServiceImpl implements AfCouponService {

	@Resource
	AfCouponDao afCouponDao;
	@Resource
	BizCacheUtil bizCacheUtil;

	@Override
	public List<AfCouponDto> selectCouponByCouponIds(String ids,Long userId) {
		return afCouponDao.selectCouponByCouponIds(ids,userId);
	}

	@Override
	public AfCouponDo getCouponById(Long couponId) {
		String key = Constants.CACHEKEY_COUPON_INFO + couponId;
		AfCouponDo couponDo = (AfCouponDo)bizCacheUtil.getObject(key);
		if(couponDo != null){
			return couponDo;
		}
		couponDo = afCouponDao.getCouponById(couponId);
		if(couponDo != null){
			bizCacheUtil.saveObject(key, couponDo);
		}
		return couponDo;
	}

	
	@Override
	public AfCouponDo getCouponInfoById(Long couponId) {
		String key = Constants.CACHEKEY_COUPON_INFO + couponId;
		AfCouponDo couponDo = (AfCouponDo)bizCacheUtil.getObject(key);
		if(couponDo != null){
			return couponDo;
		}
		couponDo = afCouponDao.getCouponInfoById(couponId);
		if(couponDo != null){
			bizCacheUtil.saveObject(key, couponDo);
		}
		return couponDo;
	}
	
	@Override
	public int updateCouponquotaAlreadyById(AfCouponDo couponDo) {
		return afCouponDao.updateCouponquotaAlreadyById(couponDo);
	}

}
