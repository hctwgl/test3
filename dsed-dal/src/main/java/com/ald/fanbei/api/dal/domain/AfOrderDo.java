package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

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
	private BigDecimal borrowAmount;//分期金额（额度支付金额）
	private BigDecimal bankAmount;  //银行支付金额
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
	private Integer  nper;//分期数
	private String riskOrderNo;//风控订单号
	private String borrowRate;//提交订单时的费率信息
	private BigDecimal couponAmount;//优惠金额
	private String interestFreeJson;//免息规则Json字符串
	
	private String consignee;//收件人姓名
	private String address;//收货人地址
	private String consigneeMobile;//收件人电话
	private String invoiceHeader;//发票抬头
	private String logisticsInfo;//物流信息
	private String logisticsCompany;//发货物流公司
	private String logisticsNo;//发货物流单号
	private Date gmtDeliver;//发货时间
	private Date gmtClosed;//关闭时间
	private String preStatus;//状态更新前状态
	private Long goodsPriceId;//商品规格Id
	private String goodsPriceName;//商品规格名字
	
	private BigDecimal lat;//支付时纬度
	private BigDecimal lng;//支付时经度
	private String gpsAddress;//支付时地址
	private String orderStatus;
	private BigDecimal auAmount;//用户的总授信额度
	private BigDecimal usedAmount;//已使用额度
	
	//add time 2017年12月11日16:36:06
	private String province;//收货人省份
	private String city;//收货人所在城市
	private String district;//收货人所在区
	private String blackBox;//同盾设备指纹
	private String ip;//ip地址
	private String bqsBlackBox;//白骑士设备指纹

	private String lc;//订单来源地址
	//addtime 2108年3月22日 scd
    private Integer isHide;//前端是否隐藏

	private String iagentStatus;//智能电核状态
	private String supportCreditStatus;//是否支持信用支付 Y：是 N：否
	private String weakRiskOrderNo;//软弱风控编号
	private String relaOrderNo;//关联订单编号

	private Integer cardType;
	private BigDecimal feeAmount =BigDecimal.ZERO;

	private String goodsStatus;
	private Integer supStatus;

	public Integer getSupStatus() {
		return supStatus;
	}

	public void setSupStatus(Integer supStatus) {
		this.supStatus = supStatus;
	}

	public String getGoodsStatus() {
		return goodsStatus;
	}

	public void setGoodsStatus(String goodsStatus) {
		this.goodsStatus = goodsStatus;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getIagentStatus() {
		return iagentStatus;
	}

	public void setIagentStatus(String iagentStatus) {
		this.iagentStatus = iagentStatus;
	}

	public String getBqsBlackBox() {
		return bqsBlackBox;
	}

	public void setBqsBlackBox(String bqsBlackBox) {
		if(bqsBlackBox.length()>1024){
			bqsBlackBox="";//针对超长字符串进行处理
		}
		this.bqsBlackBox = bqsBlackBox;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getBlackBox() {
		return blackBox;
	}
	public void setBlackBox(String blackBox) {
		if(blackBox.length()>1024){
			blackBox="";//针对超长字符串进行处理
		}
		this.blackBox = blackBox;
	}
	public String getOrderStatus() {
	    return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
	    this.orderStatus = orderStatus;
	}
	public BigDecimal getUsedAmount() {
		return usedAmount;
	}
	public void setUsedAmount(BigDecimal usedAmount) {
		this.usedAmount = usedAmount;
	}
	public Long getGoodsPriceId() {
		return goodsPriceId;
	}
	public BigDecimal getAuAmount() {
		return auAmount;
	}
	public void setAuAmount(BigDecimal auAmount) {
		this.auAmount = auAmount;
	}
	public void setGoodsPriceId(Long goodsPriceId) {
		this.goodsPriceId = goodsPriceId;
	}
	public String getGoodsPriceName() {
		return goodsPriceName;
	}
	public void setGoodsPriceName(String goodsPriceName) {
		this.goodsPriceName = goodsPriceName;
	}
	private String cancelReason; // 取消理由
	private String cancelDetail; // 取消详情
	private String closedReason; // 关闭理由
	private String closedDetail; // 关闭详情
	private String statusRemark; // 订单状态对应描述
	
	public Long getRid() {
		return rid;
	}
	public String getRiskOrderNo() {
		return riskOrderNo;
	}
	public void setRiskOrderNo(String riskOrderNo) {
		this.riskOrderNo = riskOrderNo;
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

	/**
	 * @return the logisticsInfo
	 */
	public String getLogisticsInfo() {
		return logisticsInfo;
	}

	/**
	 * @param logisticsInfo the logisticsInfo to set
	 */
	public void setLogisticsInfo(String logisticsInfo) {
		this.logisticsInfo = logisticsInfo;
	}

	/**
	 * @return the borrowRate
	 */
	public String getBorrowRate() {
		return borrowRate;
	}

	/**
	 * @param borrowRate the borrowRate to set
	 */
	public void setBorrowRate(String borrowRate) {
		this.borrowRate = borrowRate;
	}
	/**
	 * @return the couponAmount
	 */
	public BigDecimal getCouponAmount() {
		return couponAmount;
	}
	/**
	 * @param couponAmount the couponAmount to set
	 */
	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}
	/**
	 * @return the interestFreeJson
	 */
	public String getInterestFreeJson() {
		return interestFreeJson;
	}
	/**
	 * @param interestFreeJson the interestFreeJson to set
	 */
	public void setInterestFreeJson(String interestFreeJson) {
		this.interestFreeJson = interestFreeJson;
	}
	/**
	 * @return the nper
	 */
	public Integer getNper() {
		return nper;
	}
	/**
	 * @param nper the nper to set
	 */
	public void setNper(Integer nper) {
		this.nper = nper;
	}
	public String getPreStatus() {
		return preStatus;
	}
	public void setPreStatus(String preStatus) {
		this.preStatus = preStatus;
	}
	public BigDecimal getBorrowAmount() {
		return borrowAmount;
	}
	public void setBorrowAmount(BigDecimal borrowAmount) {
		this.borrowAmount = borrowAmount;
	}
	public BigDecimal getBankAmount() {
		return bankAmount;
	}
	public void setBankAmount(BigDecimal bankAmount) {
		this.bankAmount = bankAmount;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	public String getCancelDetail() {
		return cancelDetail;
	}
	public void setCancelDetail(String cancelDetail) {
		this.cancelDetail = cancelDetail;
	}
	public String getClosedReason() {
		return closedReason;
	}
	public void setClosedReason(String closedReason) {
		this.closedReason = closedReason;
	}
	public String getClosedDetail() {
		return closedDetail;
	}
	public void setClosedDetail(String closedDetail) {
		this.closedDetail = closedDetail;
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
	public Date getGmtClosed() {
		return gmtClosed;
	}
	public void setGmtClosed(Date gmtClosed) {
		this.gmtClosed = gmtClosed;
	}
	public String getStatusRemark() {
		return statusRemark;
	}
	public void setStatusRemark(String statusRemark) {
		this.statusRemark = statusRemark;
	}
	/**
	 * @return the lat
	 */
	public BigDecimal getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}
	/**
	 * @return the lng
	 */
	public BigDecimal getLng() {
		return lng;
	}
	/**
	 * @param lng the lng to set
	 */
	public void setLng(BigDecimal lng) {
		this.lng = lng;
	}

	public String getGpsAddress() {
		return gpsAddress;
	}

	public void setGpsAddress(String gpsAddress) {
		this.gpsAddress = gpsAddress;
	}

	public String getLc() {
		return lc;
	}

	public void setLc(String lc) {
		this.lc = lc;
	}

	public Integer getIsHide() {
		return isHide;
	}

	public void setIsHide(Integer isHide) {
		this.isHide = isHide;
	}

	public String getSupportCreditStatus() {
		return supportCreditStatus;
	}

	public void setSupportCreditStatus(String supportCreditStatus) {
		this.supportCreditStatus = supportCreditStatus;
	}

	public String getWeakRiskOrderNo() {
		return weakRiskOrderNo;
	}

	public void setWeakRiskOrderNo(String weakRiskOrderNo) {
		this.weakRiskOrderNo = weakRiskOrderNo;
	}

	public String getRelaOrderNo() {
		return relaOrderNo;
	}

	public void setRelaOrderNo(String relaOrderNo) {
		this.relaOrderNo = relaOrderNo;
	}
	
}

