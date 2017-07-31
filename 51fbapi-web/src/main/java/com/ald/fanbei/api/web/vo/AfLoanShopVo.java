package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
/**
 * 
 * <p>Title:AfLoanShopVo <p>
 * <p>Description: <p>
 * @Copyright (c)  浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 * @author qiao
 * @date 2017年7月28日下午3:25:01
 *
 */
public class AfLoanShopVo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6521223382149167673L;
	private String lsmNo;
	private String lsmName;
	private String lsmIntro;
	private String linkUrl;
	private String label;
	private String iconUrl;
	private String marketPoint;
	
	public String getMarketPoint() {
		return marketPoint;
	}

	public void setMarketPoint(String marketPoint) {
		this.marketPoint = marketPoint;
	}

	public String getLsmNo() {
		return lsmNo;
	}

	public void setLsmNo(String lsmNo) {
		this.lsmNo = lsmNo;
	}

	public String getLsmName() {
		return lsmName;
	}

	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}

	public String getLsmIntro() {
		return lsmIntro;
	}

	public void setLsmIntro(String lsmIntro) {
		this.lsmIntro = lsmIntro;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}
