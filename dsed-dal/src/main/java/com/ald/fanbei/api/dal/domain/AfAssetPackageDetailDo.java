package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 资产包与债权记录关系实体
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAssetPackageDetailDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 借款id
     */
    private Long borrowCashId;

    /**
     * 借款编号
     */
    private String borrowNo;

    /**
     * 资产包id
     */
    private Long assetPackageId;

    /**
     * 匹配记录状态 Y:初始有效 N:标记无效 R:已重新分配
     */
    private String status;

    /**
     * 备注，当资产方撤回时等情况下备注说明
     */
    private String remark;
    
    private BigDecimal borrowRate;
    
    private BigDecimal profitRate;

    private Date loanTime;

    public Date getLoanTime() {
        return loanTime;
    }

    public void setLoanTime(Date loanTime) {
        this.loanTime = loanTime;
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
    public String getBorrowNo(){
      return borrowNo;
    }

    /**
     * 设置借款编号
     * 
     * @param borrowNo 要设置的借款编号
     */
    public void setBorrowNo(String borrowNo){
      this.borrowNo = borrowNo;
    }

    /**
     * 获取资产包id
     *
     * @return 资产包id
     */
    public Long getAssetPackageId(){
      return assetPackageId;
    }

    /**
     * 设置资产包id
     * 
     * @param assetPackageId 要设置的资产包id
     */
    public void setAssetPackageId(Long assetPackageId){
      this.assetPackageId = assetPackageId;
    }

    /**
     * 获取匹配记录状态 Y:初始有效 N:标记无效 R:已重新分配
     *
     * @return 匹配记录状态 Y:初始有效 N:标记无效 R:已重新分配
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置匹配记录状态 Y:初始有效 N:标记无效 R:已重新分配
     * 
     * @param status 要设置的匹配记录状态 Y:初始有效 N:标记无效 R:已重新分配
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取备注，当资产方撤回时等情况下备注说明
     *
     * @return 备注，当资产方撤回时等情况下备注说明
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置备注，当资产方撤回时等情况下备注说明
     * 
     * @param remark 要设置的备注，当资产方撤回时等情况下备注说明
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * @return the borrowRate
	 */
	public BigDecimal getBorrowRate() {
		return borrowRate;
	}

	/**
	 * @param borrowRate the borrowRate to set
	 */
	public void setBorrowRate(BigDecimal borrowRate) {
		this.borrowRate = borrowRate;
	}

	/**
	 * @return the profitRate
	 */
	public BigDecimal getProfitRate() {
		return profitRate;
	}

	/**
	 * @param profitRate the profitRate to set
	 */
	public void setProfitRate(BigDecimal profitRate) {
		this.profitRate = profitRate;
	}
	
}