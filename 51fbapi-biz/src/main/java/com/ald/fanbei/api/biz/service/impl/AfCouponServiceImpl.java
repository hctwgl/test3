/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfCouponService;
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

	@Override
	public List<AfCouponDto> selectCouponByCouponIds(String ids,Long userId) {
		return afCouponDao.selectCouponByCouponIds(ids,userId);
	}

	@Override
	public AfCouponDo getCouponById(Long couponId) {
		return afCouponDao.getCouponById(couponId);
	}

	
	@Override
	public int updateCouponquotaAlreadyById(AfCouponDo couponDo) {
		return afCouponDao.updateCouponquotaAlreadyById(couponDo);
	}

}
