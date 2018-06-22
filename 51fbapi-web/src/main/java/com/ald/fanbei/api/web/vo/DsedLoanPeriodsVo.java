package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author gsq
 * @Description: 都市e贷借钱信息vo
 * @date 2018年1月25日
 */
public class DsedLoanPeriodsVo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    private int curPeriod;        // 当前期数
    private String status;        // 贷款状态
    private BigDecimal totalAmount;        // 每期应还款总金额
    private int totalPeriod;        // 总期限
    private BigDecimal unrepayAmount;        // 剩余未还金额
    private Date gmtPlanRepay;        // 本月还款时间
    private BigDecimal amount;        // 借款金额

    public int getCurPeriod() {
        return curPeriod;
    }

    public void setCurPeriod(int curPeriod) {
        this.curPeriod = curPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalPeriod() {
        return totalPeriod;
    }

    public void setTotalPeriod(int totalPeriod) {
        this.totalPeriod = totalPeriod;
    }

    public BigDecimal getUnrepayAmount() {
        return unrepayAmount;
    }

    public void setUnrepayAmount(BigDecimal unrepayAmount) {
        this.unrepayAmount = unrepayAmount;
    }

    public Date getGmtPlanRepay() {
        return gmtPlanRepay;
    }

    public void setGmtPlanRepay(Date gmtPlanRepay) {
        this.gmtPlanRepay = gmtPlanRepay;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
