
package com.ald.fanbei.api.common.exception;

/**
 *@类AppExceptionCode.java 的实现描述：错误枚举类
 *@author 陈金虎 2017年1月16日 下午11:27:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum FanbeiExceptionCode {

    SYSTEM_ERROR("SYSTEM_ERROR", 9999, "system error", "流量过大系统开小差啦，请尝试重新发起"),
    CALCULATE_SHA_256_ERROR("CALCULATE_SHA_256_ERROR",1009,"cal sha 265 error","系统错误"),
    UPS_QUERY_TRADE_ERROR("UPS_QUERY_TRADE_ERROR",1555,"ups query trade error","单笔交易查询失败"),
    BANK_CARD_PAY_ERR("BANK_CARD_PAY_ERR",1563,"bank card pay error","银行卡支付失败"),
    UPS_AUTH_SIGN_ERROR("UPS_AUTH_SIGN_ERROR",1556,"ups auth sign error","签约失败"),
    UPS_QUICKPAY_RESEND_CODE_ERROR("UPS_QUICKPAY_RESEND_CODE_ERROR",1660,"ups quickpay recend code error","快捷支付发送验证码失败"),
    UPS_QUERY_AUTH_SIGN_ERROR("UPS_QUERY_AUTH_SIGN_ERROR",1558,"ups query auth sign error","查询签约验证失败"),
    UPS_COLLECT_ERROR("UPS_COLLECT_ERROR",1560,"ups collect error","单笔代收失败"),
    UPS_CACHE_EXPIRE("UPS_CACHE_EXPIRE",1580,"repayment has expired and closed.","支付请求已经关闭,请重新支付"),
    UPS_QUICK_PAY_CONFIRM_ERROR("UPS_QUICK_PAY_CONFIRM_ERROR",1760,"ups quick pay confirm error","快捷支付确认支付失败"),
    SIGN_RELEASE_ERROR("SIGN_RELEASE_ERROR",1568,"sign release error","银行卡解绑失败"),
    UPS_ORDERNO_BUILD_ERROR("UPS_ORDERNO_BUILD_ERROR",1561,"ups order build error","构建订单错误"),
    LOAN_UPS_DRIECT_FAIL("",2107,"","请求打款实时失败"),
    LOAN_CONCURRENT_LIMIT("",2110,"","同一时刻只能发起一笔贷款申请"),
    LOAN_PERIOD_CAN_NOT_REPAY_ERROR("LOAN_PERIOD_CAN_NOT_REPAY_ERROR",2014,"loan period can not repay error","当前借款未到还款时间"),
    LOAN_REPAY_PROCESS_ERROR("LOAN_REPAY_PROCESS_ERROR",2018,"loan repay not exist","您有一笔还款正在处理中，请稍后重试"),
    UPS_REPEAT_NOTIFY("UPS_REPEAT_NOTIFY",1549,"ups repeat notify","重复UPS回调"),
    UPS_KUAIJIE_NOT_SUPPORT("UPS_KUAIJIE_NOT_SUPPORT",1581,"ups kuaijie not support","当前业务，未支持快捷支付"),
    BORROW_DETAIL_NOT_EXIST_ERROR("BORROW_DETAIL_NOT_EXIST_ERROR",1705,"borrow detail not exist error","借款详情不存在"),
    USER_ACCOUNT_NOT_EXIST_ERROR("USER_ACCOUNT_NOT_EXIST_ERROR",1118,"user not exist error","账户不存在"),
    DSED_BANK_NOT_EXIST_ERROR("DSED_BANK_NOT_EXIST_ERROR",1120,"dsed bank not exist error","绑定银行卡不存在"),
    DSED_BANK_BINDED("DSED_BANK_BINDED",1545,"bind card error","绑定银行卡已占用"),
    AUTH_BINDCARD_ERROR("AUTH_BINDCARD_ERROR",1542,"bind card error","绑定银行卡失败"),
    AUTH_BINDCARD_SMS_ERROR("AUTH_BINDCARD_SMS_ERROR",1567,"auth bindcard sms error","绑卡信息与银行预留不一致，请核实信息后重新尝试"),
    BORROW_CASH_NOT_EXIST_ERROR("BORROW_CASH_NOT_EXIST_ERROR",2002,"borrow cash not exist","借钱信息不存在"),
    LOAN_REPAY_AMOUNT_ERROR("LOAN_REPAY_AMOUNT_ERROR",2013,"loan repay amount error","还款金额有误请重新检查"),
    REQUEST_INVALID_SIGN_ERROR("REQUEST_INVALID_SIGN_ERROR", 1005, "sign is invalid", "非法请求"),
    USER_BANKCARD_NOT_EXIST_ERROR("USER_BANKCARD_NOT_EXIST_ERROR",1302,"user bankcard not exist error","用户银行卡不存在"),
    REQUEST_PARAM_ERROR("REQUEST_PARAM_ERROR", 1006, "request param error", "请求参数不正确"),
    SUCCESS("SUCCESS", 200, "success", "成功"), FAILED("FAILED", 201, "failed", "失败");

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误编号
     */
    private int  errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 错误描述
     */
    private String desc;

    FanbeiExceptionCode(String code, int errorCode, String errorMsg, String desc) {
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

	public void setCode(String code) {
		this.code = code;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
