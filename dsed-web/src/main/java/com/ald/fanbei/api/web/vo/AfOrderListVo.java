package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月17日下午17:09:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfOrderListVo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private Long orderId;
	private String orderNo;
	private String orderStatus;
	private String secOrderType;
	private String goodsIcon;
	private String goodsName;
	private BigDecimal rebateAmount;
	private BigDecimal saleAmount;
	private Date gmtCreate;
	private String type;
	private BigDecimal actualAmount;
	private BigDecimal couponAmount;
	private String payType;
	private BigDecimal goodsSaleAmount;
	private String orderStatusMsg;
	private String orderStatusRemark;
	private String afterSaleStatus;
	private String isCanApplyAfterSale;
	private String goodsPriceName;
	private String businessIcon;
	private String isCanDelOrder;
	private String businessName;
	private Integer  nper;//分期数
	private String numId;
	private int showLogistics;
	private int count;
	private Date rebateTime;
	
	private String secType;

	private String refundMsg;

	public String getRefundMsg() {
		return refundMsg;
	}

	public void setRefundMsg(String refundMsg) {
		this.refundMsg = refundMsg;
	}

	public String getSecType() {
	    return secType;
	}

	public void setSecType(String secType) {
	    this.secType = secType;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessIcon() {
		return businessIcon;
	}

	public void setBusinessIcon(String businessIcon) {
		this.businessIcon = businessIcon;
	}

	public String getGoodsPriceName() {
		return goodsPriceName;
	}
	public void setGoodsPriceName(String goodsPriceName) {
		this.goodsPriceName = goodsPriceName;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getGoodsIcon() {
		return goodsIcon;
	}
	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	public BigDecimal getActualAmount() {
		return actualAmount;
	}
	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}
	public BigDecimal getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public BigDecimal getGoodsSaleAmount() {
		return goodsSaleAmount;
	}
	public void setGoodsSaleAmount(BigDecimal goodsSaleAmount) {
		this.goodsSaleAmount = goodsSaleAmount;
	}
	public String getOrderStatusMsg() {
		return orderStatusMsg;
	}
	public void setOrderStatusMsg(String orderStatusMsg) {
		this.orderStatusMsg = orderStatusMsg;
	}
	public String getOrderStatusRemark() {
		return orderStatusRemark;
	}
	public void setOrderStatusRemark(String orderStatusRemark) {
		this.orderStatusRemark = orderStatusRemark;
	}
	public String getAfterSaleStatus() {
		return afterSaleStatus;
	}
	public void setAfterSaleStatus(String afterSaleStatus) {
		this.afterSaleStatus = afterSaleStatus;
	}
	public String getIsCanApplyAfterSale() {
		return isCanApplyAfterSale;
	}
	public void setIsCanApplyAfterSale(String isCanApplyAfterSale) {
		this.isCanApplyAfterSale = isCanApplyAfterSale;
	}

	public String getIsCanDelOrder() {
		return isCanDelOrder;
	}

	public void setIsCanDelOrder(String isCanDelOrder) {
		this.isCanDelOrder = isCanDelOrder;
	}

	public Integer getNper() {
		return nper;
	}

	public void setNper(Integer nper) {
		this.nper = nper;
	}

	public String getNumId() {
		return numId;
	}

	public void setNumId(String numId) {
		this.numId = numId;
	}

	public int getShowLogistics() {
		return showLogistics;
	}

	public void setShowLogistics(int showLogistics) {
		this.showLogistics = showLogistics;
	}

	public String getSecOrderType() {
		return secOrderType;
	}

	public void setSecOrderType(String secOrderType) {
		this.secOrderType = secOrderType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	public Date getRebateTime() {
		return rebateTime;
	}

	public void setRebateTime(Date rebateTime) {
		this.rebateTime = rebateTime;
	}
}