package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.common.enums.AfLoanStatus;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 借款实体
 * 
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:48:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedLoanDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

   public static DsedLoanDo gen(Long userId, String loanNo, String prdType, int periods,
                              BigDecimal serviceRate, BigDecimal interestRate, BigDecimal overdueRate, BigDecimal userLayDailyRate,
                              BigDecimal amount, BigDecimal totalServiceFee, BigDecimal totalInterestFee) {
      DsedLoanDo l = new DsedLoanDo();
      l.userId = userId;
      l.loanNo = loanNo;
      l.prdType = prdType;
      l.periods = periods;
      l.riskDailyRate = userLayDailyRate;
      l.serviceRate = serviceRate;
      l.interestRate = interestRate;
      l.overdueRate = overdueRate;
      l.amount = amount;
      l.totalServiceFee = totalServiceFee;
      l.totalInterestFee = totalInterestFee;

      l.gmtCreate = new Date();
      l.status = AfLoanStatus.APPLY.name();
      return l;
   }

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 借款流水
     */
    private String loanNo;

    /**
     * 资金方三方交易流水号
     */
    private String tradeNoOut;

    /**
     * 借款期数
     */
    private Integer periods;

    /**
     * 借款金额
     */
    private BigDecimal amount;

    /**
     * 实际到账金额
     */
    private BigDecimal arrivalAmount;

    /**
     * 公司内部字段：返还利息记录用
     */
    private BigDecimal remitAmount;

    /**
     * 贷款产品类型
     */
    private String prdType;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 最大逾期天数
     */
    private Integer overdueDays;

    /**
     * 逾期总金额（未还）。注意：跑逾期时会覆盖更新；还款时则相应减量操作；
     */
    private BigDecimal overdueAmount;

    /**
     * 逾期费率
     */
    private BigDecimal overdueRate;

    /**
     * 逾期状态，Y表示逾期，N表示未逾期
     */
    private String overdueStatus;

    /**
     * 审核人姓名
     */
    private String reviewName;

    /**
     * 审核人帐号
     */
    private String reviewNo;

    /**
     * 审核详情
     */
    private String reviewDetails;

    /**
     * 审核状态【APPLY:申请/待风控审核 ，AGREE:风控同意 ， WAIT_FBREVIEW:待返呗审核通过 ，REFUSE:风控拒绝， FBAGREE:返呗审核同意， FBREFUSE:返呗平台审核拒绝】 
     */
    private String reviewStatus;

    /**
     * 风控流水号
     */
    private String riskNo;

    /**
     * 借钱状态：【APPLY:申请/未审核， TRANSFERING:打款中 ，  TRANSFERRED:已经打款/待还款 ，CLOSED:关闭，FINISHED:已结清】
     */
    private String status;

    /**
     * 总手续费
     */
    private BigDecimal totalServiceFee;

    /**
     * 手续费率
     */
    private BigDecimal serviceRate;

    /**
     * 总利息
     */
    private BigDecimal totalInterestFee;

    /**
     * 利率
     */
    private BigDecimal interestRate;

    /**
     * 用户分层日利率
     */
    private BigDecimal riskDailyRate;

    /**
     * 白领贷额度，用户授信额度
     */
    private BigDecimal auAmount;

    /**
     * App来源: www为返呗,其余的为马甲包
     */
    private String appName;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 借款用途
     */
    private String loanRemark;

    /**
     * 还款来源
     */
    private String repayRemark;

    /**
     * 打款时间
     */
    private Date gmtArrival;

    /**
     * 审核时间
     */
    private Date gmtReview;

    /**
     * 借款关闭时间
     */
    private Date gmtClose;

    /**
     * 结清时间
     */
    private Date gmtFinish;

    /**
     * 借款发生时IP地址
     */
    private String ip;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 省份
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县
     */
    private String county;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 设备编号
     */
    private String deviceNo;


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
     * 获取借款流水
     *
     * @return 借款流水
     */
    public String getLoanNo(){
      return loanNo;
    }

    /**
     * 设置借款流水
     * 
     * @param loanNo 要设置的借款流水
     */
    public void setLoanNo(String loanNo){
      this.loanNo = loanNo;
    }

    /**
     * 获取资金方三方交易流水号
     *
     * @return 资金方三方交易流水号
     */
    public String getTradeNoOut(){
      return tradeNoOut;
    }

    /**
     * 设置资金方三方交易流水号
     * 
     * @param tradeNoOut 要设置的资金方三方交易流水号
     */
    public void setTradeNoOut(String tradeNoOut){
      this.tradeNoOut = tradeNoOut;
    }

    /**
     * 获取借款期数
     *
     * @return 借款期数
     */
    public Integer getPeriods(){
      return periods;
    }

    /**
     * 设置借款期数
     * 
     * @param periods 要设置的借款期数
     */
    public void setPeriods(Integer periods){
      this.periods = periods;
    }

    /**
     * 获取借款金额
     *
     * @return 借款金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置借款金额
     * 
     * @param amount 要设置的借款金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取实际到账金额
     *
     * @return 实际到账金额
     */
    public BigDecimal getArrivalAmount(){
      return arrivalAmount;
    }

    /**
     * 设置实际到账金额
     * 
     * @param arrivalAmount 要设置的实际到账金额
     */
    public void setArrivalAmount(BigDecimal arrivalAmount){
      this.arrivalAmount = arrivalAmount;
    }

    /**
     * 获取公司内部字段：返还利息记录用
     *
     * @return 公司内部字段：返还利息记录用
     */
    public BigDecimal getRemitAmount(){
      return remitAmount;
    }

    /**
     * 设置公司内部字段：返还利息记录用
     * 
     * @param remitAmount 要设置的公司内部字段：返还利息记录用
     */
    public void setRemitAmount(BigDecimal remitAmount){
      this.remitAmount = remitAmount;
    }

    /**
     * 获取贷款产品类型
     *
     * @return 贷款产品类型
     */
    public String getPrdType(){
      return prdType;
    }

    /**
     * 设置贷款产品类型
     * 
     * @param prdType 要设置的贷款产品类型
     */
    public void setPrdType(String prdType){
      this.prdType = prdType;
    }

    /**
     * 获取卡号
     *
     * @return 卡号
     */
    public String getCardNo(){
      return cardNo;
    }

    /**
     * 设置卡号
     * 
     * @param cardNo 要设置的卡号
     */
    public void setCardNo(String cardNo){
      this.cardNo = cardNo;
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
     * 获取最大逾期天数
     *
     * @return 最大逾期天数
     */
    public Integer getOverdueDays(){
      return overdueDays;
    }

    /**
     * 设置最大逾期天数
     * 
     * @param overdueDays 要设置的最大逾期天数
     */
    public void setOverdueDays(Integer overdueDays){
      this.overdueDays = overdueDays;
    }

    /**
     * 获取逾期总金额（未还）。注意：跑逾期时会覆盖更新；还款时则相应减量操作；
     *
     * @return 逾期总金额（未还）。注意：跑逾期时会覆盖更新；还款时则相应减量操作；
     */
    public BigDecimal getOverdueAmount(){
      return overdueAmount;
    }

    /**
     * 设置逾期总金额（未还）。注意：跑逾期时会覆盖更新；还款时则相应减量操作；
     * 
     * @param overdueAmount 要设置的逾期总金额（未还）。注意：跑逾期时会覆盖更新；还款时则相应减量操作；
     */
    public void setOverdueAmount(BigDecimal overdueAmount){
      this.overdueAmount = overdueAmount;
    }

    /**
     * 获取逾期费率
     *
     * @return 逾期费率
     */
    public BigDecimal getOverdueRate(){
      return overdueRate;
    }

    /**
     * 设置逾期费率
     * 
     * @param overdueRate 要设置的逾期费率
     */
    public void setOverdueRate(BigDecimal overdueRate){
      this.overdueRate = overdueRate;
    }

    /**
     * 获取逾期状态，Y表示逾期，N表示未逾期
     *
     * @return 逾期状态，Y表示逾期，N表示未逾期
     */
    public String getOverdueStatus(){
      return overdueStatus;
    }

    /**
     * 设置逾期状态，Y表示逾期，N表示未逾期
     * 
     * @param overdueStatus 要设置的逾期状态，Y表示逾期，N表示未逾期
     */
    public void setOverdueStatus(String overdueStatus){
      this.overdueStatus = overdueStatus;
    }

    /**
     * 获取审核人姓名
     *
     * @return 审核人姓名
     */
    public String getReviewName(){
      return reviewName;
    }

    /**
     * 设置审核人姓名
     * 
     * @param reviewName 要设置的审核人姓名
     */
    public void setReviewName(String reviewName){
      this.reviewName = reviewName;
    }

    /**
     * 获取审核人帐号
     *
     * @return 审核人帐号
     */
    public String getReviewNo(){
      return reviewNo;
    }

    /**
     * 设置审核人帐号
     * 
     * @param reviewNo 要设置的审核人帐号
     */
    public void setReviewNo(String reviewNo){
      this.reviewNo = reviewNo;
    }

    /**
     * 获取审核详情
     *
     * @return 审核详情
     */
    public String getReviewDetails(){
      return reviewDetails;
    }

    /**
     * 设置审核详情
     * 
     * @param reviewDetails 要设置的审核详情
     */
    public void setReviewDetails(String reviewDetails){
      this.reviewDetails = reviewDetails;
    }

    /**
     * 获取审核状态【APPLY:申请/待风控审核 ，AGREE:风控同意 ， WAIT_FBREVIEW:待返呗审核通过 ，REFUSE:风控拒绝， FBAGREE:返呗审核同意， FBREFUSE:返呗平台审核拒绝】 
     *
     * @return 审核状态【APPLY:申请/待风控审核 ，AGREE:风控同意 ， WAIT_FBREVIEW:待返呗审核通过 ，REFUSE:风控拒绝， FBAGREE:返呗审核同意， FBREFUSE:返呗平台审核拒绝】 
     */
    public String getReviewStatus(){
      return reviewStatus;
    }

    /**
     * 设置审核状态【APPLY:申请/待风控审核 ，AGREE:风控同意 ， WAIT_FBREVIEW:待返呗审核通过 ，REFUSE:风控拒绝， FBAGREE:返呗审核同意， FBREFUSE:返呗平台审核拒绝】 
     * 
     * @param reviewStatus 要设置的审核状态【APPLY:申请/待风控审核 ，AGREE:风控同意 ， WAIT_FBREVIEW:待返呗审核通过 ，REFUSE:风控拒绝， FBAGREE:返呗审核同意， FBREFUSE:返呗平台审核拒绝】 
     */
    public void setReviewStatus(String reviewStatus){
      this.reviewStatus = reviewStatus;
    }

    /**
     * 获取风控流水号
     *
     * @return 风控流水号
     */
    public String getRiskNo(){
      return riskNo;
    }

    /**
     * 设置风控流水号
     * 
     * @param riskNo 要设置的风控流水号
     */
    public void setRiskNo(String riskNo){
      this.riskNo = riskNo;
    }

    /**
     * 获取借钱状态：【APPLY:申请/未审核， TRANSFERING:打款中 ，  TRANSFERRED:已经打款/待还款 ，CLOSED:关闭，FINISHED:已结清】
     *
     * @return 借钱状态：【APPLY:申请/未审核， TRANSFERING:打款中 ，  TRANSFERRED:已经打款/待还款 ，CLOSED:关闭，FINISHED:已结清】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置借钱状态：【APPLY:申请/未审核， TRANSFERING:打款中 ，  TRANSFERRED:已经打款/待还款 ，CLOSED:关闭，FINISHED:已结清】
     * 
     * @param status 要设置的借钱状态：【APPLY:申请/未审核， TRANSFERING:打款中 ，  TRANSFERRED:已经打款/待还款 ，CLOSED:关闭，FINISHED:已结清】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取总手续费
     *
     * @return 总手续费
     */
    public BigDecimal getTotalServiceFee(){
      return totalServiceFee;
    }

    /**
     * 设置总手续费
     * 
     * @param totalServiceFee 要设置的总手续费
     */
    public void setTotalServiceFee(BigDecimal totalServiceFee){
      this.totalServiceFee = totalServiceFee;
    }

    /**
     * 获取手续费率
     *
     * @return 手续费率
     */
    public BigDecimal getServiceRate(){
      return serviceRate;
    }

    /**
     * 设置手续费率
     * 
     * @param serviceRate 要设置的手续费率
     */
    public void setServiceRate(BigDecimal serviceRate){
      this.serviceRate = serviceRate;
    }

    /**
     * 获取总利息
     *
     * @return 总利息
     */
    public BigDecimal getTotalInterestFee(){
      return totalInterestFee;
    }

    /**
     * 设置总利息
     * 
     * @param totalInterestFee 要设置的总利息
     */
    public void setTotalInterestFee(BigDecimal totalInterestFee){
      this.totalInterestFee = totalInterestFee;
    }

    /**
     * 获取利率
     *
     * @return 利率
     */
    public BigDecimal getInterestRate(){
      return interestRate;
    }

    /**
     * 设置利率
     * 
     * @param interestRate 要设置的利率
     */
    public void setInterestRate(BigDecimal interestRate){
      this.interestRate = interestRate;
    }

    /**
     * 获取用户分层日利率
     *
     * @return 用户分层日利率
     */
    public BigDecimal getRiskDailyRate(){
      return riskDailyRate;
    }

    /**
     * 设置用户分层日利率
     * 
     * @param riskDailyRate 要设置的用户分层日利率
     */
    public void setRiskDailyRate(BigDecimal riskDailyRate){
      this.riskDailyRate = riskDailyRate;
    }

    /**
     * 获取白领贷额度，用户授信额度
     *
     * @return 白领贷额度，用户授信额度
     */
    public BigDecimal getAuAmount(){
      return auAmount;
    }

    /**
     * 设置白领贷额度，用户授信额度
     * 
     * @param auAmount 要设置的白领贷额度，用户授信额度
     */
    public void setAuAmount(BigDecimal auAmount){
      this.auAmount = auAmount;
    }

    /**
     * 获取App来源: www为返呗,其余的为马甲包
     *
     * @return App来源: www为返呗,其余的为马甲包
     */
    public String getAppName(){
      return appName;
    }

    /**
     * 设置App来源: www为返呗,其余的为马甲包
     * 
     * @param appName 要设置的App来源: www为返呗,其余的为马甲包
     */
    public void setAppName(String appName){
      this.appName = appName;
    }

    /**
     * 获取备注信息
     *
     * @return 备注信息
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置备注信息
     * 
     * @param remark 要设置的备注信息
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

    /**
     * 获取借款用途
     *
     * @return 借款用途
     */
    public String getLoanRemark(){
      return loanRemark;
    }

    /**
     * 设置借款用途
     * 
     * @param loanRemark 要设置的借款用途
     */
    public void setLoanRemark(String loanRemark){
      this.loanRemark = loanRemark;
    }

    /**
     * 获取还款来源
     *
     * @return 还款来源
     */
    public String getRepayRemark(){
      return repayRemark;
    }

    /**
     * 设置还款来源
     * 
     * @param repayRemark 要设置的还款来源
     */
    public void setRepayRemark(String repayRemark){
      this.repayRemark = repayRemark;
    }

    /**
     * 获取打款时间
     *
     * @return 打款时间
     */
    public Date getGmtArrival(){
      return gmtArrival;
    }

    /**
     * 设置打款时间
     * 
     * @param gmtArrival 要设置的打款时间
     */
    public void setGmtArrival(Date gmtArrival){
      this.gmtArrival = gmtArrival;
    }

    /**
     * 获取审核时间
     *
     * @return 审核时间
     */
    public Date getGmtReview(){
      return gmtReview;
    }

    /**
     * 设置审核时间
     * 
     * @param gmtReview 要设置的审核时间
     */
    public void setGmtReview(Date gmtReview){
      this.gmtReview = gmtReview;
    }

    /**
     * 获取借款关闭时间
     *
     * @return 借款关闭时间
     */
    public Date getGmtClose(){
      return gmtClose;
    }

    /**
     * 设置借款关闭时间
     * 
     * @param gmtClose 要设置的借款关闭时间
     */
    public void setGmtClose(Date gmtClose){
      this.gmtClose = gmtClose;
    }

    /**
     * 获取结清时间
     *
     * @return 结清时间
     */
    public Date getGmtFinish(){
      return gmtFinish;
    }

    /**
     * 设置结清时间
     * 
     * @param gmtFinish 要设置的结清时间
     */
    public void setGmtFinish(Date gmtFinish){
      this.gmtFinish = gmtFinish;
    }

    /**
     * 获取借款发生时IP地址
     *
     * @return 借款发生时IP地址
     */
    public String getIp(){
      return ip;
    }

    /**
     * 设置借款发生时IP地址
     * 
     * @param ip 要设置的借款发生时IP地址
     */
    public void setIp(String ip){
      this.ip = ip;
    }

    /**
     * 获取纬度
     *
     * @return 纬度
     */
    public BigDecimal getLatitude(){
      return latitude;
    }

    /**
     * 设置纬度
     * 
     * @param latitude 要设置的纬度
     */
    public void setLatitude(BigDecimal latitude){
      this.latitude = latitude;
    }

    /**
     * 获取经度
     *
     * @return 经度
     */
    public BigDecimal getLongitude(){
      return longitude;
    }

    /**
     * 设置经度
     * 
     * @param longitude 要设置的经度
     */
    public void setLongitude(BigDecimal longitude){
      this.longitude = longitude;
    }

    /**
     * 获取省份
     *
     * @return 省份
     */
    public String getProvince(){
      return province;
    }

    /**
     * 设置省份
     * 
     * @param province 要设置的省份
     */
    public void setProvince(String province){
      this.province = province;
    }

    /**
     * 获取市
     *
     * @return 市
     */
    public String getCity(){
      return city;
    }

    /**
     * 设置市
     * 
     * @param city 要设置的市
     */
    public void setCity(String city){
      this.city = city;
    }

    /**
     * 获取县
     *
     * @return 县
     */
    public String getCounty(){
      return county;
    }

    /**
     * 设置县
     * 
     * @param county 要设置的县
     */
    public void setCounty(String county){
      this.county = county;
    }

    /**
     * 获取详细地址
     *
     * @return 详细地址
     */
    public String getAddress(){
      return address;
    }

    /**
     * 设置详细地址
     * 
     * @param address 要设置的详细地址
     */
    public void setAddress(String address){
      this.address = address;
    }

    /**
     * 获取设备编号
     *
     * @return 设备编号
     */
    public String getDeviceNo(){
      return deviceNo;
    }

    /**
     * 设置设备编号
     * 
     * @param deviceNo 要设置的设备编号
     */
    public void setDeviceNo(String deviceNo){
      this.deviceNo = deviceNo;
    }

}