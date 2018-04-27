package com.ald.fanbei.api.web.vo;

import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.dto.AfBrandDto;

public class AfBrandListVo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;
	private List<AfBrandDto> brandsList;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<AfBrandDto> getBrandsList() {
		return brandsList;
	}
	public void setBrandsList(List<AfBrandDto> brandsList) {
		this.brandsList = brandsList;
	}
	public AfBrandListVo(String key, List<AfBrandDto> brandsList) {
		super();
		this.key = key;
		this.brandsList = brandsList;
	}
	public AfBrandListVo() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
