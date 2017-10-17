package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 *  资金方账户信息表实体
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-10-06 10:20:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfFundSideAccountDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 资金方id
     */
    private Long fundSideInfoId;

    /**
     * 总额(包含冻结+可用+待收本金)
     */
    private BigDecimal amount;

    /**
     * 提现冻结
     */
    private BigDecimal frozenAmount;

    /**
     * 可用总额
     */
    private BigDecimal usableAmount;

    /**
     * 历史收益总额
     */
    private BigDecimal incomeAmount;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 待收本金
     */
    private BigDecimal collectCapital;

    /**
     * 待收利息
     */
    private BigDecimal collectInterest;

    /**
     * 放款总额(待回款+已回款)
     */
    private BigDecimal borrowTotalAmount;


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
     * 获取总额(包含冻结+可用+待收本金)
     *
     * @return 总额(包含冻结+可用+待收本金)
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置总额(包含冻结+可用+待收本金)
     * 
     * @param amount 要设置的总额(包含冻结+可用+待收本金)
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取提现冻结
     *
     * @return 提现冻结
     */
    public BigDecimal getFrozenAmount(){
      return frozenAmount;
    }

    /**
     * 设置提现冻结
     * 
     * @param frozenAmount 要设置的提现冻结
     */
    public void setFrozenAmount(BigDecimal frozenAmount){
      this.frozenAmount = frozenAmount;
    }

    /**
     * 获取可用总额
     *
     * @return 可用总额
     */
    public BigDecimal getUsableAmount(){
      return usableAmount;
    }

    /**
     * 设置可用总额
     * 
     * @param usableAmount 要设置的可用总额
     */
    public void setUsableAmount(BigDecimal usableAmount){
      this.usableAmount = usableAmount;
    }

    /**
     * 获取历史收益总额
     *
     * @return 历史收益总额
     */
    public BigDecimal getIncomeAmount(){
      return incomeAmount;
    }

    /**
     * 设置历史收益总额
     * 
     * @param incomeAmount 要设置的历史收益总额
     */
    public void setIncomeAmount(BigDecimal incomeAmount){
      this.incomeAmount = incomeAmount;
    }

    /**
     * 获取修改时间
     *
     * @return 修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置修改时间
     * 
     * @param gmtModified 要设置的修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取待收本金
     *
     * @return 待收本金
     */
    public BigDecimal getCollectCapital(){
      return collectCapital;
    }

    /**
     * 设置待收本金
     * 
     * @param collectCapital 要设置的待收本金
     */
    public void setCollectCapital(BigDecimal collectCapital){
      this.collectCapital = collectCapital;
    }

    /**
     * 获取待收利息
     *
     * @return 待收利息
     */
    public BigDecimal getCollectInterest(){
      return collectInterest;
    }

    /**
     * 设置待收利息
     * 
     * @param collectInterest 要设置的待收利息
     */
    public void setCollectInterest(BigDecimal collectInterest){
      this.collectInterest = collectInterest;
    }

    /**
     * 获取放款总额(待回款+已回款)
     *
     * @return 放款总额(待回款+已回款)
     */
    public BigDecimal getBorrowTotalAmount(){
      return borrowTotalAmount;
    }

    /**
     * 设置放款总额(待回款+已回款)
     * 
     * @param borrowTotalAmount 要设置的放款总额(待回款+已回款)
     */
    public void setBorrowTotalAmount(BigDecimal borrowTotalAmount){
      this.borrowTotalAmount = borrowTotalAmount;
    }

}