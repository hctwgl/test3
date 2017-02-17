package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;

/**
 * 
 *@类描述：AfUserCouponDao
 *@author 何鑫 2017年1月20日  11:28:46
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserCouponDao {

	/**
	 * 获取优惠券列表
	 * @param query
	 * @return
	 */
	List<AfUserCouponDto> getUserCouponByUser(AfUserCouponQuery query);
	
	/**
	 * 获取可用优惠券数量
	 * @param userId
	 * @return
	 */
	int getUserCouponByUserNouse(@Param("userId")Long userId);
	
	/**
	 * 获取可用优惠券数量
	 * @param userId
	 * @param couponId
	 * @return
	 */
	int getUserCouponByUserIdAndCouponId(@Param("userId")Long userId,@Param("couponId")Long couponId);
	
	int addUserCoupon(AfUserCouponDo afUserCouponDo);
	
	
	BigDecimal getUserCouponByInvite(@Param("userId")Long userId);
}
