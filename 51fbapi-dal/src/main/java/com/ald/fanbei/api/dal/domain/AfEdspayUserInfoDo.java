package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 钱包出借用户查询表实体
 * 
 * @author gsq
 * @version 1.0.0 初始化
 * @date 2018-04-18 11:50:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfEdspayUserInfoDo extends AbstractSerial {

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
     * 用户名称
     */
    private String userName;

    /**
     * 1:现金借款协议 2:分期协议 3:续借协议 4:小贷平台服务协议 5:白领贷借款协议 6:白领贷平台服务协议 7:点对点搭售商品协议
     */
    private Integer type;

    /**
     * 类型id
     */
    private Long typeId;

    /**
     * 出借金额
     */
    private BigDecimal amount;

    /**
     * e都市钱包用户身份证号
     */
    private String edspayUserCardId;

    /**
     * e都市钱包pdfUrl
     */
    private String protocolUrl;

    /**
     * 手机号
     */
    private String mobile;

    private BigDecimal investorAmount;

    public BigDecimal getInvestorAmount() {
        return investorAmount;
    }

    public void setInvestorAmount(BigDecimal investorAmount) {
        this.investorAmount = investorAmount;
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
     * 获取用户名称
     *
     * @return 用户名称
     */
    public String getUserName(){
      return userName;
    }

    /**
     * 设置用户名称
     * 
     * @param userName 要设置的用户名称
     */
    public void setUserName(String userName){
      this.userName = userName;
    }

    /**
     * 获取1:现金借款协议 2:分期协议 3:续借协议 4:小贷平台服务协议 5:白领贷借款协议 6:白领贷平台服务协议 7:点对点搭售商品协议
     *
     * @return 1:现金借款协议 2:分期协议 3:续借协议 4:小贷平台服务协议 5:白领贷借款协议 6:白领贷平台服务协议 7:点对点搭售商品协议
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置1:现金借款协议 2:分期协议 3:续借协议 4:小贷平台服务协议 5:白领贷借款协议 6:白领贷平台服务协议 7:点对点搭售商品协议
     * 
     * @param type 要设置的1:现金借款协议 2:分期协议 3:续借协议 4:小贷平台服务协议 5:白领贷借款协议 6:白领贷平台服务协议 7:点对点搭售商品协议
     */
    public void setType(Integer type){
      this.type = type;
    }

    /**
     * 获取类型id
     *
     * @return 类型id
     */
    public Long getTypeId(){
      return typeId;
    }

    /**
     * 设置类型id
     * 
     * @param typeId 要设置的类型id
     */
    public void setTypeId(Long typeId){
      this.typeId = typeId;
    }

    /**
     * 获取出借金额
     *
     * @return 出借金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置出借金额
     * 
     * @param amount 要设置的出借金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取e都市钱包用户身份证号
     *
     * @return e都市钱包用户身份证号
     */
    public String getEdspayUserCardId(){
      return edspayUserCardId;
    }

    /**
     * 设置e都市钱包用户身份证号
     * 
     * @param edspayUserCardId 要设置的e都市钱包用户身份证号
     */
    public void setEdspayUserCardId(String edspayUserCardId){
      this.edspayUserCardId = edspayUserCardId;
    }

    /**
     * 获取e都市钱包pdfUrl
     *
     * @return e都市钱包pdfUrl
     */
    public String getProtocolUrl(){
      return protocolUrl;
    }

    /**
     * 设置e都市钱包pdfUrl
     * 
     * @param protocolUrl 要设置的e都市钱包pdfUrl
     */
    public void setProtocolUrl(String protocolUrl){
      this.protocolUrl = protocolUrl;
    }

    /**
     * 获取手机号
     *
     * @return 手机号
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置手机号
     * 
     * @param mobile 要设置的手机号
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
    }

}