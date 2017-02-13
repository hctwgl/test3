package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：还款记录表
 * @author hexin 2017年2月09日下午1:18:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRepaymentDo extends AbstractSerial{

	private static final long serialVersionUID = -3442537058087511444L;

	private Long rid;
	
	private Date gmtCreate;
	
	private Date gmtModified;
	
	private BigDecimal billAmount;//账单金额
	
	private BigDecimal actualAmount;//实际支付金额
	
	private String billIds;//借款账单id，多个逗号隔开
	
	private Long tradeId;//支付交易id
	
	private Long outTradeId;//三方支付id
	
	private String payType;//支付类型【Z:支付宝支付，W:微信支付】
	
	private String userCouponIds;//优惠券id,多个逗号隔开
	
	private BigDecimal couponAmount;//优惠券金额
	
	private BigDecimal rebateAmount;//返现金额

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public BigDecimal getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public String getBillIds() {
		return billIds;
	}

	public void setBillIds(String billIds) {
		this.billIds = billIds;
	}

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public Long getOutTradeId() {
		return outTradeId;
	}

	public void setOutTradeId(Long outTradeId) {
		this.outTradeId = outTradeId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getUserCouponIds() {
		return userCouponIds;
	}

	public void setUserCouponIds(String userCouponIds) {
		this.userCouponIds = userCouponIds;
	}

	public BigDecimal getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}

	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}

	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	
}
