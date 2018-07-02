/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *
 *@类描述：有得卖 回收业务
 *@author weiqingeng 2018年2月27日  09:47:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class AfRecycleDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private String refOrderId;//有得卖对应的订单号
	private Integer payType;//支付方式
	private BigDecimal settlePrice;//结算价格
	private Integer status;//订单状态 1：待确认 2：待上门 3：待检测 6：待发货 7：待收货 8：待支付 66：已完成 98：终止退回 99：已终止

	public String getRefOrderId() {
		return refOrderId;
	}

	public void setRefOrderId(String refOrderId) {
		this.refOrderId = refOrderId;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public BigDecimal getSettlePrice() {
		return settlePrice;
	}

	public void setSettlePrice(BigDecimal settlePrice) {
		this.settlePrice = settlePrice;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}