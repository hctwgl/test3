package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;

public class AfTradeRebateModelBo {
    
    private int nper;
    private BigDecimal rebatePercent;
    private int freeNper;

    public int getNper() {
        return nper;
    }

    public void setNper(int nper) {
        this.nper = nper;
    }

    public BigDecimal getRebatePercent() {
        return rebatePercent;
    }

    public void setRebatePercent(BigDecimal  rebatePercent) {
        this.rebatePercent = rebatePercent;
    }

    public int getFreeNper() {
        return freeNper;
    }

    public void setFreeNper(int freeNper) {
        this.freeNper = freeNper;
    }
}
