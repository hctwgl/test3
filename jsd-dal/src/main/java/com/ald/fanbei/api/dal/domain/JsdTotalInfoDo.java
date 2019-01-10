package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.dto.LoanDto;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2019-01-03 13:49:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdTotalInfoDo extends Page<LoanDto>{

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    
    
    //  期限 
 private String nper;
   
   //开始日期
   private Date startDate;
   
   //结束日期
   private Date endDate;
   
   

   //查询日期
   private Date queryDate;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 数据日期
     */
    private Date countDate;

    /**
     * 放款笔数
     */
    private Long loanNum;

    /**
     * 申请金额
     */
    private BigDecimal applyAmount;

    /**
     * 实际放款金额
     */
    private BigDecimal loanAmount;

    /**
     * 搭售金额
     */
    private BigDecimal tyingAmount;

    /**
     * 应还款金额
     */
    private BigDecimal repaymentAmount;

    /**
     * 正常还款金额
     */
    private BigDecimal normalAmount;

    /**
     * 总还款额
     */
    private BigDecimal countRepaymentAmount;

    /**
     * 应还款笔数
     */
    private Long repaymentNum;

    /**
     * 正常还款笔数
     */
    private Long normalNum;

    /**
     * 总还款笔数
     */
    private Long countRepaymentNum;

    /**
     * 展期笔数
     */
    private Long extensionNum;

    /**
     * 展期还本
     */
    private BigDecimal extensionReturnPrincipal;

    /**
     * 展期费用
     */
    private BigDecimal extensionCost;

    /**
     * 在展本金
     */
    private BigDecimal inExhibitionCapital;

    /**
     * 首逾率
     */
    private BigDecimal rirstRate;

    /**
     * 逾期率
     */
    private BigDecimal overdueRate;

    /**
     * 未回收率
     */
    private BigDecimal unrecoveredRate;

    /**
     * 坏账金额
     */
    private BigDecimal badDebtAmount;

    /**
     * 盈利率
     */
    private BigDecimal profitabilityRate;


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
     * 获取数据日期
     *
     * @return 数据日期
     */
    public Date getCountDate(){
      return countDate;
    }

    /**
     * 设置数据日期
     * 
     * @param countDate 要设置的数据日期
     */
    public void setCountDate(Date countDate){
      this.countDate = countDate;
    }

    /**
     * 获取放款笔数
     *
     * @return 放款笔数
     */
    public Long getLoanNum(){
      return loanNum;
    }

    /**
     * 设置放款笔数
     *
     * @param loanNum 要设置的放款笔数
     */
    public void setLoanNum(Long loanNum){
      this.loanNum = loanNum;
    }

    /**
     * 获取申请金额
     *
     * @return 申请金额
     */
    public BigDecimal getApplyAmount(){
      return applyAmount;
    }

    /**
     * 设置申请金额
     * 
     * @param applyAmount 要设置的申请金额
     */
    public void setApplyAmount(BigDecimal applyAmount){
      this.applyAmount = applyAmount;
    }

    /**
     * 获取实际放款金额
     *
     * @return 实际放款金额
     */
    public BigDecimal getLoanAmount(){
      return loanAmount;
    }

    /**
     * 设置实际放款金额
     * 
     * @param loanAmount 要设置的实际放款金额
     */
    public void setLoanAmount(BigDecimal loanAmount){
      this.loanAmount = loanAmount;
    }

    /**
     * 获取搭售金额
     *
     * @return 搭售金额
     */
    public BigDecimal getTyingAmount(){
      return tyingAmount;
    }

    /**
     * 设置搭售金额
     * 
     * @param tyingAmount 要设置的搭售金额
     */
    public void setTyingAmount(BigDecimal tyingAmount){
      this.tyingAmount = tyingAmount;
    }

    /**
     * 获取应还款金额
     *
     * @return 应还款金额
     */
    public BigDecimal getRepaymentAmount(){
      return repaymentAmount;
    }

    /**
     * 设置应还款金额
     * 
     * @param repaymentAmount 要设置的应还款金额
     */
    public void setRepaymentAmount(BigDecimal repaymentAmount){
      this.repaymentAmount = repaymentAmount;
    }

    /**
     * 获取正常还款金额
     *
     * @return 正常还款金额
     */
    public BigDecimal getNormalAmount(){
      return normalAmount;
    }

    /**
     * 设置正常还款金额
     * 
     * @param normalAmount 要设置的正常还款金额
     */
    public void setNormalAmount(BigDecimal normalAmount){
      this.normalAmount = normalAmount;
    }

    /**
     * 获取总还款额
     *
     * @return 总还款额
     */
    public BigDecimal getCountRepaymentAmount(){
      return countRepaymentAmount;
    }

    /**
     * 设置总还款额
     * 
     * @param countRepaymentAmount 要设置的总还款额
     */
    public void setCountRepaymentAmount(BigDecimal countRepaymentAmount){
      this.countRepaymentAmount = countRepaymentAmount;
    }

    /**
     * 获取应还款笔数
     *
     * @return 应还款笔数
     */
    public Long getRepaymentNum(){
      return repaymentNum;
    }

    /**
     * 设置应还款笔数
     * 
     * @param repaymentNum 要设置的应还款笔数
     */
    public void setRepaymentNum(Long repaymentNum){
      this.repaymentNum = repaymentNum;
    }

    /**
     * 获取正常还款笔数
     *
     * @return 正常还款笔数
     */
    public Long getNormalNum(){
      return normalNum;
    }

    /**
     * 设置正常还款笔数
     * 
     * @param normalNum 要设置的正常还款笔数
     */
    public void setNormalNum(Long normalNum){
      this.normalNum = normalNum;
    }

    /**
     * 获取总还款笔数
     *
     * @return 总还款笔数
     */
    public Long getCountRepaymentNum(){
      return countRepaymentNum;
    }

    /**
     * 设置总还款笔数
     * 
     * @param countRepaymentNum 要设置的总还款笔数
     */
    public void setCountRepaymentNum(Long countRepaymentNum){
      this.countRepaymentNum = countRepaymentNum;
    }

    /**
     * 获取展期笔数
     *
     * @return 展期笔数
     */
    public Long getExtensionNum(){
      return extensionNum;
    }

    /**
     * 设置展期笔数
     * 
     * @param extensionNum 要设置的展期笔数
     */
    public void setExtensionNum(Long extensionNum){
      this.extensionNum = extensionNum;
    }

    /**
     * 获取展期还本
     *
     * @return 展期还本
     */
    public BigDecimal getExtensionReturnPrincipal(){
      return extensionReturnPrincipal;
    }

    /**
     * 设置展期还本
     * 
     * @param extensionReturnPrincipal 要设置的展期还本
     */
    public void setExtensionReturnPrincipal(BigDecimal extensionReturnPrincipal){
      this.extensionReturnPrincipal = extensionReturnPrincipal;
    }

    /**
     * 获取展期费用
     *
     * @return 展期费用
     */
    public BigDecimal getExtensionCost(){
      return extensionCost;
    }

    /**
     * 设置展期费用
     * 
     * @param extensionCost 要设置的展期费用
     */
    public void setExtensionCost(BigDecimal extensionCost){
      this.extensionCost = extensionCost;
    }

    /**
     * 获取在展本金
     *
     * @return 在展本金
     */
    public BigDecimal getInExhibitionCapital(){
      return inExhibitionCapital;
    }

    /**
     * 设置在展本金
     * 
     * @param inExhibitionCapital 要设置的在展本金
     */
    public void setInExhibitionCapital(BigDecimal inExhibitionCapital){
      this.inExhibitionCapital = inExhibitionCapital;
    }

    /**
     * 获取首逾率
     *
     * @return 首逾率
     */
    public BigDecimal getRirstRate(){
      return rirstRate;
    }

    /**
     * 设置首逾率
     * 
     * @param rirstRate 要设置的首逾率
     */
    public void setRirstRate(BigDecimal rirstRate){
      this.rirstRate = rirstRate;
    }

    /**
     * 获取逾期率
     *
     * @return 逾期率
     */
    public BigDecimal getOverdueRate(){
      return overdueRate;
    }

    /**
     * 设置逾期率
     * 
     * @param overdueRate 要设置的逾期率
     */
    public void setOverdueRate(BigDecimal overdueRate){
      this.overdueRate = overdueRate;
    }

    /**
     * 获取未回收率
     *
     * @return 未回收率
     */
    public BigDecimal getUnrecoveredRate(){
      return unrecoveredRate;
    }

    /**
     * 设置未回收率
     * 
     * @param unrecoveredRate 要设置的未回收率
     */
    public void setUnrecoveredRate(BigDecimal unrecoveredRate){
      this.unrecoveredRate = unrecoveredRate;
    }

    /**
     * 获取坏账金额
     *
     * @return 坏账金额
     */
    public BigDecimal getBadDebtAmount(){
      return badDebtAmount;
    }

    /**
     * 设置坏账金额
     * 
     * @param badDebtAmount 要设置的坏账金额
     */
    public void setBadDebtAmount(BigDecimal badDebtAmount){
      this.badDebtAmount = badDebtAmount;
    }

    /**
     * 获取盈利率
     *
     * @return 盈利率
     */
    public BigDecimal getProfitabilityRate(){
      return profitabilityRate;
    }

    /**
     * 设置盈利率
     * 
     * @param profitabilityRate 要设置的盈利率
     */
    public void setProfitabilityRate(BigDecimal profitabilityRate){
      this.profitabilityRate = profitabilityRate;
    }

	public String getNper() {
		return nper;
	}

	public void setNper(String nper) {
		this.nper = nper;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getQueryDate() {
		return queryDate;
	}

	public void setQueryDate(Date queryDate) {
		this.queryDate = queryDate;
	}
    
}