
package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author hexin 2017年2月16日下午15:24:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfOrderDo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4712137543511553120L;
	
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Long userId;
	private String orderNo;
	private String thirdOrderNo;//第三方订单编号
	private String thirdDetailUrl;//第三方订单详情地址
	private String secType;//二级分类
	private String status;
	private Long userCouponId;
	private String orderType;
	private Long goodsId;
	private String openId;
	private String numId;
	private String goodsName;
	private String goodsIcon;
	private Integer count;
	private BigDecimal priceAmount;
	private BigDecimal saleAmount;
	private BigDecimal actualAmount;
	private String shopName;
	private String payStatus;
	private String payType;
	private String payTradeNo;
	private Date gmtPay;
	private String tradeNo;
	private BigDecimal rebateAmount;
	private Date gmtRebated;
	private String mobile;
	private Date gmtFinished;
	private BigDecimal commissionAmount;
	private String source;
	private Long bankId;
	private Date gmtPayEnd;//截止支付时间
	private String serviceProvider;
	private int  nper;//分期数
	private String riskOrderNo;//风控订单号
	
	public Long getRid() {
		return rid;
	}
	public int getNper() {
		return nper;
	}
	public String getRiskOrderNo() {
		return riskOrderNo;
	}
	public void setRiskOrderNo(String riskOrderNo) {
		this.riskOrderNo = riskOrderNo;
	}
	public void setNper(int nper) {
		this.nper = nper;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getUserCouponId() {
		return userCouponId;
	}
	public void setUserCouponId(Long userCouponId) {
		this.userCouponId = userCouponId;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsIcon() {
		return goodsIcon;
	}
	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public BigDecimal getPriceAmount() {
		return priceAmount;
	}
	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
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
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayTradeNo() {
		return payTradeNo;
	}
	public void setPayTradeNo(String payTradeNo) {
		this.payTradeNo = payTradeNo;
	}
	public Date getGmtPay() {
		return gmtPay;
	}
	public void setGmtPay(Date gmtPay) {
		this.gmtPay = gmtPay;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public Date getGmtRebated() {
		return gmtRebated;
	}
	public void setGmtRebated(Date gmtRebated) {
		this.gmtRebated = gmtRebated;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Date getGmtFinished() {
		return gmtFinished;
	}
	public void setGmtFinished(Date gmtFinished) {
		this.gmtFinished = gmtFinished;
	}
	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}
	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Long getBankId() {
		return bankId;
	}
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
	/**
	 * @return the thirdOrderNo
	 */
	public String getThirdOrderNo() {
		return thirdOrderNo;
	}
	/**
	 * @param thirdOrderNo the thirdOrderNo to set
	 */
	public void setThirdOrderNo(String thirdOrderNo) {
		this.thirdOrderNo = thirdOrderNo;
	}
	/**
	 * @return the thirdDetailUrl
	 */
	public String getThirdDetailUrl() {
		return thirdDetailUrl;
	}
	/**
	 * @param thirdDetailUrl the thirdDetailUrl to set
	 */
	public void setThirdDetailUrl(String thirdDetailUrl) {
		this.thirdDetailUrl = thirdDetailUrl;
	}
	/**
	 * @return the gmtPayEnd
	 */
	public Date getGmtPayEnd() {
		return gmtPayEnd;
	}
	/**
	 * @param gmtPayEnd the gmtPayEnd to set
	 */
	public void setGmtPayEnd(Date gmtPayEnd) {
		this.gmtPayEnd = gmtPayEnd;
	}
	/**
	 * @return the secType
	 */
	public String getSecType() {
		return secType;
	}
	/**
	 * @param secType the secType to set
	 */
	public void setSecType(String secType) {
		this.secType = secType;
	}
	/**
	 * @return the numId
	 */
	public String getNumId() {
		return numId;
	}
	/**
	 * @param numId the numId to set
	 */
	public void setNumId(String numId) {
		this.numId = numId;
	}
	/**
	 * @return the serviceProvider
	 */
	public String getServiceProvider() {
		return serviceProvider;
	}
	/**
	 * @param serviceProvider the serviceProvider to set
	 */
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
}

