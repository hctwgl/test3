package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 11:21:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdAssetPackageDetailDo extends AbstractSerial {

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

    /**
     * 债权推送时的年化利率
     */
    private BigDecimal borrowRate;

    /**
     * 债权推送时的分润利率
     */
    private BigDecimal profitRate;

    /**
     * 
     */
    private Date loanTime;


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

    /**
     * 获取债权推送时的年化利率
     *
     * @return 债权推送时的年化利率
     */
    public BigDecimal getBorrowRate(){
      return borrowRate;
    }

    /**
     * 设置债权推送时的年化利率
     * 
     * @param borrowRate 要设置的债权推送时的年化利率
     */
    public void setBorrowRate(BigDecimal borrowRate){
      this.borrowRate = borrowRate;
    }

    /**
     * 获取债权推送时的分润利率
     *
     * @return 债权推送时的分润利率
     */
    public BigDecimal getProfitRate(){
      return profitRate;
    }

    /**
     * 设置债权推送时的分润利率
     * 
     * @param profitRate 要设置的债权推送时的分润利率
     */
    public void setProfitRate(BigDecimal profitRate){
      this.profitRate = profitRate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getLoanTime(){
      return loanTime;
    }

    /**
     * 设置
     * 
     * @param loanTime 要设置的
     */
    public void setLoanTime(Date loanTime){
      this.loanTime = loanTime;
    }

}