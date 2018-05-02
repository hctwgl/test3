package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfBrandDo;
/**
 * 品牌dto对象，增加品牌名称首字母字段
 * @author liutengyuan 
 * @date 2018年4月12日
 */
public class AfBrandDto extends AfBrandDo {

	private static final long serialVersionUID = 1L;
	
	private String nameIndex;

	public String getNameIndex() {
		return nameIndex;
	}

	public void setNameIndex(String nameIndex) {
		this.nameIndex = nameIndex;
	}
}
