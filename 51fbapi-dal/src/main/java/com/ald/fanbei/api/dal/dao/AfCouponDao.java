/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

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

}
