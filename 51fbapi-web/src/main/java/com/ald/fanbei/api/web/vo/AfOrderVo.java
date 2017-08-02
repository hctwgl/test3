package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月17日下午16:26:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfOrderVo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private String orderNo;
	private String type;
	private Date gmtCreate;
	private Date gmtFinished;
	private Date gmtRebated;
	private Date gmtClosed;
	private String orderStatus;
	private String goodsIcon;
	private String goodsName;
	private String goodsPriceName;
	public String getGoodsPriceName() {
		return goodsPriceName;
	}
	public void setGoodsPriceName(String goodsPriceName) {
		this.goodsPriceName = goodsPriceName;
	}
	private Integer goodsCount;
	private BigDecimal orderAmount;
	private BigDecimal rebateAmount;
	private BigDecimal actualAmount;
	private BigDecimal couponAmount;
	private String mobile;
	
	private String gmtPay;
	private String consignee;
	private String address;
	private String consigneeMobile;
	private String invoiceHeader;
	private String logisticsInfo;
	private String payType;
	private BigDecimal goodsSaleAmount;
	private String orderStatusMsg;
	private String orderStatusRemark;
	private String afterSaleStatus;
	private Date gmtRefundApply;
	private Date gmtPayStart;
	private Date gmtPayEnd;
	private String isCanApplyAfterSale;
	private String logisticsCompany;//发货物流公司
	private String logisticsNo;//发货物流单号
	private Date gmtDeliver;//发货时间

	private String businessName; //商家名称
	private String businessIcon; //商家图片
	private BigDecimal nperAmount; //每期金额
	private Integer nper; //分期数
	private String installmentInfo;//分期详情
	private Long goodsId;
	private String isCanDelOrder;

	private String refundTime; //退款时间
	private BigDecimal refundActualAmount; //实际退款金额
	private String refundContent; //退款原因
	
	private BigDecimal quotaAmount;
	private BigDecimal bankPayAmount;
	
	public String getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(String refundTime) {
		this.refundTime = refundTime;
	}

	public BigDecimal getRefundActualAmount() {
		return refundActualAmount;
	}

	public void setRefundActualAmount(BigDecimal refundActualAmount) {
		this.refundActualAmount = refundActualAmount;
	}

	public String getRefundContent() {
		return refundContent;
	}

	public void setRefundContent(String refundContent) {
		this.refundContent = refundContent;
	}

	public String getBusinessIcon() {
		return businessIcon;
	}

	public void setBusinessIcon(String businessIcon) {
		this.businessIcon = businessIcon;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public BigDecimal getNperAmount() {
		return nperAmount;
	}

	public void setNperAmount(BigDecimal nperAmount) {
		this.nperAmount = nperAmount;
	}

	public Integer getNper() {
		return nper;
	}

	public void setNper(Integer nper) {
		this.nper = nper;
	}

	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtFinished() {
		return gmtFinished;
	}
	public void setGmtFinished(Date gmtFinished) {
		this.gmtFinished = gmtFinished;
	}
	public Date getGmtRebated() {
		return gmtRebated;
	}
	public void setGmtRebated(Date gmtRebated) {
		this.gmtRebated = gmtRebated;
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
	public Integer getGoodsCount() {
		return goodsCount;
	}
	public void setGoodsCount(Integer goodsCount) {
		this.goodsCount = goodsCount;
	}
	public BigDecimal getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public Date getGmtClosed() {
		return gmtClosed;
	}
	public void setGmtClosed(Date gmtClosed) {
		this.gmtClosed = gmtClosed;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getConsignee() {
		return consignee;
	}
	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getConsigneeMobile() {
		return consigneeMobile;
	}
	public void setConsigneeMobile(String consigneeMobile) {
		this.consigneeMobile = consigneeMobile;
	}
	public String getInvoiceHeader() {
		return invoiceHeader;
	}
	public void setInvoiceHeader(String invoiceHeader) {
		this.invoiceHeader = invoiceHeader;
	}
	public String getGmtPay() {
		return gmtPay;
	}
	public void setGmtPay(String gmtPay) {
		this.gmtPay = gmtPay;
	}
	public String getLogisticsInfo() {
		return logisticsInfo;
	}
	public void setLogisticsInfo(String logisticsInfo) {
		this.logisticsInfo = logisticsInfo;
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
	public Date getGmtRefundApply() {
		return gmtRefundApply;
	}
	public void setGmtRefundApply(Date gmtRefundApply) {
		this.gmtRefundApply = gmtRefundApply;
	}
	public Date getGmtPayEnd() {
		return gmtPayEnd;
	}
	public void setGmtPayEnd(Date gmtPayEnd) {
		this.gmtPayEnd = gmtPayEnd;
	}
	public Date getGmtPayStart() {
		return gmtPayStart;
	}
	public void setGmtPayStart(Date gmtPayStart) {
		this.gmtPayStart = gmtPayStart;
	}
	public String getIsCanApplyAfterSale() {
		return isCanApplyAfterSale;
	}
	public void setIsCanApplyAfterSale(String isCanApplyAfterSale) {
		this.isCanApplyAfterSale = isCanApplyAfterSale;
	}
	public String getLogisticsCompany() {
		return logisticsCompany;
	}
	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}
	public String getLogisticsNo() {
		return logisticsNo;
	}
	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}
	public Date getGmtDeliver() {
		return gmtDeliver;
	}
	public void setGmtDeliver(Date gmtDeliver) {
		this.gmtDeliver = gmtDeliver;
	}

	public String getInstallmentInfo() {
		return installmentInfo;
	}

	public void setInstallmentInfo(String installmentInfo) {
		this.installmentInfo = installmentInfo;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getIsCanDelOrder() {
		return isCanDelOrder;
	}

	public void setIsCanDelOrder(String isCanDelOrder) {
		this.isCanDelOrder = isCanDelOrder;
	}
	public BigDecimal getQuotaAmount() {
		return quotaAmount;
	}
	public void setQuotaAmount(BigDecimal quotaAmount) {
		this.quotaAmount = quotaAmount;
	}
	public BigDecimal getBankPayAmount() {
		return bankPayAmount;
	}
	public void setBankPayAmount(BigDecimal bankPayAmount) {
		this.bankPayAmount = bankPayAmount;
	}
	
}
