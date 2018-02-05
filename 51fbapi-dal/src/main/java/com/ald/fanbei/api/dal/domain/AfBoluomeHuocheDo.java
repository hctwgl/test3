package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.dto.BoluomeCouponDto;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * 菠萝觅订单详情实体
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-02-02 16:34:02 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBoluomeHuocheDo extends BoluomeCouponDto {

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
    @JSONField(name="contactName")
    private String contactorName;

    /**
     * 联系人电话
     */
    @JSONField(name="contactPhone")
    private String contactorPhone;

    /**
     * 乘车日期
     */
    private String startDate;

    /**
     * 开车时间
     */
    private String startTime;

    /**
     * 起始站
     */
    private String station;

    /**
     * 到达站
     */
    private String destination;

    /**
     * 到达时间
     */
    private String endTime;

    /**
     * 车次
     */
    private String trainNumber;

    /**
     * 座位类型
     */
    private String seatName;

    /**
     * 车次名称详情
     */
    private String name;

    private List<AfBoluomeHuochePassengerDo> passengers;

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
    @JSONField(name="contactName")
    public String getContactorName() {
	return contactorName;
    }

    /**
     * 设置联系人名称
     * 
     * @param contactorName
     *            要设置的联系人名称
     */
    @JSONField(name="contactName")
    public void setContactorName(String contactorName) {
	this.contactorName = contactorName;
    }

    /**
     * 获取联系人电话
     *
     * @return 联系人电话
     */
    @JSONField(name="contactPhone")
    public String getContactorPhone() {
	return contactorPhone;
    }

    /**
     * 设置联系人电话
     * 
     * @param contactorPhone
     *            要设置的联系人电话
     */
    @JSONField(name="contactPhone")
    public void setContactorPhone(String contactorPhone) {
	this.contactorPhone = contactorPhone;
    }

    /**
     * 获取乘车日期
     *
     * @return 乘车日期
     */
    @JSONField(name="date")
    public String getStartDate() {
	return startDate;
    }

    /**
     * 设置乘车日期
     * 
     * @param date
     *            要设置的乘车日期
     */
    @JSONField(name="date")
    public void setStartDate(String startDate) {
	this.startDate = startDate;
    }

    /**
     * 获取开车时间
     *
     * @return 开车时间
     */
    public String getStartTime() {
	return startTime;
    }

    /**
     * 设置开车时间
     * 
     * @param startTime
     *            要设置的开车时间
     */
    public void setStartTime(String startTime) {
	this.startTime = startTime;
    }

    /**
     * 获取起始站
     *
     * @return 起始站
     */
    @JSONField(name="from")
    public String getStation() {
	return station;
    }

    /**
     * 设置起始站
     * 
     * @param from
     *            要设置的起始站
     */
    @JSONField(name="from")
    public void setStation(String station) {
	this.station = station;
    }

    /**
     * 获取到达站
     *
     * @return 到达站
     */
    @JSONField(name="to")
    public String getDestination() {
	return destination;
    }

    /**
     * 设置到达站
     * 
     * @param to
     *            要设置的到达站
     */
    @JSONField(name="to")
    public void setDestination(String destination) {
	this.destination = destination;
    }

    /**
     * 获取到达时间
     *
     * @return 到达时间
     */
    public String getEndTime() {
	return endTime;
    }

    /**
     * 设置到达时间
     * 
     * @param endTime
     *            要设置的到达时间
     */
    public void setEndTime(String endTime) {
	this.endTime = endTime;
    }

    /**
     * 获取车次
     *
     * @return 车次
     */
    public String getTrainNumber() {
	return trainNumber;
    }

    /**
     * 设置车次
     * 
     * @param trainNumber
     *            要设置的车次
     */
    public void setTrainNumber(String trainNumber) {
	this.trainNumber = trainNumber;
    }

    /**
     * 获取座位类型
     *
     * @return 座位类型
     */
    public String getSeatName() {
	return seatName;
    }

    /**
     * 设置座位类型
     * 
     * @param seatName
     *            要设置的座位类型
     */
    public void setSeatName(String seatName) {
	this.seatName = seatName;
    }

    /**
     * 获取车次名称详情
     *
     * @return 车次名称详情
     */
    public String getName() {
	return name;
    }

    /**
     * 设置车次名称详情
     * 
     * @param name
     *            要设置的车次名称详情
     */
    public void setName(String name) {
	this.name = name;
    }

    public List<AfBoluomeHuochePassengerDo> getPassengers() {
	return passengers;
    }

    public void setPassengers(List<AfBoluomeHuochePassengerDo> passengers) {
	this.passengers = passengers;
    }

}