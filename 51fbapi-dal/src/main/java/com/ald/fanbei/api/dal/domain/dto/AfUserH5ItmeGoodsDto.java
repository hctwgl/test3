/**
 * 
 */
package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年3月2日下午9:05:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserH5ItmeGoodsDto extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long rid;

	private String thumbnailIcon;
	private String goodsUrl;
	private String openId;
	private String goodsId;

	private String numId;
	private BigDecimal priceAmount;
	private BigDecimal saleAmount;
	private String source;

	
	private BigDecimal rebateAmount;
	private BigDecimal rebateRate;
	private String name;
	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}
	/**
	 * @return the thumbnailIcon
	 */
	public String getThumbnailIcon() {
		return thumbnailIcon;
	}
	/**
	 * @param thumbnailIcon the thumbnailIcon to set
	 */
	public void setThumbnailIcon(String thumbnailIcon) {
		this.thumbnailIcon = thumbnailIcon;
	}
	/**
	 * @return the goodsUrl
	 */
	public String getGoodsUrl() {
		return goodsUrl;
	}
	/**
	 * @param goodsUrl the goodsUrl to set
	 */
	public void setGoodsUrl(String goodsUrl) {
		this.goodsUrl = goodsUrl;
	}
	/**
	 * @return the openId
	 */
	public String getOpenId() {
		return openId;
	}
	/**
	 * @param openId the openId to set
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * @return the goodsId
	 */
	public String getGoodsId() {
		return goodsId;
	}
	/**
	 * @param goodsId the goodsId to set
	 */
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
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
	 * @return the priceAmount
	 */
	public BigDecimal getPriceAmount() {
		return priceAmount;
	}
	/**
	 * @param priceAmount the priceAmount to set
	 */
	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}
	/**
	 * @return the saleAmount
	 */
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	/**
	 * @param saleAmount the saleAmount to set
	 */
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the rebateAmount
	 */
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	/**
	 * @param rebateAmount the rebateAmount to set
	 */
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	/**
	 * @return the rebateRate
	 */
	public BigDecimal getRebateRate() {
		return rebateRate;
	}
	/**
	 * @param rebateRate the rebateRate to set
	 */
	public void setRebateRate(BigDecimal rebateRate) {
		this.rebateRate = rebateRate;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
