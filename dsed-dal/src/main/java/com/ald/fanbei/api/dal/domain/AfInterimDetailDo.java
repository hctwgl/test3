package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhourui on 2017年11月17日 17:16
 * @类描述：用户临时额度使用记录
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfInterimDetailDo  extends AbstractSerial {
    private static final long serialVersionUID = 1L;

    private Long rid;
    private Long userId;//用户ID
    private Integer type;//类型 1 支付，2 还款，3退款
    private BigDecimal amount;//金额
    private BigDecimal interimUsed;//已使用临时额度
    private Long orderId;//订单ID
    private Date gmtCreate;//创建时间
    private Date gmtModify;//修改时间

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public BigDecimal getInterimUsed() {
        return interimUsed;
    }

    public void setInterimUsed(BigDecimal interimUsed) {
        this.interimUsed = interimUsed;
    }
}
