package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 资产包信息实体
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAssetPackageDo extends AbstractSerial {

    public Integer getBusiType() {
		return busiType;
	}

	public void setBusiType(Integer busiType) {
		this.busiType = busiType;
	}

	private static final long serialVersionUID = 1L;

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
     * 资产包类型【0：爱上街主动推送，1：合作方主动请求】
     */
    private Integer type;

    /**
     * 状态【togenerate：待匹配 toupload：待上传 tosend：待发送，sended：已发送，canceled：已撤销 returned：已回款】
     */
    private String status;

    /**
     * 资产包名称
     */
    private String assetName;

    /**
     * 资产包编号
     */
    private String assetNo;

    /**
     * 资产方id
     */
    private Long assetSideId;

    /**
     * 资产包有效状态 Y:有效 N:无效
     */
    private String validStatus;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 总金额【单位：元】
     */
    private BigDecimal totalMoney;

    /**
     * 实际总金额【单位：元】
     */
    private BigDecimal realTotalMoney;

    /**
     * 七天的金额
     */
    private BigDecimal minMoney;

    /**
     * 七天的金额对应笔数
     */
    private Integer minNum;

    /**
     * 十四天的金额
     */
    private BigDecimal maxMoney;

    /**
     * 十四天的金额对应笔数
     */
    private Integer maxNum;

    /**
     * 借钱利率%
     */
    private BigDecimal borrowRate;

    /**
     * 年化利率%
     */
    private BigDecimal annualRate;

    /**
     * 还款方式【0：一次性还本付息】
     */
    private Integer repaymentMethod;

    /**
     * 对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     */
    private Integer sendMode;

    /**
     * 资产包下载地址
     */
    private String downloadUrl;

    /**
     * 备注，记录当前发送邮箱地址及资产方诉求等信息
     */
    private String remark;
    
    /**
     * 业务类型 【0：现金贷，1：消费分期】
     */
    private Integer busiType;


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
     * 获取资产包类型【0：爱上街主动推送，1：合作方主动请求】
     *
     * @return 资产包类型【0：爱上街主动推送，1：合作方主动请求】
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置资产包类型【0：爱上街主动推送，1：合作方主动请求】
     * 
     * @param type 要设置的资产包类型【0：爱上街主动推送，1：合作方主动请求】
     */
    public void setType(Integer type){
      this.type = type;
    }

    /**
     * 获取状态【togenerate：待匹配 toupload：待上传 tosend：待发送，sended：已发送，canceled：已撤销 returned：已回款】
     *
     * @return 状态【togenerate：待匹配 toupload：待上传 tosend：待发送，sended：已发送，canceled：已撤销 returned：已回款】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置状态【togenerate：待匹配 toupload：待上传 tosend：待发送，sended：已发送，canceled：已撤销 returned：已回款】
     * 
     * @param status 要设置的状态【togenerate：待匹配 toupload：待上传 tosend：待发送，sended：已发送，canceled：已撤销 returned：已回款】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取资产包名称
     *
     * @return 资产包名称
     */
    public String getAssetName(){
      return assetName;
    }

    /**
     * 设置资产包名称
     * 
     * @param assetName 要设置的资产包名称
     */
    public void setAssetName(String assetName){
      this.assetName = assetName;
    }

    /**
     * 获取资产包编号
     *
     * @return 资产包编号
     */
    public String getAssetNo(){
      return assetNo;
    }

    /**
     * 设置资产包编号
     * 
     * @param assetNo 要设置的资产包编号
     */
    public void setAssetNo(String assetNo){
      this.assetNo = assetNo;
    }

    /**
     * 获取资产方id
     *
     * @return 资产方id
     */
    public Long getAssetSideId(){
      return assetSideId;
    }

    /**
     * 设置资产方id
     * 
     * @param assetSideId 要设置的资产方id
     */
    public void setAssetSideId(Long assetSideId){
      this.assetSideId = assetSideId;
    }

    /**
     * 获取资产包有效状态 Y:有效 N:无效
     *
     * @return 资产包有效状态 Y:有效 N:无效
     */
    public String getValidStatus(){
      return validStatus;
    }

    /**
     * 设置资产包有效状态 Y:有效 N:无效
     * 
     * @param validStatus 要设置的资产包有效状态 Y:有效 N:无效
     */
    public void setValidStatus(String validStatus){
      this.validStatus = validStatus;
    }

    /**
     * 获取开始时间
     *
     * @return 开始时间
     */
    public Date getBeginTime(){
      return beginTime;
    }

    /**
     * 设置开始时间
     * 
     * @param beginTime 要设置的开始时间
     */
    public void setBeginTime(Date beginTime){
      this.beginTime = beginTime;
    }

    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    public Date getEndTime(){
      return endTime;
    }

    /**
     * 设置结束时间
     * 
     * @param endTime 要设置的结束时间
     */
    public void setEndTime(Date endTime){
      this.endTime = endTime;
    }

    /**
     * 获取总金额【单位：元】
     *
     * @return 总金额【单位：元】
     */
    public BigDecimal getTotalMoney(){
      return totalMoney;
    }

    /**
     * 设置总金额【单位：元】
     * 
     * @param totalMoney 要设置的总金额【单位：元】
     */
    public void setTotalMoney(BigDecimal totalMoney){
      this.totalMoney = totalMoney;
    }

    /**
     * 获取实际总金额【单位：元】
     *
     * @return 实际总金额【单位：元】
     */
    public BigDecimal getRealTotalMoney(){
      return realTotalMoney;
    }

    /**
     * 设置实际总金额【单位：元】
     * 
     * @param realTotalMoney 要设置的实际总金额【单位：元】
     */
    public void setRealTotalMoney(BigDecimal realTotalMoney){
      this.realTotalMoney = realTotalMoney;
    }

    /**
     * 获取借钱利率%
     *
     * @return 借钱利率%
     */
    public BigDecimal getBorrowRate(){
      return borrowRate;
    }

    /**
     * 设置借钱利率%
     * 
     * @param borrowRate 要设置的借钱利率%
     */
    public void setBorrowRate(BigDecimal borrowRate){
      this.borrowRate = borrowRate;
    }

    /**
     * 获取年化利率%
     *
     * @return 年化利率%
     */
    public BigDecimal getAnnualRate(){
      return annualRate;
    }

    /**
     * 设置年化利率%
     * 
     * @param annualRate 要设置的年化利率%
     */
    public void setAnnualRate(BigDecimal annualRate){
      this.annualRate = annualRate;
    }

    /**
     * 获取还款方式【0：一次性还本付息】
     *
     * @return 还款方式【0：一次性还本付息】
     */
    public Integer getRepaymentMethod(){
      return repaymentMethod;
    }

    /**
     * 设置还款方式【0：一次性还本付息】
     * 
     * @param repaymentMethod 要设置的还款方式【0：一次性还本付息】
     */
    public void setRepaymentMethod(Integer repaymentMethod){
      this.repaymentMethod = repaymentMethod;
    }

    /**
     * 获取对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     *
     * @return 对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     */
    public Integer getSendMode(){
      return sendMode;
    }

    /**
     * 设置对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     * 
     * @param sendMode 要设置的对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     */
    public void setSendMode(Integer sendMode){
      this.sendMode = sendMode;
    }

    /**
     * 获取资产包下载地址
     *
     * @return 资产包下载地址
     */
    public String getDownloadUrl(){
      return downloadUrl;
    }

    /**
     * 设置资产包下载地址
     * 
     * @param downloadUrl 要设置的资产包下载地址
     */
    public void setDownloadUrl(String downloadUrl){
      this.downloadUrl = downloadUrl;
    }

    /**
     * 获取备注，记录当前发送邮箱地址及资产方诉求等信息
     *
     * @return 备注，记录当前发送邮箱地址及资产方诉求等信息
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置备注，记录当前发送邮箱地址及资产方诉求等信息
     * 
     * @param remark 要设置的备注，记录当前发送邮箱地址及资产方诉求等信息
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

	public BigDecimal getMinMoney() {
		return minMoney;
	}

	public void setMinMoney(BigDecimal minMoney) {
		this.minMoney = minMoney;
	}

	public Integer getMinNum() {
		return minNum;
	}

	public void setMinNum(Integer minNum) {
		this.minNum = minNum;
	}

	public BigDecimal getMaxMoney() {
		return maxMoney;
	}

	public void setMaxMoney(BigDecimal maxMoney) {
		this.maxMoney = maxMoney;
	}

	public Integer getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(Integer maxNum) {
		this.maxNum = maxNum;
	}

}