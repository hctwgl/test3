package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wangli
 * @date 2018/4/13 9:53
 */
@Getter
@Setter
public class AfOrderCountDto extends AbstractSerial {

    // 待付款订单数
    private Integer newOrderNum = 0;

    // 待发货订单数
    private Integer paidOrderNum = 0;

    // 待收货订单数
    private Integer deliveredOrderNum = 0;

    // 待返利订单数
    private Integer finishedOrderNum = 0;

    //售后处理中订单数
    private Integer afterSaleOrderNum = 0;

}