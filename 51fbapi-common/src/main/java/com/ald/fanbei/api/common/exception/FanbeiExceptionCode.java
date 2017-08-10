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
    REQUEST_PARAM_TOKEN_TIMEOUT("REQUEST_PARAM_TOKEN_ERROR", 1141, "token is invalid", "您的登录已超时, 请重新登录"),
    
    // user mode code from 1100 - 1199
    USER_BORROW_NOT_EXIST_ERROR("USER_BORROW_NOT_EXIST_ERROR",1100,"user not exist error","用户未登录"), 
    USER_NOT_EXIST_ERROR("USER_NOT_EXIST_ERROR",1005,"user not exist error","用户不存在"),

    
    USER_INVALID_MOBILE_NO("USER_INVALID_MOBILE_NO",1101,"invalid mobile number","无效手机号"),
    USER_HAS_REGIST_ERROR("USER_HAS_REGIST_ERROR",1102,"user has been regist","该号码已经注册"),
    USER_PASSWORD_ERROR("USER_PASSWORD_ERROR",1103,"user or password error","用户名或密码不正确"),
    USER_PASSWORD_ERROR_GREATER_THAN5("USER_PASSWORD_ERROR_GREATER_THAN5",1104,"user password error count to max","密码错误次数超过限制锁定2小时"),
    USER_REGIST_SMS_NOTEXIST("USER_REGIST_SMS_NOTEXIST",1105,"user regist sms not exist","验证码不正确"),
    USER_REGIST_SMS_ERROR("USER_REGIST_SMS_ERROR",1106,"user regist sms error","验证码不正确"),
    USER_REGIST_CHANNEL_NOTEXIST("USER_REGIST_SMS_NOTEXIST",1138,"user regist channel code not exist","渠道编号不正确"),
    USER_REGIST_IMAGE_NOTEXIST("USER_REGIST_IMAGE_NOTEXIST",1139,"user regist image not exist","图片验证码不正确"),
    USER_REGIST_IMAGE_ERROR("USER_REGIST_IMAGE_ERROR",1140,"user regist image error","图片验证码不正确"),

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
    APPLY_CASHED_AMOUNT_TOO_SMALL_JFB("APPLY_CASHED_AMOUNT_TOO_SMALL_JFB",1127,"apply cash amount jfb to small","至少提现2000集分宝"),
    RENEWAL_ORDER_NOT_EXIST_ERROR("RENEWAL_ORDER_NOT_EXIST_ERROR",1128,"nothing order can renewal","无可续期的订单"),
    HAVE_A_REPAYMENT_PROCESSING_ERROR("HAVE_A_REPAYMENT_PROCESSING_ERROR",1129,"There is a repayment is processing","有一笔还款正在处理中"),
    
    USER_PASSWORD_ERROR_FIRST("USER_PASSWORD_ERROR_FIRST",1131,"user password error first","密码输入有误,剩余次数(5)"),
    USER_PASSWORD_ERROR_SECOND("USER_PASSWORD_ERROR_SECOND",1132,"user password error second","密码输入有误,剩余次数(4)"),
    USER_PASSWORD_ERROR_THIRD("USER_PASSWORD_ERROR_THIRD",1133,"user password error third","密码输入有误,剩余次数(3)"),
    USER_PASSWORD_ERROR_FOURTH("USER_PASSWORD_ERROR_FOURTH",1134,"user password error fourth","密码输入有误,剩余次数(2)"),
    USER_PASSWORD_ERROR_FIFTH("USER_PASSWORD_ERROR_FIFTH",1135,"user password error fifth","密码输入有误,剩余次数(1)"),
    USER_PASSWORD_ERROR_ZERO("USER_PASSWORD_ERROR_SIXTH",1136,"user password error sixth","密码输入有误,剩余次数(6)"),
    USER_PASSWORD_OLD_ERROR("USER_PASSWORD_OLD_ERROR",1137,"user password old error","旧密码输入有误"),

    USER_GET_COUPON_ERROR("USER_GET_COUPON_ERROR",1200,"user coupon error ","优惠券已领取"),
    //优惠券不可用，不能修改code
    USER_COUPON_ERROR("USER_COUPON_ERROR",1201,"user coupon error ","优惠券不可用"),
    USER_SIGNIN_AGAIN_ERROR("USER_SIGNIN_AGAIN_ERROR",1210,"user coupon error ","今日已签到"),
    USER_COUPON_NOT_EXIST_ERROR("USER_COUPON_NOT_EXIST_ERROR",1211,"user coupon error ","优惠券不存在"),
    USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR("USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR",1212,"user coupon error ","优惠券个数超过最大领券个数"),
    USER_COUPON_PICK_OVER_ERROR("USER_COUPON_PICK_OVER_ERROR",1213,"pick coupon over error ","优惠券已领取完"),

    USER_CASH_MONEY_ERROR("USER_CASH_MONEY_ERROR",1300,"user cash money error","取现金额超过上限"),
    USER_MAIN_BANKCARD_NOT_EXIST_ERROR("USER_MAIN_BANKCARD_NOT_EXIST_ERROR",1301,"user main bankcard not exist error","您未绑定主卡"),
    USER_BANKCARD_NOT_EXIST_ERROR("USER_BANKCARD_NOT_EXIST_ERROR",1302,"user bankcard not exist error","用户银行卡不存在"),
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
    SMS_REGIST_EXCEED_TIME("SMS_REGIST_EXCEED_TIME",1141,"user regist exceed time","发送注册验证码超过每日限制次数"),
    SMS_FORGET_PASSWORD_EXCEED_TIME("SMS_FORGET_PASSWORD_EXCEED_TIME",1142,"user forget password exceed time","发送找回密码验证码超过每日限制次数"),
    SMS_MOBILE_BIND_EXCEED_TIME("SMS_MOBILE_BIND_EXCEED_TIME",1143,"user bind mobile exceed time","发送绑定手机号短信超过每日限制次数"),
    SMS_SET_PAY_PASSWORD_EXCEED_TIME("SMS_SET_PAY_PASSWORD_EXCEED_TIME",1144,"user set pay password exceed time","发送设置支付密码短信超过每日限制次数"),


    AUTH_REALNAME_ERROR("AUTH_REALNAME_ERROR",1540,"auth realname error","实名认证失败"),
    AUTH_CARD_ERROR("AUTH_CARD_ERROR",1541,"auth card error","银行卡认证失败"),
    AUTH_BINDCARD_ERROR("AUTH_BINDCARD_ERROR",1542,"bind card error","绑定银行卡失败"),
    
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
    UPS_ORDERNO_BUILD_ERROR("UPS_ORDERNO_BUILD_ERROR",1561,"ups order build error","构建订单错误"),
    REQ_WXPAY_ERR("REQ_WXPAY_ERR",1562,"request wx error","微信app支付失败"),
    BANK_CARD_PAY_ERR("BANK_CARD_PAY_ERR",1563,"bank card pay error","银行卡支付失败"),
    BANK_CARD_PAY_SMS_ERR("BANK_CARD_PAY_SMS_ERR",1564,"bank card pay sms error","银行卡支付短信验证失败"),
    REFUND_ERR("REFUND_ERR",1565,"refund error","退款失败"),
    UPS_BATCH_DELEGATE_ERR("UPS_BATCH_DELEGATE_ERR",1566,"ups batch delegate error","批量代付失败"),
    AUTH_BINDCARD_SMS_ERROR("AUTH_BINDCARD_SMS_ERROR",1567,"auth bindcard sms error","短信验证码获取失败,请重试"),
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
    
    //order model 1600-1699
    USER_ORDER_NOT_EXIST_ERROR("USER_ORDER_NOT_EXIST_ERROR",1600,"user order not exist error","用户订单不存在"),
    GOODS_NOT_EXIST_ERROR("GOODS_NOT_EXIST_ERROR",1601,"goods not exist error","商品不存在"),
    GOODS_COLLECTION_ALREADY_EXIST_ERROR("GOODS_COLLECTION_ALREADY_EXIST_ERROR",1602,"goods not exist error","商品已经收藏"),
    ORDER_NOFINISH_CANNOT_DELETE("ORDER_NOFINISH_CANNOT_DELETE",1603,"order not finish cannot delete","订单未完成，删除失败"),
    GOODS_HAVE_CANCEL("GOODS_HAVE_CANCEL",1604,"goods have cancel","商品已下架"),
    
    //borrow model 1700-1799 
    USER_ORDER_HAVE_CLOSED("USER_ORDER_HAVE_CLOSED",1604,"user order have closed","用户订单已关闭"),
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
    RISK_VERIFY_ERROR("RISK_VERIFY_ERROR",1902,"risk verify error","风控审批失败"),
    RISK_MODIFY_ERROR("RISK_VERIFY_ERROR",1903,"risk modify error","用户信息修改失败"),
    RISK_OPERATOR_ERROR("RISK_OPERATOR_ERROR",1904,"risk operator error","上树运营商数据查询失败"),
    AUTH_MOBILE_ERROR("AUTH_MOBILE_ERROR",1905,"auth mobile error","手机运营商认证失败"),
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
    
    //2000-2100
    BORROW_CASH_AMOUNT_ERROR("BORROW_CASH_AMOUNT_ERROR",2000,"borrow cash amount or day error","借钱金额或者时间有误"),
    BORROW_CASH_STATUS_ERROR("BORROW_CASH_STATUS_ERROR",2001,"borrow cash amount status","您有一笔未结清账单"),
    
    BORROW_CASH_NOT_EXIST_ERROR("BORROW_CASH_NOT_EXIST_ERROR",2002,"borrow cash not exist","借钱信息不存在"),
    BORROW_CASH_REPAY_NOT_EXIST_ERROR("BORROW_CASH_REPAY_NOT_EXIST_ERROR",2003,"borrow cash repay not exist","还钱信息不存在或已删除"),
   
    //还款中，不能修改code
    BORROW_CASH_REPAY_PROCESS_ERROR("BORROW_CASH_REPAY_PROCESS_ERROR",2004,"borrow cash repay not exist","您有一笔还款正在处理中"),
   
    
    BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR("BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR",2005,"borrow cash repay more than borrow cash","还款金额大于借款金额"),

    BORROW_CASH_REPAY_AMOUNT__ERROR("BORROW_CASH_REPAY_AMOUNT_BORROW_ERROR",2006,"borrow cash repay  borrow cash error","还款金额有误请重新检查"),
    BORROW_CASH_SWITCH_NO("BORROW_CASH_SWITCH_NO",2007,"borrow cash switch error","今日放款已达上限， 明天尽早哦！"),
    BORROW_CASH_MORE_ACCOUNT_ERROR("BORROW_CASH_MORE_ACCOUNT_ERROR",2008,"borrow cash  more  account  borrow error","借款金额超过可借金额，请下拉刷新后重新提交"),

    //3000-3999
    BOLUOME_ORDER_NOT_EXIST("BOLUOME_ORDER_NOT_EXIST",3000,"order don't exist","该订单暂时未同步"),
    ORDER_REFUND_TYPE_ERROR("ORDER_REFUND_TYPE_ERROR",3001,"order refund type error","此订单类型暂不支持"),

    //4000-4999
    PICK_BRAND_COUPON_NOT_START("PICK_BRAND_COUPON_NOT_START",4000,"pick brand not start","领取活动还未开始,敬请期待"),
    PICK_BRAND_COUPON_DATE_END("PICK_BRAND_COUPON_DATE_END",4001,"pick brand has end","活动已经结束,请期待下一次活动"),
    PICK_BRAND_COUPON_NOT_REAL_NAME("PICK_BRAND_COUPON_NOT_REAL_NAME",4002,"pick brand coupon","需要先实名认证,才可以领取优惠券"),
    PUSH_BRAND_ORDER_STATUS_FAILED("PUSH_BRAND_ORDER_STATUS_FAILED",4003,"push brand order status failed","推送品牌订单消息失败"),
    PICK_BRAND_COUPON_FAILED("PICK_BRAND_COUPON_FAILED",4004,"pick brand coupon failed","领取优惠券失败"),
    
    
    TONGTUN_FENGKONG_REGIST_ERROR("TONGTUN_FENGKONG_REGIST_ERROR",4004,"tongtun fengkong error","您注册手机号存在安全风险，如有疑问请联系客服:0571-88193918"),
    TONGTUN_FENGKONG_LOGIN_ERROR("TONGTUN_FENGKONG_LOGIN_ERROR",4005,"tongtun login fengkong error","您登录手机号存在安全风险，如有疑问请联系客服:0571-88193918"),
    TONGTUN_FENGKONG_TRADE_ERROR("TONGTUN_FENGKONG_TRADE_ERROR",4006,"tongtun trade fengkong error","您投资手机号存在安全风险，如有疑问请联系客服:0571-88193918"),
    
    GAME_CHANCE_CODE_ERROR("GAME_CHANCE_CODE_ERROR",4030,"game chance code error","无效抓娃娃机会"),
    GAME_COUPONS_LIMIT_ERROR("GAME_COUPONS_LIMIT_ERROR",4031,"coupon limit","未中奖"),
    NOT_CONFIG_GAME_INFO_ERROR("NOT_CONFIG_GAME_INFO_ERROR",4032,"not config game info error","请配置游戏信息"),
    NOT_CHANCE_TEAR_PACKET_ERROR("NOT_CHANCE_TEAR_PACKET_ERROR",4033,"not chance tear packet error","无抽红包机会"),
    // 地址管理
    CHANG_ADDRESS_ERROR("CHANG_ADDRESS_ERROR",5000,"set default address error","亲,已经是最后一个地址了,留下这个作为默认地址吧"),
    CHANG_DEFAULT_ADDRESS_ERROR("CHANG_DEFAULT_ADDRESS_ERROR",5001,"change default address error","亲,不能取消默认地址"),
    USER_ADDRESS_NOT_EXIST("USER_ADDRESS_NOT_EXIST",5002,"user_address_not_exist","地址信息不存在"),

    //订单
    ORDER_NOT_EXIST("ORDER_NOT_EXIST",6001,"order_not_exist","订单不存在"),

    //系统升级该code不能随便修改
    SYSTEM_UPDATE("SYSTEM_UPDATE", 8888, "system update", "51返呗V3.7.0新版上线 更新内容： \n1.用户提额全新上线 \n2.多种支付方式随心享，更有好礼相赠。您即将前往的下一站是【App Store】更新，如无更新按钮，请稍后重试或卸载后重新安装"),

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
