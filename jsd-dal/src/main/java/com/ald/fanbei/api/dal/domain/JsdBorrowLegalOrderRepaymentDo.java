package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 极速贷实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdBorrowLegalOrderRepaymentDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long borrowId;

    /**
     * 
     */
    private Long borrowLegalOrderCashId;

    /**
     * 还款总额
     */
    private BigDecimal repayAmount;

    /**
     * 实付现金
     */
    private BigDecimal actualAmount;

    /**
     * 还款名称
     */
    private String name;

    /**
     * 我方系统还款交易流水号
     */
    private String tradeNo;

    /**
     * UPS交易流水号
     */
    private String tradeNoUps;

    /**
     * 微信交易流水号
     */
    private String tradeNoWx;

    /**
     * 支付宝交易流水号
     */
    private String tradeNoZfb;

    /**
     * 还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     */
    private String status;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 银行卡号
     */
    private String cardNo;

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;


    private String repayNo;



    /**
     * 获取主键Id
     *
     * @return rid
     */
    public Long getRid(){
      return rid;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    
    /**
     * 获取
     *
     * @return 
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置
     * 
     * @param userId 要设置的
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置
     * 
     * @param borrowId 要设置的
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Long getBorrowLegalOrderCashId(){
      return borrowLegalOrderCashId;
    }

    /**
     * 设置
     * 
     * @param borrowLegalOrderCashId 要设置的
     */
    public void setBorrowLegalOrderCashId(Long borrowLegalOrderCashId){
      this.borrowLegalOrderCashId = borrowLegalOrderCashId;
    }

    /**
     * 获取还款总额
     *
     * @return 还款总额
     */
    public BigDecimal getRepayAmount(){
      return repayAmount;
    }

    /**
     * 设置还款总额
     * 
     * @param repayAmount 要设置的还款总额
     */
    public void setRepayAmount(BigDecimal repayAmount){
      this.repayAmount = repayAmount;
    }

    /**
     * 获取实付现金
     *
     * @return 实付现金
     */
    public BigDecimal getActualAmount(){
      return actualAmount;
    }

    /**
     * 设置实付现金
     * 
     * @param actualAmount 要设置的实付现金
     */
    public void setActualAmount(BigDecimal actualAmount){
      this.actualAmount = actualAmount;
    }

    /**
     * 获取还款名称
     *
     * @return 还款名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置还款名称
     * 
     * @param name 要设置的还款名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取我方系统还款交易流水号
     *
     * @return 我方系统还款交易流水号
     */
    public String getTradeNo(){
      return tradeNo;
    }

    /**
     * 设置我方系统还款交易流水号
     * 
     * @param tradeNo 要设置的我方系统还款交易流水号
     */
    public void setTradeNo(String tradeNo){
      this.tradeNo = tradeNo;
    }

    /**
     * 获取UPS交易流水号
     *
     * @return UPS交易流水号
     */
    public String getTradeNoUps(){
      return tradeNoUps;
    }

    /**
     * 设置UPS交易流水号
     * 
     * @param tradeNoUps 要设置的UPS交易流水号
     */
    public void setTradeNoUps(String tradeNoUps){
      this.tradeNoUps = tradeNoUps;
    }

    /**
     * 获取微信交易流水号
     *
     * @return 微信交易流水号
     */
    public String getTradeNoWx(){
      return tradeNoWx;
    }

    /**
     * 设置微信交易流水号
     * 
     * @param tradeNoWx 要设置的微信交易流水号
     */
    public void setTradeNoWx(String tradeNoWx){
      this.tradeNoWx = tradeNoWx;
    }

    /**
     * 获取支付宝交易流水号
     *
     * @return 支付宝交易流水号
     */
    public String getTradeNoZfb(){
      return tradeNoZfb;
    }

    /**
     * 设置支付宝交易流水号
     * 
     * @param tradeNoZfb 要设置的支付宝交易流水号
     */
    public void setTradeNoZfb(String tradeNoZfb){
      this.tradeNoZfb = tradeNoZfb;
    }

    /**
     * 获取还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     *
     * @return 还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     * 
     * @param status 要设置的还款状态【’A’-新建状态,'Y'-还款成功, N：”还款失败”,'P':处理中】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取卡名称
     *
     * @return 卡名称
     */
    public String getCardName(){
      return cardName;
    }

    /**
     * 设置卡名称
     * 
     * @param cardName 要设置的卡名称
     */
    public void setCardName(String cardName){
      this.cardName = cardName;
    }

    /**
     * 获取银行卡号
     *
     * @return 银行卡号
     */
    public String getCardNo(){
      return cardNo;
    }

    /**
     * 设置银行卡号
     * 
     * @param cardNo 要设置的银行卡号
     */
    public void setCardNo(String cardNo){
      this.cardNo = cardNo;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置
     * 
     * @param gmtCreate 要设置的
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置
     * 
     * @param gmtModified 要设置的
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    public String getRepayNo() {
        return repayNo;
    }

    public void setRepayNo(String repayNo) {
        this.repayNo = repayNo;
    }
}