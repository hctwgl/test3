package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;

/**
 * 
 *@类描述：AfUserCouponService
 *@author 何鑫 2017年1月20日  11:57:43
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserCouponService {

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
	int getUserCouponByUserNouse(Long userId);
	/**
	 * 获取可用优惠券数量
	 * @param userId
	 * @param couponId
	 * @return
	 */
	int getUserCouponByUserIdAndCouponId(Long userId,Long couponId);
	
	/**
	 * 添加抵用券
	 * @param afUserCouponDo
	 * @return
	 */
	int addUserCoupon(AfUserCouponDo afUserCouponDo);
	
	BigDecimal getUserCouponByInvite(Long userId);
	/**
	 * 获取可用优惠券
	 * @return
	 */
	AfUserCouponDto getUserCouponById(Long rid);
	
	/**
	 * 修改优惠券为已使用状态
	 * @param rid
	 * @return
	 */
	int updateUserCouponSatusUsedById(Long rid);
}
