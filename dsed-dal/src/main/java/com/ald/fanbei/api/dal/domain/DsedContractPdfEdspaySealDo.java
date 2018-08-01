package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 * e都市钱包用户出借信息与协议关联表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-01-22 17:34:26
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedContractPdfEdspaySealDo extends AbstractSerial {

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
     * 创建人
     */
    private String creator;

    /**
     * 修改人

     */
    private String modifier;

    /**
     * 协议id
     */
    private Long pdfId;

    /**
     * 出借人印章id
     */
    private Long userSealId;

    /**
     * 出借金额
     */
    private BigDecimal investorAmount;


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
     * 获取创建人
     *
     * @return 创建人
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置创建人
     * 
     * @param creator 要设置的创建人
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取修改人

     *
     * @return 修改人

     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置修改人

     * 
     * @param modifier 要设置的修改人

     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    /**
     * 获取协议id
     *
     * @return 协议id
     */
    public Long getPdfId(){
      return pdfId;
    }

    /**
     * 设置协议id
     * 
     * @param pdfId 要设置的协议id
     */
    public void setPdfId(Long pdfId){
      this.pdfId = pdfId;
    }

    /**
     * 获取出借人印章id
     *
     * @return 出借人印章id
     */
    public Long getUserSealId(){
      return userSealId;
    }

    /**
     * 设置出借人印章id
     * 
     * @param userSealId 要设置的出借人印章id
     */
    public void setUserSealId(Long userSealId){
      this.userSealId = userSealId;
    }

    /**
     * 获取出借金额
     *
     * @return 出借金额
     */
    public BigDecimal getInvestorAmount(){
      return investorAmount;
    }

    /**
     * 设置出借金额
     * 
     * @param investorAmount 要设置的出借金额
     */
    public void setInvestorAmount(BigDecimal investorAmount){
      this.investorAmount = investorAmount;
    }

}