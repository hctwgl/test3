package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 资金方放款与用户借款记录关联表实体
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-10-06 09:54:27
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfFundSideBorrowCashDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 借款id
     */
    private Long borrowCashId;

    /**
     * 借款编号
     */
    private String borrowCashNo;

    /**
     * 预计获利
     */
    private BigDecimal planCollectInterest;

    /**
     * 资金方id
     */
    private Long fundSideInfoId;

    /**
     * 预计回款时间
     */
    private Date fundSidePreBackTime;

    /**
     * 回款金额(包含本金+利息)
     */
    private BigDecimal fundSideBackMoney;

    /**
     * 实际回款时间
     */
    private Date fundSideBackTime;

    /**
     * Y:已回款 N:未回款
     */
    private String status;

    /**
     * 借款时间
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;

    /**
     * 资金方利率
     */
    private BigDecimal fundSideAnnualRate;

    
    public AfFundSideBorrowCashDo() {
		super();
	}

	public AfFundSideBorrowCashDo(Long borrowCashId, String borrowCashNo,
			BigDecimal planCollectInterest, Long fundSideInfoId,
			Date fundSidePreBackTime, BigDecimal fundSideBackMoney,
			Date fundSideBackTime, String status, Date gmtCreate,
			Date gmtModified, BigDecimal fundSideAnnualRate) {
		super();
		this.borrowCashId = borrowCashId;
		this.borrowCashNo = borrowCashNo;
		this.planCollectInterest = planCollectInterest;
		this.fundSideInfoId = fundSideInfoId;
		this.fundSidePreBackTime = fundSidePreBackTime;
		this.fundSideBackMoney = fundSideBackMoney;
		this.fundSideBackTime = fundSideBackTime;
		this.status = status;
		this.gmtCreate = gmtCreate;
		this.gmtModified = gmtModified;
		this.fundSideAnnualRate = fundSideAnnualRate;
	}

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
     * 获取借款id
     *
     * @return 借款id
     */
    public Long getBorrowCashId(){
      return borrowCashId;
    }

    /**
     * 设置借款id
     * 
     * @param borrowCashId 要设置的借款id
     */
    public void setBorrowCashId(Long borrowCashId){
      this.borrowCashId = borrowCashId;
    }

    /**
     * 获取借款编号
     *
     * @return 借款编号
     */
    public String getBorrowCashNo(){
      return borrowCashNo;
    }

    /**
     * 设置借款编号
     * 
     * @param borrowCashNo 要设置的借款编号
     */
    public void setBorrowCashNo(String borrowCashNo){
      this.borrowCashNo = borrowCashNo;
    }

    /**
     * 获取预计获利
     *
     * @return 预计获利
     */
    public BigDecimal getPlanCollectInterest(){
      return planCollectInterest;
    }

    /**
     * 设置预计获利
     * 
     * @param planCollectInterest 要设置的预计获利
     */
    public void setPlanCollectInterest(BigDecimal planCollectInterest){
      this.planCollectInterest = planCollectInterest;
    }

    /**
     * 获取资金方id
     *
     * @return 资金方id
     */
    public Long getFundSideInfoId(){
      return fundSideInfoId;
    }

    /**
     * 设置资金方id
     * 
     * @param fundSideInfoId 要设置的资金方id
     */
    public void setFundSideInfoId(Long fundSideInfoId){
      this.fundSideInfoId = fundSideInfoId;
    }

    /**
     * 获取预计回款时间
     *
     * @return 预计回款时间
     */
    public Date getFundSidePreBackTime(){
      return fundSidePreBackTime;
    }

    /**
     * 设置预计回款时间
     * 
     * @param fundSidePreBackTime 要设置的预计回款时间
     */
    public void setFundSidePreBackTime(Date fundSidePreBackTime){
      this.fundSidePreBackTime = fundSidePreBackTime;
    }

    /**
     * 获取回款金额(包含本金+利息)
     *
     * @return 回款金额(包含本金+利息)
     */
    public BigDecimal getFundSideBackMoney(){
      return fundSideBackMoney;
    }

    /**
     * 设置回款金额(包含本金+利息)
     * 
     * @param fundSideBackMoney 要设置的回款金额(包含本金+利息)
     */
    public void setFundSideBackMoney(BigDecimal fundSideBackMoney){
      this.fundSideBackMoney = fundSideBackMoney;
    }

    /**
     * 获取实际回款时间
     *
     * @return 实际回款时间
     */
    public Date getFundSideBackTime(){
      return fundSideBackTime;
    }

    /**
     * 设置实际回款时间
     * 
     * @param fundSideBackTime 要设置的实际回款时间
     */
    public void setFundSideBackTime(Date fundSideBackTime){
      this.fundSideBackTime = fundSideBackTime;
    }

    /**
     * 获取Y:已回款 N:未回款
     *
     * @return Y:已回款 N:未回款
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置Y:已回款 N:未回款
     * 
     * @param status 要设置的Y:已回款 N:未回款
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取借款时间
     *
     * @return 借款时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置借款时间
     * 
     * @param gmtCreate 要设置的借款时间
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

    /**
     * 获取资金方利率
     *
     * @return 资金方利率
     */
    public BigDecimal getFundSideAnnualRate(){
      return fundSideAnnualRate;
    }

    /**
     * 设置资金方利率
     * 
     * @param fundSideAnnualRate 要设置的资金方利率
     */
    public void setFundSideAnnualRate(BigDecimal fundSideAnnualRate){
      this.fundSideAnnualRate = fundSideAnnualRate;
    }

}