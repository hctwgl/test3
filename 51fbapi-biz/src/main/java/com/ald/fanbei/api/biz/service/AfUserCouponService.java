package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;



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
	 * 获取用户优惠券
	 * @return
	 */
	AfUserCouponDto getUserCouponById(Long rid);
	
	/**
	 * 修改优惠券为已使用状态
	 * @param rid
	 * @return
	 */
	int updateUserCouponSatusUsedById(Long rid);
	
	int updateUserCouponSatusNouseById(Long rid);
	/**修改优惠券过期状态
	 * 
	 * @param rid
	 * @return
	 */
	int updateUserCouponSatusExpireById(Long rid);
	
	
	/**
	 * 获取类型可用优惠券
	 * @param userId
	 * @param type
	 * @return
	 */
	List<AfUserCouponDto> getUserCouponByUserIdAndType(Long userId,String type,BigDecimal amount);
	/**
	 * 获取类型可用优惠券
	 * @param userId
	 * @param type
	 * @return
	 */
	List<AfUserCouponDto> getUserCouponByType(Long userId,String type);
	
	/**
	 * 发放优惠券
	 * @param userId
	 * @param couponId
	 * @param source_type 来源类型
	 * @param source_ref  来源关联id
	 */
	void grantCoupon(Long userId,Long couponId,String sourceType,String sourceRef);

	/**
	 * 获取用户专场可使用满减券
	 * @param userId
	 * @param amount
	 * @param goodsId
	 * @return
	 */
	List<AfUserCouponDto> getUserAcgencyCouponByAmount(Long userId,BigDecimal amount);

	/**
	 * 获取用户专场可使用满减券个数
	 * @param userId
	 * @param amount
	 * @param goodsId
	 * @return
	 */
	int getUserAcgencyCountByAmount(Long userId,BigDecimal amount,Long goodsId);
	
	/**
	 * 通过用户ID和优惠劵来源获取用户信息
	 * @param userId
	 * @return
	 */
	List<AfUserCouponDto> getUserCouponByUserIdAndSourceType(Long userId,String sourceType);

	AfUserCouponDo getUserCouponByDo(AfUserCouponDo afUserCouponDo);

	AfUserCouponDto getSubjectUserCouponByAmountAndCouponId(Long userId, BigDecimal actualAmount, String couponId);

	/**
	 * 根据用户Id和couponId查询用户的优惠券
	 * @param userId
	 * @param couponId
	 * @return
	 * 
	 * **/
	List<AfUserCouponDto> getUserCouponListByUserIdAndCouponId(Long userId,Long couponId);

	List<AfUserCouponDto> getActivitySpecialCouponByAmount(Long userId, BigDecimal actualAmount,Long activityId, String activityType);

	void grantCouponForRedRain(Long userId, Long couponId, String sourceType, String sourceRef);

}
