package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 贷款业务实体
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-23 13:41:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfLoanRepaymentDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 借款编号
     */
    private Long loanId;

    /**
     * 还款名称
     */
    private String name;

    /**
     * 还款金额
     */
    private BigDecimal repayAmount;

    /**
     * 实际付款金额
     */
    private BigDecimal actualAmount;

    /**
     * 还款状态【'A'-新建状态, 'Y'-还款成功, 'N'-还款失败, 'P'-处理中】
     */
    private String status;

    /**
     * 我方交易流水号
     */
    private String tradeNo;

    /**
     * 三方资金交易流水
     */
    private String tradeNoOut;

    /**
     * 用户优惠券Id
     */
    private Long userCouponId;

    /**
     * 优惠券金额
     */
    private BigDecimal couponAmount;

    /**
     * 使用的返利余额
     */
    private BigDecimal userAmount;

    /**
     * 是否提前还款，"Y"表示提前还款，"N"表示非提前还款
     */
    private String preRepayStatus;

    /**
     * 产品类型
     */
    private String prdType;

    /**
     * 该还款对应的期数信息
     */
    private String repayPeriods;

    /**
     * 银行卡号
     */
    private String cardNo;

    /**
     * 银行卡名称
     */
    private String cardName;

    /**
     * 还款备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;



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
     * 获取用户编号
     *
     * @return 用户编号
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户编号
     * 
     * @param userId 要设置的用户编号
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取借款编号
     *
     * @return 借款编号
     */
    public Long getLoanId(){
      return loanId;
    }

    /**
     * 设置借款编号
     * 
     * @param loanId 要设置的借款编号
     */
    public void setLoanId(Long loanId){
      this.loanId = loanId;
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
     * 获取还款金额
     *
     * @return 还款金额
     */
    public BigDecimal getRepayAmount(){
      return repayAmount;
    }

    /**
     * 设置还款金额
     * 
     * @param repayAmount 要设置的还款金额
     */
    public void setRepayAmount(BigDecimal repayAmount){
      this.repayAmount = repayAmount;
    }

    /**
     * 获取实际付款金额
     *
     * @return 实际付款金额
     */
    public BigDecimal getActualAmount(){
      return actualAmount;
    }

    /**
     * 设置实际付款金额
     * 
     * @param actualAmount 要设置的实际付款金额
     */
    public void setActualAmount(BigDecimal actualAmount){
      this.actualAmount = actualAmount;
    }

    /**
     * 获取还款状态【'A'-新建状态, 'Y'-还款成功, 'N'-还款失败, 'P'-处理中】
     *
     * @return 还款状态【'A'-新建状态, 'Y'-还款成功, 'N'-还款失败, 'P'-处理中】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置还款状态【'A'-新建状态, 'Y'-还款成功, 'N'-还款失败, 'P'-处理中】
     * 
     * @param status 要设置的还款状态【'A'-新建状态, 'Y'-还款成功, 'N'-还款失败, 'P'-处理中】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取我方交易流水号
     *
     * @return 我方交易流水号
     */
    public String getTradeNo(){
      return tradeNo;
    }

    /**
     * 设置我方交易流水号
     * 
     * @param tradeNo 要设置的我方交易流水号
     */
    public void setTradeNo(String tradeNo){
      this.tradeNo = tradeNo;
    }

    /**
     * 获取三方资金交易流水
     *
     * @return 三方资金交易流水
     */
    public String getTradeNoOut(){
      return tradeNoOut;
    }

    /**
     * 设置三方资金交易流水
     * 
     * @param tradeNoOut 要设置的三方资金交易流水
     */
    public void setTradeNoOut(String tradeNoOut){
      this.tradeNoOut = tradeNoOut;
    }

    /**
     * 获取用户优惠券Id
     *
     * @return 用户优惠券Id
     */
    public Long getUserCouponId(){
      return userCouponId;
    }

    /**
     * 设置用户优惠券Id
     * 
     * @param userCouponId 要设置的用户优惠券Id
     */
    public void setUserCouponId(Long userCouponId){
      this.userCouponId = userCouponId;
    }

    /**
     * 获取优惠券金额
     *
     * @return 优惠券金额
     */
    public BigDecimal getCouponAmount(){
      return couponAmount;
    }

    /**
     * 设置优惠券金额
     * 
     * @param couponAmount 要设置的优惠券金额
     */
    public void setCouponAmount(BigDecimal couponAmount){
      this.couponAmount = couponAmount;
    }

    /**
     * 获取使用的返利余额
     *
     * @return 使用的返利余额
     */
    public BigDecimal getUserAmount(){
      return userAmount;
    }

    /**
     * 设置使用的返利余额
     * 
     * @param userAmount 要设置的使用的返利余额
     */
    public void setUserAmount(BigDecimal userAmount){
      this.userAmount = userAmount;
    }

    /**
     * 获取是否提前还款，"Y"表示提前还款，"N"表示非提前还款
     *
     * @return 是否提前还款，"Y"表示提前还款，"N"表示非提前还款
     */
    public String getPreRepayStatus(){
      return preRepayStatus;
    }

    /**
     * 设置是否提前还款，"Y"表示提前还款，"N"表示非提前还款
     * 
     * @param preRepayStatus 要设置的是否提前还款，"Y"表示提前还款，"N"表示非提前还款
     */
    public void setPreRepayStatus(String preRepayStatus){
      this.preRepayStatus = preRepayStatus;
    }

    /**
     * 获取产品类型
     *
     * @return 产品类型
     */
    public String getPrdType(){
      return prdType;
    }

    /**
     * 设置产品类型
     * 
     * @param prdType 要设置的产品类型
     */
    public void setPrdType(String prdType){
      this.prdType = prdType;
    }

    /**
     * 获取该还款对应的期数信息
     *
     * @return 该还款对应的期数信息
     */
    public String getRepayPeriods(){
      return repayPeriods;
    }

    /**
     * 设置该还款对应的期数信息
     * 
     * @param referLoanPeriods 要设置的该还款对应的期数信息
     */
    public void setRepayPeriods(String repayPeriods){
      this.repayPeriods = repayPeriods;
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
     * 获取银行卡名称
     *
     * @return 银行卡名称
     */
    public String getCardName(){
      return cardName;
    }

    /**
     * 设置银行卡名称
     * 
     * @param cardName 要设置的银行卡名称
     */
    public void setCardName(String cardName){
      this.cardName = cardName;
    }

    /**
     * 获取还款备注
     *
     * @return 还款备注
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置还款备注
     * 
     * @param remark 要设置的还款备注
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间
     * 
     * @param gmtCreate 要设置的创建时间
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置最后修改时间
     * 
     * @param gmtModified 要设置的最后修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }


}