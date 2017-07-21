package com.ald.fanbei.api.web.vo;

import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfPropertyVo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3138182473112979555L;
	
	private Long propertyId;
	private String propertyName;
	private List<AfpropertyValueVo> propertyValues ;
	
	public Long getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public List<AfpropertyValueVo> getPropertyValues() {
		return propertyValues;
	}
	public void setPropertyValues(List<AfpropertyValueVo> propertyValues) {
		this.propertyValues = propertyValues;
	}

}
