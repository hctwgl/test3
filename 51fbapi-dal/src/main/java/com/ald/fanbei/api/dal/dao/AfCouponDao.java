/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfCouponDo;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午3:47:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCouponDao {
	
	List<AfCouponDo> selectCouponByCouponIds(@Param("ids")String ids);
	
	AfCouponDo getCouponById(@Param("couponId")Long couponId);

}
