package com.ald.fanbei.api.web.validator.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.stereotype.Component;

@Component("applyLegalBorrowCashParam")
public class ApplyLegalBorrowCashParam {
	
	@NotNull
	@Pattern(regexp="^([1-9]\\d{0,9}|0)([.]?|(\\.\\d{1,2})?)$")
	private String amount;
	@NotNull
	
	private String pwd;
	@NotNull
	private String type;
	@NotNull
	private String latitude;
	@NotNull
	private String longitude;
	@NotNull
	private String province;
	@NotNull
	private String city;
	@NotNull
	private String county;
	@NotNull
	private String address;
	@NotNull
	private String blackBox;
	@NotNull
	private String borrowRemark;
	@NotNull
	private String refundRemark;
	@NotNull
	private String goodsId;
	@NotNull
	private String goodsName;
	@NotNull
	private String goodsAmount;
	@NotNull
	private String deliveryAddress;
	@NotNull
	private String deliveryUser;
	@NotNull
	private String deliveryPhone;

}
