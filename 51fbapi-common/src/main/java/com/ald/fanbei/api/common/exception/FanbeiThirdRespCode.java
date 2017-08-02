package com.ald.fanbei.api.common.exception;


/**
 *@类FanbeiThirdRespCode的实现描述：返呗与三方联调时，对三方的响应码及响应说明
 *@author 程康 2017年8月2日 下午14:46:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum FanbeiThirdRespCode {

	SUCCESS("200", "success", "操作成功"), 
	FAILED("-100", "failed", "操作失败"),
    
    // PARAM_CODE 100-199
    REQUEST_PARAM_NOT_EXIST("101", "request param is invalid", "请求参数缺失"),
    REQUEST_INVALID_SIGN_ERROR( "102", "sign is invalid", "非法请求"),
    CALCULATE_SHA_256_ERROR("103","system error","系统错误");

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 错误描述
     */
    private String desc;
    

    private FanbeiThirdRespCode( String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public static FanbeiThirdRespCode findByCode(String code) {
        for (FanbeiThirdRespCode respInfo : FanbeiThirdRespCode.values()) {
            if (respInfo.getCode().equals(code)) {
                return respInfo;
            }
        }
        return null;
    }
    
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


}
