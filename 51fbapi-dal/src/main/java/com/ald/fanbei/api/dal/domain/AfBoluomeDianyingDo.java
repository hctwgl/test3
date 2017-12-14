package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.dto.BoluomeCinemaDto;
import com.ald.fanbei.api.dal.domain.dto.BoluomeCouponDto;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 新人专享实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-12-13 10:51:11 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBoluomeDianyingDo extends BoluomeCouponDto {

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
     * 
     */
    private String cinemaAddr;

    /**
     * 
     */
    private String cinemaName;

    /**
     * 
     */
    private Integer count;

    /**
     * 
     */
    private Date showDate;

    /**
     * 
     */
    private String showTime;

    /**
     * 
     */
    private String endTime;

    /**
     * 
     */
    private String hallName;

    /**
     * 
     */
    private String language;

    /**
     * 
     */
    private String movieName;

    /**
     * 
     */
    private String moviePhoto;

    /**
     * 
     */
    private String screenType;

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
     * 绑卡手机号
     */
    private String phone;

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

    private BoluomeCinemaDto cinema;

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
     * 获取
     *
     * @return
     */
    public String getCinemaAddr() {
	return cinemaAddr;
    }

    /**
     * 设置
     * 
     * @param cinemaAddr
     *            要设置的
     */
    public void setCinemaAddr(String cinemaAddr) {
	this.cinemaAddr = cinemaAddr;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getCinemaName() {
	return cinemaName;
    }

    /**
     * 设置
     * 
     * @param cinemaName
     *            要设置的
     */
    public void setCinemaName(String cinemaName) {
	this.cinemaName = cinemaName;
    }

    /**
     * 获取
     *
     * @return
     */
    public Integer getCount() {
	return count;
    }

    /**
     * 设置
     * 
     * @param count
     *            要设置的
     */
    public void setCount(Integer count) {
	this.count = count;
    }

    /**
     * 获取
     *
     * @return
     */
    public Date getShowDate() {
	return showDate;
    }

    /**
     * 设置
     * 
     * @param showDate
     *            要设置的
     */
    public void setShowDate(Date showDate) {
	this.showDate = showDate;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getShowTime() {
	return showTime;
    }

    /**
     * 设置
     * 
     * @param showTime
     *            要设置的
     */
    public void setShowTime(String showTime) {
	this.showTime = showTime;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getEndTime() {
	return endTime;
    }

    /**
     * 设置
     * 
     * @param endTime
     *            要设置的
     */
    public void setEndTime(String endTime) {
	this.endTime = endTime;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getHallName() {
	return hallName;
    }

    /**
     * 设置
     * 
     * @param hallName
     *            要设置的
     */
    public void setHallName(String hallName) {
	this.hallName = hallName;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getLanguage() {
	return language;
    }

    /**
     * 设置
     * 
     * @param language
     *            要设置的
     */
    public void setLanguage(String language) {
	this.language = language;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getMovieName() {
	return movieName;
    }

    /**
     * 设置
     * 
     * @param movieName
     *            要设置的
     */
    public void setMovieName(String movieName) {
	this.movieName = movieName;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getMoviePhoto() {
	return moviePhoto;
    }

    /**
     * 设置
     * 
     * @param moviePhoto
     *            要设置的
     */
    public void setMoviePhoto(String moviePhoto) {
	this.moviePhoto = moviePhoto;
    }

    /**
     * 获取
     *
     * @return
     */
    public String getScreenType() {
	return screenType;
    }

    /**
     * 设置
     * 
     * @param screenType
     *            要设置的
     */
    public void setScreenType(String screenType) {
	this.screenType = screenType;
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
     * 获取绑卡手机号
     *
     * @return 绑卡手机号
     */
    public String getPhone() {
	return phone;
    }

    /**
     * 设置绑卡手机号
     * 
     * @param phone
     *            要设置的绑卡手机号
     */
    public void setPhone(String phone) {
	this.phone = phone;
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

    public BoluomeCinemaDto getCinema() {
	return cinema;
    }

    public void setCinema(BoluomeCinemaDto cinema) {
	this.cinema = cinema;
	if (cinema != null) {
	    this.cinemaAddr = cinema.getAddr();
	    this.cinemaName = cinema.getName();
	}
    }

}