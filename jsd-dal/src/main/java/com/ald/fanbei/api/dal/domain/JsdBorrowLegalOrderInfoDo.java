package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-19 14:09:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdBorrowLegalOrderInfoDo extends AbstractSerial {

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
     * 借款id
     */
    private Long borrowId;

    /**
     * 借款纬度
     */
    private BigDecimal latitude;

    /**
     * 借款经度
     */
    private BigDecimal longitude;

    /**
     * 借款详细地址
     */
    private String borrowAddress;

    /**
     * 借款省份
     */
    private String borrowProvince;

    /**
     * 借款城市
     */
    private String borrowCity;

    /**
     * 借款区县
     */
    private String borrowCounty;

    /**
     * 商品订单状态
     */
    private String orderStatus;

    /**
     * 快递单号
     */
    private String shipperNumber;

    /**
     * 物流公司
     */
    private String shipperName;

    /**
     * 收货人姓名
     */
    private String consignee;

    /**
     * 收货人手机号
     */
    private String mobile;

    /**
     * 收货省份
     */
    private String province;

    /**
     * 收货城市
     */
    private String city;

    /**
     * 收货区县
     */
    private String county;

    /**
     * 收货街道地址
     */
    private String address;

    /**
     * 拼接后的地址全称
     */
    private String fullAddress;

    /**
     * 发货时间
     */
    private Date gmtSended;

    /**
     * 收货时间
     */
    private Date gmtReceived;

    /**
     * 物流跟踪信息
     */
    private String traces;

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
     * 获取借款id
     *
     * @return 借款id
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置借款id
     * 
     * @param borrowId 要设置的借款id
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取借款纬度
     *
     * @return 借款纬度
     */
    public BigDecimal getLatitude(){
      return latitude;
    }

    /**
     * 设置借款纬度
     * 
     * @param latitude 要设置的借款纬度
     */
    public void setLatitude(BigDecimal latitude){
      this.latitude = latitude;
    }

    /**
     * 获取借款经度
     *
     * @return 借款经度
     */
    public BigDecimal getLongitude(){
      return longitude;
    }

    /**
     * 设置借款经度
     * 
     * @param longitude 要设置的借款经度
     */
    public void setLongitude(BigDecimal longitude){
      this.longitude = longitude;
    }

    /**
     * 获取借款详细地址
     *
     * @return 借款详细地址
     */
    public String getBorrowAddress(){
      return borrowAddress;
    }

    /**
     * 设置借款详细地址
     * 
     * @param borrowAddress 要设置的借款详细地址
     */
    public void setBorrowAddress(String borrowAddress){
      this.borrowAddress = borrowAddress;
    }

    /**
     * 获取借款省份
     *
     * @return 借款省份
     */
    public String getBorrowProvince(){
      return borrowProvince;
    }

    /**
     * 设置借款省份
     * 
     * @param borrowProvince 要设置的借款省份
     */
    public void setBorrowProvince(String borrowProvince){
      this.borrowProvince = borrowProvince;
    }

    /**
     * 获取借款城市
     *
     * @return 借款城市
     */
    public String getBorrowCity(){
      return borrowCity;
    }

    /**
     * 设置借款城市
     * 
     * @param borrowCity 要设置的借款城市
     */
    public void setBorrowCity(String borrowCity){
      this.borrowCity = borrowCity;
    }

    /**
     * 获取借款区县
     *
     * @return 借款区县
     */
    public String getBorrowCounty(){
      return borrowCounty;
    }

    /**
     * 设置借款区县
     * 
     * @param borrowCounty 要设置的借款区县
     */
    public void setBorrowCounty(String borrowCounty){
      this.borrowCounty = borrowCounty;
    }

    /**
     * 获取商品订单状态
     *
     * @return 商品订单状态
     */
    public String getOrderStatus(){
      return orderStatus;
    }

    /**
     * 设置商品订单状态
     * 
     * @param orderStatus 要设置的商品订单状态
     */
    public void setOrderStatus(String orderStatus){
      this.orderStatus = orderStatus;
    }

    /**
     * 获取快递单号
     *
     * @return 快递单号
     */
    public String getShipperNumber(){
      return shipperNumber;
    }

    /**
     * 设置快递单号
     * 
     * @param shipperNumber 要设置的快递单号
     */
    public void setShipperNumber(String shipperNumber){
      this.shipperNumber = shipperNumber;
    }

    /**
     * 获取物流公司
     *
     * @return 物流公司
     */
    public String getShipperName(){
      return shipperName;
    }

    /**
     * 设置物流公司
     * 
     * @param shipperName 要设置的物流公司
     */
    public void setShipperName(String shipperName){
      this.shipperName = shipperName;
    }

    /**
     * 获取收货人姓名
     *
     * @return 收货人姓名
     */
    public String getConsignee(){
      return consignee;
    }

    /**
     * 设置收货人姓名
     * 
     * @param consignee 要设置的收货人姓名
     */
    public void setConsignee(String consignee){
      this.consignee = consignee;
    }

    /**
     * 获取收货人手机号
     *
     * @return 收货人手机号
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置收货人手机号
     * 
     * @param mobile 要设置的收货人手机号
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
    }

    /**
     * 获取收货省份
     *
     * @return 收货省份
     */
    public String getProvince(){
      return province;
    }

    /**
     * 设置收货省份
     * 
     * @param province 要设置的收货省份
     */
    public void setProvince(String province){
      this.province = province;
    }

    /**
     * 获取收货城市
     *
     * @return 收货城市
     */
    public String getCity(){
      return city;
    }

    /**
     * 设置收货城市
     * 
     * @param city 要设置的收货城市
     */
    public void setCity(String city){
      this.city = city;
    }

    /**
     * 获取收货区县
     *
     * @return 收货区县
     */
    public String getCounty(){
      return county;
    }

    /**
     * 设置收货区县
     * 
     * @param county 要设置的收货区县
     */
    public void setCounty(String county){
      this.county = county;
    }

    /**
     * 获取收货街道地址
     *
     * @return 收货街道地址
     */
    public String getAddress(){
      return address;
    }

    /**
     * 设置收货街道地址
     * 
     * @param address 要设置的收货街道地址
     */
    public void setAddress(String address){
      this.address = address;
    }

    /**
     * 获取拼接后的地址全称
     *
     * @return 拼接后的地址全称
     */
    public String getFullAddress(){
      return fullAddress;
    }

    /**
     * 设置拼接后的地址全称
     * 
     * @param fullAddress 要设置的拼接后的地址全称
     */
    public void setFullAddress(String fullAddress){
      this.fullAddress = fullAddress;
    }

    /**
     * 获取发货时间
     *
     * @return 发货时间
     */
    public Date getGmtSended(){
      return gmtSended;
    }

    /**
     * 设置发货时间
     * 
     * @param gmtSended 要设置的发货时间
     */
    public void setGmtSended(Date gmtSended){
      this.gmtSended = gmtSended;
    }

    /**
     * 获取收货时间
     *
     * @return 收货时间
     */
    public Date getGmtReceived(){
      return gmtReceived;
    }

    /**
     * 设置收货时间
     * 
     * @param gmtReceived 要设置的收货时间
     */
    public void setGmtReceived(Date gmtReceived){
      this.gmtReceived = gmtReceived;
    }

    /**
     * 获取物流跟踪信息
     *
     * @return 物流跟踪信息
     */
    public String getTraces(){
      return traces;
    }

    /**
     * 设置物流跟踪信息
     * 
     * @param traces 要设置的物流跟踪信息
     */
    public void setTraces(String traces){
      this.traces = traces;
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