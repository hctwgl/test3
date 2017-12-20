package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.dto.BoluomeCouponDto;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 新人专享实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-12-13 10:51:09
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeShoujiDo extends BoluomeCouponDto {

    /** 
    * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
    */
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
     * 充值号码归属地
     */
    private String area;

    /**
     * 充值号码运营商
     */
    private String isp;

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
     * 订单类型名称
     */
    private String orderTypeName;

    /**
     * 充值号码
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
     * 获取第三方订单号
     *
     * @return 第三方订单号
     */
    @JSONField(name="id")
    public String getThirdOrderNo(){
      return thirdOrderNo;
    }

    /**
     * 设置第三方订单号
     * 
     * @param thirdOrderNo 要设置的第三方订单号
     */
    @JSONField(name="id")
    public void setThirdOrderNo(String thirdOrderNo){
      this.thirdOrderNo = thirdOrderNo;
    }

    /**
     * 获取充值号码归属地
     *
     * @return 充值号码归属地
     */
    public String getArea(){
      return area;
    }

    /**
     * 设置充值号码归属地
     * 
     * @param area 要设置的充值号码归属地
     */
    public void setArea(String area){
      this.area = area;
    }

    /**
     * 获取充值号码运营商
     *
     * @return 充值号码运营商
     */
    public String getIsp(){
      return isp;
    }

    /**
     * 设置充值号码运营商
     * 
     * @param isp 要设置的充值号码运营商
     */
    public void setIsp(String isp){
      this.isp = isp;
    }

    /**
     * 获取商品名称
     *
     * @return 商品名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置商品名称
     * 
     * @param name 要设置的商品名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取订单金额
     *
     * @return 订单金额
     */
    public BigDecimal getOrderPrice(){
      return orderPrice;
    }

    /**
     * 设置订单金额
     * 
     * @param orderPrice 要设置的订单金额
     */
    public void setOrderPrice(BigDecimal orderPrice){
      this.orderPrice = orderPrice;
    }

    /**
     * 获取订单类型
     *
     * @return 订单类型
     */
    public String getOrderType(){
      return orderType;
    }

    /**
     * 设置订单类型
     * 
     * @param orderType 要设置的订单类型
     */
    public void setOrderType(String orderType){
      this.orderType = orderType;
    }

    /**
     * 获取订单类型名称
     *
     * @return 订单类型名称
     */
    public String getOrderTypeName(){
      return orderTypeName;
    }

    /**
     * 设置订单类型名称
     * 
     * @param orderTypeName 要设置的订单类型名称
     */
    public void setOrderTypeName(String orderTypeName){
      this.orderTypeName = orderTypeName;
    }

    /**
     * 获取充值号码
     *
     * @return 充值号码
     */
    public String getPhone(){
      return phone;
    }

    /**
     * 设置充值号码
     * 
     * @param phone 要设置的充值号码
     */
    public void setPhone(String phone){
      this.phone = phone;
    }

    /**
     * 获取支付金额
     *
     * @return 支付金额
     */
    public BigDecimal getPrice(){
      return price;
    }

    /**
     * 设置支付金额
     * 
     * @param price 要设置的支付金额
     */
    public void setPrice(BigDecimal price){
      this.price = price;
    }

    /**
     * 获取登录用户id
     *
     * @return 登录用户id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置登录用户id
     * 
     * @param userId 要设置的登录用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取登录用户名
     *
     * @return 登录用户名
     */
    public String getUserPhone(){
      return userPhone;
    }

    /**
     * 设置登录用户名
     * 
     * @param userPhone 要设置的登录用户名
     */
    public void setUserPhone(String userPhone){
      this.userPhone = userPhone;
    }

}