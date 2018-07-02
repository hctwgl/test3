package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;

/**
 * @author zhourui on 2018年01月17日 11:51
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfOrderSceneAmountDto extends AbstractSerial {
    private String scene;
    private BigDecimal auAmount;
    private BigDecimal usedAmount;

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
