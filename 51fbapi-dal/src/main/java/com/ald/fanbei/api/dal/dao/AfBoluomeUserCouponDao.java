
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;

/**
 * 点亮活动新版Dao
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:33
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeUserCouponDao extends BaseDao<AfBoluomeUserCouponDo, Long> {

    List<AfBoluomeUserCouponDo> getUserRecommendCouponListByUserId(@Param("userId")Long userId);

    List<AfBoluomeUserCouponDo> getUserCouponListByUerIdAndChannel(AfBoluomeUserCouponDo queryUserCoupon);

    int getByCouponIdAndUserIdAndChannel(AfBoluomeUserCouponDo userCoupon);

    int checkIfHaveCoupon(@Param("userId") Long userId);

    int isHasCouponInDb(@Param("userId")Long userId, @Param("couponId")Long couponId);

 //   AfBoluomeUserCouponDo getLastUserCouponByUserId(@Param("userId") Long userId);

    AfBoluomeUserCouponDo getUserCouponByUerIdAndRefIdAndChannel(AfBoluomeUserCouponDo userCoupon);


    AfBoluomeUserCouponDo getLastUserCouponByUserIdSentCouponId(@Param("userId")Long userId,@Param("newUser") Long newUser,@Param("inviter") Long inviter);

    int getTodayNumByUserId(@Param("userId")Long  userId);


    

}

