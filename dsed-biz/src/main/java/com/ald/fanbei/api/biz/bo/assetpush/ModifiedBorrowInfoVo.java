package com.ald.fanbei.api.biz.bo.assetpush;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 退货变更的债权信息vo
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017年12月15日下午4:42:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class ModifiedBorrowInfoVo implements Serializable {
	
	private static final long serialVersionUID = 4204652534348461359L;
	
	private Long userId;//借款人Id
	private String orderNo;//借款订单号
	private Integer isPeriod;//是否分期,(0:未分期[Default],1:分期)
	private List<ModifiedRepaymentPlan> repaymentPlans;//还款计划数组
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getIsPeriod() {
		return isPeriod;
	}
	public void setIsPeriod(Integer isPeriod) {
		this.isPeriod = isPeriod;
	}
	public List<ModifiedRepaymentPlan> getRepaymentPlans() {
		return repaymentPlans;
	}
	public void setRepaymentPlans(List<ModifiedRepaymentPlan> repaymentPlans) {
		this.repaymentPlans = repaymentPlans;
	}
	
}
