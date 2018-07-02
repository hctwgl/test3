package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * eds每日拉取爱上街的债权金额限制类
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017年12月18日下午1:47:38
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayGetCreditDayLimit implements Serializable{
	private static final long serialVersionUID = 4204652534348461359L;
	
	private BigDecimal borrowDayLimit;//消费分期每日限制金额
	private BigDecimal borrowCashDayLimit;//现金贷，每日限制金额
	public BigDecimal getBorrowDayLimit() {
		return borrowDayLimit;
	}
	public void setBorrowDayLimit(BigDecimal borrowDayLimit) {
		this.borrowDayLimit = borrowDayLimit;
	}
	public BigDecimal getBorrowCashDayLimit() {
		return borrowCashDayLimit;
	}
	public void setBorrowCashDayLimit(BigDecimal borrowCashDayLimit) {
		this.borrowCashDayLimit = borrowCashDayLimit;
	}
	
}
