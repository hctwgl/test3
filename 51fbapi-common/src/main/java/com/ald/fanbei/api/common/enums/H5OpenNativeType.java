/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年3月6日下午5:06:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum H5OpenNativeType {
	GoodsInfo("GOODS_DETAIL_INFO", "商品详情"),
	AppLogin("APP_LOGIN", "去登录"),
	DoScanId("DO_SCAN_ID","银行卡认证"),
	DoPromoteBasic("DO_PROMOTE_BASIC","基础认证");
	
	

    private String    code;

    private String name;
    H5OpenNativeType(String code, String name) {
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
