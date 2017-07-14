package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfpropertyValueVo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3115501453044096948L;
	
	private Long propertyValueId;
	private String propertyValueName;
	public Long getPropertyValueId() {
		return propertyValueId;
	}
	public void setPropertyValueId(Long propertyValueId) {
		this.propertyValueId = propertyValueId;
	}
	public String getPropertyValueName() {
		return propertyValueName;
	}
	public void setPropertyValueName(String propertyValueName) {
		this.propertyValueName = propertyValueName;
	}

}
