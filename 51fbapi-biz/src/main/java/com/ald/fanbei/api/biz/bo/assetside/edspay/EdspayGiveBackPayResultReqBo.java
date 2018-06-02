package com.ald.fanbei.api.biz.bo.assetside.edspay;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 钱包回传打款结果的请求实体
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018年2月28日下午5:33:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayGiveBackPayResultReqBo implements Serializable {

	private static final long serialVersionUID = 1923132956548193237L;
	
	private Integer debtType;
	private String orderNo;
	private Integer type ;//回传类型 1审核结果，2放款结果
	private Integer code ;//回传结果:0成功1失败
	private String message ;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date loanTime;

	public Date getLoanTime() {
		return loanTime;
	}

	public void setLoanTime(Date loanTime) {
		this.loanTime = loanTime;
	}

	public Integer getDebtType() {
		return debtType;
	}
	public void setDebtType(Integer debtType) {
		this.debtType = debtType;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
