/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *
 *@类描述：有得卖 回收业务 页面访问统计
 *@author weiqingeng 2018年2月27日  09:47:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRecycleViewStatisticsDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;

	private Integer uv;//访问用户数
	private Integer pv;//访问次数
	private Integer orderQty;//订单数
	private Integer returnAmount;//返现总额

	public Integer getUv() {
		return uv;
	}

	public void setUv(Integer uv) {
		this.uv = uv;
	}

	public Integer getPv() {
		return pv;
	}

	public void setPv(Integer pv) {
		this.pv = pv;
	}

	public Integer getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(Integer orderQty) {
		this.orderQty = orderQty;
	}

	public Integer getReturnAmount() {
		return returnAmount;
	}

	public void setReturnAmount(Integer returnAmount) {
		this.returnAmount = returnAmount;
	}
}