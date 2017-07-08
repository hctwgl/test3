package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * '虚拟商品额度记录实体
 * 
 * @author xiaotianjian
 * @version 1.0.0 初始化
 * @date 2017-07-08 14:16:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserVirtualAccountDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 虚拟商品代码
     */
    private String code;

    /**
     * 计算年
     */
    private Integer year;

    /**
     * 计算月
     */
    private Integer month;

    /**
     * 花费金额
     */
    private BigDecimal amount;

    /**
     * 当月总额度
     */
    private BigDecimal totalAmount;


    /**
     * 获取主键Id
     *
     * @return id
     */
    public Long getId(){
      return id;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setId(Long id){
      this.id = id;
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
     * 获取用户id
     *
     * @return 用户id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户id
     * 
     * @param userId 要设置的用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取订单id
     *
     * @return 订单id
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置订单id
     * 
     * @param orderId 要设置的订单id
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
    }

    /**
     * 获取订单编号
     *
     * @return 订单编号
     */
    public String getOrderNo(){
      return orderNo;
    }

    /**
     * 设置订单编号
     * 
     * @param orderNo 要设置的订单编号
     */
    public void setOrderNo(String orderNo){
      this.orderNo = orderNo;
    }

    /**
     * 获取虚拟商品代码
     *
     * @return 虚拟商品代码
     */
    public String getCode(){
      return code;
    }

    /**
     * 设置虚拟商品代码
     * 
     * @param code 要设置的虚拟商品代码
     */
    public void setCode(String code){
      this.code = code;
    }

    /**
     * 获取计算年
     *
     * @return 计算年
     */
    public Integer getYear(){
      return year;
    }

    /**
     * 设置计算年
     * 
     * @param year 要设置的计算年
     */
    public void setYear(Integer year){
      this.year = year;
    }

    /**
     * 获取计算月
     *
     * @return 计算月
     */
    public Integer getMonth(){
      return month;
    }

    /**
     * 设置计算月
     * 
     * @param month 要设置的计算月
     */
    public void setMonth(Integer month){
      this.month = month;
    }

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}


}