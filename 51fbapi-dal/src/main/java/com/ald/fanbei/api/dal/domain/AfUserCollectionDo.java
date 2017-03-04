/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年2月25日下午2:13:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserCollectionDo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private Long userId;
	private Long goodsId;
	private String goodsName;
	private String goodsIcon;
	private String openId;
	private String goodsUrl;
	private BigDecimal priceAmount;
	private String actualAmount;
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
	 * @return the gmtCreate
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * @param gmtCreate the gmtCreate to set
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	/**
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}
	/**
	 * @param gmtModified the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the goodsId
	 */
	public Long getGoodsId() {
		return goodsId;
	}
	/**
	 * @param goodsId the goodsId to set
	 */
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	/**
	 * @return the goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}
	/**
	 * @param goodsName the goodsName to set
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	/**
	 * @return the goodsIcon
	 */
	public String getGoodsIcon() {
		return goodsIcon;
	}
	/**
	 * @param goodsIcon the goodsIcon to set
	 */
	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
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
	 * @return the actualAmount
	 */
	public String getActualAmount() {
		return actualAmount;
	}
	/**
	 * @param actualAmount the actualAmount to set
	 */
	public void setActualAmount(String actualAmount) {
		this.actualAmount = actualAmount;
	}

	
}
