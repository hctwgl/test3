package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年3月3日上午11:49:41
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGoodsDetailInfoVo extends AbstractSerial{

	private static final long serialVersionUID = -7150400787252733902L;

	private Long goodsId;
	private String numId;
	private String openId;
	private BigDecimal saleAmount;
	private String realAmount;
	private String rebateAmount;
	private String goodsName;
	private String goodsIcon;
	private String goodsUrl;
	private String source;
	private List<GoodsDetailPicInfoVo> goodsDetail;
	private Integer saleCount;
	private List<String> goodsPics;
	private Map<String,Object> nperMap;
	private  Integer limitedPurchase;
	private Long activityId;
	private Integer activityType;
	private String activityName;
	private Long gmtStart;
	private Long gmtEnd;
	private Long gmtPstart;
	private Long nowDate;
	private Integer limitCount;
	private Integer goodsLimitCount;
	private String payType;
	private Integer actSaleCount;
	private String remark;
	private BigDecimal specialPrice;
	private List<Map<String, Object>> nperList;
	
	private String interestCutDesc;
	private String interestFreeDesc;
	private String interestCutMark;
	private String interestFreeMark;
	private int isShow=0;
	private String goodsType; // 商品类型(common ,auth 与权限包区别)

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getInterestCutMark() {
		return interestCutMark;
	}

	public void setInterestCutMark(String interestCutMark) {
		this.interestCutMark = interestCutMark;
	}

	public String getInterestFreeMark() {
		return interestFreeMark;
	}

	public void setInterestFreeMark(String interestFreeMark) {
		this.interestFreeMark = interestFreeMark;
	}

	public String getInterestCutDesc() {
		return interestCutDesc;
	}
	public void setInterestCutDesc(String interestCutDesc) {
		this.interestCutDesc = interestCutDesc;
	}
	public String getInterestFreeDesc() {
		return interestFreeDesc;
	}
	public void setInterestFreeDesc(String interestFreeDesc) {
		this.interestFreeDesc = interestFreeDesc;
	}
	public int getIsShow() {
		return isShow;
	}
	public void setIsShow(int isShow) {
		this.isShow = isShow;
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
	public String getGoodsUrl() {
		return goodsUrl;
	}
	public void setGoodsUrl(String goodsUrl) {
		this.goodsUrl = goodsUrl;
	}
	public String getNumId() {
		return numId;
	}
	public void setNumId(String numId) {
		this.numId = numId;
	}
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	public String getRealAmount() {
		return realAmount;
	}
	public void setRealAmount(String realAmount) {
		this.realAmount = realAmount;
	}
	public String getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(String rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public Integer getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}
	public List<String> getGoodsPics() {
		return goodsPics;
	}
	public void setGoodsPics(List<String> goodsPics) {
		this.goodsPics = goodsPics;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public List<GoodsDetailPicInfoVo> getGoodsDetail() {
		return goodsDetail;
	}
	public void setGoodsDetail(List<GoodsDetailPicInfoVo> goodsDetail) {
		this.goodsDetail = goodsDetail;
	}
	public Map<String, Object> getNperMap() {
		return nperMap;
	}
	public void setNperMap(Map<String, Object> nperMap) {
		this.nperMap = nperMap;
	}

	public Integer getLimitedPurchase() {
		return limitedPurchase;
	}

	public void setLimitedPurchase(Integer limitedPurchase) {
		this.limitedPurchase = limitedPurchase;
	}

	public Integer getActivityType() {
		return activityType;
	}

	public void setActivityType(Integer activityType) {
		this.activityType = activityType;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Integer getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(Integer limitCount) {
		this.limitCount = limitCount;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Integer getGoodsLimitCount() {
		return goodsLimitCount;
	}

	public void setGoodsLimitCount(Integer goodsLimitCount) {
		this.goodsLimitCount = goodsLimitCount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Integer getActSaleCount() {
		return actSaleCount;
	}

	public void setActSaleCount(Integer actSaleCount) {
		this.actSaleCount = actSaleCount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(BigDecimal specialPrice) {
		this.specialPrice = specialPrice;
	}

	public List<Map<String, Object>> getNperList() {
		return nperList;
	}

	public void setNperList(List<Map<String, Object>> nperList) {
		this.nperList = nperList;
	}

	public Long getGmtStart() {
		return gmtStart;
	}

	public void setGmtStart(Long gmtStart) {
		this.gmtStart = gmtStart;
	}

	public Long getGmtEnd() {
		return gmtEnd;
	}

	public void setGmtEnd(Long gmtEnd) {
		this.gmtEnd = gmtEnd;
	}

	public Long getGmtPstart() {
		return gmtPstart;
	}

	public void setGmtPstart(Long gmtPstart) {
		this.gmtPstart = gmtPstart;
	}

	public Long getNowDate() {
		return nowDate;
	}

	public void setNowDate(Long nowDate) {
		this.nowDate = nowDate;
	}
}
