package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 白领带非实时推送债权视图信息实体
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-25 13:39:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfViewAssetLoanDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，自增长
     */
    private Long loanId;

    /**
     * 主键，自增id
     */
    private Long minLoanPeriodsId;

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 借款流水
     */
    private String loanNo;

    /**
     * 主键，自增id
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 银行预留手机号
     */
    private String mobile;

    /**
     * 银行卡号
     */
    private String bankno;

    /**
     * 借款金额
     */
    private BigDecimal amount;

    /**
     * 借款期数
     */
    private Integer nper;
    
    private BigDecimal interestRate;


    /**
     * 获取主键，自增长
     *
     * @return 主键，自增长
     */
    public Long getLoanId(){
      return loanId;
    }

    /**
     * 设置主键，自增长
     * 
     * @param loanId 要设置的主键，自增长
     */
    public void setLoanId(Long loanId){
      this.loanId = loanId;
    }

    /**
     * 获取主键，自增id
     *
     * @return 主键，自增id
     */
    public Long getMinLoanPeriodsId(){
      return minLoanPeriodsId;
    }

    /**
     * 设置主键，自增id
     * 
     * @param minLoanPeriodsId 要设置的主键，自增id
     */
    public void setMinLoanPeriodsId(Long minLoanPeriodsId){
      this.minLoanPeriodsId = minLoanPeriodsId;
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
     * 获取银行预留手机号
     *
     * @return 银行预留手机号
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置银行预留手机号
     * 
     * @param mobile 要设置的银行预留手机号
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
    }

    /**
     * 获取银行卡号
     *
     * @return 银行卡号
     */
    public String getBankno(){
      return bankno;
    }

    /**
     * 设置银行卡号
     * 
     * @param bankno 要设置的银行卡号
     */
    public void setBankno(String bankno){
      this.bankno = bankno;
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
     * 获取借款期数
     *
     * @return 借款期数
     */
    public Integer getNper(){
      return nper;
    }

    /**
     * 设置借款期数
     * 
     * @param nper 要设置的借款期数
     */
    public void setNper(Integer nper){
      this.nper = nper;
    }

	/**
	 * @return the interestRate
	 */
	public BigDecimal getInterestRate() {
		return interestRate;
	}

	/**
	 * @param interestRate the interestRate to set
	 */
	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}
    
}