
package com.ald.fanbei.api.common.exception;

/**
 *@类AppExceptionCode.java 的实现描述：错误枚举类
 *@author 陈金虎 2017年1月16日 下午11:27:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum FanbeiExceptionCode {

    SUCCESS("SUCCESS", 200, "success", "成功"), FAILED("FAILED", 201, "failed", "失败"),
	
    GET_CASHER_ERROR("GET_CASHER_ERROR",11602,"GET_CASHER_ERROR","跳转收银台失败"),
    RESUBMIT_ERROR("RESUBMIT_ERROR",11615,"RESUBMIT_ERROR","请勿重复确认收货"),
    ACTIVE_CLOSE("ACTIVE_CLOSE",11600,"active close","新邀请有礼活动，从首页Banner进入"),

    VERSION_ERROR("VERSION_ERROR", -2000, "version is error", "版本过低，前往应用市场更新到最新版本获得更好体验"),

    ZFB_NOT_USERD("PARAM_ERROR", 1145, "param error", "支付宝支付正在维护中,请用其它支付方式"),
    WEBCHAT_NOT_USERD("PARAM_ERROR", 1142, "param error", "微信支付正在维护中,请用其它支付方式"),
    BANK_REPAY_ERROR("BANK_REPAY_ERROR", 1152, "param error", "还款方式只支持银行卡"),
    // PARAM_CODE 1001-1099
    PARAM_ERROR("PARAM_ERROR", 1001, "param error", "参数错误"),
    REQUEST_PARAM_NOT_EXIST("REQUEST_PARAM_NOT_EXIST", 1002, "request param is invalid", "请求参数缺失"),
    POSITION_EXCEPTION("POSITION_EXCEPTION", 1018, "position exception", "所在位置异常，无法进行借款"),
    REQUEST_PARAM_METHOD_NOT_EXIST("REQUEST_PARAM_METHOD_NOT_EXIST", 1003, "request method is invalid", "请求方法不存在"),
    REQUEST_PARAM_TOKEN_ERROR("REQUEST_PARAM_TOKEN_ERROR", 1004, "token is invalid", "您未登录，请登录"),
    REQUEST_INVALID_SIGN_ERROR("REQUEST_INVALID_SIGN_ERROR", 1005, "sign is invalid", "非法请求"),
    REQUEST_PARAM_ERROR("REQUEST_PARAM_ERROR", 1006, "request param error", "请求参数不正确"),
    REQUEST_PARAM_METHOD_ERROR("REQUEST_PARAM_METHOD_ERROR",1007,"request method param error","请求方法不正确"),
    REQUEST_PARAM_SYSTEM_NOT_EXIST("REQUEST_PARAM_SYSTEM_NOT_EXIST", 1008, "system param is invalid", "系统参数缺失"),
    CALCULATE_SHA_256_ERROR("CALCULATE_SHA_256_ERROR",1009,"cal sha 265 error","系统错误"),
    SYSTEM_REPAIRING_ERROR("SYSTEM_REPAIRING_ERROR",1010,"system repairing","系统维护中"),
    REQUEST_PARAM_ILLEGAL ("REQUEST_PARAM_ILLEGAL", 1011, "request param illegal", "请求参数不合法"),
    REQUEST_PARAM_TOKEN_TIMEOUT("REQUEST_PARAM_TOKEN_ERROR", 1012, "token is invalid", "您的登录已超时, 请重新登录"),
    USER_NOT_EXIST_ERROR("USER_NOT_EXIST_ERROR",1015,"user not exist error","用户不存在"),
    USER_LOGIN_SMS_NOTEXIST("USER_LOGIN_SMS_NOTEXIST",1016,"user login sms not exist","请获取短信验证码"),
    USER_LOGIN_SMS_WRONG_ERROR("USER_LOGIN_SMS_WRONG_ERROR",1017,"user login sms wrong error","验证码不正确"),

    // user mode code from 1100 - 1199
    USER_BORROW_NOT_EXIST_ERROR("USER_BORROW_NOT_EXIST_ERROR",1100,"user not exist error","用户未登录"),
    USER_INVALID_MOBILE_NO("USER_INVALID_MOBILE_NO",1101,"invalid mobile number","无效手机号"),
    USER_HAS_REGIST_ERROR("USER_HAS_REGIST_ERROR",1102,"user has been regist","该号码已经注册"),
    USER_PASSWORD_ERROR("USER_PASSWORD_ERROR",1103,"user or password error","用户名或密码不正确"),
    USER_PASSWORD_ERROR_GREATER_THAN5("USER_PASSWORD_ERROR_GREATER_THAN5",1104,"user password error count to max","密码错误次数超过限制锁定2小时"),
    USER_REGIST_SMS_NOTEXIST("USER_REGIST_SMS_NOTEXIST",1105,"user regist sms not exist","验证码不正确"),
    USER_REGIST_SMS_ERROR("USER_REGIST_SMS_ERROR",1106,"user regist sms error","验证码不正确"),

    USER_REGIST_SMS_OVERDUE("USER_REGIST_SMS_OVERDUE",1107,"user regist sms overdue","验证码已经过期"),
    USER_REGIST_ACCOUNT_EXIST("USER_REGIST_ACCOUNT_EXIST",1108,"user regist account exist","用户已存在"),

    USER_SEND_SMS_ERROR("USER_SEND_SMS_ERROR",1109,"user send sms error","用户发送验证码失败"),
    USER_REGIST_SMS_ALREADY_ERROR("USER_REGIST_SMS_ALREADY_ERROR",1110,"user regist sms already error","验证码已验证"),

    USER_DUPLICATE_INVITE_CODE("USER_DUPLICATE_INVITE_CODE",1111,"user duplicate commit invite code","用户已经重复输入邀请码"),
    COMMIT_INVITE_CODE_EXPIRE_TIME("COMMIT_INVITE_CODE_EXPIRE_TIME",1112,"commit invite code expire time","输入邀请码时限已经超过72小时"),
    LIMIT_INVITE_EACH_OTHER("LIMIT_INVITE_EACH_OTHER",1113,"limit invite each other","限制互相推荐"),
    CODE_NOT_EXIST("CODE_NOT_EXIST",1114,"code not exist","您输入的邀请码不存在"),
    LIMIT_INVITE_SELF("LIMIT_INVITE_SELF",1115,"limit invite self","您不能邀请自己"),
    CAN_NOT_APPLY_CASHED("CAN_NOT_APPLY_CASHED",1116,"can not apply cashed","系统维护中，暂不能申请提现"),
    APPLY_CASHED_AMOUNT_ERROR("APPLY_CASHED_AMOUNT_ERROR",1117,"apply cashed amount invalid","申请的金额无效"),
    USER_ACCOUNT_NOT_EXIST_ERROR("USER_ACCOUNT_NOT_EXIST_ERROR",1118,"user not exist error","账户不存在"),

    USER_ACCOUNT_IDNUMBER_INVALID_ERROR("USER_ACCOUNT_IDNUMBER_INVALID_ERROR",1119,"id number error","身份证输入有误"),
    USER_PAY_PASSWORD_INVALID_ERROR("USER_PAY_PASSWORD_INVALID_ERROR",1120,"pay password error","您的支付密码不正确或者尚未设置,请点击\"忘记密码\"找回或者重置"),
    APPLY_CASHED_AMOUNT_MORE_ACCOUNT("APPLY_CASHED_AMOUNT_MORE_ACCOUNT",1121,"apply cash amount more than account money","申请金额大于账户可提现金额"),
    APPLY_CASHED_BANK_ERROR("APPLY_CASHED_BANK_ERROR",1122,"apply cash bank id error","该卡不支持提现"),

    APPLY_CASHED_ZHIFUBAO_ERROR("APPLY_CASHED_ZHIFUBAO_ERROR",1123,"apply cash zhifubao error","支付宝账号输入有误"),

    USER_FROZEN_ERROR("USER_FROZEN_ERROR",1124,"user frozen error","用户冻结中"),
    USER_SMS_FAIL_MAX_ERROR("USER_SMS_FAIL_MAX_ERROR",1125,"user  sms check more than max","验证码输入错误次数过多"),
    APPLY_CASHED_AMOUNT_TOO_SMALL("APPLY_CASHED_AMOUNT_TOO_SMALL",1126,"apply cash amount more than account money","至少提现20元"),
    APPLY_CASHED_AMOUNT_SMALL("APPLY_CASHED_AMOUNT_TOO_SMALL",111291,"apply cash amount more than account money","提现金额太少,无法提现"),
    APPLY_CASHED_AMOUNT_TOO_SMALL_JFB("APPLY_CASHED_AMOUNT_TOO_SMALL_JFB",1127,"apply cash amount jfb to small","至少提现2000集分宝"),
    RENEWAL_ORDER_NOT_EXIST_ERROR("RENEWAL_ORDER_NOT_EXIST_ERROR",1128,"nothing order can renewal","无可续期的订单"),
    HAVE_A_REPAYMENT_PROCESSING_ERROR("HAVE_A_REPAYMENT_PROCESSING_ERROR",1129,"There is a repayment is processing","有一笔还款正在处理中"),
    BACK_MONEY_CHECK("BACK_MONEY_CHECK",11290,"There is a repayment is processing","还款金额要大于1块钱"),

    USER_LOGIN_UNTRUST_ERROW("USER_LOGIN_UNTRUST_ERROW",1130,"user login untrust error ","请返回登录页面重新登录"),
    USER_PASSWORD_ERROR_FIRST("USER_PASSWORD_ERROR_FIRST",1131,"user password error first","密码输入有误,剩余次数(5)"),
    USER_PASSWORD_ERROR_SECOND("USER_PASSWORD_ERROR_SECOND",1132,"user password error second","密码输入有误,剩余次数(4)"),
    USER_PASSWORD_ERROR_THIRD("USER_PASSWORD_ERROR_THIRD",1133,"user password error third","密码输入有误,剩余次数(3)"),
    USER_PASSWORD_ERROR_FOURTH("USER_PASSWORD_ERROR_FOURTH",1134,"user password error fourth","密码输入有误,剩余次数(2)"),
    USER_PASSWORD_ERROR_FIFTH("USER_PASSWORD_ERROR_FIFTH",1135,"user password error fifth","密码输入有误,剩余次数(1)"),
    USER_PASSWORD_ERROR_ZERO("USER_PASSWORD_ERROR_SIXTH",1136,"user password error sixth","密码输入有误,剩余次数(6)"),
    USER_PASSWORD_OLD_ERROR("USER_PASSWORD_OLD_ERROR",1137,"user password old error","旧密码输入有误"),

    USER_REGIST_CHANNEL_NOTEXIST("USER_REGIST_SMS_NOTEXIST",1138,"user regist channel code not exist","渠道编号不正确"),
    USER_REGIST_IMAGE_NOTEXIST("USER_REGIST_IMAGE_NOTEXIST",1139,"user regist image not exist","图片验证码不正确"),
    CHANGE_MOBILE_PASSWORD_ERROR_EXCEED_THRESHOLD("CHANGE_MOBILE_PASSWORD_ERROR_EXCEED_THRESHOLD",1140,"Wrong password entered more than three times","您已尝试超过5次，帐号将被锁定，请在24小时后重试。"),
    CHANGE_MOBILE_IDENTITY_CARD_ERROR_EXCEED_THRESHOLD("CHANGE_MOBILE_IDENTITY_CARD_ERROR_EXCEED_THRESHOLD",1141,"ENTER AN IDENTITY CARD ERROR MORE THAN THREE TIMES","您已尝试超过5次，帐号将被锁定，请在24小时后重试。"),
    CHANGE_BIND_MOBILE_LIMIT("CHANGE_BIND_MOBILE_LIMIT",1142,"chnage bind mobile limit","该功能正在维护中,如需改绑请联系客服:0571-88193918"),
    CHANGE_MOBILE_TARGET_LOST("CHANGE_MOBILE_TARGET_LOST", 1143, "change mobile target lost", "要更换的新手机号已丢失，请重新操作"),
    USER_REGIST_IMAGE_ERROR("USER_REGIST_IMAGE_ERROR",1144,"user regist image error","图片验证码不正确"),
    USER_REGIST_IMAGE_ERROR2("USER_REGIST_IMAGE_ERROR",1145,"user regist image error","图片验证码不正确"),
    USER_REGIST_FREQUENTLY_ERROR("USER_REGIST_FREQUENTLY_ERROR",1146,"user_regist_frequently_error","验证码获取过于频繁，请稍后重试"),
    USER_REGIST_SMS_LESSDUE("USER_REGIST_SMS_LESSDUE",1147,"user regist sms lessdue","验证码60秒内已获取过"),
    BOLUOME_UNTRUST_SHOPGOODS("BOLUOME_UNTRUST_SHOPGOODS",1148,"boluome untrust shopgoods","支付维护中，请选择其他商户购买!"),
    MUST_UPGRADE_NEW_VERSION_REPAY("MUST_UPGRADE_NEW_VERSION_REPAY",1147,"must_upgrade_new_version_repay","请升级到最新版APP进行操作"),
    // 1200 -
    USER_GET_COUPON_ERROR("USER_GET_COUPON_ERROR",1200,"user coupon error ","优惠券已领取"),
    //优惠券不可用，不能修改code
    USER_COUPON_ERROR("USER_COUPON_ERROR",1201,"user coupon error ","优惠券不可用"),
    USER_SIGNIN_AGAIN_ERROR("USER_SIGNIN_AGAIN_ERROR",1210,"user coupon error ","今日已签到"),
    USER_COUPON_NOT_EXIST_ERROR("USER_COUPON_NOT_EXIST_ERROR",1211,"user coupon error ","优惠券不存在"),
    USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR("USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR",1212,"user coupon error ","优惠券个数超过最大领券个数"),
    USER_COUPON_PICK_OVER_ERROR("USER_COUPON_PICK_OVER_ERROR",1213,"pick coupon over error ","优惠券已领取完"),
    USER_GET_SIGN_AWARD_ERROR("USER_GET_SIGN_AWARD_ERROR",1214,"user get sign award error ","签到奖励已领取"),
    USER_GET_TO_COUPON_CENTER("USER_GET_TO_COUPON_CENTER",1215,"user get coupon to my coupon center ","您已领取，可前往我的优惠券中查看~"),
    USER_SIGNIN_SUCCESS("USER_SIGNIN_SUCCESS", 1000, "success", "签到成功"),
    REPLACE_MAIN_CARD_FAIL("REPLACE_MAIN_CARD_FAIL",1216,"replace main card fail","更换银行主副卡失败~"),
    USER_GET_LOGIN_SUPERMAN_COUPON_ERROR("USER_GET_LOGIN_SUPERMAN_COUPON_ERROR",1217,"coupon nothing  ","优惠券已发完"),

    // 1300 -
    USER_CASH_MONEY_ERROR("USER_CASH_MONEY_ERROR",1300,"user cash money error","取现金额超过上限"),
    USER_MAIN_BANKCARD_NOT_EXIST_ERROR("USER_MAIN_BANKCARD_NOT_EXIST_ERROR",1301,"user main bankcard not exist error","您未绑定主卡"),
    USER_BANKCARD_NOT_EXIST_ERROR("USER_BANKCARD_NOT_EXIST_ERROR",1302,"user bankcard not exist error","用户银行卡不存在"),
    USER_BANKCARD_LIMIT_ERROR("USER_BANKCARD_LIMIT_ERROR",1319,"user bankcard limit error"," 该银行卡单笔限额3000元，请分批还款或使用其他银行卡还款，谢谢！"),
    USER_BANKCARD_RENEW_LIMIT_ERROR("USER_BANKCARD_LIMIT_ERROR",1320,"user bankcard limit error"," 该银行卡单笔限额3000元，请使用其他银行卡还款，谢谢！"),
    USER_FACE_AUTH_ERROR("USER_FACE_AUTH_ERROR",1303,"user face auth error","用户未通过人脸识别"),
    USER_BANKCARD_EXIST_ERROR("USER_BANKCARD_EXIST_ERROR",1304,"user bankcard exist error","用户银行卡已被绑定"),
    USER_REALNAME_AUTH_ERROR("USER_REALNAME_AUTH_ERROR",1305,"user realname auth error","用户实名认证失败"),

    USER_CARD_AUTH_ERROR("USER_CARD_AUTH_ERROR",1306,"user card auth error","身份证图片获取失败,请重试!"),
    USER_CARD_INFO_EXIST_ERROR("USER_CARD_INFO_EXIST_ERROR",1307,"user card exist error","请先完成实名信息提交!"),
    USER_CARD_INFO_ATYPISM_ERROR("USER_CARD_INFO_ATYPISM_ERROR",1308,"user card info atypism error","身份证信息与实名信息不符!"),
    USER_CARD_GET_ERROR("USER_CARD_GET_ERROR",1309,"user card auth error","实名信息获取失败,请重试!"),
    USER_CARD_IS_EXIST("USER_CARD_IS_EXIST",1310,"user card is exist","该身份证已被认证。"),
    ZHIMA_CREDIT_INFO_EXIST_ERROR("ZHIMA_CREDIT_INFO_EXIST_ERROR",1311,"zhima Credit exist error","请先完成芝麻信用授权!"),
    OPERATOR_INFO_EXIST_ERROR("OPERATOR_INFO_EXIST_ERROR",1312,"operator exist error","请先完成运营商授权!"),
    EMERGENCY_CONTACT_INFO_EXIST_ERROR("EMERGENCY_CONTACT_INFO_EXIST_ERROR",1313,"emergency contact exist error","请先完成紧急联系人设置!"),
    CANOT_FIND_DIRECTORY_ERROR("CANOT_FIND_DIRECTORY_ERROR",1314,"canot find directory error","请重新设置紧急联系人!"),
    NEED_AGAIN_DIRECTORY_PROOF_ERROR("NEED_AGAIN_DIRECTORY_PROOF_ERROR",1315,"need again directory proof error","请重新通讯录认证!"),
    RISK_OREADY_FINISH_ERROR("RISK_OREADY_FINISH_ERROR",1316,"risk oready finish error","正在认证中，请耐心等待!"),
    PLEASE_PASS_THE_BASIC_CERTIFICATION("PLEASE_PASS_THE_BASIC_CERTIFICATION",1317,"please pass the basic certification","基础认证未通过审核!"),
    ALIPAY_CERTIFIED_UNDER_MAINTENANCE("ALIPAY_CERTIFIED_UNDER_MAINTENANCE",1318,"alipay certified under maintenance","支付宝认证正在维护中，请等待！"),
    ZM_STATUS_EXPIRED("ZM_STATUS_EXPIRED",1319,"zm status expired","芝麻信用已过期，请至信用中心-基础认证中重新认证芝麻信用分！"),
    CREDIT_CERTIFIED_UNDER_MAINTENANCE("CREDIT_CERTIFIED_UNDER_MAINTENANCE",1320,"credit certified under maintenance","信用卡认证正在维护中，请等待！"),
    CHSI_CERTIFIED_UNDER_MAINTENANCE("CHSI_CERTIFIED_UNDER_MAINTENANCE",1321,"chsi certified under maintenance","学信网认证正在维护中，请等待！"),

	USER_AUTH_INFO_NOT_EXIST("USER_AUTH_INFO_NOT_EXIST",1322,"user auth info not exist","用户认证信息不存在"),
    AUTH_FUND_GETTOKEN_ERROR("AUTH_FUND_GETTOKEN_ERROR",1322,"auth fund gettoken error","51公积金认证获取令牌失败！"),
    AUTH_FUND_GETORDERSN_ERROR("AUTH_FUND_GETORDERSN_ERROR",1322,"auth fund getordersn error","51公积金认证获取订单号失败！"),
    AUTH_FUND_SUBMIT_ERROR("AUTH_FUND_SUBMIT_ERROR",1323,"auth fund submit error","公积金提交认证失败，请稍后重试"),

    BANK_LIMIT_MONEY("BANK_LIMIT_MONEY",1324,"bank limit money","该银行单笔限额%.2f元，请分批还款或使用其他银行卡还款，谢谢！"),
    UPS_ERROR_MSG("UPS_ERROR_MSG",1325,"ups error msg",""),


    // third mode code 1500-1599
    JPUSH_ERROR("JPUSH_ERROR",1500,"jpush error","推送失败"),

    ZM_ERROR("ZM_ERROR",1510,"zm error","调用芝麻信用失败"),
    ZM_AUTH_ERROR("ZM_AUTH_ERROR",1511,"zm auth error","芝麻信用授权失败"),
//    ZM_CREDIT_WATCHLISTII_ERROR("ZM_CREDIT_WATCHLISTII_ERROR",1511,"zm credit watchlistii error","调用芝麻行业关注名单失败"),
//    ZM_CREDIT_SCORE_GET_ERROR("ZM_CREDIT_SCORE_GET_ERROR",1512,"zm credit score get error","调用芝麻信用评分失败"),
    DEALWITH_YOUDUN_NOTIFY_ERROR("DEALWITH_YOUDUN_NOTIFY_ERROR",1520,"dealwith youdun notify error","有盾回调出错"),

    SMS_MOBILE_NO_ERROR("SMS_MOBILE_NO_ERROR",1530,"invalid mobile","无效手机号"),
    SMS_MOBILE_COUNT_TOO_MANAY("SMS_MOBILE_COUNT_TOO_MANAY",1531,"too manay mobiles","手机号太多"),
    SMS_MOBILE_ERROR("SMS_MOBILE_ERROR",1532,"too manay mobiles","手机号有误"),
    SMS_REGIST_EXCEED_TIME("SMS_REGIST_EXCEED_TIME",1141,"user regist exceed time","验证码获取已达上限，请明天再试"),
    SMS_FORGET_PASSWORD_EXCEED_TIME("SMS_FORGET_PASSWORD_EXCEED_TIME",1142,"user forget password exceed time","发送找回密码验证码超过每日限制次数"),
    SMS_MOBILE_BIND_EXCEED_TIME("SMS_MOBILE_BIND_EXCEED_TIME",1143,"user bind mobile exceed time","发送绑定手机号短信超过每日限制次数"),
    SMS_SET_PAY_PASSWORD_EXCEED_TIME("SMS_SET_PAY_PASSWORD_EXCEED_TIME",1144,"user set pay password exceed time","发送设置支付密码短信超过每日限制次数"),
    SMS_LOGIN_EXCEED_TIME("SMS_LOGIN_EXCEED_TIME",1145,"user login sms exceed time","发送登录验证码超过每日限制次数"),
    USER_ACCOUNT_MONEY_LESS("USER_ACCOUNT_MONEY_LESS", 1146, "user account money less error", "账户余额不足,请用其它支付方式"),
    SMS_SET_QUICK_PASSWORD_EXCEED_TIME("SMS_SET_QUICK_PASSWORD_EXCEED_TIME",1148,"set quick password exceed time","发送设置快捷登录密码验证码超过每日限制次数"),

    AUTH_REALNAME_ERROR("AUTH_REALNAME_ERROR",1540,"auth realname error","实名认证失败"),
    AUTH_CARD_ERROR("AUTH_CARD_ERROR",1541,"auth card error","银行卡认证失败"),
    AUTH_BINDCARD_ERROR("AUTH_BINDCARD_ERROR",1542,"bind card error","绑定银行卡失败"),
    DSED_BANK_BINDED("DSED_BANK_BINDED",1545,"bind card error","绑定银行卡已占用"),

    UPS_REPEAT_NOTIFY("UPS_REPEAT_NOTIFY",1549,"ups repeat notify","重复UPS回调"),
    COMPLETE_ORDER("COMPLETE_ORDER",1580,"complete order","确认收货"),
    UPS_AUTH_BF_SIGN_ERROR("UPS_AUTH_BF_SIGN_ERROR",1550,"bao fu auth error","银行卡认证失败"),
    UPS_AUTH_YSB_SIGN_ERROR("UPS_AUTH_YSB_SIGN_ERROR",1551,"bao fu auth error","银行卡认证失败"),
    UPS_DELEGATE_PAY_ERROR("UPS_DELEGATE_PAY_ERROR",1552,"ups delegate pay error","单笔代付失败"),
    UPS_AUTH_PAY_ERROR("UPS_AUTH_PAY_ERROR",1553,"ups auth pay error","认证支付失败"),
    UPS_AUTH_PAY_CONFIRM_ERROR("UPS_AUTH_PAY_CONFIRM_ERROR",1554,"ups auth pay confirm error","支付认证确认失败"),
    UPS_QUERY_TRADE_ERROR("UPS_QUERY_TRADE_ERROR",1555,"ups query trade error","单笔交易查询失败"),
    UPS_AUTH_SIGN_ERROR("UPS_AUTH_SIGN_ERROR",1556,"ups auth sign error","签约失败"),
    UPS_AUTH_SIGN_VALID_ERROR("UPS_AUTH_SIGN_VALID_ERROR",1557,"ups auth sign valid error","签约短信验证失败"),
    UPS_QUERY_AUTH_SIGN_ERROR("UPS_QUERY_AUTH_SIGN_ERROR",1558,"ups query auth sign error","查询签约验证失败"),
    UPS_SIGN_DELAY_ERROR("UPS_SIGN_DELAY_ERROR",1559,"ups sign delay error","协议延期失败"),
    UPS_COLLECT_ERROR("UPS_COLLECT_ERROR",1560,"ups collect error","单笔代收失败"),
    UPS_QUICK_PAY_CONFIRM_ERROR("UPS_QUICK_PAY_CONFIRM_ERROR",1760,"ups quick pay confirm error","快捷支付确认支付失败"),
    UPS_QUICKPAY_RESEND_CODE_ERROR("UPS_QUICKPAY_RESEND_CODE_ERROR",1660,"ups quickpay recend code error","快捷支付发送验证码失败"),
    UPS_ORDERNO_BUILD_ERROR("UPS_ORDERNO_BUILD_ERROR",1561,"ups order build error","构建订单错误"),
    REQ_WXPAY_ERR("REQ_WXPAY_ERR",1562,"request wx error","微信app支付失败"),
    BANK_CARD_PAY_ERR("BANK_CARD_PAY_ERR",1563,"bank card pay error","银行卡支付失败"),
    BANK_CARD_PAY_SMS_ERR("BANK_CARD_PAY_SMS_ERR",1564,"bank card pay sms error","银行卡支付短信验证失败"),
    REFUND_ERR("REFUND_ERR",1565,"refund error","退款失败"),
    UPS_BATCH_DELEGATE_ERR("UPS_BATCH_DELEGATE_ERR",1566,"ups batch delegate error","批量代付失败"),
    AUTH_BINDCARD_SMS_ERROR("AUTH_BINDCARD_SMS_ERROR",1567,"auth bindcard sms error","绑卡信息与银行预留不一致，请核实信息后重新尝试"),
    SIGN_RELEASE_ERROR("SIGN_RELEASE_ERROR",1568,"sign release error","银行卡解绑失败"),
    ORDER_PAY_DEALING("ORDER_PAY_DEALING",1569,"order pay dealing","订单正在支付中,请勿重复提交"),
    ORDER_HAS_PAID("ORDER_HAS_PAID",1570,"order has been paid","订单已经支付,请勿重复提交"),
    REFUND_TOTAL_AMOUNT_ERROR("REFUND_TOTAL_AMOUNT_ERROR",1571,"refund total amount error","当前退款总金额大于订单金额"),
    REFUND_AMOUNT_ERROR("REFUND_AMOUNT_ERROR",1572,"refund amount error","退款金额有误"),
    REFUND_HAVE_SUCCESS("REFUND_HAVE_SUCCESS",1573,"refund have success","退款已完成"),
    CREDIT_AMOUNT_ORDER_PAY_LIMIT("CREDIT_AMOUNT_ORDER_PAY_LIMIT",1574,"credit amount order pay limit","该商品暂不支持信用分期,请切换其他支付方式"),
    AFTERSALE_PROCESSING("REFUND_PROCESSING",1575,"aftersale processing","售后处理中,提交失败"),
    ORDER_HAVE_CLOSED("ORDER_HAVE_CLOSED",1576,"order have closed","订单已完结"),
    AFTERSALE_APPLY_NOT_EXIST("AFTERSALE_APPLY_NOT_EXIST",1577,"aftersale apply not exist","售后申请记录不存在"),
    FUNCTION_REPAIRING_ERROR("FUNCTION_REPAIRING_ERROR",1578,"function repairing error","此功能正在维护中，请耐心等待！"),
    ORDER_HAS_CLOSED("ORDER_HAS_CLOSED",1579,"order has closed","订单已经关闭,请重新下单"),
    UPS_CACHE_EXPIRE("UPS_CACHE_EXPIRE",1580,"order has closed","支付请求已经关闭,请重新支付"),
    UPS_KUAIJIE_NOT_SUPPORT("UPS_KUAIJIE_NOT_SUPPORT",1581,"ups kuaijie not support","当前业务，未支持快捷支付"),
    ORDER_PAY_FAIL("ORDER_PAY_FAIL",1582,"ORDER_PAY_FAIL","订单支付失败"),
    BINDCARD_PAY_PWD_MISS("BINDCARD_PAY_PWD_MISS",1583,"BINDCARD_PAY_PWD_MISS","绑卡时缺失支付密码"),
    BINDCARD_REALINFO_MISS("BINDCARD_REALINFO_MISS",1584,"BINDCARD_REALINFO_MISS","绑卡时缺失实名信息"),

    //order model 1600-1699
    USER_ORDER_NOT_EXIST_ERROR("USER_ORDER_NOT_EXIST_ERROR",1600,"user order not exist error","用户订单不存在"),
    GOODS_NOT_EXIST_ERROR("GOODS_NOT_EXIST_ERROR",1601,"goods not exist error","商品不存在"),
    GOODS_NOT_LEASE_ERROR("GOODS_NOT_LEASE_ERROR",1610,"goods not exist error","不可重复租赁"),
    GOODS_COLLECTION_ALREADY_EXIST_ERROR("GOODS_COLLECTION_ALREADY_EXIST_ERROR",1602,"goods not exist error","商品已经收藏"),
    ORDER_NOFINISH_CANNOT_DELETE("ORDER_NOFINISH_CANNOT_DELETE",1603,"order not finish cannot delete","订单未完成，删除失败"),
    GOODS_HAVE_CANCEL("GOODS_HAVE_CANCEL",1604,"goods have cancel","商品已下架"),
    GOODS_HAVE_BEEN_RESERVED("GOODS_HAVE_BEEN_RESERVED",1605,"goods have been reserved","商品已预约"),
    SOLD_OUT("SOLD_OUT",1607,"sold out","您来晚了，商品已抢光"),
    GOODS_ARE_NOT_IN_STOCK("GOODS_ARE_NOT_IN_STOCK",1608,"goods are not in stock","商品库存不足，请重新购买"),
    EXCEED_THE_LIMIT_OF_PURCHASE("EXCEED_THE_LIMIT_OF_PURCHASE",1609,"exceed the limit of purchase","超过限购数量，请修改商品数量"),
    WEAK_VERIFY_VIP_GOODS_REPEAT_BUY("WEAK_VERIFY_VIP_GOODS_REPEAT_BUY",1611,"weak verify vip goods repeat buy","请勿重复购买权限包"),
    WEAK_VERIFY_VIP_GOODS_BUY_NUMS_OVER("WEAK_VERIFY_VIP_GOODS_BUY_NUMS_OVER",1612,"weak verify vip goods buy nums over","权限包购买数量超限"),
    
    //borrow model 1700-1799 
    USER_ORDER_HAVE_CLOSED("USER_ORDER_HAVE_CLOSED",1606,"user order have closed","用户订单已关闭"),
    BORROW_CONSUME_NOT_EXIST_ERROR("BORROW_CONSUME_NOT_EXIST_ERROR",1701,"borrow consume not exist error","分期未配置"),
    BORROW_CONSUME_MONEY_ERROR("BORROW_CONSUME_MONEY_ERROR",1702,"borrow consume money error","分期金额超过上限"),
    BORROW_BILL_NOT_EXIST_ERROR("BORROW_BILL_NOT_EXIST_ERROR",1703,"borrow bill not exist error","账单不存在"),
    BORROW_BILL_UPDATE_ERROR("BORROW_BILL_UPDATE_ERROR",1704,"borrow bill update error","用户账单已更新"),
    BORROW_DETAIL_NOT_EXIST_ERROR("BORROW_DETAIL_NOT_EXIST_ERROR",1705,"borrow detail not exist error","借款详情不存在"),
    REPAYMENT_DETAIL_NOT_EXIST_ERROR("REPAYMENT_DETAIL_NOT_EXIST_ERROR",1706,"repayment detail not exist error","还款详情不存在"),
    BORROW_CONSUME_GOODS_IS_EMPTY("BORROW_CONSUME_GOODS_IS_EMPTY",1707,"borrow consume goods is empty","商品不存在"),
    AVAILABLE_CREDIT_NOT_ENOUGH("AVAILABLE_CREDIT_NOT_ENOUGH",1708,"available credit not enough","可用额度不足"),
    AVAILABLE_CREDIT_NOT_ENOUGH_TEN("AVAILABLE_CREDIT_NOT_ENOUGH_TEN",1709,"available credit not enough ten","可用额度不足，您可在10天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_NINE("AVAILABLE_CREDIT_NOT_ENOUGH_NINE",1710,"available credit not enough nine","可用额度不足，您可在9天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_EIGHT("AVAILABLE_CREDIT_NOT_ENOUGH_EIGHT",1711,"available credit not enough eight","可用额度不足，您可在8天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_SEVEN("AVAILABLE_CREDIT_NOT_ENOUGH_SEVEN",1712,"available credit not enough seven","可用额度不足，您可在7天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_SIX("AVAILABLE_CREDIT_NOT_ENOUGH_SIX",1713,"available credit not enough six","可用额度不足，您可在6天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_FIVE("AVAILABLE_CREDIT_NOT_ENOUGH_FIVE",1714,"available credit not enough five","可用额度不足，您可在5天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_FOUR("AVAILABLE_CREDIT_NOT_ENOUGH_FOUR",1715,"available credit not enough four","可用额度不足，您可在4天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_THREE("AVAILABLE_CREDIT_NOT_ENOUGH_THREE",1716,"available credit not enough three","可用额度不足，您可在3天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_TWO("AVAILABLE_CREDIT_NOT_ENOUGH_TWO",1717,"available credit not enough two","可用额度不足，您可在2天后进入信用认证页面重新提交审核。"),
    AVAILABLE_CREDIT_NOT_ENOUGH_ONE("AVAILABLE_CREDIT_NOT_ENOUGH_ONE",1718,"available credit not enough one","可用额度不足，您可在1天后进入信用认证页面重新提交审核。"),
    BORROW_BILL_IS_REPAYING("BORROW_BILL_IS_REPAYING",1719,"borrow bill is repaying","当前有一笔还款正在处理中，请耐心等待"),
    //h5 1800-1900
    RESOURES_H5_ERROR("RESOURES_H5_ERROR",1800,"resoures h5 not exist error","信息不存在，请联系管理员"),

    //1901-1999
    RISK_REGISTER_ERROR("RISK_REGISTER_ERROR",1901,"risk register error","用户信息同步失败"),
    RISK_VERIFY_ERROR("RISK_VERIFY_ERROR",1902,"risk verify error","风控审核拒绝"),
    RISK_REFUSE_ERROR("RISK_REFUSE_ERROR",1950,"risk refuse error","您上次的审核未通过，请过几天后 再试吧"),
    RISK_MODIFY_ERROR("RISK_VERIFY_ERROR",1903,"risk modify error","用户信息修改失败"),
    RISK_OPERATOR_ERROR("RISK_OPERATOR_ERROR",1904,"risk operator error","上树运营商数据查询失败"),
    AUTH_MOBILE_ERROR("AUTH_MOBILE_ERROR",1905,"auth mobile error","手机运营商认证失败，请稍后重试。若一直认证失败，请联系客服电话400-002-5151"),
    RISK_ADDRESSLIST_PRIMARIES_ERROR("RISK_ADDRESSLIST_PRIMARIES_ERROR",1906,"risk address list primaries error","通讯录同步失败"),
    ADD_WHITE_USER_PRIMARIES_ERROR("ADD_WHITE_USER_PRIMARIES_ERROR",1907,"add white user primaries error","添加白名单失败"),
    QUERY_GRANT_AMOUNT_ERROR("Query_GRANT_AMOUNT_ERROR",1908,"query user grantAmount error","正在获取信用额度，请稍候！"),
    RISK_RAISE_QUOTA_ERROR("RISK_RAISE_QUOTA_ERROR",1909,"risk raise quota error","风控提额失败"),


    AUTH_ALL_AUTH_ERROR("AUTH_ALL_AUTH_ERROR",1910,"all auth  error","信用认证未完成"),
    API_CALL_NUM_OVERFLOW("API_CALL_NUM_OVERFLOW",1911,"api call num overflow","您的认证次数已达上限，请明日再试！"),

    API_RISK_MOBILE_VERIFYING("API_RISK_MOBILE_VERIFYING",1912,"api risk mobile verifying","认证中，请勿重复发起！"),
    API_RISK_MOBILE_VERIFY_PASSED("API_RISK_MOBILE_VERIFY_PASSED",1913,"api risk mobile verify passed","认证已通过，请勿重复发起！"),
    VIRTUAL_PRODUCT_QUOTA_ERROR("VIRTUAL_PRODUCT_QUOTA_ERROR",1914,"virtual product quota error","获取虚拟商品可用额度失败"),
    RISK_AUTH_AMOUNT_LIMIT("AUTH_AMOUNT_LIMIT",1915,"auth amount limit","亲，您的信用还需努力，本次分期购买申请没有通过。"),
    RISK_OTHER_RULE("RISK_OTHER_RULE",1916,"risk other rule","亲，您的信用还需努力，本次分期购买申请没有通过。"),
    RISK_BORROW_OVERDUED("RISK_BORROW_OVERDUED",1917,"risk borrow overdued","风控逾期分期限制"),
    RISK_BORROW_CASH_OVERDUED("RISK_BORROW_CASH_OVERDUED",1918,"risk other rule","风控逾期借钱限制"),
    QUERY_OVERDUE_ORDER_ERROR("QUERY_OVERDUE_ORDER_ERROR",1919,"query overdue order error","查询逾期账单失败"),
    RISK_USERLAY_RATE_ERROR("RISK_USERLAY_RATE_ERROR",1920,"risk userlay rate error","获取用户手续费率失败"),
    RISK_SYN_LOGIN_VERIFY_ERROR("RISK_SYN_LOGIN_VERIFY_ERROR",1921,"risk syn login verify error","风控同步登陆失败"),
    RISK_CREDIT_PAYMENT_ERROR("RISK_CREDIT_PAYMENT_ERROR",1922,"get risk credit payment error","获取信用支付额度失败"),
    RISK_SYNC_CONTACTS_ERROR("RISK_MODIFY_CONTACTS_ERROR", 1922, "risk_modify contacts error", "风控同步通讯录失败"),
    RISK_RAISE_CAPTIL_ERROR("RISK_RAISE_CAPTIL_ERROR",1923,"get captil error","风控应还本金获取失败"),
    RISK_FORBIDDEN_ERROR("RISK_FORBIDDEN_ERROR",1923,"get captil error","抱歉，无法续期"),
    RISK_RAISE_AMOUNT_ERROR("RISK_RAISE_AMOUNT_ERROR",1924,"risk raise amount error","提额异常"),
    RISK_NEWFUND_NOTIFY_ERROR("RISK_NEWFUND_NOTIFY_ERROR",1925,"risk newfund notify error","51公积金信息通知风控异常"),
    RISK_RESPONSE_DATA_ERROR("RISK_RESPONSE_DATA_ERROR",1924,"risk response data error","风控返回数据异常"),
    UESR_ACCOUNT_SENCE_ERROR("UESR_ACCOUNT_SENCE_ERROR",1930,"get uesr account sence error","获取用户多场景额度信息失败"),
    RISK_VERIFY_ERROR_BORROW("RISK_VERIFY_ERROR_BORROW",1931,"risk verify error","风控拒绝，您可能有未还款的账单，请还款后再支付"),
    //2000-2100
    BORROW_CASH_AMOUNT_ERROR("BORROW_CASH_AMOUNT_ERROR",2000,"borrow cash amount or day error","版本过低，无法申请借钱，请稍后查看短信提示，重新下载最新版本"),
    BORROW_CASH_STATUS_ERROR("BORROW_CASH_STATUS_ERROR",2001,"borrow cash amount status","您有一笔未结清账单"),
    BORROW_CASH_RECYCLE_STATUS_ERROR("BORROW_CASH_RECYCLE_STATUS_ERROR",2023,"borrow cash amount status","您有一笔未回收成功订单"),
    JSD_BORROW_CASH_STATUS_ERROR("JSD_BORROW_CASH_STATUS_ERROR",2022,"jsd borrow cash amount status","您有一笔未结清极速贷借款"),
    BORROW_ERROR("BORROW_ERROR",7001,"borrow_errow","借款回调修改异常"),

    BORROW_CASH_NOT_EXIST_ERROR("BORROW_CASH_NOT_EXIST_ERROR",2002,"borrow cash not exist","借钱信息不存在"),
    BORROW_CASH_REPAY_NOT_EXIST_ERROR("BORROW_CASH_REPAY_NOT_EXIST_ERROR",2003,"borrow cash repay not exist","还钱信息不存在或已删除"),

    //还款中，不能修改code
    BORROW_CASH_REPAY_PROCESS_ERROR("BORROW_CASH_REPAY_PROCESS_ERROR",2004,"borrow cash repay not exist","您有一笔还款正在处理中"),


    BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR("BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR",2005,"borrow cash repay more than borrow cash","还款金额大于借款金额"),
    ORDER_BORROW_CASH_NOT_EXIST_ERROR("ORDER_BORROW_CASH_NOT_EXIST_ERROR",2006,"order borrow cash not exist","商品借款信息不存在"),

    USER_REALNAME_NOT_EXIST_ERROR("USER_REALNAME_NOT_EXIST_ERROR",3008,"user realName not exist error","用户真实姓名不存在"),

    RENEWAL_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR("RENEWAL_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR",3005,"renewal cash repay more than borrow cash","续借金额大于借款金额"),
    RENEWAL_CASH_REPAY_AMOUNT_LESS_ONE_HUNDRED("RENEWAL_CASH_REPAY_AMOUNT_LESS_ONE_HUNDRED",3006,"renewal cash repay less one hundred","续借金额小于100"),

    BORROW_CASH_REPAY_AMOUNT__ERROR("BORROW_CASH_REPAY_AMOUNT_BORROW_ERROR",2006,"borrow cash repay  borrow cash error","还款金额有误请重新检查"),
    BORROW_CASH_SWITCH_NO("BORROW_CASH_SWITCH_NO",2007,"borrow cash switch error","今日放款已达上限， 明天尽早哦！"),
    BORROW_CASH_MORE_ACCOUNT_ERROR("BORROW_CASH_MORE_ACCOUNT_ERROR",2008,"borrow cash  more  account  borrow error","借款金额超过可借金额，请下拉刷新后重新提交"),

    HAVE_A_PROCESS_RENEWAL_DETAIL("HAVE_A_PROCESS_RENEWAL_DETAIL",2009,"borrow a process renewal record","存在处理中续期记录,请稍后重试"),

    STRONG_RISK_STATUS_ERROR("STRONG_RISK_STATUS_ERROR",2010,"strong risk apply status error","您的风控审核正在提交"),
    ADD_BORROW_CASH_INFO_FAIL("ADD_BORROW_CASH_INFO_FAIL",2011,"add borrow cash info fail","生成借款信息失败"),
    BORROW_CASH_REPAY_REPEAT_ERROR("BORROW_CASH_REPAY_REPEAT_ERROR",2004,"borrow cash repay repeat","重复的还款操作"),
    BORROW_CASH_REPAY_REBATE_ERROR("BORROW_CASH_REPAY_REBATE_ERROR",2016,"borrow cash repay rebate error","余额还款失败，请检查您的输入或重试"),
    BORROW_CASH_MAJIABAO_STOP_ERROR("BORROW_CASH_MAJIABAO_STOP_ERROR",2020,"borrow cash majiabao stop error","本应用已暂停更新，请搜索\"爱上街\"app，使用相同账号借款"),
    BORROW_CASH_STOP_ERROR("BORROW_CASH_STOP_ERROR",2021,"borrow cash stop error","本服务已暂停，请移步\"生活\"页面，\"回收拿钱\"业务将继续为您提供资金服务"),

    /* 2100-2200 贷款相关！！！！！！！ */
    LOAN_NO_AUTHZ("",2101,"", "您还未认证"),
    LOAN_QUOTA_TOO_SMALL("",2102,"", "可用额度小于最小借款额"),
    LOAN_NO_PASS_STRO_RISK("",2103,"", "未通过强风控审核"),
    LOAN_NO_PASS_WEAK_RISK("",2104,"", "未通过弱风控审核"),
    LOAN_SWITCH_OFF("",2105,"", "贷款开关关闭"),
    LOAN_RISK_REFUSE("",2106,"", "贷款风控拒绝"),
    LOAN_UPS_DRIECT_FAIL("",2107,"","请求打款实时失败"),
    LOAN_UPS_CALLBACK_FAIL("",2108,"","请求打款失败"),
    LOAN_REPEAT_APPLY("",2109,"","已有处理中的贷款申请，不可重复申请"),
    LOAN_CONCURRENT_LIMIT("",2110,"","同一时刻只能发起一笔贷款申请"),
    LOAN_OVERFLOW("",2111,"","贷款额违法"),
    LOAN_PERIOD_NOT_EXIST_ERROR("LOAN_PERIOD_NOT_EXIST_ERROR",2012,"loan period not exist error","分期借款信息不存在"),
    LOAN_REPAY_AMOUNT_ERROR("LOAN_REPAY_AMOUNT_ERROR",2013,"loan repay amount error","还款金额有误请重新检查"),
    LOAN_PERIOD_CAN_NOT_REPAY_ERROR("LOAN_PERIOD_CAN_NOT_REPAY_ERROR",2014,"loan period can not repay error","当前借款未到还款时间"),
    LOAN_REPAY_REBATE_ERROR("LOAN_REPAY_REBATE_ERROR",2015,"loan repay rebate error","余额还款失败，请检查您的输入或重试"),
    GO_BLD_AUTH("",2016,"","跳转白领贷认证"),
    AUTHING("",2017,"","认证中"),
    LOAN_REPAY_PROCESS_ERROR("LOAN_REPAY_PROCESS_ERROR",2018,"loan repay not exist","您有一笔还款正在处理中，请稍后重试"),
    LOAN_NOT_EXIST_ERROR("LOAN__NOT_EXIST_ERROR",2020,"loan  not exist error","借款信息不存在"),
    //3000-3999
    BOLUOME_ORDER_NOT_EXIST("BOLUOME_ORDER_NOT_EXIST",3000,"order don't exist","该订单暂时未同步"),
    ORDER_REFUND_TYPE_ERROR("ORDER_REFUND_TYPE_ERROR",3001,"order refund type error","此订单类型暂不支持"),
    BORROW_CASH_ORDER_NOT_EXIST_ERROR("BORROW_CASH_ORDER_NOT_EXIST_ERROR",3002,"borrow cash order not exist","借钱订单信息不存在"),
    BORROW_CASH_RATE_ERROR("BORROW_CASH_RATE_ERROR",3003,"borrow cash rate not exist","获取利率失败，请联系客服"),
    BORROW_CASH_GOOD_NOT_EXIST_ERROR("BORROW_CASH_GOOD_NOT_EXIST_ERROR",3004,"borrow cash goods not exist","商品信息不存在"),
    BORROW_CASH_COUPON_NOT_EXIST_ERROR("BORROW_CASH_COUPON_NOT_EXIST_ERROR",3005,"borrow cash coupon not exist","优惠券信息不存在"),
    AUTH_GOOD_CAN_NOT_REFUND("AUTH_GOOD_CAN_NOT_REFUND",3006,"auth good can not apply refund","权限包商品不可退款"),

    //4000-4999
    PICK_BRAND_COUPON_NOT_START("PICK_BRAND_COUPON_NOT_START",4000,"pick brand not start","领取活动还未开始,敬请期待"),
    PICK_BRAND_COUPON_DATE_END("PICK_BRAND_COUPON_DATE_END",4001,"pick brand has end","活动已经结束,请期待下一次活动"),
    PICK_BRAND_COUPON_NOT_REAL_NAME("PICK_BRAND_COUPON_NOT_REAL_NAME",4002,"pick brand coupon","需要先实名认证,才可以领取优惠券"),
    PUSH_BRAND_ORDER_STATUS_FAILED("PUSH_BRAND_ORDER_STATUS_FAILED",4003,"push brand order status failed","推送品牌订单消息失败"),
    PICK_BRAND_COUPON_FAILED("PICK_BRAND_COUPON_FAILED",4004,"pick brand coupon failed","领取优惠券失败"),
    NO_QUALIFIED_SIGN_AWARD("NO_QUALIFIED_SIGN_AWARD",4008,"no qualified sign award","不符合领取条件"),
    PERSON_SEAL_CREATE_FAILED("PERSON_SEAL_CREATE_FAILED",4100,"person_seal_create_failed","个人印章创建失败"),
    COMPANY_SEAL_CREATE_FAILED("COMPANY_SEAL_CREATE_FAILED",4101,"COMPANY_SEAL_CREATE_FAILED","公司印章创建失败"),
    CONTRACT_CREATE_FAILED("CONTRACT_CREATE_FAILED",4103,"contract_create_failed","合同生成失败"),
    CONTRACT_NOT_FIND("CONTRACT_NOT_FIND",4104,"contract_not_find","合同不存在"),
    COMPANY_SIGN_ACCOUNT_CREATE_FAILED("COMPANY_SIGN_ACCOUNT_CREATE_FAILED",4102,"company_sign_account_create_failed","e签宝账户创建失败"),
    TONGTUN_FENGKONG_REGISTER_PWD_ERROR("TONGTUN_FENGKONG_REGISTER_PWD_ERROR",4007,"tongtun fengkong error","您要找回的手机号存在安全风险，如有疑问请联系客服:0571-88193918"),
    TONGTUN_FENGKONG_REGIST_ERROR("TONGTUN_FENGKONG_REGIST_ERROR",4004,"tongtun fengkong error","您注册手机号存在安全风险，如有疑问请联系客服:0571-88193918"),
    TONGTUN_FENGKONG_LOGIN_ERROR("TONGTUN_FENGKONG_LOGIN_ERROR",4005,"tongtun login fengkong error","您登录手机号存在安全风险，如有疑问请联系客服:0571-88193918"),
    TONGTUN_FENGKONG_TRADE_ERROR("TONGTUN_FENGKONG_TRADE_ERROR",4006,"tongtun trade fengkong error","您投资手机号存在安全风险，如有疑问请联系客服:0571-88193918"),

    GAME_CHANCE_CODE_ERROR("GAME_CHANCE_CODE_ERROR",4030,"game chance code error","无效抓娃娃机会"),
    GAME_COUPONS_LIMIT_ERROR("GAME_COUPONS_LIMIT_ERROR",4031,"coupon limit","未中奖"),
    NOT_CONFIG_GAME_INFO_ERROR("NOT_CONFIG_GAME_INFO_ERROR",4032,"not config game info error","请配置游戏信息"),
    NOT_CHANCE_TEAR_PACKET_ERROR("NOT_CHANCE_TEAR_PACKET_ERROR",4033,"not chance tear packet error","无抽红包机会"),
    ONLY_ONE_GOODS_ACCEPTED("ONLY_ONE_GOODS_ACCEPTED",4034,"only one goods could be accepted","新人专享只能购买一件商品"),
    ONLY_ONE_DOUBLE12GOODS_ACCEPTED("ONLY_ONE_DOUBLE12GOODS_ACCEPTED",4035,"only one double12Goods could be accepted","亲，每个账号限购1件，不要太贪心哦"),
    DOUBLE_EGGS_EXPIRE("DOUBLE_EGGS_EXPIRE",4038,"double eggs goods expired","亲，已经过了秒杀时间哦，请准备下一场秒杀吧！"),
    DOUBLE_EGGS_WITHOUT_START("DOUBLE_EGGS_WITHOUT_START",4039,"double eggs goods has not started yet","亲，此商品是秒杀商品，还未开始哟！"),
    DOUBLE_EGGS_LIMIT_TIME("DOUBLE_EGGS_LIMIT_TIME",4040,"double eggs goods has limit time","亲，此商品是秒杀商品，只能指定时间购买哟！"),
    NO_DOUBLE12GOODS_ACCEPTED("NO_DOUBLE12GOODS_ACCEPTED",4036,"only one double12Goods could be accepted","秒杀商品已售空"),
    DOUBLE12ORDER_ERROR("DOUBLE12ORDER_ERROR",4037,"double12 activity order error","秒杀商品下单异常"),
    HAVE_BOUGHT_GOODS("HAVE_BOUGHT_GOODS",4039,"have bought goods","您已经购物过商品了哦"),

    // 地址管理
    CHANG_ADDRESS_ERROR("CHANG_ADDRESS_ERROR",5000,"set default address error","亲,已经是最后一个地址了,留下这个作为默认地址吧"),
    CHANG_DEFAULT_ADDRESS_ERROR("CHANG_DEFAULT_ADDRESS_ERROR",5001,"change default address error","亲,不能取消默认地址"),
    USER_ADDRESS_NOT_EXIST("USER_ADDRESS_NOT_EXIST",5002,"user_address_not_exist","地址信息不存在"),
    CASH_LIMIT("CASH_LIMIT", 5003, "param error", "目前提现进行维护中,请明日再进行尝试"),

    //订单
    ORDER_NOT_EXIST("ORDER_NOT_EXIST",6001,"order_not_exist","订单不存在"),
    AMOUNT_IS_LESS("AMOUNT_IS_LESS",6002,"amount_is_less","还款金额过少"),
    LEASE_NOT_BUY("LEASE_NOT_BUY",6007,"order_not_exist","分层得分过低不能租赁"),


    CUT_PRICE_ISBUY("CUT_PRICE_ISBUY",6003,"cut_price_isbut","砍价商品已购买"),
    SHARE_PRICE_BOUGHT("SHARE_PRICE_BOUGHT",6004,"shared goods has already been bought","您已不是新用户，暂不能购买，可以去邀请朋友购买或参加邀请有礼活动"),
    NO_NEW_USER("NO_NEW_USER",6006,"no new user","您不是新用户"),
    SHARE_PAYTYPE_ERROR("SHARE_PAYTYPE_ERROR",6005,"shared payType is not agent pay","专享商品支付方式必须是额度支付"),

    //系统升级该code不能随便修改
    SYSTEM_UPDATE("SYSTEM_UPDATE", 8888, "system update", "爱上街新版上线啦！\n您即将前往的下一站是【App Store】更新，如无更新按钮，请稍后重试或卸载后重新安装"),

    // SERVICE 9999
    SYSTEM_ERROR("SYSTEM_ERROR", 9999, "system error", "流量过大系统开小差啦，请尝试重新发起"),
    //7000
    BANKCARD_NOT_EXIST("BANKCARD_NOT_EXIST",7000,"bankcard is null","该用户没绑定银行卡"),

    USER_BANKCARD_NOT_EXIST("USER_BANKCARD_NOT_EXIST",7100,"user bankcard is null","未绑定此卡"),

    //物流信息不存在
    LOGISTICS_NOT_EXIST("Logistics_NOT_EXIST",6002,"order_not_exist","物流信息不存在"),

    //代扣提示信息
    WHIT_HOLD_DEALING("WHIT_HOLD_DEALING",5555,"whithold is dealing","抱歉，当前代扣进行中，暂时无法操作，请稍后再试！"),

    //租房使用
    TENEMENT_USER_INVALID("TENEMENT_USER_INVALID",7002,"tenement_user_invalid","对不起，该用户尚未进行注册/身份证认证"),
    //信息已审核
    TENEMENT_ALREADY_AUDIT("TENEMENT_ALREADY_AUDIT",7003,"tenement_already_audit","对不起，您的审核已将完成，无法更改"),

    //游戏充值
    GAME_IS_NOT_EXIST("GAME_IS_NOT_EXIST",8001,"game_is_not_exist","游戏信息不存在，请确认后操作"),
    GAME_IS_ILLEGAL("GAME_IS_NOT_EXIST",8002,"game_name_is_illegal","游戏名称错误，请确认后操作"),
    AMOUNT_IS_NULL("AMOUNT_IS_NULL",7004,"amount_is_null","对不起，您查询的退还款详情不存在，请刷新后重试"),

    ZHI_BALANCE_EXITS_ERROR("ZHI_BALANCE_EXITS_ERROR",8000,"zhi exists error","支付宝账号已被绑定"),
    ZHI_BALANCE_INVALID_ERROR("ZHI_BALANCE_INVALID_ERROR",8001,"zhi invalid error","支付宝账号有误"),
    ZHI_BALANCE_CODE_INVALID_ERROR("ZHI_BALANCE_CODE_INVALID_ERROR",8002,"zhi code invalid error","验证码有误"),

    AMOUNT_COMPARE_ERROR("AMOUNT_COMPARE_ERROR",7005,"money error","提前结清金额对不上"),
	//公信宝认证
    AUTH_GXB_GETTOKEN_ERROR("AUTH_GXB_GETTOKEN_ERROR",7008,"auth gxb gettoken error","认证公信宝获取token失败"),


	//用户现金流不存在

    //调用风控失败
    CALL_RISK_FAIL("FAIL_RISK_FAIL",7008,"call risk fail","调用风控失败!"),
    //有过期数据
    ZZYH_ERROR("ZZYH_ERROR", 7006, "ZZYH_ERROR ", "种子用户不能使用'提前结清'功能"),
    FAILURE_DATA_ERROR("FAILURE_DATA_ERROR",7007,"failure data error","有过期数据!"),
    TEMPORARY_AMOUNT_SWITH_EMPTY("TEMPORARY_AMOUNT_SWITH_EMPTY",7010,"TEMPORARY_AMOUNT_SWITH_EMPTY","临时额度开关没有设置"),

    SELECTED_AUTH_TYPE_NOT_PASS("SELECTED_AUTH_TYPE_NOT_PASS", 7007, "SELECTED_AUTH_TYPE_NOT_PASS ", "所选认证不通过"),
	//paypwd wrong less than specific times
	PAYPWD_ERROR_LESS_THAN_SPECIFIC_TIMES("PAYPWD_ERROR_LESS_THAN_SPECIFIC_TIMES",8003,"paypwd wrong less than specific times","支付密码错误，您还有x次机会！"),
	//paypwd wrong more than specific times
	PAYPWD_ERROR_MORE_THAN_SPECIFIC_TIMES("PAYPWD_ERROR_MORE_THAN_SPECIFIC_TIMES",8004,"paypwd wrong more than specific times","您已多次尝试失败，暂时被锁定，请x小时y分后再试或更改密码！"),
	PAYPWD_ERROR_SETTING_EMPTY("PAYPWD_ERROR_SETTING_EMPTY",8005,"paypwd error setting is empty ","支付密码次数和冻结时间未配置，请联系客服，谢谢！"),
