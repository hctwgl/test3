
package com.ald.fanbei.api.biz.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;

/**
 * 点亮活动新版Service
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:33
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeUserCouponService extends ParentService<AfBoluomeUserCouponDo, Long>{


 //   List<AfBoluomeUserCouponDo> getUserCouponListByUerIdAndChannel(AfBoluomeUserCouponDo queryUserCoupon);

    int getByCouponIdAndUserIdAndChannel(AfBoluomeUserCouponDo userCoupon);
    boolean sendCoupon(Long userId);
    int isHasCouponInDb(Long userId, Long couponId);
   // AfBoluomeUserCouponDo getLastUserCouponByUserId(Long userId);
    AfBoluomeUserCouponDo getUserCouponByUerIdAndRefIdAndChannel(AfBoluomeUserCouponDo queryUserCoupon);
    AfBoluomeUserCouponDo getLastUserCouponByUserIdSentCouponId(Long userId, Long newUser, Long inviter);
    /*
     * 今天获得的优惠券数量
     */
    int getTodayNumByUserId(Long userId);


}

