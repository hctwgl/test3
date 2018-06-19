package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市e贷借款逾期记录表实体
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:42:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedLoanOverdueLogDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 现金贷id
     */
    private Long periodsId;

    /**
     * 利息
     */
    private BigDecimal interest;

    /**
     * 当时应还款金额
     */
    private BigDecimal currentAmount;


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
     * 获取现金贷id
     *
     * @return 现金贷id
     */
    public Long getPeriodsId(){
      return periodsId;
    }

    /**
     * 设置现金贷id
     * 
     * @param periodsId 要设置的现金贷id
     */
    public void setPeriodsId(Long periodsId){
      this.periodsId = periodsId;
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

}