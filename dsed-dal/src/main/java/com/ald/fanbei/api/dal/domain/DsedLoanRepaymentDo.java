package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市易贷借款还款表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:45:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedLoanRepaymentDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;


    private int isDelete;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

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
     * 还款状态【'APPLY'-新建状态, 'SUCC'-还款成功, 'FAIL'-还款失败, 'PROCESSING'-处理中，CLOSED-关闭】
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
     * 该还款对应的期数信息(提前还款 期数id用逗号隔开)
     */
    private String repayPeriods;

    /**
     * 银行卡号
     */
    private String bankCardNumber;

    /**
     * 银行卡名称
     */
    private String bankCardName;

    /**
     * 还款备注
     */
    private String remark;

    /**
     * 还款渠道：ONLINE-线上主动还款，COLLECT-催收平台还款，ADMIN-管理后台财务还款，AUTO_REPAY-自动还款（代扣）
     */
    private String repayChannel;


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

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
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
     * 获取还款状态【'APPLY'-新建状态, 'SUCC'-还款成功, 'FAIL'-还款失败, 'PROCESSING'-处理中，CLOSED-关闭】
     *
     * @return 还款状态【'APPLY'-新建状态, 'SUCC'-还款成功, 'FAIL'-还款失败, 'PROCESSING'-处理中，CLOSED-关闭】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置还款状态【'APPLY'-新建状态, 'SUCC'-还款成功, 'FAIL'-还款失败, 'PROCESSING'-处理中，CLOSED-关闭】
     * 
     * @param status 要设置的还款状态【'APPLY'-新建状态, 'SUCC'-还款成功, 'FAIL'-还款失败, 'PROCESSING'-处理中，CLOSED-关闭】
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
     * 获取该还款对应的期数信息(提前还款 期数id用逗号隔开)
     *
     * @return 该还款对应的期数信息(提前还款 期数id用逗号隔开)
     */
    public String getRepayPeriods(){
      return repayPeriods;
    }

    /**
     * 设置该还款对应的期数信息(提前还款 期数id用逗号隔开)
     * 
     * @param repayPeriods 要设置的该还款对应的期数信息(提前还款 期数id用逗号隔开)
     */
    public void setRepayPeriods(String repayPeriods){
      this.repayPeriods = repayPeriods;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public String getBankCardName() {
        return bankCardName;
    }

    public void setBankCardName(String bankCardName) {
        this.bankCardName = bankCardName;
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
     * 获取还款渠道：ONLINE-线上主动还款，COLLECT-催收平台还款，ADMIN-管理后台财务还款，AUTO_REPAY-自动还款（代扣）
     *
     * @return 还款渠道：ONLINE-线上主动还款，COLLECT-催收平台还款，ADMIN-管理后台财务还款，AUTO_REPAY-自动还款（代扣）
     */
    public String getRepayChannel(){
      return repayChannel;
    }

    /**
     * 设置还款渠道：ONLINE-线上主动还款，COLLECT-催收平台还款，ADMIN-管理后台财务还款，AUTO_REPAY-自动还款（代扣）
     * 
     * @param repayChannel 要设置的还款渠道：ONLINE-线上主动还款，COLLECT-催收平台还款，ADMIN-管理后台财务还款，AUTO_REPAY-自动还款（代扣）
     */
    public void setRepayChannel(String repayChannel){
      this.repayChannel = repayChannel;
    }

}