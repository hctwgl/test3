/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年2月23日下午9:50:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum H5ItemModelType {
	
	BANNELl("BANNELl", "导航"),
	SUBJECT("SUBJECT", "主题"),
	CATEGORY("CATEGORY", "类别"),
	IMAGE("IMAGE", "图片"),
	GOODSLIST("GOODSLIST", "商品");
	 private String    code;

    private String name;
    H5ItemModelType(String code, String name) {
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
