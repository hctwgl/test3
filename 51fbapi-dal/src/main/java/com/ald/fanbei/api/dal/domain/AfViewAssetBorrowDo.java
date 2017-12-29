package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 资产方消费分期债权视图实体
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017-12-14 16:59:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfViewAssetBorrowDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 消费分期债权id
     */
    private Long borrowId;

    /**
     * 消费分期债权第一期账单id
     */
    private Long minBorrowBillId;

    /**
     * 消费分期债权第一期账单创建时间
     */
    private Date gmtCreate;

    /**
     * 消费分期债权编号
     */
    private String borrowNo;

    /**
     * 用户id
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
     * 借款金额
     */
    private BigDecimal amount;

    /**
     * 分期金额， 每期应还金额
     */
    private BigDecimal nperAmount;

    /**
     * 分期数
     */
    private Integer nper;


    /**
     * 获取主键，自增id
     *
     * @return 主键，自增id
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置主键，自增id
     * 
     * @param borrowId 要设置的主键，自增id
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取主键，自增id
     *
     * @return 主键，自增id
     */
    public Long getMinBorrowBillId(){
      return minBorrowBillId;
    }

    /**
     * 设置主键，自增id
     * 
     * @param minBorrowBillId 要设置的主键，自增id
     */
    public void setMinBorrowBillId(Long minBorrowBillId){
      this.minBorrowBillId = minBorrowBillId;
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
     * 获取借款编号 jkYYYYMMDDHHMMSSXXXX
     *
     * @return 借款编号 jkYYYYMMDDHHMMSSXXXX
     */
    public String getBorrowNo(){
      return borrowNo;
    }

    /**
     * 设置借款编号 jkYYYYMMDDHHMMSSXXXX
     * 
     * @param borrowNo 要设置的借款编号 jkYYYYMMDDHHMMSSXXXX
     */
    public void setBorrowNo(String borrowNo){
      this.borrowNo = borrowNo;
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
     * 获取分期金额， 每期应还金额
     *
     * @return 分期金额， 每期应还金额
     */
    public BigDecimal getNperAmount(){
      return nperAmount;
    }

    /**
     * 设置分期金额， 每期应还金额
     * 
     * @param nperAmount 要设置的分期金额， 每期应还金额
     */
    public void setNperAmount(BigDecimal nperAmount){
      this.nperAmount = nperAmount;
    }

    /**
     * 获取分期数
     *
     * @return 分期数
     */
    public Integer getNper(){
      return nper;
    }

    /**
     * 设置分期数
     * 
     * @param nper 要设置的分期数
     */
    public void setNper(Integer nper){
      this.nper = nper;
    }

}