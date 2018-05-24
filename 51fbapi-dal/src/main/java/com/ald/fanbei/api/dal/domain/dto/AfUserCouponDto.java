package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AfUserCouponDto extends AfUserCouponDo {

    private static final long serialVersionUID = 1833447250830899472L;
    private String name;//优惠券名称
    private BigDecimal amount;//优惠券金额
    private BigDecimal limitAmount;//起始金额
    private String useRule;//使用须知
    private Integer validDays;//
    private String willExpireStatus;//即将过去状态：Y：即将过期，N：没有即将过期
    private String type; // 优惠券类型【MOBILE：话费充值， REPAYMENT：还款, FULLVOUCHER:满减卷,CASH:现金奖励,ACTIVITY:会场券,FREEINTEREST:借钱免息券】
    private String useRange;
    private String shopUrl;
    private String goodsIds;
    private Integer isGlobal;//对应场景
    private Long activityId;
    private String activityType;
    private String expiryType;
    private BigDecimal discount;
    private Integer quota;
    private Integer quotaAlready;
    private Integer limitCount;
    private Integer userAlready;
    private String couponStatus;
}
