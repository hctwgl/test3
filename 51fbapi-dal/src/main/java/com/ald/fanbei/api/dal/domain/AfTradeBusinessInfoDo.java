package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * 商圈商户信息表实体
 *
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:01 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfTradeBusinessInfoDo extends AbstractSerial {

   private static final long serialVersionUID = 1L;

   /**
    * 主键Id
    */
   private Long id;

   /**
    * 商户id
    */
   private Long businessId;

   /**
    * 商户名称
    */
   private String name;

   /**
    * 商户类型
    */
   private Integer type;

   /**
    * 商户图片(已,分割)
    */
   private String imageUrl;

   /**
    * 商户地址
    */
   private String address;
   /**
    * 工作日
    */
   private String workdays;
   /**
    * 营业开始时间
    */
   private String openTime;

   /**
    * 营业结束时间
    */
   private String endTime;

   /**
    * 座机号码
    */
   private String phone;

   /**
    * 法人或授权人姓名
    */
   private String corporationName;

   /**
    * 法人或授权人手机号
    */
   private String corporationMobile;

   /**
    * 法人或授权人邮箱
    */
   private String corporationEmail;

   /**
    * 营业执照
    */
   private String licenceUrl;

   /**
    * 身份证正面图片
    */
   private String frontUrl;

   /**
    * 身份证反面图片
    */
   private String inverseUrl;

   /**
    * 合作协议图片
    */
   private String protocolUrl;

   /**
    * 授权协议图片
    */
   private String authorUrl;

   /**
    * 提现周期 多少天内不能提现 1表示当天内不能提现
    */
   private Integer withdrawCycle;

   /**
    * 银行卡持卡人姓名
    */
   private String cardName;

   /**
    * 银行卡号
    */
   private String cardNo;

   /**
    * 银行卡id
    */
   private Long cardId;

   /**
    * 银行卡代码
    */
   private String cardCode;

   /**
    * 银行名称
    */
   private String cardBank;

   private String cardPhone;
   private String cardIdnumber;
   /**
    * 联系人姓名
    */
   private String linkName;

   /**
    * 联系人手机号
    */
   private String linkMobile;

   /**
    * 联系人QQ
    */
   private String linkQq;

   /**
    * 联系人微信
    */
   private String linkWx;

   /**
    * 联系人邮箱
    */
   private String linkEmail;

   /**
    * 状态 1:正常 2:失效
    */
   private Integer status;

   /**
    * 可提现金额
    */
   private Double canWithdraw;

   /**
    * 不可提现金额
    */
   private Double cannotWithdraw;

   /**
    * 创建人
    */
   private String creator;

   /**
    * 创建时间
    */
   private Date gmtCreate;

   /**
    * 修改人
    */
   private String modifier;

   /**
    * 修改时间
    */
   private Date gmtModified;

   /**
    * 获取主键Id
    *
    * @return id
    */
   public Long getId() {
      return id;
   }

   /**
    * 设置主键Id
    *
    * @param 要设置的主键Id
    */
   public void setId(Long id) {
      this.id = id;
   }

   /**
    * 获取商户id
    *
    * @return 商户id
    */
   public Long getBusinessId() {
      return businessId;
   }

   /**
    * 设置商户id
    *
    * @param businessId
    *            要设置的商户id
    */
   public void setBusinessId(Long businessId) {
      this.businessId = businessId;
   }

   /**
    * 获取商户名称
    *
    * @return 商户名称
    */
   public String getName() {
      return name;
   }

   /**
    * 设置商户名称
    *
    * @param name
    *            要设置的商户名称
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * 获取商户类型
    *
    * @return 商户类型
    */
   public Integer getType() {
      return type;
   }

   /**
    * 设置商户类型
    *
    * @param type
    *            要设置的商户类型
    */
   public void setType(Integer type) {
      this.type = type;
   }

   /**
    * 获取商户图片(已,分割)
    *
    * @return 商户图片(已,分割)
    */
   public String getImageUrl() {
      return imageUrl;
   }

   /**
    * 设置商户图片(已,分割)
    *
    * @param imageUrl
    *            要设置的商户图片(已,分割)
    */
   public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
   }

   /**
    * 获取商户地址
    *
    * @return 商户地址
    */
   public String getAddress() {
      return address;
   }

   /**
    * 设置商户地址
    *
    * @param address
    *            要设置的商户地址
    */
   public void setAddress(String address) {
      this.address = address;
   }

   /**
    * 获取营业开始时间
    *
    * @return 营业开始时间
    */
   public String getOpenTime() {
      return openTime;
   }

   /**
    * 设置营业开始时间
    *
    * @param openTime
    *            要设置的营业开始时间
    */
   public void setOpenTime(String openTime) {
      this.openTime = openTime;
   }

   /**
    * 获取营业结束时间
    *
    * @return 营业结束时间
    */
   public String getEndTime() {
      return endTime;
   }

   /**
    * 设置营业结束时间
    *
    * @param endTime
    *            要设置的营业结束时间
    */
   public void setEndTime(String endTime) {
      this.endTime = endTime;
   }

   /**
    * 获取座机号码
    *
    * @return 座机号码
    */
   public String getPhone() {
      return phone;
   }

   /**
    * 设置座机号码
    *
    * @param phone
    *            要设置的座机号码
    */
   public void setPhone(String phone) {
      this.phone = phone;
   }

   /**
    * 获取法人或授权人姓名
    *
    * @return 法人或授权人姓名
    */
   public String getCorporationName() {
      return corporationName;
   }

   /**
    * 设置法人或授权人姓名
    *
    * @param corporationName
    *            要设置的法人或授权人姓名
    */
   public void setCorporationName(String corporationName) {
      this.corporationName = corporationName;
   }

   /**
    * 获取法人或授权人手机号
    *
    * @return 法人或授权人手机号
    */
   public String getCorporationMobile() {
      return corporationMobile;
   }

   /**
    * 设置法人或授权人手机号
    *
    * @param corporationMobile
    *            要设置的法人或授权人手机号
    */
   public void setCorporationMobile(String corporationMobile) {
      this.corporationMobile = corporationMobile;
   }

   /**
    * 获取法人或授权人邮箱
    *
    * @return 法人或授权人邮箱
    */
   public String getCorporationEmail() {
      return corporationEmail;
   }

   /**
    * 设置法人或授权人邮箱
    *
    * @param corporationEmail
    *            要设置的法人或授权人邮箱
    */
   public void setCorporationEmail(String corporationEmail) {
      this.corporationEmail = corporationEmail;
   }

   /**
    * 获取营业执照
    *
    * @return 营业执照
    */
   public String getLicenceUrl() {
      return licenceUrl;
   }

   /**
    * 设置营业执照
    *
    * @param licenceUrl
    *            要设置的营业执照
    */
   public void setLicenceUrl(String licenceUrl) {
      this.licenceUrl = licenceUrl;
   }

   /**
    * 获取身份证正面图片
    *
    * @return 身份证正面图片
    */
   public String getFrontUrl() {
      return frontUrl;
   }

   /**
    * 设置身份证正面图片
    *
    * @param frontUrl
    *            要设置的身份证正面图片
    */
   public void setFrontUrl(String frontUrl) {
      this.frontUrl = frontUrl;
   }

   /**
    * 获取身份证反面图片
    *
    * @return 身份证反面图片
    */
   public String getInverseUrl() {
      return inverseUrl;
   }

   /**
    * 设置身份证反面图片
    *
    * @param inverseUrl
    *            要设置的身份证反面图片
    */
   public void setInverseUrl(String inverseUrl) {
      this.inverseUrl = inverseUrl;
   }

   /**
    * 获取合作协议图片
    *
    * @return 合作协议图片
    */
   public String getProtocolUrl() {
      return protocolUrl;
   }

   /**
    * 设置合作协议图片
    *
    * @param protocolUrl
    *            要设置的合作协议图片
    */
   public void setProtocolUrl(String protocolUrl) {
      this.protocolUrl = protocolUrl;
   }

   /**
    * 获取提现周期 多少天内不能提现 1表示当天内不能提现
    *
    * @return 提现周期 多少天内不能提现 1表示当天内不能提现
    */
   public Integer getWithdrawCycle() {
      return withdrawCycle;
   }

   /**
    * 设置提现周期 多少天内不能提现 1表示当天内不能提现
    *
    * @param withdrawCycle
    *            要设置的提现周期 多少天内不能提现 1表示当天内不能提现
    */
   public void setWithdrawCycle(Integer withdrawCycle) {
      this.withdrawCycle = withdrawCycle;
   }

   /**
    * 获取银行卡持卡人姓名
    *
    * @return 银行卡持卡人姓名
    */
   public String getCardName() {
      return cardName;
   }

   /**
    * 设置银行卡持卡人姓名
    *
    * @param cardName
    *            要设置的银行卡持卡人姓名
    */
   public void setCardName(String cardName) {
      this.cardName = cardName;
   }

   /**
    * 获取银行卡号
    *
    * @return 银行卡号
    */
   public String getCardNo() {
      return cardNo;
   }

   /**
    * 设置银行卡号
    *
    * @param cardNo
    *            要设置的银行卡号
    */
   public void setCardNo(String cardNo) {
      this.cardNo = cardNo;
   }

   /**
    * 获取银行卡id
    *
    * @return 银行卡id
    */
   public Long getCardId() {
      return cardId;
   }

   /**
    * 设置银行卡id
    *
    * @param cardId
    *            要设置的银行卡id
    */
   public void setCardId(Long cardId) {
      this.cardId = cardId;
   }

   /**
    * 获取银行卡代码
    *
    * @return 银行卡代码
    */
   public String getCardCode() {
      return cardCode;
   }

   /**
    * 设置银行卡代码
    *
    * @param cardCode
    *            要设置的银行卡代码
    */
   public void setCardCode(String cardCode) {
      this.cardCode = cardCode;
   }

   /**
    * 获取银行名称
    *
    * @return 银行名称
    */
   public String getCardBank() {
      return cardBank;
   }

   /**
    * 设置银行名称
    *
    * @param cardBank
    *            要设置的银行名称
    */
   public void setCardBank(String cardBank) {
      this.cardBank = cardBank;
   }

   /**
    * 获取联系人姓名
    *
    * @return 联系人姓名
    */
   public String getLinkName() {
      return linkName;
   }

   /**
    * 设置联系人姓名
    *
    * @param linkName
    *            要设置的联系人姓名
    */
   public void setLinkName(String linkName) {
      this.linkName = linkName;
   }

   /**
    * 获取联系人手机号
    *
    * @return 联系人手机号
    */
   public String getLinkMobile() {
      return linkMobile;
   }

   /**
    * 设置联系人手机号
    *
    * @param linkMobile
    *            要设置的联系人手机号
    */
   public void setLinkMobile(String linkMobile) {
      this.linkMobile = linkMobile;
   }

   /**
    * 获取联系人QQ
    *
    * @return 联系人QQ
    */
   public String getLinkQq() {
      return linkQq;
   }

   /**
    * 设置联系人QQ
    *
    * @param linkQq
    *            要设置的联系人QQ
    */
   public void setLinkQq(String linkQq) {
      this.linkQq = linkQq;
   }

   /**
    * 获取联系人微信
    *
    * @return 联系人微信
    */
   public String getLinkWx() {
      return linkWx;
   }

   /**
    * 设置联系人微信
    *
    * @param linkWx
    *            要设置的联系人微信
    */
   public void setLinkWx(String linkWx) {
      this.linkWx = linkWx;
   }

   /**
    * 获取联系人邮箱
    *
    * @return 联系人邮箱
    */
   public String getLinkEmail() {
      return linkEmail;
   }

   /**
    * 设置联系人邮箱
    *
    * @param linkEmail
    *            要设置的联系人邮箱
    */
   public void setLinkEmail(String linkEmail) {
      this.linkEmail = linkEmail;
   }

   /**
    * 获取状态 1:正常 2:失效
    *
    * @return 状态 1:正常 2:失效
    */
   public Integer getStatus() {
      return status;
   }

   /**
    * 设置状态 1:正常 2:失效
    *
    * @param status
    *            要设置的状态 1:正常 2:失效
    */
   public void setStatus(Integer status) {
      this.status = status;
   }

   /**
    * 获取可提现金额
    *
    * @return 可提现金额
    */
   public Double getCanWithdraw() {
      return canWithdraw;
   }

   /**
    * 设置可提现金额
    *
    * @param canWithdraw
    *            要设置的可提现金额
    */
   public void setCanWithdraw(Double canWithdraw) {
      this.canWithdraw = canWithdraw;
   }

   /**
    * 获取不可提现金额
    *
    * @return 不可提现金额
    */
   public Double getCannotWithdraw() {
      return cannotWithdraw;
   }

   /**
    * 设置不可提现金额
    *
    * @param cannotWithdraw
    *            要设置的不可提现金额
    */
   public void setCannotWithdraw(Double cannotWithdraw) {
      this.cannotWithdraw = cannotWithdraw;
   }

   /**
    * 获取创建人
    *
    * @return 创建人
    */
   public String getCreator() {
      return creator;
   }

   /**
    * 设置创建人
    *
    * @param creator
    *            要设置的创建人
    */
   public void setCreator(String creator) {
      this.creator = creator;
   }

   /**
    * 获取创建时间
    *
    * @return 创建时间
    */
   public Date getGmtCreate() {
      return gmtCreate;
   }

   /**
    * 设置创建时间
    *
    * @param gmtCreate
    *            要设置的创建时间
    */
   public void setGmtCreate(Date gmtCreate) {
      this.gmtCreate = gmtCreate;
   }

   /**
    * 获取修改人
    *
    * @return 修改人
    */
   public String getModifier() {
      return modifier;
   }

   /**
    * 设置修改人
    *
    * @param modifier
    *            要设置的修改人
    */
   public void setModifier(String modifier) {
      this.modifier = modifier;
   }

   /**
    * 获取修改时间
    *
    * @return 修改时间
    */
   public Date getGmtModified() {
      return gmtModified;
   }

   /**
    * 设置修改时间
    *
    * @param gmtModified
    *            要设置的修改时间
    */
   public void setGmtModified(Date gmtModified) {
      this.gmtModified = gmtModified;
   }

   public String getWorkdays() {
      return workdays;
   }

   public void setWorkdays(String workdays) {
      this.workdays = workdays;
   }

   public String getCardPhone() {
      return cardPhone;
   }

   public void setCardPhone(String cardPhone) {
      this.cardPhone = cardPhone;
   }


   public String getCardIdnumber() {
      return cardIdnumber;
   }

   public void setCardIdnumber(String cardIdnumber) {
      this.cardIdnumber = cardIdnumber;
   }

   public String getAuthorUrl() {
      return authorUrl;
   }

   public void setAuthorUrl(String authorUrl) {
      this.authorUrl = authorUrl;
   }
}