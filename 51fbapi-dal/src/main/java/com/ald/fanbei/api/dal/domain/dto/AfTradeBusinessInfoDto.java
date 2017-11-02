package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;

/**
 * @author 沈铖 2017/7/17 下午4:00
 * @类描述: AfTradeBusinessInfoDto
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfTradeBusinessInfoDto extends AfTradeBusinessInfoDo {
    private Long orderId;

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
