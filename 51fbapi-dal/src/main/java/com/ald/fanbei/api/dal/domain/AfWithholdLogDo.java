package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 银行卡代扣日志实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-11-06 19:18:17
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfWithholdLogDo extends AbstractSerial {

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
     * 用户id
     */
    private Long userId;

    /**
     * 银行卡
     */
    private String cardNumber;

    /**
     * 还款金额
     */
    private BigDecimal amount;

    /**
     * 账单类型：1 借款 2 分期
     */
    private Integer borrowType;

    /**
     * 是否成功：1成功，0失败
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 借款账单id
     */
    private Long borrowcashId;

    /**
     * 还款id（借款或分期）
     */
    private Long refId;

    /**
     * 分期账单id
     */
    private String borrowbillId;

    /**
     * 银行卡id
     */
    private Long cardId;

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
     * 获取银行卡
     *
     * @return 银行卡
     */
    public String getCardNumber(){
      return cardNumber;
    }

    /**
     * 设置银行卡
     * 
     * @param cardNumber 要设置的银行卡
     */
    public void setCardNumber(String cardNumber){
      this.cardNumber = cardNumber;
    }

    /**
     * 获取还款金额
     *
     * @return 还款金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置还款金额
     * 
     * @param amount 要设置的还款金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取账单类型：1 借款 2 分期
     *
     * @return 账单类型：1 借款 2 分期
     */
    public Integer getBorrowType(){
      return borrowType;
    }

    /**
     * 设置账单类型：1 借款 2 分期
     * 
     * @param borrowType 要设置的账单类型：1 借款 2 分期
     */
    public void setBorrowType(Integer borrowType){
      this.borrowType = borrowType;
    }

    /**
     * 获取是否成功：1成功，0失败
     *
     * @return 是否成功：1成功，0失败
     */
    public Integer getStatus(){
      return status;
    }

    /**
     * 设置是否成功：1成功，0失败
     * 
     * @param status 要设置的是否成功：1成功，0失败
     */
    public void setStatus(Integer status){
      this.status = status;
    }

    /**
     * 获取备注
     *
     * @return 备注
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置备注
     * 
     * @param remark 要设置的备注
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

    /**
     * 获取借款账单id
     *
     * @return 借款账单id
     */
    public Long getBorrowcashId(){
      return borrowcashId;
    }

    /**
     * 设置借款账单id
     * 
     * @param borrowcashId 要设置的借款账单id
     */
    public void setBorrowcashId(Long borrowcashId){
      this.borrowcashId = borrowcashId;
    }

    /**
     * 获取还款id（借款或分期）
     *
     * @return 还款id（借款或分期）
     */
    public Long getRefId(){
      return refId;
    }

    /**
     * 设置还款id（借款或分期）
     * 
     * @param refId 要设置的还款id（借款或分期）
     */
    public void setRefId(Long refId){
      this.refId = refId;
    }

    /**
     * 获取分期账单id
     *
     * @return 分期账单id
     */
    public String getBorrowbillId(){
      return borrowbillId;
    }

    /**
     * 设置分期账单id
     * 
     * @param borrowbillId 要设置的分期账单id
     */
    public void setBorrowbillId(String borrowbillId){
      this.borrowbillId = borrowbillId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}