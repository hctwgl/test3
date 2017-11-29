package com.ald.fanbei.api.common.exception;


/**
 *@类FanbeiAssetSideRespCode的实现描述：返呗与资产方响应码及响应说明配置
 *@author 程康 2017年11月29日 上午11:48:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum FanbeiAssetSideRespCode {

	SUCCESS(200, "SUCCESS", "成功"), 
	VALIDATE_SIGNATURE_ERROR(101, "VALIDATE_SIGNATURE_ERROR", "签名验证失败"),
	VALIDATE_APPID_ERROR( 102, "VALIDATE_APPID_ERROR", "AppID校验失败"),
	VALIDATE_TIMESTAMP_ERROR(103," VALIDATE_TIMESTAMP_ERROR","时间戳过期"),
	COMPUTE_SIGNATURE_ERROR(104,"COMPUTE_SIGNATURE_ERROR","生成签名失败"),
	ENCRYPT_AES_ERROR(105,"ENCRYPT_AES_ERROR","加密失败"),
	DECRYPT_AES_ERROR(106,"DECRYPT_AES_ERROR","解密失败"),
	INVALID_PARAMETER(107, "INVALID_PARAMETER", "请求参数出错"),
	GEN_RETURN_MSG_ERROR(108, "GEN_RETURN_MSG_ERROR", "生成返回包失败"),
	PARSE_JSON_ERROR(109, "PARSE_JSON_ERROR", "JSON反序列化出错"),
	APPLICATION_ERROR(500, "APPLICATION_ERROR", "系统异常");

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
    

    private FanbeiAssetSideRespCode( Integer code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public static FanbeiAssetSideRespCode findByCode(Integer code) {
        for (FanbeiAssetSideRespCode respInfo : FanbeiAssetSideRespCode.values()) {
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
