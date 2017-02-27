package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;

/**
 * 
 * @类描述：借款账单关联表
 * @author hexin 2017年2月25日下午17:07:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowBillDto extends AfBorrowBillDo {
	
	private static final long serialVersionUID = -7305207059853020128L;
	
	private String orderNo;//订单编号

	private Long orderId;//订单id

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
}
