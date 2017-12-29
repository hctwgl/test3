package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 资产方信息实体
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAssetSideInfoDo extends AbstractSerial {

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
     * 最后修改人
     */
    private String modifier;

    /**
     * Y:启用 N:禁用
     */
    private String status;

    /**
     * 资产方名称
     */
    private String name;

    /**
     * 资产方编号
     */
    private String assetSideNo;

    /**
     * 资产方标示
     */
    private String assetSideFlag;

    /**
     * 联系人名称
     */
    private String contactsName;

    /**
     * 联系人电话
     */
    private String contactsPhone;

    /**
     * 借钱利率%'
     */
    private BigDecimal borrowRate;

    /**
     * 年化利率%
     */
    private BigDecimal annualRate;

    /**
     * 还款方式【0：一次性还本付息】
     */
    private Integer repaytType;

    /**
     * 联系人邮箱
     */
    private String contactsEmail;

    /**
     * 对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     */
    private Integer sendMode;


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
     * 获取最后修改人
     *
     * @return 最后修改人
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置最后修改人
     * 
     * @param modifier 要设置的最后修改人
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    /**
     * 获取Y:启用 N:禁用
     *
     * @return Y:启用 N:禁用
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置Y:启用 N:禁用
     * 
     * @param status 要设置的Y:启用 N:禁用
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取资产方名称
     *
     * @return 资产方名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置资产方名称
     * 
     * @param name 要设置的资产方名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取资产方编号
     *
     * @return 资产方编号
     */
    public String getAssetSideNo(){
      return assetSideNo;
    }

    /**
     * 设置资产方编号
     * 
     * @param assetSideNo 要设置的资产方编号
     */
    public void setAssetSideNo(String assetSideNo){
      this.assetSideNo = assetSideNo;
    }

    /**
     * 获取资产方标示
     *
     * @return 资产方标示
     */
    public String getAssetSideFlag(){
      return assetSideFlag;
    }

    /**
     * 设置资产方标示
     * 
     * @param assetSideFlag 要设置的资产方标示
     */
    public void setAssetSideFlag(String assetSideFlag){
      this.assetSideFlag = assetSideFlag;
    }

    /**
     * 获取联系人名称
     *
     * @return 联系人名称
     */
    public String getContactsName(){
      return contactsName;
    }

    /**
     * 设置联系人名称
     * 
     * @param contactsName 要设置的联系人名称
     */
    public void setContactsName(String contactsName){
      this.contactsName = contactsName;
    }

    /**
     * 获取联系人电话
     *
     * @return 联系人电话
     */
    public String getContactsPhone(){
      return contactsPhone;
    }

    /**
     * 设置联系人电话
     * 
     * @param contactsPhone 要设置的联系人电话
     */
    public void setContactsPhone(String contactsPhone){
      this.contactsPhone = contactsPhone;
    }

    /**
     * 获取借钱利率%'
     *
     * @return 借钱利率%'
     */
    public BigDecimal getBorrowRate(){
      return borrowRate;
    }

    /**
     * 设置借钱利率%'
     * 
     * @param borrowRate 要设置的借钱利率%'
     */
    public void setBorrowRate(BigDecimal borrowRate){
      this.borrowRate = borrowRate;
    }

    /**
     * 获取年化利率%
     *
     * @return 年化利率%
     */
    public BigDecimal getAnnualRate(){
      return annualRate;
    }

    /**
     * 设置年化利率%
     * 
     * @param annualRate 要设置的年化利率%
     */
    public void setAnnualRate(BigDecimal annualRate){
      this.annualRate = annualRate;
    }

    /**
     * 获取还款方式【0：一次性还本付息】
     *
     * @return 还款方式【0：一次性还本付息】
     */
    public Integer getRepaytType(){
      return repaytType;
    }

    /**
     * 设置还款方式【0：一次性还本付息】
     * 
     * @param repaytType 要设置的还款方式【0：一次性还本付息】
     */
    public void setRepaytType(Integer repaytType){
      this.repaytType = repaytType;
    }

    /**
     * 获取联系人邮箱
     *
     * @return 联系人邮箱
     */
    public String getContactsEmail(){
      return contactsEmail;
    }

    /**
     * 设置联系人邮箱
     * 
     * @param contactsEmail 要设置的联系人邮箱
     */
    public void setContactsEmail(String contactsEmail){
      this.contactsEmail = contactsEmail;
    }

    /**
     * 获取对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     *
     * @return 对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     */
    public Integer getSendMode(){
      return sendMode;
    }

    /**
     * 设置对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     * 
     * @param sendMode 要设置的对接方式【0：邮箱发送，1：接口对接，2：线下发送】
     */
    public void setSendMode(Integer sendMode){
      this.sendMode = sendMode;
    }

}