package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.dto.BoluomeCouponDto;
import com.ald.fanbei.api.dal.domain.dto.BoluomeWaimaiContactDto;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 新人专享实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-12-13 10:51:11 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBoluomeWaimaiDo extends BoluomeCouponDto {

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
     * 第三方订单号
     */
    private String thirdOrderNo;

    /**
     * 商品详细信息（json）
     */
    private String goodsinfo;

    /**
     * 购买商品种类数量
     */
    private Integer quantity;

    /**
     * 预计配送完成时间
     */
    private String deliveryTimeShow;

    /**
     * 店铺联系电话
     */
    private String restaurantPhone;

    /**
     * 店铺名称
     */
    private String restaurantName;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 订单金额
     */
    private BigDecimal orderPrice;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 
     */
    private String orderTypeName;

    /**
     * 收件联系人手机号
     */
    private String contactMobile;

    /**
     * 收件联系人姓名
     */
    private String contactName;

    /**
     * 收件地址
     */
    private String contactAddr;

    /**
     * 支付金额
     */
    private BigDecimal price;

    /**
     * 登录用户id
     */
    private Long userId;

    /**
     * 登录用户名
     */
    private String userPhone;

    private BoluomeWaimaiContactDto contact;

    /**
     * 获取主键Id
     *
     * @return rid
     */
    public Long getRid() {
	return rid;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setRid(Long rid) {
	this.rid = rid;
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
     * 获取最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified() {
	return gmtModified;
    }

    /**
     * 设置最后修改时间
     * 
     * @param gmtModified
     *            要设置的最后修改时间
     */
    public void setGmtModified(Date gmtModified) {
	this.gmtModified = gmtModified;
    }

    /**
     * 获取第三方订单号
     *
     * @return 第三方订单号
     */
    @JSONField(name = "id")
    public String getThirdOrderNo() {
	return thirdOrderNo;
    }

    /**
     * 设置第三方订单号
     * 
     * @param thirdOrderNo
     *            要设置的第三方订单号
     */
    @JSONField(name = "id")
    public void setThirdOrderNo(String thirdOrderNo) {
	this.thirdOrderNo = thirdOrderNo;
    }

    /**
     * 获取商品详细信息（json）
     *
     * @return 商品详细信息（json）
     */
    @JSONField(name = "food")
    public String getGoodsinfo() {
	return goodsinfo;
    }

    /**
     * 设置商品详细信息（json）
     * 
     * @param goodsinfo
     *            要设置的商品详细信息（json）
     */
    @JSONField(name = "food")
    public void setGoodsinfo(String goodsinfo) {
	this.goodsinfo = goodsinfo;
    }

    /**
     * 获取购买商品种类数量
     *
     * @return 购买商品种类数量
     */
    public Integer getQuantity() {
	return quantity;
    }

    /**
     * 设置购买商品种类数量
     * 
     * @param quantity
     *            要设置的购买商品种类数量
     */
    public void setQuantity(Integer quantity) {
	this.quantity = quantity;
    }

    /**
     * 获取预计配送完成时间
     *
     * @return 预计配送完成时间
     */
    public String getDeliveryTimeShow() {
	return deliveryTimeShow;
    }

    /**
     * 设置预计配送完成时间
     * 
     * @param deliveryTimeShow
     *            要设置的预计配送完成时间
     */
    public void setDeliveryTimeShow(String deliveryTimeShow) {
	this.deliveryTimeShow = deliveryTimeShow;
    }

    /**
     * 获取店铺联系电话
     *
     * @return 店铺联系电话
     */
    public String getRestaurantPhone() {
	return restaurantPhone;
    }

    /**
     * 设置店铺联系电话
     * 
     * @param restaurantPhone
     *            要设置的店铺联系电话
     */
    public void setRestaurantPhone(String restaurantPhone) {
	this.restaurantPhone = restaurantPhone;
    }

    /**
     * 获取店铺名称
     *
     * @return 店铺名称
     */
    public String getRestaurantName() {
	return restaurantName;
    }

    /**
     * 设置店铺名称
     * 
     * @param restaurantName
     *            要设置的店铺名称
     */
    public void setRestaurantName(String restaurantName) {
	this.restaurantName = restaurantName;
    }

    /**
     * 获取商品名称
     *
     * @return 商品名称
     */
    public String getName() {
	return name;
    }

    /**
     * 设置商品名称
     * 
     * @param name
     *            要设置的商品名称
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * 获取订单金额
     *
     * @return 订单金额
     */
    public BigDecimal getOrderPrice() {
	return orderPrice;
    }

    /**
     * 设置订单金额
     * 
     * @param orderPrice
     *            要设置的订单金额
     */
    public void setOrderPrice(BigDecimal orderPrice) {
	this.orderPrice = orderPrice;
    }

    /**
     * 获取订单类型
     *
     * @return 订单类型
     */
    public String getOrderType() {
	return orderType;
    }

    /**
     * 设置订单类型
     * 
     * @param orderType
     *            要设置的订单类型
     */
    public void setOrderType(String orderType) {
	this.orderType = orderType;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getOrderTypeName() {
	return orderTypeName;
    }

    /**
     * 设置
     * 
     * @param orderTypeName
     *            要设置的
     */
    public void setOrderTypeName(String orderTypeName) {
	this.orderTypeName = orderTypeName;
    }

    /**
     * 获取收件联系人手机号
     *
     * @return 收件联系人手机号
     */
    @JSONField(name = "phone")
    public String getContactMobile() {
	return contactMobile;
    }

    /**
     * 设置收件联系人手机号
     * 
     * @param contactMobile
     *            要设置的收件联系人手机号
     */
    @JSONField(name = "phone")
    public void setContactMobile(String contactMobile) {
	this.contactMobile = contactMobile;
    }

    /**
     * 获取收件联系人姓名
     *
     * @return 收件联系人姓名
     */
    public String getContactName() {
	return contactName;
    }

    /**
     * 设置收件联系人姓名
     * 
     * @param contactName
     *            要设置的收件联系人姓名
     */
    public void setContactName(String contactName) {
	this.contactName = contactName;
    }

    /**
     * 获取收件地址
     *
     * @return 收件地址
     */
    public String getContactAddr() {
	return contactAddr;
    }

    /**
     * 设置收件地址
     * 
     * @param contactAddr
     *            要设置的收件地址
     */
    public void setContactAddr(String contactAddr) {
	this.contactAddr = contactAddr;
    }

    /**
     * 获取支付金额
     *
     * @return 支付金额
     */
    public BigDecimal getPrice() {
	return price;
    }

    /**
     * 设置支付金额
     * 
     * @param price
     *            要设置的支付金额
     */
    public void setPrice(BigDecimal price) {
	this.price = price;
    }

    /**
     * 获取登录用户id
     *
     * @return 登录用户id
     */
    public Long getUserId() {
	return userId;
    }

    /**
     * 设置登录用户id
     * 
     * @param userId
     *            要设置的登录用户id
     */
    public void setUserId(Long userId) {
	this.userId = userId;
    }

    /**
     * 获取登录用户名
     *
     * @return 登录用户名
     */
    public String getUserPhone() {
	return userPhone;
    }

    /**
     * 设置登录用户名
     * 
     * @param userPhone
     *            要设置的登录用户名
     */
    public void setUserPhone(String userPhone) {
	this.userPhone = userPhone;
    }

    public BoluomeWaimaiContactDto getContact() {
	return contact;
    }

    public void setContact(BoluomeWaimaiContactDto contact) {
	this.contact = contact;
	if (contact != null) {
	    this.contactAddr = contact.getAddress();
	    this.contactMobile = contact.getMobile();
	    this.contactName = contact.getName();
	}
    }

}