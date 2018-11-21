package com.ald.jsd.mgr.dal.domain;


import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体
 * 
 * @author fanmanfu
 * @version 1.0.0 初始化
 * @date 2018-10-29 14:29:27
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class FinaneceDataDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    private String borrowNo;
    private String repayNo;
    private BigDecimal actualAmount;
    private Date actualTime;
    private String tppNid;
    private String payTradeNo;
    private String payType;
    private String productName;
    private String productType;
    private String liquidationCompany;
    /**
     * 期数
     */
    private String periods;

    /**
     * 应还时间
     */
    private Date gmtPlanRepay;

    /**
     * 应收总额
     */
    private BigDecimal predictAmount;

    /**
     * 累计已还金额
     */
    private BigDecimal repayAmount;

    /**
     * 剩余应还金额
     */
    private BigDecimal noneAmount;

    /**
     * 应收借款本金
     */
    private BigDecimal amount;

    /**
     * 应收利息
     */
    private BigDecimal retaAmount;

    /**
     * 应收手续费
     */
    private BigDecimal poundageAmount;

    /**
     * 应收逾期费
     */
    private BigDecimal overdueAmount;

    /**
     * 应收搭售商品费用
     */
    private BigDecimal shopAmount;

    /**
     * 状态
     */
    private String status;


    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public Date getGmtPlanRepay() {
        return gmtPlanRepay;
    }

    public void setGmtPlanRepay(Date gmtPlanRepay) {
        this.gmtPlanRepay = gmtPlanRepay;
    }

    public BigDecimal getPredictAmount() {
        return predictAmount;
    }

    public void setPredictAmount(BigDecimal predictAmount) {
        this.predictAmount = predictAmount;
    }

    public BigDecimal getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }

    public BigDecimal getNoneAmount() {
        return noneAmount;
    }

    public void setNoneAmount(BigDecimal noneAmount) {
        this.noneAmount = noneAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRetaAmount() {
        return retaAmount;
    }

    public void setRetaAmount(BigDecimal retaAmount) {
        this.retaAmount = retaAmount;
    }

    public BigDecimal getPoundageAmount() {
        return poundageAmount;
    }

    public void setPoundageAmount(BigDecimal poundageAmount) {
        this.poundageAmount = poundageAmount;
    }

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public BigDecimal getShopAmount() {
        return shopAmount;
    }

    public void setShopAmount(BigDecimal shopAmount) {
        this.shopAmount = shopAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getRepayNo() {
        return repayNo;
    }

    public void setRepayNo(String repayNo) {
        this.repayNo = repayNo;
    }
    /**
     * 获取借款编号
     *
     * @return 借款编号
     */
    public String getBorrowNo(){
      return borrowNo;
    }

    /**
     * 设置借款编号
     * 
     * @param borrowNo 要设置的借款编号
     */
    public void setBorrowNo(String borrowNo){
      this.borrowNo = borrowNo;
    }

    /**
     * 获取实际支付金额
     *
     * @return 实际支付金额
     */
    public BigDecimal getActualAmount(){
      return actualAmount;
    }

    /**
     * 设置实际支付金额
     * 
     * @param actualAmount 要设置的实际支付金额
     */
    public void setActualAmount(BigDecimal actualAmount){
      this.actualAmount = actualAmount;
    }

    /**
     * 获取实际支付时间
     *
     * @return 实际支付时间
     */
    public Date getActualTime(){
      return actualTime;
    }

    /**
     * 设置实际支付时间
     * 
     * @param actualTime 要设置的实际支付时间
     */
    public void setActualTime(Date actualTime){
      this.actualTime = actualTime;
    }

    /**
     * 获取第三方简称
     *
     * @return 第三方简称
     */
    public String getTppNid(){
      return tppNid;
    }

    /**
     * 设置第三方简称
     * 
     * @param tppNid 要设置的第三方简称
     */
    public void setTppNid(String tppNid){
      this.tppNid = tppNid;
    }

    /**
     *
     * @return 第三方流水号
     */
    public String getPayTradeNo(){
      return payTradeNo;
    }

    /**
     * 设置第三方流水号
     * 
     * @param payTradeNo 要设置的第三方流水号
     */
    public void setPayTradeNo(String payTradeNo){
      this.payTradeNo = payTradeNo;
    }

    /**
     * 获取产品类型
     *
     * @return 产品类型
     */
    public String getProductName(){
      return productName;
    }

    /**
     * 设置产品类型
     * 
     * @param productName 要设置的产品类型
     */
    public void setProductName(String productName){
      this.productName = productName;
    }

    /**
     * 获取产品类型
     *
     * @return 产品类型
     */
    public String getProductType(){
      return productType;
    }

    /**
     * 设置产品类型
     * 
     * @param productType 要设置的产品类型
     */
    public void setProductType(String productType){
      this.productType = productType;
    }

    /**
     * 获取清算公司
     *
     * @return 清算公司
     */
    public String getLiquidationCompany(){
      return liquidationCompany;
    }

    /**
     * 设置清算公司
     * 
     * @param liquidationCompany 要设置的清算公司
     */
    public void setLiquidationCompany(String liquidationCompany){
      this.liquidationCompany = liquidationCompany;
    }

}