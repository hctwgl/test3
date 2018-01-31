package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;

/**
 * @author zhourui on 2018年01月17日 10:17
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfOrderSceneAmountDo extends AbstractSerial {
    private static final long serialVersionUID = -4712137543511553120L;

    private Long id;
    private Long orderId;
    private Long userId;
    private String scene;
    private BigDecimal auAmount;
    private BigDecimal usedAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public BigDecimal getAuAmount() {
        return auAmount;
    }

    public void setAuAmount(BigDecimal auAmount) {
        this.auAmount = auAmount;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }
}
