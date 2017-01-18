package com.ald.fanbei.api.common.exception;

/**
 * 
 *@类AppExceptionCode.java 的实现描述：错误枚举类
 *@author 陈金虎 2017年1月16日 下午11:27:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum FanbeiExceptionCode {

    SUCCESS("SUCCESS", 1000, "success", "成功"), FAILED("FAILED", 1001, "failed", "失败"),
    
    // PARAM_CODE 1001-1099
    PARAM_ERROR("PARAM_ERROR", 1001, "param error", "参数错误"),
    REQUEST_PARAM_NOT_EXIST("REQUEST_PARAM_NOT_EXIST", 1002, "request param is invalid", "请求参数缺失"),
    REQUEST_PARAM_METHOD_NOT_EXIST("REQUEST_PARAM_METHOD_NOT_EXIST", 1003, "request method is invalid", "请求方法不存在"),
    REQUEST_PARAM_TOKEN_ERROR("REQUEST_PARAM_TOKEN_ERROR", 1004, "token is invalid", "您未登录，请登录"),
    REQUEST_INVALID_SIGN_ERROR("REQUEST_INVALID_SIGN_ERROR", 1005, "sign is invalid", "非法请求"),
    REQUEST_PARAM_ERROR("REQUEST_PARAM_ERROR", 1006, "request param error", "请求参数不正确"),
    REQUEST_PARAM_METHOD_ERROR("REQUEST_PARAM_METHOD_ERROR",1007,"request method param error","请求方法不正确"),
    REQUEST_PARAM_SYSTEM_NOT_EXIST("REQUEST_PARAM_SYSTEM_NOT_EXIST", 1008, "system param is invalid", "系统参数缺失"),
    CALCULATE_SHA_256_ERROR("CALCULATE_SHA_256_ERROR",1009,"cal sha 265 error","系统错误"),
    SYSTEM_REPAIRING_ERROR("SYSTEM_REPAIRING_ERROR",1010,"system repairing","系统维护中"),
    
    // user mode code from 1100 - 1199
    USER_NOT_EXIST_ERROR("USER_NOT_EXIST_ERROR",1100,"user not exist error","用户不存在"),
    USER_INVALID_MOBILE_NO("USER_INVALID_MOBILE_NO",1101,"invalid mobile number","无效手机号"),
    USER_HAS_REGIST_ERROR("USER_HAS_REGIST_ERROR",1102,"user has been regist","该号码已经注册"),
    USER_PASSWORD_ERROR("USER_PASSWORD_ERROR",1103,"user or password error","用户名或密码不正确"),
    USER_PASSWORD_ERROR_GREATER_THAN5("USER_PASSWORD_ERROR_GREATER_THAN5",1104,"user password error count to max","密码错误次数超过限制锁定2小时"),
    USER_REGIST_SMS_NOTEXIST("USER_REGIST_SMS_NOTEXIST",1105,"user regist sms not exist","验证码不正确"),
    USER_REGIST_SMS_ERROR("USER_REGIST_SMS_ERROR",1106,"user regist sms error","验证码不正确"),
    USER_REGIST_SMS_OVERDUE("USER_REGIST_SMS_OVERDUE",1107,"user regist sms overdue","验证码已经过期"),
    USER_SEND_SMS_ERROR("USER_SEND_SMS_ERROR",1108,"user send sms error","用户发送验证码失败"),
    
    // SERVICE 9999
    SYSTEM_ERROR("SYSTEM_ERROR", 9999, "system error", "服务器操作错误");

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误编号
     */
    private int    errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 错误描述
     */
    private String desc;

    private FanbeiExceptionCode(String code, int errorCode, String errorMsg, String desc) {
        this.code = code;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.desc = desc;
    }

    public static FanbeiExceptionCode getByCode(String code) {
        if (code == null || "".equalsIgnoreCase(code)) {
            return null;
        }
        FanbeiExceptionCode[] errorCodes = values();

        for (FanbeiExceptionCode acsErrorCode : errorCodes) {
            if (acsErrorCode.getCode().equals(code)) {
                return acsErrorCode;
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
