package com.ald.fanbei.api.common.enums;

/**
 * @author chengkang
 * 商品区分类型，现主要针对于 普通商品和权限包vip商品
 */
public enum AfGoodsSpecType {
	COMMON("COMMON", "普通商品"),
	AUTH("AUTH", "权限包vip商品");

	private String code;
    private String name;
    
    
    AfGoodsSpecType(String code, String name) {
        this.code = code;
        this.name = name;
    }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
