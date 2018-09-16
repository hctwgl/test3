package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdUserAuthDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 
     */
    private Long userId;

    /**
     * 认证流水号
     */
    private String riskNo;

    /**
     * 认证状态，直接存入西关递交的值
     */
    private String riskStatus;

    /**
     * 授信额度
     */
    private BigDecimal riskAmount;

    /**
     * 认证通过后的分层利率
     */
    private BigDecimal riskRate;

    /**
     * 
     */
    private Date gmtRisk;

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
     * 获取
     *
     * @return 
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置
     * 
     * @param userId 要设置的
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取认证流水号
     *
     * @return 认证流水号
     */
    public String getRiskNo(){
      return riskNo;
    }

    /**
     * 设置认证流水号
     * 
     * @param riskNo 要设置的认证流水号
     */
    public void setRiskNo(String riskNo){
      this.riskNo = riskNo;
    }

    /**
     * 获取认证状态，直接存入西关递交的值
     *
     * @return 认证状态，直接存入西关递交的值
     */
    public String getRiskStatus(){
      return riskStatus;
    }

    /**
     * 设置认证状态，直接存入西关递交的值
     * 
     * @param riskStatus 要设置的认证状态，直接存入西关递交的值
     */
    public void setRiskStatus(String riskStatus){
      this.riskStatus = riskStatus;
    }

    /**
     * 获取授信额度
     *
     * @return 授信额度
     */
    public BigDecimal getRiskAmount(){
      return riskAmount;
    }

    /**
     * 设置授信额度
     * 
     * @param riskAmount 要设置的授信额度
     */
    public void setRiskAmount(BigDecimal riskAmount){
      this.riskAmount = riskAmount;
    }

    /**
     * 获取认证通过后的分层利率
     *
     * @return 认证通过后的分层利率
     */
    public BigDecimal getRiskRate(){
      return riskRate;
    }

    /**
     * 设置认证通过后的分层利率
     * 
     * @param riskRate 要设置的认证通过后的分层利率
     */
    public void setRiskRate(BigDecimal riskRate){
      this.riskRate = riskRate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getGmtRisk(){
      return gmtRisk;
    }

    /**
     * 设置
     * 
     * @param gmtRisk 要设置的
     */
    public void setGmtRisk(Date gmtRisk){
      this.gmtRisk = gmtRisk;
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