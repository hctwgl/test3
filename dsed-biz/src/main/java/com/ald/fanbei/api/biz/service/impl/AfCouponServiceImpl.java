/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
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
		return afCouponDao.getCouponById(couponId);
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

	@Override
	public List<String> getCouponNames(List<String> ids) {
		return afCouponDao.getCouponNames(ids);
	}

	@Override
	public int updateCouponquotaById(AfCouponDo couponDo) {
		return afCouponDao.updateCouponquotaAndCouponquotaAlreadyById(couponDo);
	}

	@Override
	public AfCouponDo getCoupon(Long couponId) {
		return afCouponDao.getCouponById(couponId);
	}

	@Override
	public List<AfCouponDto> getCouponByActivityIdAndType(Long activityId, String activityType) {
		return afCouponDao.getCouponByActivityIdAndType(activityId, activityType);
	}

	@Override
	public List<AfCouponDo> listCouponByIds(List<Long> couponIds) {
	    // TODO Auto-generated method stub
	    	return afCouponDao.listCouponByIds(couponIds);
	}

	@Override
	public List<AfCouponDo> getByActivityType(String activityType) {
	    // TODO Auto-generated method stub
	         return afCouponDao.getByActivityType(activityType);
	}

	@Override
	public List<AfCouponDo> getCouponByIds(List<String> ids) {
		return afCouponDao.getCouponByIds(ids);
	}

}
