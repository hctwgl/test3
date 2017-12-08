package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author hexin 2017年2月17日上午9:47:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGoodsDo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5566947920375407089L;

	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String name;
	private String goodsIcon;
	private String thumbnailIcon;
	private String goodsUrl;
	private String openId;
	private String numId;
	private BigDecimal priceAmount;
	private BigDecimal saleAmount;
	private Long categoryId;
	private Integer saleCount;
	private Date gmtStart;
	private Date gmtEnd;
	private String status;
	private String source;
	private BigDecimal rebateAmount;
	private BigDecimal rebateRate;
	private String shopName;
	private BigDecimal realAmount;//到手价
	private String goodsPic1;
	private String goodsPic2;
	private String goodsPic3;
	private String goodsPic4;
	private String goodsDetail;
	private String remark;
	private String tags;//免息活动标签
	
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
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getNumId() {
		return numId;
	}
	public void setNumId(String numId) {
		this.numId = numId;
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
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public Integer getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}
	public Date getGmtStart() {
		return gmtStart;
	}
	public void setGmtStart(Date gmtStart) {
		this.gmtStart = gmtStart;
	}
	public Date getGmtEnd() {
		return gmtEnd;
	}
	public void setGmtEnd(Date gmtEnd) {
		this.gmtEnd = gmtEnd;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public BigDecimal getRebateRate() {
		return rebateRate;
	}
	public void setRebateRate(BigDecimal rebateRate) {
		this.rebateRate = rebateRate;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getThumbnailIcon() {
		return thumbnailIcon;
	}
	public void setThumbnailIcon(String thumbnailIcon) {
		this.thumbnailIcon = thumbnailIcon;
	}
	public BigDecimal getRealAmount() {
		return realAmount;
	}
	public void setRealAmount(BigDecimal realAmount) {
		this.realAmount = realAmount;
	}
	public String getGoodsPic1() {
		return goodsPic1;
	}
	public void setGoodsPic1(String goodsPic1) {
		this.goodsPic1 = goodsPic1;
	}
	public String getGoodsPic2() {
		return goodsPic2;
	}
	public void setGoodsPic2(String goodsPic2) {
		this.goodsPic2 = goodsPic2;
	}
	public String getGoodsPic3() {
		return goodsPic3;
	}
	public void setGoodsPic3(String goodsPic3) {
		this.goodsPic3 = goodsPic3;
	}
	public String getGoodsPic4() {
		return goodsPic4;
	}
	public void setGoodsPic4(String goodsPic4) {
		this.goodsPic4 = goodsPic4;
	}
	public String getGoodsDetail() {
		return goodsDetail;
	}
	public void setGoodsDetail(String goodsDetail) {
		this.goodsDetail = goodsDetail;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}
	/**
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}
	
}
