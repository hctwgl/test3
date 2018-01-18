package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 资产可用债权信息实体
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:50:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfViewAssetBorrowCashDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    private Long borrowCashId;

    /**
     * 主键，自增id
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 借款编号
     */
    private String borrowNo;

    /**
     * 申请金额
     */
    private BigDecimal amount;

    /**
     * 到账金额
     */
    private BigDecimal arrivalAmount;

    /**
     * 借钱手续费率（日）
     */
    private BigDecimal poundageRate;

    /**
     * 央行基准利率
     */
    private BigDecimal baseBankRate;

    /**
     * 借款类型【SEVEN:7天,FOURTEEN】
     */
    private String type;

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 打款时间
     */
    private Date gmtArrival;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 绑定手机号
     */
    private String mobile;

    /**
     * 地址
     */
    private String address;

    /**
     * 省
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
     * 卡名称,或者支付宝
     */
    private String cardName;

    /**
     * 卡号或者支付宝账号
     */
    private String cardNumber;

    /**
     * 紧急联系人关系
     */
    private String contactorType;

    /**
     * 紧急联系人名字
     */
    private String contactorName;

    /**
     * 身份证正面照片
     */
    private String idFrontUrl;

    /**
     * 身份证反面照片
     */
    private String idBehindUrl;

    /**
     * 阿里云pdf存储路径
     */
    private String contractPdfUrl;
    
    /**
     * 借款用途
     */
    private String borrowRemark;
    
    /**
     * 还款来源
     */
    private String refundRemark;


	public String getBorrowRemark() {
		return borrowRemark;
	}

	public void setBorrowRemark(String borrowRemark) {
		this.borrowRemark = borrowRemark;
	}

	public String getRefundRemark() {
		return refundRemark;
	}

	public void setRefundRemark(String refundRemark) {
		this.refundRemark = refundRemark;
	}
	/**
     * 获取
     *
     * @return 
     */
    public Long getBorrowCashId(){
      return borrowCashId;
    }

    /**
     * 设置
     * 
     * @param borrowCashId 要设置的
     */
    public void setBorrowCashId(Long borrowCashId){
      this.borrowCashId = borrowCashId;
    }

    /**
     * 获取主键，自增id
     *
     * @return 主键，自增id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置主键，自增id
     * 
     * @param userId 要设置的主键，自增id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取真实姓名
     *
     * @return 真实姓名
     */
    public String getRealName(){
      return realName;
    }

    /**
     * 设置真实姓名
     * 
     * @param realName 要设置的真实姓名
     */
    public void setRealName(String realName){
      this.realName = realName;
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
     * 获取申请金额
     *
     * @return 申请金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置申请金额
     * 
     * @param amount 要设置的申请金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取到账金额
     *
     * @return 到账金额
     */
    public BigDecimal getArrivalAmount(){
      return arrivalAmount;
    }

    /**
     * 设置到账金额
     * 
     * @param arrivalAmount 要设置的到账金额
     */
    public void setArrivalAmount(BigDecimal arrivalAmount){
      this.arrivalAmount = arrivalAmount;
    }

    /**
     * 获取借钱手续费率（日）
     *
     * @return 借钱手续费率（日）
     */
    public BigDecimal getPoundageRate(){
      return poundageRate;
    }

    /**
     * 设置借钱手续费率（日）
     * 
     * @param poundageRate 要设置的借钱手续费率（日）
     */
    public void setPoundageRate(BigDecimal poundageRate){
      this.poundageRate = poundageRate;
    }

    /**
     * 获取央行基准利率
     *
     * @return 央行基准利率
     */
    public BigDecimal getBaseBankRate(){
      return baseBankRate;
    }

    /**
     * 设置央行基准利率
     * 
     * @param baseBankRate 要设置的央行基准利率
     */
    public void setBaseBankRate(BigDecimal baseBankRate){
      this.baseBankRate = baseBankRate;
    }

    /**
     * 获取借款类型【SEVEN:7天,FOURTEEN】
     *
     * @return 借款类型【SEVEN:7天,FOURTEEN】
     */
    public String getType(){
      return type;
    }

    /**
     * 设置借款类型【SEVEN:7天,FOURTEEN】
     * 
     * @param type 要设置的借款类型【SEVEN:7天,FOURTEEN】
     */
    public void setType(String type){
      this.type = type;
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
     * 获取身份证号
     *
     * @return 身份证号
     */
    public String getIdNumber(){
      return idNumber;
    }

    /**
     * 设置身份证号
     * 
     * @param idNumber 要设置的身份证号
     */
    public void setIdNumber(String idNumber){
      this.idNumber = idNumber;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUserName(){
      return userName;
    }

    /**
     * 设置用户名
     * 
     * @param userName 要设置的用户名
     */
    public void setUserName(String userName){
      this.userName = userName;
    }

    /**
     * 获取绑定手机号
     *
     * @return 绑定手机号
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置绑定手机号
     * 
     * @param mobile 要设置的绑定手机号
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
    }

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getAddress(){
      return address;
    }

    /**
     * 设置地址
     * 
     * @param address 要设置的地址
     */
    public void setAddress(String address){
      this.address = address;
    }

    /**
     * 获取省
     *
     * @return 省
     */
    public String getProvince(){
      return province;
    }

    /**
     * 设置省
     * 
     * @param province 要设置的省
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
     * 获取卡名称,或者支付宝
     *
     * @return 卡名称,或者支付宝
     */
    public String getCardName(){
      return cardName;
    }

    /**
     * 设置卡名称,或者支付宝
     * 
     * @param cardName 要设置的卡名称,或者支付宝
     */
    public void setCardName(String cardName){
      this.cardName = cardName;
    }

    /**
     * 获取卡号或者支付宝账号
     *
     * @return 卡号或者支付宝账号
     */
    public String getCardNumber(){
      return cardNumber;
    }

    /**
     * 设置卡号或者支付宝账号
     * 
     * @param cardNumber 要设置的卡号或者支付宝账号
     */
    public void setCardNumber(String cardNumber){
      this.cardNumber = cardNumber;
    }

    /**
     * 获取紧急联系人关系
     *
     * @return 紧急联系人关系
     */
    public String getContactorType(){
      return contactorType;
    }

    /**
     * 设置紧急联系人关系
     * 
     * @param contactorType 要设置的紧急联系人关系
     */
    public void setContactorType(String contactorType){
      this.contactorType = contactorType;
    }

    /**
     * 获取紧急联系人名字
     *
     * @return 紧急联系人名字
     */
    public String getContactorName(){
      return contactorName;
    }

    /**
     * 设置紧急联系人名字
     * 
     * @param contactorName 要设置的紧急联系人名字
     */
    public void setContactorName(String contactorName){
      this.contactorName = contactorName;
    }

    /**
     * 获取身份证正面照片
     *
     * @return 身份证正面照片
     */
    public String getIdFrontUrl(){
      return idFrontUrl;
    }

    /**
     * 设置身份证正面照片
     * 
     * @param idFrontUrl 要设置的身份证正面照片
     */
    public void setIdFrontUrl(String idFrontUrl){
      this.idFrontUrl = idFrontUrl;
    }

    /**
     * 获取身份证反面照片
     *
     * @return 身份证反面照片
     */
    public String getIdBehindUrl(){
      return idBehindUrl;
    }

    /**
     * 设置身份证反面照片
     * 
     * @param idBehindUrl 要设置的身份证反面照片
     */
    public void setIdBehindUrl(String idBehindUrl){
      this.idBehindUrl = idBehindUrl;
    }

    /**
     * 获取阿里云pdf存储路径
     *
     * @return 阿里云pdf存储路径
     */
    public String getContractPdfUrl(){
      return contractPdfUrl;
    }

    /**
     * 设置阿里云pdf存储路径
     * 
     * @param contractPdfUrl 要设置的阿里云pdf存储路径
     */
    public void setContractPdfUrl(String contractPdfUrl){
      this.contractPdfUrl = contractPdfUrl;
    }

}