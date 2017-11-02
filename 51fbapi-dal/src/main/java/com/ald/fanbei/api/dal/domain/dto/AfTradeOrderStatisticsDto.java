package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

/**
 * @author shencheng 2017/7/31 上午10:59
 * @类描述: AfTradeOrderStatisticsDto
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfTradeOrderStatisticsDto {

    private BigDecimal money = BigDecimal.ZERO;

    private Long count = 0l;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
