/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午4:28:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCouponService {
	
	List<AfCouponDto> selectCouponByCouponIds(String ids,Long userId);
	
	AfCouponDo getCouponById(Long couponId);
	
	AfCouponDo getCouponInfoById(Long couponId);
	
	int updateCouponquotaAlreadyById(AfCouponDo couponDo);

	/**
	 * 根据优惠券id更新券的数量
	 * @ param couponDo
	 * **/
	int updateCouponquotaById(AfCouponDo couponDo);

	AfCouponDo getCoupon(Long couponId);
	List<String> getCouponNames(List<String> ids);

	List<AfCouponDto> getCouponByActivityIdAndType(Long modelId, String code);

	List<AfCouponDo> listCouponByIds(List<Long> couponIds);

	List<AfCouponDo> getByActivityType(String activityType);

	List<AfCouponDo> getCouponByIds(List<String> ids);

}
