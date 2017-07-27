package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

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
