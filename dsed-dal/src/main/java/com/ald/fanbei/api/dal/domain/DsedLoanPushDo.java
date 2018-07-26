package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市e贷的实时债权推送拓展表实体
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-16 16:54:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedLoanPushDo extends AbstractSerial {

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
     * 都市e贷id
     */
    private Long loanId;

    /**
     * 债权推送的资产方标识
     */
    private String assetSideFlag;

    /**
     * 资金方（钱包）的处理结果【reviewFail：浙商审核失败，payFail：打款失败，paySuccess：打款成功】
     */
    private String status;

    /**
     * 债权推送时的年化利率
     */
    private BigDecimal borrowRate;

    /**
     * 债权推送时的分润利率
     */
    private BigDecimal profitRate;

    /**
     * 到账时间
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
     * 获取都市e贷id
     *
     * @return 都市e贷id
     */
    public Long getLoanId(){
      return loanId;
    }

    /**
     * 设置都市e贷id
     * 
     * @param loanId 要设置的都市e贷id
     */
    public void setLoanId(Long loanId){
      this.loanId = loanId;
    }

    /**
     * 获取债权推送的资产方标识
     *
     * @return 债权推送的资产方标识
     */
    public String getAssetSideFlag(){
      return assetSideFlag;
    }

    /**
     * 设置债权推送的资产方标识
     * 
     * @param assetSideFlag 要设置的债权推送的资产方标识
     */
    public void setAssetSideFlag(String assetSideFlag){
      this.assetSideFlag = assetSideFlag;
    }

    /**
     * 获取资金方（钱包）的处理结果【reviewFail：浙商审核失败，payFail：打款失败，paySuccess：打款成功】
     *
     * @return 资金方（钱包）的处理结果【reviewFail：浙商审核失败，payFail：打款失败，paySuccess：打款成功】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置资金方（钱包）的处理结果【reviewFail：浙商审核失败，payFail：打款失败，paySuccess：打款成功】
     * 
     * @param status 要设置的资金方（钱包）的处理结果【reviewFail：浙商审核失败，payFail：打款失败，paySuccess：打款成功】
     */
    public void setStatus(String status){
      this.status = status;
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
     * 获取到账时间
     *
     * @return 到账时间
     */
    public Date getLoanTime(){
      return loanTime;
    }

    /**
     * 设置到账时间
     * 
     * @param loanTime 要设置的到账时间
     */
    public void setLoanTime(Date loanTime){
      this.loanTime = loanTime;
    }

}