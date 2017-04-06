/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfCouponDo;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午4:28:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCouponService {
	
	List<AfCouponDo> selectCouponByCouponIds(String ids);
	
	AfCouponDo getCouponById(Long couponId);
	
	int updateCouponquotaAlreadyById(AfCouponDo couponDo);
}
