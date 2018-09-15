
package com.ald.fanbei.api.common.exception;

import org.apache.commons.lang.StringUtils;

/**
 *@类AppExceptionCode.java 的实现描述：错误枚举类
 *@author 陈金虎 2017年1月16日 下午11:27:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum BizExceptionCode {

    JSD_PARAMS_ERROR("JSD_PARAMS_ERROR", 9999, "params error", "参数错误"),
    JSD_REPAY_REPAY_ERROR("JSD_REPAY_REPAY_ERROR",2018,"loan repayNo is exist","支付编号已存在"),
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
    BORROW_STATS_IS_NOT_TRANSFERRED("BORROW_STATS_IS_NOT_TRANSFERRED",1540,"borrow stats is not transferred","借款正在审核中"),
    
    LOAN_UPS_DRIECT_FAIL("LOAN_UPS_DRIECT_FAIL",2107,"loan ups driect fail","请求打款实时失败"),
    LOAN_CONCURRENT_LIMIT("LOAN_CONCURRENT_LIMIT",2110,"loan concurrent limit","同一时刻只能发起一笔贷款申请"),
    LOAN_PERIOD_CAN_NOT_REPAY_ERROR("LOAN_PERIOD_CAN_NOT_REPAY_ERROR",2014,"loan period can not repay error","当前借款未到还款时间"),
    LOAN_REPAY_PROCESS_ERROR("LOAN_REPAY_PROCESS_ERROR",2018,"loan repay not exist","您有一笔还款正在处理中，请稍后重试"),

    BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR("BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR",2020,"borrow cash repay more than borrow cash","还款金额大于借款金额"),

    HAVE_A_PROCESS_RENEWAL_DETAIL("HAVE_A_PROCESS_RENEWAL_DETAIL",2021,"borrow a process renewal record","存在处理中续期记录,请稍后重试"),

    LEGAL_REPAY_PROCESS_ERROR("LEGAL_REPAY_PROCESS_ERROR",2122,"legal order repay not exist","您有一笔商品还款正在处理中，请稍后重试"),

    JSD_BORROW_IS_NULL("JSD_BORROW_IS_NULL", 2126, "borrow is null", "借款信息是空"),


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
    USER_NOT_EXIST_ERROR("USER_NOT_EXIST_ERROR",1303,"user not exist error","用户不存在"),
    REQUEST_PARAM_ERROR("REQUEST_PARAM_ERROR", 1006, "request param error", "请求参数不正确"),
    SUCCESS("SUCCESS", 200, "success", "成功"), FAILED("FAILED", 201, "failed", "失败"),
    BANK_LIMIT_MONEY("BANK_LIMIT_MONEY",1324,"bank limit money","该银行单笔限额，请使用其他银行卡还款，谢谢！"),
    BORROW_CASH_REPAY_REPEAT_ERROR("BORROW_CASH_REPAY_REPEAT_ERROR",2004,"borrow cash repay repeat","重复的还款操作"),
    PERSON_SEAL_CREATE_FAILED("PERSON_SEAL_CREATE_FAILED",4100,"person_seal_create_failed","个人印章创建失败"),
    COMPANY_SEAL_CREATE_FAILED("COMPANY_SEAL_CREATE_FAILED",4101,"COMPANY_SEAL_CREATE_FAILED","公司印章创建失败"),
    CONTRACT_CREATE_FAILED("CONTRACT_CREATE_FAILED",4103,"contract_create_failed","合同生成失败"),
    CONTRACT_NOT_FIND("CONTRACT_NOT_FIND",4104,"contract_not_find","合同不存在"),
    COMPANY_SIGN_ACCOUNT_CREATE_FAILED("COMPANY_SIGN_ACCOUNT_CREATE_FAILED",4102,"company_sign_account_create_failed","e签宝账户创建失败"),
    UPS_ERROR_0000("UPS_ERROR_0000",0000,"","请求成功" ),
    UPS_ERROR_0001("UPS_ERROR_0001",0001,"","交易处理中，您可稍后查询"),
    UPS_ERROR_1001("UPS_ERROR_1001",1001,"","系统正忙，您可联系客服或稍后重试"),
    UPS_ERROR_2001("UPS_ERROR_2001",2001,"","银行维护中"),
    UPS_ERROR_4001("UPS_ERROR_4001",4001,"","卡余额不足，您可换卡重试"),
    UPS_ERROR_4002("UPS_ERROR_4002",4002,"","余额不足次数超限，您可换卡或次日重试"),
    UPS_ERROR_4003("UPS_ERROR_4003",4003,"","银行卡单笔金额超限，您可换卡重试"),
    UPS_ERROR_4004("UPS_ERROR_4004",4004,"","银行卡单日金额超限，您可换卡重试"),
    UPS_ERROR_4005("UPS_ERROR_4005",4005,"","金额小于银行最低单笔限额，您可换卡重试"),
    UPS_ERROR_4006("UPS_ERROR_4006",4006,"","金额超过银行月累计限额，您可换卡重试"),
    UPS_ERROR_4007("UPS_ERROR_4007",4007,"","银行卡失败次数超限，您可换卡或次日重试"),
    UPS_ERROR_4008("UPS_ERROR_4008",4008,"","支付次数超过发卡行限制，您可换卡重试"),
    UPS_ERROR_4009("UPS_ERROR_4009",4009,"","当前银行卡不支持该业务，您可换卡重试"),
    UPS_ERROR_4010("UPS_ERROR_4010",4010,"","密码输入失败次数超限，您可换卡重试"),
    UPS_ERROR_4011("UPS_ERROR_4011",4011,"","持卡人信息有误，您可核对身份信息是否有误"),
    UPS_ERROR_4012("UPS_ERROR_4012",4012,"","银行卡已过有效期，您可换卡重试"),
    UPS_ERROR_4013("UPS_ERROR_4013",4013,"","银行卡状态异常，您可换卡重试"),
    UPS_ERROR_4014("UPS_ERROR_4014",4014,"","交易失败，您可联系发卡行"),
    UPS_ERROR_5001("UPS_ERROR_5001",5001,"","卡未开通银联无卡支付，您可换卡重试"),
    UPS_ERROR_default("UPS_ERROR_default",5003,"","银行卡交易失败，您可换卡或稍后重试"),
    UPS_ERROR_5002("UPS_ERROR_5002",5002,"","银行卡交易失败，您可换卡或稍后重试"),

    HAVE_A_REPAYMENT_PROCESSING("HAVE_A_REPAYMENT_PROCESSING",6001,"have a repayment processing","有一笔还款在处理中"),
    HAVE_A_RENEWAL_PROCESSING("HAVE_A_RENEWAL_PROCESSING",6002,"have a renewal processing","有一笔续期在处理中"),
    FUNCTIONAL_MAINTENANCE("FUNCTIONAL_MAINTENANCE",6003,"functional maintenance","功能维护中"),
    GET_JSD_RATE_ERROR("GET_JSD_RATE_ERROR",6004,"get jsd rate error","获取利率失败，请联系客服"),
    RENEWAL_ORDER_NOT_EXIST_ERROR("RENEWAL_ORDER_NOT_EXIST_ERROR",6005,"renewal order not exist error","无可续期的订单"),
    RENEWAL_NOT_EXIST_ERROR("RENEWAL_NOT_EXIST_ERROR",6006,"renewal not exist error","续期信息不存在"),
    CAN_NOT_RENEWAL_ERROR("CAN_NOT_RENEWAL_ERROR",6007,"can not renewal error","您的续期机会已用完"),
    RENEWAL_FAIL_ERROR("RENEWAL_FAIL_ERROR", 6008,"renewal fail error","续期失败"),
    JSD_BORROW_CASH_STATUS_ERROR("JSD_BORROW_CASH_STATUS_ERROR",2001,"borrow cash amount status","您有一笔未结清账单"),
    ADD_JSD_BORROW_CASH_INFO_FAIL("ADD_JSD_BORROW_CASH_INFO_FAIL",2011,"add jsd borrow cash info fail","生成借款信息失败"),
    DELEGATEPAY_DIRECT_FAIL("DELEGATEPAY_DIRECT_FAIL", 2012,"delegatepay direct fail","借款打款直接失败"),
    PROTOCOL_NOT_SUPPORT_YET("PROTOCOL_NOT_SUPPORT_YET", 2013, "protocol not support yet", "暂不支持目标协议"),
    ;





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

    BizExceptionCode(String code, int errorCode, String errorMsg, String desc) {
        this.code = code;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.desc = desc;
    }

    public static BizExceptionCode getByCode(String code) {
        if (code == null || "".equalsIgnoreCase(code)) {
            return null;
        }
        BizExceptionCode[] errorCodes = values();

        for (BizExceptionCode acsErrorCode : errorCodes) {
            if (acsErrorCode.getCode().equals(code)) {
                return acsErrorCode;
            }
        }

        return null;
    }

    public static BizExceptionCode getByErrorMessage(String errorMsg) {
        for (BizExceptionCode bizExceptionCode : BizExceptionCode.values()) {
            if (StringUtils.equals(errorMsg, bizExceptionCode.getErrorMsg())) {
                return bizExceptionCode;
            }
        }
        return UPS_ERROR_5002;
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
