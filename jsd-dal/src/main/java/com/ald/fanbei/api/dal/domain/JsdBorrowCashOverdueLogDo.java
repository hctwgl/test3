package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-10 13:22:35
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdBorrowCashOverdueLogDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * CASH-现金贷借款，ORDER_CASH-搭售的商品借款
     */
    private String type;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 极速id
     */
    private Long borrowId;

    /**
     * 利息
     */
    private BigDecimal interest;

    /**
     * 当时应还款金额
     */
    private BigDecimal currentAmount;

    /**
     * 
     */
    private Date gmtCreate;


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
     * 获取CASH-现金贷借款，ORDER_CASH-搭售的商品借款
     *
     * @return CASH-现金贷借款，ORDER_CASH-搭售的商品借款
     */
    public String getType(){
      return type;
    }

    /**
     * 设置CASH-现金贷借款，ORDER_CASH-搭售的商品借款
     * 
     * @param type 要设置的CASH-现金贷借款，ORDER_CASH-搭售的商品借款
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户id
     * 
     * @param userId 要设置的用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取极速id
     *
     * @return 极速id
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置极速id
     * 
     * @param borrowId 要设置的极速id
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取利息
     *
     * @return 利息
     */
    public BigDecimal getInterest(){
      return interest;
    }

    /**
     * 设置利息
     * 
     * @param interest 要设置的利息
     */
    public void setInterest(BigDecimal interest){
      this.interest = interest;
    }

    /**
     * 获取当时应还款金额
     *
     * @return 当时应还款金额
     */
    public BigDecimal getCurrentAmount(){
      return currentAmount;
    }

    /**
     * 设置当时应还款金额
     * 
     * @param currentAmount 要设置的当时应还款金额
     */
    public void setCurrentAmount(BigDecimal currentAmount){
      this.currentAmount = currentAmount;
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

}