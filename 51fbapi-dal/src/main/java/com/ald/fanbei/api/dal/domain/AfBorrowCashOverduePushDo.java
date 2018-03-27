package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 逾期债权推送的现金贷拓展表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-03-27 16:33:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBorrowCashOverduePushDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 逾期借款编号
     */
    private String borrowNo;

    /**
     * 原借款id
     */
    private Long borrowId;

    /**
     * 虚拟借钱年化利率
     */
    private BigDecimal borrowRate;

    /**
     * 虚拟借款时间
     */
    private Date gmtArrival;

    /**
     * 虚拟预计还款时间
     */
    private Date gmtPlanRepayment;

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 
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
     * 获取逾期借款编号
     *
     * @return 逾期借款编号
     */
    public String getBorrowNo(){
      return borrowNo;
    }

    /**
     * 设置逾期借款编号
     * 
     * @param borrowNo 要设置的逾期借款编号
     */
    public void setBorrowNo(String borrowNo){
      this.borrowNo = borrowNo;
    }

    /**
     * 获取原借款id
     *
     * @return 原借款id
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置原借款id
     * 
     * @param borrowId 要设置的原借款id
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取虚拟借钱年化利率
     *
     * @return 虚拟借钱年化利率
     */
    public BigDecimal getBorrowRate(){
      return borrowRate;
    }

    /**
     * 设置虚拟借钱年化利率
     * 
     * @param borrowRate 要设置的虚拟借钱年化利率
     */
    public void setBorrowRate(BigDecimal borrowRate){
      this.borrowRate = borrowRate;
    }

    /**
     * 获取虚拟借款时间
     *
     * @return 虚拟借款时间
     */
    public Date getGmtArrival(){
      return gmtArrival;
    }

    /**
     * 设置虚拟借款时间
     * 
     * @param gmtArrival 要设置的虚拟借款时间
     */
    public void setGmtArrival(Date gmtArrival){
      this.gmtArrival = gmtArrival;
    }

    /**
     * 获取虚拟预计还款时间
     *
     * @return 虚拟预计还款时间
     */
    public Date getGmtPlanRepayment(){
      return gmtPlanRepayment;
    }

    /**
     * 设置虚拟预计还款时间
     * 
     * @param gmtPlanRepayment 要设置的虚拟预计还款时间
     */
    public void setGmtPlanRepayment(Date gmtPlanRepayment){
      this.gmtPlanRepayment = gmtPlanRepayment;
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


}