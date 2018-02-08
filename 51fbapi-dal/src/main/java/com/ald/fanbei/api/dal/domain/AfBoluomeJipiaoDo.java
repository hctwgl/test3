package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.dto.BoluomeAirplaneContactDto;
import com.ald.fanbei.api.dal.domain.dto.BoluomeCouponDto;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 菠萝觅订单详情实体
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-02-02 16:34:00 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBoluomeJipiaoDo extends BoluomeCouponDto {

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
    @JSONField(name = "id")
    private String thirdOrderNo;

    /**
     * 登录用户id
     */
    private Long userId;

    /**
     * 登录用户名
     */
    private String userName;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 金额
     */
    private BigDecimal price;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单金额
     */
    private BigDecimal orderPrice;

    /**
     * 订单状态
     */
    private String displayStatus;

    /**
     * 购票数量
     */
    private short ticketCount;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 是否可以取消
     */
    private Integer canCancel;

    /**
     * 联系人名称
     */
    private String contactorName;

    /**
     * 联系人电话
     */
    private String contactorPhone;

    private BoluomeAirplaneContactDto contactor;
    
    private List<AfBoluomeJipiaoPassengerDo> passengers;
    private List<AfBoluomeJipiaoFlightDo> flights;

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
    @JSONField(name="userPhone")
    public String getUserName() {
	return userName;
    }

    /**
     * 设置登录用户名
     * 
     * @param userPhone
     *            要设置的登录用户名
     */
    @JSONField(name="userPhone")
    public void setUserName(String userName) {
	this.userName = userName;
    }

    /**
     * 获取订单状态
     *
     * @return 订单状态
     */
    public Integer getStatus() {
	return status;
    }

    /**
     * 设置订单状态
     * 
     * @param status
     *            要设置的订单状态
     */
    public void setStatus(Integer status) {
	this.status = status;
    }

    /**
     * 获取金额
     *
     * @return 金额
     */
    public BigDecimal getPrice() {
	return price;
    }

    /**
     * 设置金额
     * 
     * @param price
     *            要设置的金额
     */
    public void setPrice(BigDecimal price) {
	this.price = price;
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
     * 获取订单状态
     *
     * @return 订单状态
     */
    public String getDisplayStatus() {
	return displayStatus;
    }

    /**
     * 设置订单状态
     * 
     * @param displayStatus
     *            要设置的订单状态
     */
    public void setDisplayStatus(String displayStatus) {
	this.displayStatus = displayStatus;
    }

    /**
     * 获取购票数量
     *
     * @return 购票数量
     */
    @JSONField(name="count")
    public short getTicketCount() {
	return ticketCount;
    }

    /**
     * 设置购票数量
     * 
     * @param count
     *            要设置的购票数量
     */
    @JSONField(name="count")
    public void setTicketCount(short ticketCount) {
	this.ticketCount = ticketCount;
    }

    /**
     * 获取渠道
     *
     * @return 渠道
     */
    public String getChannel() {
	return channel;
    }

    /**
     * 设置渠道
     * 
     * @param channel
     *            要设置的渠道
     */
    public void setChannel(String channel) {
	this.channel = channel;
    }

    /**
     * 获取是否可以取消
     *
     * @return 是否可以取消
     */
    public Integer getCanCancel() {
	return canCancel;
    }

    /**
     * 设置是否可以取消
     * 
     * @param canCancel
     *            要设置的是否可以取消
     */
    public void setCanCancel(Integer canCancel) {
	this.canCancel = canCancel;
    }

    /**
     * 获取联系人名称
     *
     * @return 联系人名称
     */
    public String getContactorName() {
	return contactorName;
    }

    /**
     * 设置联系人名称
     * 
     * @param contactorName
     *            要设置的联系人名称
     */
    public void setContactorName(String contactorName) {
	this.contactorName = contactorName;
    }

    /**
     * 获取联系人电话
     *
     * @return 联系人电话
     */
    public String getContactorPhone() {
	return contactorPhone;
    }

    /**
     * 设置联系人电话
     * 
     * @param contactorPhone
     *            要设置的联系人电话
     */
    public void setContactorPhone(String contactorPhone) {
	this.contactorPhone = contactorPhone;
    }
    
    public BoluomeAirplaneContactDto getContactor() {
        return contactor;
    }

    public void setContactor(BoluomeAirplaneContactDto contactor) {
        this.contactor = contactor;

        this.contactorName = contactor.getName();
        this.contactorPhone = contactor.getPhone();
    }

    public List<AfBoluomeJipiaoPassengerDo> getPassengers() {
	return passengers;
    }

    public void setPassengers(List<AfBoluomeJipiaoPassengerDo> passengers) {
	this.passengers = passengers;
    }

    public List<AfBoluomeJipiaoFlightDo> getFlights() {
	return flights;
    }

    public void setFlights(List<AfBoluomeJipiaoFlightDo> flights) {
	this.flights = flights;
    }

}