package com.ald.fanbei.api.common.exception;


/**
 *@类FanbeiAssetSideRespCode的实现描述：爱上街与资产方响应码及响应说明配置
 *@author wujun 2017年11月29日 上午11:48:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum Auth51FundRespCode {

	SUCCESS(0, "SUCCESS", "成功"), 
	APPLICATION_ERROR(-1, "APPLICATION_ERROR", "系统异常"),
	INVALID_PARAMETER(101, "INVALID_PARAMETER", "参数错误"),
	VALIDATE_TIMESTAMP_ERROR(102," VALIDATE_TIMESTAMP_ERROR","时间戳超时"),
	VALIDATE_APPKEY_ERROR(103,"VALIDATE_APPKEY_ERROR","无效的appKey"),
	SIGN_ERROR(104,"SIGN_ERROR","签名错误");
	
    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误名称
     */
    private String msg;

    /**
     * 错误描述
     */
    private String desc;
    

    private Auth51FundRespCode( Integer code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public static Auth51FundRespCode findByCode(Integer code) {
        for (Auth51FundRespCode respInfo : Auth51FundRespCode.values()) {
            if (respInfo.getCode().equals(code)) {
                return respInfo;
            }
        }
        return null;
    }
    
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
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
