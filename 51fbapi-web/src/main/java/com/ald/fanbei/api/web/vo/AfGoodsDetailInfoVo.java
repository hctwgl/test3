package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.List;

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
	private String goodsDetail;
	private Integer saleCount;
	private List<String> goodsPics;
	
	
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
	public String getGoodsDetail() {
		return goodsDetail;
	}
	public void setGoodsDetail(String goodsDetail) {
		this.goodsDetail = goodsDetail;
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
	
}
