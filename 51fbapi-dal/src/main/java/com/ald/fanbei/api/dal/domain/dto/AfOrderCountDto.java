package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.common.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

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

    public AfOrderCountDto(List<Map<String, Object>> data) {
        for (Map<String, Object> e : data) {
            Integer num = (Integer) e.get("num");
            String status = (String) e.get("status");
            if (status.equals(OrderStatus.NEW.getCode())) {
                newOrderNum = num;
            } else if (status.equals(OrderStatus.PAID.getCode())) {
                paidOrderNum = num;
            } else if (status.equals(OrderStatus.DELIVERED.getCode())) {
                deliveredOrderNum = num;
            } else if (status.equals(OrderStatus.FINISHED.getCode())) {
                finishedOrderNum = num;
            }
        }
    }
}