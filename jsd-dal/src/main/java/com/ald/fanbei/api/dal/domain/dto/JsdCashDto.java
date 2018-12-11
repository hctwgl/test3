/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain.dto;


import java.math.BigDecimal;
import java.sql.Date;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

/**
 * 
 * @类描述：
 * @author wujun
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class JsdCashDto extends JsdBorrowCashDo {

	private static final long serialVersionUID = -3162832676757115210L;

	private String orderNo;// 商品订单号
	private BigDecimal priceAmount;//订单价格
	private String goodsName;//商品名称
	private String deliveryUser;//收件人姓名
	private String deliveryPhone;//收货手机号
	private String address;//收货地址

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getDeliveryUser() {
		return deliveryUser;
	}

	public void setDeliveryUser(String deliveryUser) {
		this.deliveryUser = deliveryUser;
	}

	public String getDeliveryPhone() {
		return deliveryPhone;
	}

	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
