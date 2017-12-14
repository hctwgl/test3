package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.dto.BoluomeJiudianContactDto;
import com.ald.fanbei.api.dal.domain.dto.BoluomeCouponDto;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 新人专享实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-12-13 10:51:10 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBoluomeJiudianDo extends BoluomeCouponDto {

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
     * 入住日期
     */
    private Date arrivalDate;

    /**
     * 结束日期
     */
    private Date departureDate;

    /**
     * 最早到店时间
     */
    private Date earliestArrivalTime;

    /**
     * 酒店地址
     */
    private String hotelAddr;

    /**
     * 酒店图标
     */
    private String hotelImage;

    /**
     * 酒店名称
     */
    private String hotelName;

    /**
     * 酒店电话
     */
    private String hotelTel;

    /**
     * 最晚到店时间
     */
    private Date latestArrivalTime;

    /**
     * 入住人数
     */
    private Integer numberOfCustomers;

    /**
     * 预定房间数
     */
    private Integer numberOfRooms;

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
     * 入住联系人手机号
     */
    private String contactMobile;

    /**
     * 入住联系人姓名
     */
    private String contactName;

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

    private BoluomeJiudianContactDto boluomeContactDto;

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
     * 获取入住日期
     *
     * @return 入住日期
     */
    @JSONField(name = "ArrivalDate")
    public Date getArrivalDate() {
	return arrivalDate;
    }

    /**
     * 设置入住日期
     * 
     * @param arrivalDate
     *            要设置的入住日期
     */
    @JSONField(name = "ArrivalDate")
    public void setArrivalDate(Date arrivalDate) {
	this.arrivalDate = arrivalDate;
    }

    /**
     * 获取结束日期
     *
     * @return 结束日期
     */
    @JSONField(name = "DepartureDate")
    public Date getDepartureDate() {
	return departureDate;
    }

    /**
     * 设置结束日期
     * 
     * @param departureDate
     *            要设置的结束日期
     */
    @JSONField(name = "DepartureDate")
    public void setDepartureDate(Date departureDate) {
	this.departureDate = departureDate;
    }

    /**
     * 获取最早到店时间
     *
     * @return 最早到店时间
     */
    @JSONField(name = "EarliestArrivalTime")
    public Date getEarliestArrivalTime() {
	return earliestArrivalTime;
    }

    /**
     * 设置最早到店时间
     * 
     * @param earliestArrivalTime
     *            要设置的最早到店时间
     */
    @JSONField(name = "EarliestArrivalTime")
    public void setEarliestArrivalTime(Date earliestArrivalTime) {
	this.earliestArrivalTime = earliestArrivalTime;
    }

    /**
     * 获取酒店地址
     *
     * @return 酒店地址
     */
    @JSONField(name = "HotelAddr")
    public String getHotelAddr() {
	return hotelAddr;
    }

    /**
     * 设置酒店地址
     * 
     * @param hotelAddr
     *            要设置的酒店地址
     */
    @JSONField(name = "HotelAddr")
    public void setHotelAddr(String hotelAddr) {
	this.hotelAddr = hotelAddr;
    }

    /**
     * 获取酒店图标
     *
     * @return 酒店图标
     */
    @JSONField(name = "HotelImg")
    public String getHotelImage() {
	return hotelImage;
    }

    /**
     * 设置酒店图标
     * 
     * @param hotelImage
     *            要设置的酒店图标
     */
    @JSONField(name = "HotelImg")
    public void setHotelImage(String hotelImage) {
	this.hotelImage = hotelImage;
    }

    /**
     * 获取酒店名称
     *
     * @return 酒店名称
     */
    @JSONField(name = "HotelName")
    public String getHotelName() {
	return hotelName;
    }

    /**
     * 设置酒店名称
     * 
     * @param hotelName
     *            要设置的酒店名称
     */
    @JSONField(name = "HotelName")
    public void setHotelName(String hotelName) {
	this.hotelName = hotelName;
    }

    /**
     * 获取酒店电话
     *
     * @return 酒店电话
     */
    @JSONField(name = "HotelTel")
    public String getHotelTel() {
	return hotelTel;
    }

    /**
     * 设置酒店电话
     * 
     * @param hotelTel
     *            要设置的酒店电话
     */
    @JSONField(name = "HotelTel")
    public void setHotelTel(String hotelTel) {
	this.hotelTel = hotelTel;
    }

    /**
     * 获取最晚到店时间
     *
     * @return 最晚到店时间
     */
    @JSONField(name = "LatestArrivalTime")
    public Date getLatestArrivalTime() {
	return latestArrivalTime;
    }

    /**
     * 设置最晚到店时间
     * 
     * @param latestArrivalTime
     *            要设置的最晚到店时间
     */
    @JSONField(name = "LatestArrivalTime")
    public void setLatestArrivalTime(Date latestArrivalTime) {
	this.latestArrivalTime = latestArrivalTime;
    }

    /**
     * 获取入住人数
     *
     * @return 入住人数
     */
    @JSONField(name = "NumberOfCustomers")
    public Integer getNumberOfCustomers() {
	return numberOfCustomers;
    }

    /**
     * 设置入住人数
     * 
     * @param numberOfCustomers
     *            要设置的入住人数
     */
    @JSONField(name = "NumberOfCustomers")
    public void setNumberOfCustomers(Integer numberOfCustomers) {
	this.numberOfCustomers = numberOfCustomers;
    }

    /**
     * 获取预定房间数
     *
     * @return 预定房间数
     */
    @JSONField(name = "NumberOfRooms")
    public Integer getNumberOfRooms() {
	return numberOfRooms;
    }

    /**
     * 设置预定房间数
     * 
     * @param numberOfRooms
     *            要设置的预定房间数
     */
    @JSONField(name = "NumberOfRooms")
    public void setNumberOfRooms(Integer numberOfRooms) {
	this.numberOfRooms = numberOfRooms;
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
     * 获取入住联系人手机号
     *
     * @return 入住联系人手机号
     */
    public String getContactMobile() {
	return contactMobile;
    }

    /**
     * 设置入住联系人手机号
     * 
     * @param contactMobile
     *            要设置的入住联系人手机号
     */
    public void setContactMobile(String contactMobile) {
	this.contactMobile = contactMobile;
    }

    /**
     * 获取入住联系人姓名
     *
     * @return 入住联系人姓名
     */
    public String getContactName() {
	return contactName;
    }

    /**
     * 设置入住联系人姓名
     * 
     * @param contactName
     *            要设置的入住联系人姓名
     */
    public void setContactName(String contactName) {
	this.contactName = contactName;
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

    @JSONField(name = "Contact")
    public BoluomeJiudianContactDto getBoluomeContactDto() {
	return boluomeContactDto;
    }

    @JSONField(name = "Contact")
    public void setBoluomeContactDto(BoluomeJiudianContactDto boluomeContactDto) {
	this.boluomeContactDto = boluomeContactDto;
	if (boluomeContactDto != null) {
	    this.contactMobile = boluomeContactDto.getMobile();
	    this.contactName = boluomeContactDto.getName();
	}
    }

}