//
    RESOURCE_NOT_FOUND_CONFIGURATION("RESOURCE_NOT_FOUND_CONFIGURATION",9000,"RESOURCE_NOT_FOUND_CONFIGURATION","未找到配置信息"),
    SECKILL_ERROR_END("SECKILL_ERROR_END",9003,"seckill error activity is end ","活动未开始或已结束！"),
    SECKILL_ERROR_PRICE("SECKILL_ERROR_PRICE",9004,"seckill error price is end ","商品价格变动，请重新下单！"),
    SECKILL_ERROR_STOCK("SECKILL_ERROR_STOCK",9001,"seckill error stock is error ","超过购买数量！"),
    SECKILL_ERROR("SECKILL_ERROR",9002,"seckill error seckill is error ","人太多了，被挤爆了！"),
    BRAND_GOODS_IS_EMPTY("BRAND_GOODS_IS_EMPTY",9005,"goods of this brand is empty","改品牌下的商品信息为空!"),
    BRAND_RESULT_INIT_SUCCESS("BRAND_RESULT_INIT_SUCCESS",9006,"brand result page init success","品牌结果页初始化成功!"),
    BRAND_CATEGORY_PAGE_INIT_SUCCESS("BRAND_CATEGORY_PAGE_INIT_SUCCESS",9007,"aishangjie brand category page init success","品牌分类页面初始化成功!"),
    CHOOSE_BANK_CARD_PAY("CHOOSE_BANK_CARD_PAY",9008,"choose bank card pay","请您选择银行卡付款方式"),

    WX_BIND_FAIL("WX_BIND_FAIL",9017,"wx bind fail","微信绑定失败"),
    SUPPLEMENT_SIGN_FAIL("SUPPLEMENT_SIGN_FAIL",9016,"supplement sign fail","补签失败"),
    FRIEND_USER_SIGN_EXIST("FRIEND_USER_SIGN_EXIST",9015,"friend user sign exist","今天已经帮此好友签到，不能再次签到"),
    USER_SIGN_FAIL("USER_SIGN_FAIL",9014,"user sign fail","签到失败"),
    USER_SIGN_EXIST("USER_SIGN_EXIST",9014,"user sign exist","今天已经签到，不能再次签到"),
    RECEIVE_REWARD_FAIL("RECEIVE_REWARD_FAIL",9013,"receive reward fail","奖励领取失败"),
    TASK_NOT_EXIST("TASK_NOT_EXIST",9012,"task not exist","此任务不存在"),
    SIGN_REMIND_FAIL("SIGN_REMIND_FAIL",9011,"sign remind fail","提醒失败"),
    WITHDRAW_FAIL("WITHDRAW_FAIL",9010,"withdraw fail","提现失败"),
    CHOOSE_WITHDRAW_TYPE("CHOOSE_WITHDRAW_TYPE",9009,"choose withdraw type","请选择提现方式"),
    WITHDRAW_OVER("WITHDRAW_OVER",9008,"the maximum amount has been exceeded today, please come tomorrow","已经超过今天提现最大金额，明天再来吧"),


    WX_CODE_INVALID("WX_CODE_INVALID", 2900, "wx code is invalid", "微信code码失效"),

    // 天天拆红包code
    OPEN_REDPACKET_ACTIVITY_OVER("OPEN_REDPACKET_ACTIVITY_OVER", -1, "activity is over", "活动已结束");

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
