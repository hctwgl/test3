/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午3:47:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCouponDao {
	
    List<AfCouponDto> selectCouponByCouponIds(@Param("ids")String ids, @Param("userId")Long userId);
	
	AfCouponDo getCouponById(@Param("couponId")Long couponId);
	
	int updateCouponquotaAlreadyById(AfCouponDo couponDo);

	AfCouponDo getCouponInfoById(@Param("couponId")Long couponId);

	List<String> getCouponNames(@Param("ids")List<String> ids);

	/**
	 * 根据优惠券id更新券的数量
	 * @ param couponDo
	 * **/
	int updateCouponquotaAndCouponquotaAlreadyById(AfCouponDo couponDo);

	List<AfCouponDto> getCouponByActivityIdAndType(@Param("activityId")Long activityId, @Param("activityType")String activityType);

	List<AfCouponDo> listCouponByIds(@Param("items")List<Long> couponIds);

	List<AfCouponDo> getByActivityType(@Param("activityType")String activityType);

	Integer addCoupon(AfCouponDo couponDo);

	AfCouponDo getCouponByName(@Param("name") String name);//根据名称查找券信息

	List<AfCouponDo> getCouponByIds(@Param("ids")List<String> ids);//根据名称查找券信息

}
