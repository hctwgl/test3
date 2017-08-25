package com.ald.fanbei.api.common;

import java.math.BigDecimal;

/**
 * 
 * @类Constants.java 的实现描述：常量累
 * @author 陈金虎 2017年1月16日 下午11:17:10
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Constants {

	public static final long SECOND_OF_TEN_MINITS = 10 * 60l;
	public static final long SECOND_OF_HALF_HOUR = 30 * 60l;
	public static final long SECOND_OF_AN_HOUR = 60 * 60l;
	public static final long SECOND_OF_ONE_DAY = 24 * 60 * 60l;
	
	public static final int SECOND_OF_HALF_HOUR_INT = 30 * 60;
	public static final int SECOND_OF_AN_HOUR_INT = 60 * 60;

	public static final long SECOND_OF_ONE_WEEK = 7 * 24 * 60 * 60l;
	public static final long SECOND_OF_ONE_MONTH = 30 * 24 * 60 * 60l;

	public static final int MINITS_OF_2HOURS = 120;
	public static final int MINITS_OF_HALF_HOUR = 30;

	public static final int MONTH_OF_YEAR = 12;
	//商品详情图片信息包含数据总计部分
	public static final int GOODSDETAIL_PIC_PARTS = 3;
	//自营商品超时支付时间-小时
	public static final int SELFSUPPORT_PAY_TIMEOUT_HOUR = 1;
	public static final BigDecimal DECIMAL_MONTH_OF_YEAR = new BigDecimal(Constants.MONTH_OF_YEAR);
	//订单下单到截止支付的时间间隔小时
	public static final int ORDER_PAY_TIME_LIMIT = 24;
	//申请售后时间限制要求
	public static final int AFTER_SALE_DAYS = 30;
	//
	public static final String REQ_PARAM_NODE_SYSTEM = "system";
	public static final String REQ_PARAM_NODE_METHOD = "method";
	public static final String REQ_PARAM_NODE_PARAMS = "params";
	public static final String REQ_SYS_NODE_ID = "id";
	public static final String REQ_SYS_NODE_VERSION = "appVersion";
	public static final String REQ_SYS_NODE_USERNAME = "userName";
	public static final String REQ_SYS_NODE_SIGN = "sign";
	public static final String REQ_SYS_NODE_TIME = "time";
	public static final String REQ_SYS_NODE_NETTYPE = "netType";

	public static final String REQ_SYS_NODE_ID_FOR_QQ = "id";
	public static final String REQ_SYS_NODE_VERSION_FOR_QQ = "appversion";
	public static final String REQ_SYS_NODE_USERNAME_FOR_QQ = "username";
	public static final String REQ_SYS_NODE_SIGN_FOR_QQ = "sign";
	public static final String REQ_SYS_NODE_TIME_FOR_QQ = "time";
	public static final String REQ_SYS_NODE_NETTYPE_FOR_QQ = "nettype";

	public static final String DEFAULT_ENCODE = "UTF-8";
	public static final String SWITCH_ON = "1";
	public static final String SWITCH_OFF = "0";
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";
	public static final String DEFAULT_YEAR = "year";
	public static final String DEFAULT_MONTH = "month";
	public static final String DEFAULT_NPER = "nper";
	public static final String DEFAULT_RATE = "rate";
	/**免息期数json key**/
	public static final String DEFAULT__FREENPER = "freeNper";
	/**每期还款金额位数**/
	public static final int HALFUP_DIGIT = 2;
	/**是否免息是否还款状态--免息**/
	public static final String ISFREEINTEREST_Y = "Y";
	/**是否免息是否还款状态--不免息**/
	public static final String ISFREEINTEREST_N = "N";





	public static final Long INVITE_START_VALUE = 1679625L;
	public static final String INVELOMENT_TYPE_TEST = "test";
	public static final String INVELOMENT_TYPE_ONLINE = "online";
	public static final String INVELOMENT_TYPE_PRE_ENV = "preEnv";
	public static final String FORMAL_APP_IDENTIFY = "www";

	public static final double DEFAULT_CHARGE_MIN = 1;
	public static final double DEFAULT_CHARGE_MAX = 50;
	public static final int DEFAULT_CASH_DEVIDE = 2;
	public static final String DEFAULT_BORROW_CASH_NAME = "现金借款";
	public static final String DEFAULT_MOBILE_CHARGE_NAME = "手机充值";
	public static final String DEFAULT_BORROW_PURPOSE = "借款";
	public static final String DEFAULT_CASH_PURPOSE = "提现";
	public static final String DEFAULT_REFUND_PURPOSE = "退款";
	public static final String DEFAULT_PAY_PURPOSE = "付款";
	public static final String DEFAULT_WX_PAY_NAME = "微信";
	public static final String DEFAULT_USER_ACCOUNT = "账户余额";
	public static final String DEFAULT_BANK_PAY = "银行卡";
	public static final String DEFAULT_SERVICE_PHONE = "0571-88193918";
	public static final String DEFAULT_REPAYMENT_NAME = "主动还款-";
	public static final String DEFAULT_BRAND_SHOP = "品牌订单支付";
	public static final String DEFAULT_SELFSUPPORT_SHOP = "自营商品支付";

	public static final String DEFAULT_REPAYMENT_NAME_BORROW_CASH = "主动还款";
	public static final String DEFAULT_RENEWAL_NAME_BORROW_CASH = "续费支付";
	//支付失败信息
	public static final String PAY_ORDER_PASSWORD_ERROR = "支付密码输入有误";
	public static final String PAY_ORDER_UPS_FAIL = "支付失败";
	public static final String PAY_ORDER_UPS_FAIL_WX = "微信支付失败";
	public static final String PAY_ORDER_UPS_FAIL_BANK = "银行卡支付失败";
	public static final String PAY_ORDER_USE_AMOUNT_LESS = "可用额度不足";
	// cache keys
	public static final String CACHEKEY_GAME_LIMIT = "game_limit$";
	public static final String CACHEKEY_USER_TOKEN = "user_token$";
	public static final String CACHEKEY_USER_NAME = "user_name$";
	public static final String CACHEKEY_APK_NEWEST_VERSION = "apk_newest_version$";
	public static final String CACHEKEY_BORROW_CASH = "borrow_cash$";
	public static final String CACHEKEY_BORROW_CONSUME = "borrow_consume$";
	public static final String CACHEKEY_GAME_INFO      = "game_info$";
	public static final String CACHEKEY_GAMECONF_INFO  = "gameconf_info$";
	public static final String CACHEKEY_LATESTAWARD_LIST  = "latest_award_list$";
	public static final String CACHEKEY_LATEST_GAMEERSULT_LIST = "latest_gameresult_list$";
	public static final String CACHEKEY_COUPON_INFO      = "coupon$";
	public static final String CACHEKEY_BORROW_CONSUME_OVERDUE = "borrow_consume_overdue$";
	public static final String CACHEKEY_USER_CONTACTS = "user_contacts$";
	public static final String CACHEKEY_USER_LOGIN_WIFI_MAC = "login_wifi_mac$";
	public static final String CACHEKEY_PROMOTIONCHANBELPOINT = "promotion_channel_point$";
	public static final String CACHEKEY_CHANNEL_IMG_CODE_PREFIX = "channel_img_code$";
	
	//查询用户是否结果款，1代表已借款
	public static final String HAVE_BORROWED = "have_borrowed$";
	public static final String BOLUOME_LOGIN_ERROR_TIMES  = "boluome_login_error_times$";

	// res type
	public static final String RES_APP_QRCODE_URL = "APP_QRCODE_URL";
	public static final String RES_APP_QRCODE_GENERATE_URL = "APP_QRCODE_GENERATE_URL";
	public static final String RES_APP_LOGIN_FAILED_LOCK_HOUR = "APP_LOGIN_FAILED_LOCK_HOUR";
	public static final String RES_APP_CALL_CENTER_MESSAGE = "APP_CALL_CENTER_MESSAGE";
	public static final String RES_APP_CALL_CENTER_MESSAGE_QQ = "QQ";
	public static final String RES_APP_CALL_CENTER_MESSAGE_WX = "WX";
	public static final String RES_APP_CALL_CENTER_MESSAGE_ONLINE = "ONLINE";
	public static final String RES_APP_CALL_CENTER_MESSAGE_TEL = "TEL";
	public static final String RES_APP_CALL_CENTER_MESSAGE_QQGROUP = "QQGROUP";
	public static final String RES_THIRD_GOODS_REBATE_RATE = "THIRD_GOODS_REBATE_RATE";
	public static final String RES_BORROW_CASH_RATE_DESC = "BORROW_CASH_RATE_DESC";
	public static final String RES_BORROW_CONSUME = "BORROW_CONSUME";
	public static final String RES_BORROW_CONSUME_OVERDUE = "BORROW_CONSUME_OVERDUE";
	public static final String RES_BORROW_CASH = "BORROW_CASH";
	public static final String RES_BORROW_RATE = "BORROW_RATE";
	public static final String RES_BORROW_CASH_RANGE = "BORROW_CASH_RANGE";
	public static final String RES_BORROW_CASH_LIMIT_AMOUNT = "BORROW_CASH_LIMIT_AMOUNT";
	public static final String RES_BORROW_TRADE = "BORROW_TRADE";
	public static final String RES_BORROW_CASH_POUNDAGE_RATE = "RES_BORROW_CASH_POUNDAGE_RATE";
	public static final String RES_BORROW_CASH_POUNDAGE_TIME = "RES_BORROW_CASH_POUNDAGE_TIME";
	
	public static final String RES_BRAND_SHOP = "BRAND_SHOP";
	public static final String RES_CREDIT_SCORE_AMOUNT = "CREDIT_SCORE_AMOUNT";
	public static final String RES_CREDIT_SCORE = "CREDIT_SCORE";
	public static final String APPLY_BRROW_CASH_WHITE_LIST = "APPLY_BRROW_CASH_WHITE_LIST";
	public static final String RES_REFUND_RATE = "REFUND_RATE";
	public static final String RES_BORROW_CASH_POUNDAGE = "BORROW_CASH_POUNDAGE";
	public static final String RES_APP_POP_IMAGE        =  "APP_POP_IMAGE"; //首页弹窗配置
	public static final String RES_GAME_AWARD_OF_CATCH_DOLL = "GAME_AWARD_OF_CATCH_DOLL"; //抓娃娃游戏发奖 
	public static final String RES_GAME_CATCH_DOLL_CLIENT_RATE = "GAME_CATCH_DOLL_CLIENT_RATE";//抓娃娃游戏客户端抓中概率
	public static final String RES_OLD_USER_ID = "OLD_USER_ID";
	//risk eventType
	public static final String EVENT_FINANCE_LIMIT = "event_finance_limit";
	public static final String EVENT_FINANCE_COUNT = "event_finance_count";
	public static final String EVENT_FINANCE_LIMIT_WEAK = "event_finance_limit_weak";
	
	public static final String RES_GAME_AWARD_COUNT_LIMIT           = "GAME_AWARD_COUNT_LIMIT";//游戏中奖数量总限制
	//续期天数限制
	public static final String RES_RENEWAL_DAY_LIMIT = "RENEWAL_DAY_LIMIT";
	//允许续期的天数
	public static final String RES_ALLOW_RENEWAL_DAY = "ALLOW_RENEWAL_DAY";
	//续期的距离预计还款日的最小天数差
	public static final String RES_BETWEEN_DUEDATE = "BETWEEN_DUEDATE";
	//未还款金额限制，只有在未还款金额大于这个金额时才能续期
	public static final String RES_AMOUNT_LIMIT = "AMOUNT_LIMIT";
	//央行基准利率
	public static final String RES_BASE_BANK_RATE = "BASE_BANK_RATE";
	//借钱最高倍数
	public static final String RES_BORROW_CASH_BASE_BANK_DOUBLE = "BORROW_CASH_BASE_BANK_DOUBLE";
	//提现开关
	public static final String RES_CASH_RECORD_SWITCH = "CASH_RECORD_SWITCH";
	//信用卡认证
	public static final String RES_CREDIT_CARD_SWITCH = "CREDIT_CARD_SWITCH";
	// 为了审核定义字段
	public static final String RES_IS_FOR_AUTH = "IS_FOR_AUTH";

	// 免审核信用分
	public static final String RES_DIRECT_TRANS_CREDIT_SCORE = "DIRECT_TRANS_CREDIT_SCORE";
	// 风控
	public static final String REGIST_TONGDUN_SWITCH = "regist.tongdun.switch";
	//每月还款日期
	public static final String RES_REPAYMENT_DATE = "REPAYMENT_DATE";
	
	//客服电话
	public static final String RES_COMSUMER_PHONE = "CONSUMER_PHONE";

	// config key
	public static final String CONFKEY_CHECK_SIGN_SWITCH = "fbapi.check.sign.switch";
	public static final String CONFKEY_JPUSH_APPKEY = "fbapi.jpush.appkey";
	public static final String CONFKEY_JPUSH_SECRET = "fbapi.jpush.secret";
	public static final String CONFKEY_AES_KEY = "fbapi.aes.password";
	public static final String CONFKEY_INVELOMENT_TYPE = "fbapi.inveloment.type";
	public static final String CONFKEY_ZHIMA_APPID = "fbapi.zhima.appid";
	public static final String CONFKEY_ZHIMA_PUBKEY = "fbapi.zhima.public.key";
	public static final String CONFKEY_ZHIMA_PRIKEY = "fbapi.zhima.private.key";
	public static final String CONFKEY_TONGDUN_PARTNER_HOST = "fbapi.tongdun.partner.host";
	public static final String CONFKEY_TONGDUN_PARTNER_CODE = "fbapi.tongdun.partner.code";
	public static final String CONFKEY_TONGDUN_PARTNER_KEY = "fbapi.tongdun.partner.key";
	
	public static final String CONFKEY_THIRD_BOLUOME_APPKEY = "third.offer.boluome.appKey";
	

	public static final String CONFKEY_TONGDUN_PARTNER_WEBHOST = "fbapi.tongdun.partner.webhost";

	public static final String CONFKEY_TONGDUN_APP_NAME = "fbapi.tongdun.app.name";
	public static final String CONFKEY_YOUDUN_HOST = "fbapi.youdun.host";
	public static final String CONFKEY_YOUDUN_PUBKEY = "fbapi.youdun.pubkey";
	public static final String CONFKEY_YOUDUN_SECURITYKEY = "fbapi.youdun.securitykey";
	public static final String CONFKEY_YOUDUN_NOTIFY = "fbapi.youdun.notify";
	public static final String CONFKEY_TAOBAO_BCDS_URL = "fbapi.taobao.bcds.url";
	public static final String CONFKEY_TAOBAO_BCDS_APPID = "fbapi.taobao.bcds.appid";
	public static final String CONFKEY_TAOBAO_BCDS_SECRET = "fbapi.taobao.bcds.secret";

	public static final String CONFKEY_TAOBAO_LIANMENG_URL = "fbapi.taobao.lianmeng.url";
	public static final String CONFKEY_TAOBAO_LIANMENG_APPID = "fbapi.taobao.lianmeng.appid";
	public static final String CONFKEY_TAOBAO_LIANMENG_SECRET = "fbapi.taobao.lianmeng.secret";
	public static final String CONFKEY_SMS_DHST_PASSWORD = "fbapi.sms.dhst.password";
	public static final String CONFKEY_TAOBAO_TBK_ITEM_GET_FIELDS = "taobao.tbk.item.get.fields";
	public static final String CONFKEY_TAOBAO_TBK_ITEM_GET = "taobao.tbk.item.info.get";

	public static final String CONFKEY_TAOBAO_TAE_ITEM_LIST_FIELDS = "taobao.tae.item.list.fields";
	public static final String CONFKEY_TAOBAO_TAE_ITEM_DETAIL_GET_FIELDS = "taobao.tae.item.detail.get.fields";
	public static final String CONFKEY_TAOBAO_ICON_COMMON_LOCATION = "http://img02.taobaocdn.com/bao/uploaded/";
	public static final String CACHEKEY_BORROWCASHNO_LOCK = "fbapi_borrow_cash_no_lock";
	public static final String CACHEKEY_BORROWCASHNO = "fbapi_borrow_cash_no";
	public static final String CACHEKEY_REPAYCASHNO_LOCK = "fbapi_repay_cash_no_lock";
	public static final String CACHEKEY_REPAYCASHNO = "fbapi_repay_cash_no";
	public static final String CACHEKEY_ORDERNO_LOCK = "ala_order_lock";
	public static final String CACHEKEY_ORDERNO = "ala_order";
	public static final String CACHEKEY_BORROWNO_LOCK = "ala_borrow_no_lock";
	public static final String CACHEKEY_BORROWNO = "ala_borrow_no";
	public static final String CACHEKEY_REPAYNO_LOCK = "ala_repay_no_lock";
	public static final String CACHEKEY_REPAYNO = "ala_repay_no";
	public static final String CACHEKEY_REFUND_NO_LOCK = "ala_refund_no_lock";
	public static final String CACHEKEY_REFUND_NO = "ala_brefund_no";

	public static final String CONFIG_KEY_LOCK_TRY_TIMES = "fbapi.sync.lock.try.times";
	public static final String CONFKEY_KXG_URL_CHARGE = "fbapi.kxg.url.charge";
	public static final String CONFKEY_KXG_PASSWORD = "fbapi.kxg.password";
	public static final String CONFKEY_KXG_PAY_PASSWORD = "fbapi.kxg.pay.password";
	public static final String CONFKEY_KXG_KEY = "fbapi.kxg.key";
	public static final String CONFKEY_BILL_CREATE_TIME = "fbapi.bill.create.time";
	public static final String CONFKEY_BILL_REPAY_TIME = "fbapi.bill.repay.time";

	public static final String CACHEKEY_ORDER_PAY_NO_LOCK = "ala_order_pay_no_lock";
	public static final String CACHEKEY_ORDER_PAY_NO = "ala_order_pay_no";

	// 发送邮箱
	public static final String EMAIL_SEND_USERNAME = "fbapi.email.username";
	public static final String EMAIL_SEND_PWD = "fbapi.email.pwd";

	public static final String CONFKEY_WX_APPID = "fbapi.wx.appid";
	public static final String CONFKEY_WX_MCHID = "fbapi.wx.mchid";
	public static final String CONFKEY_WX_KEY = "fbapi.wx.key";
	public static final String CONFKEY_WX_CERTPATH = "fbapi.wx.certpath";
	public static final String CONFKEY_NOTIFY_HOST = "fbapi.notify.host";
	public static final String CONFKEY_RISK_URL = "fbapi.risk.url";
	public static final String CONFKEY_UPS_URL = "fbapi.ups.url";

	// 菠萝觅
	public static final String CONFKEY_BOLUOME_APPKEY = "fbapi.boluome.appkey";
	public static final String CONFKEY_BOLUOME_SECRET = "fbapi.boluome.secret";
	public static final String CONFKEY_BOLUOME_API_URL = "fbapi.boluome.api.url";
	public static final String CONFKEY_BOLUOME_PUSH_PAY_URL = "fbapi.boluome.push.pay.url";
	public static final String CONFKEY_BOLUOME_PUSH_REFUND_URL = "fbapi.boluome.push.refund.url";
	public static final String CONFKEY_BOLUOME_ORDER_SEARCH_URL = "fbapi.boluome.order.search.url";
	public static final String CONFKEY_BOLUOME_ORDER_CANCEL_URL = "fbapi.boluome.order.cancel.url";
	public static final String CONFKEY_BOLUOME_COUPON_URL = "fbapi.boluome.coupon.url";

	// 依图
	public static final String CONFKEY_YITU_URL = "fbapi.yitu.url";
	public static final String CONFKEY_YITU_ID = "fbapi.yitu.id";
	public static final String CONFKEY_YITU_KEY = "fbapi.yitu.key";
	public static final String CONFKEY_YITU_DEFINED_CONTENT = "fbapi.yitu.definedContent";
	public static final String CONFKEY_YITU_PEM_PATH = "fbapi.yitu.pemPath";
    public static final String CACHEKEY_YITU_FACE_SIMILARITY                   = "yitu_face_similarity$";

    //face++
    public static final String CONFKEY_FACE_PLUS_ID_CARD_URL = "fbapi.face.plus.id.card.url";
    public static final String CONFKEY_FACE_PLUS_FACE_LIVING_URL = "fbapi.face.plus.face.living.url";
    public static final String CONFKEY_FACE_PLUS_APPKEY = "fbapi.face.plus.appkey";
    public static final String CONFKEY_FACE_PLUS_SECRET = "fbapi.face.plus.secret";
    
    
	// 三方接口调用限制
	public static final String API_CALL_LIMIT = "API_CALL_LIMIT";

	//图片验证码
	public static final String IMAGE_CODE_COOKIE_NAME = "IMAGE_CODE_NAME";
	public static final String IMAGE_CODE_COOKIE_PASSWORD = "IMAGE_VERIFY_CODE_PASSWORD";
	
	public static final String VIRTUAL_CODE = "virtualCode";
	public static final String VIRTUAL_AMOUNT = "virtualAmount";

	//商品AES解密的password
	public static final String TRADE_AES_DECRYPT_PASSWORD = "trade";
	
	//人脸识别类型
	public static final String FACE_TYPE = "FACE_TYPE";
	
	//H5用户以及token cookie
	public static final String H5_USER_NAME_COOKIES_KEY = "userName";
	public static final String H5_USER_TOKEN_COOKIES_KEY = "token";
	
	public static final String H5_CACHE_USER_NAME_COOKIES_KEY = "h5_cookie_userName";
	public static final String H5_CACHE_USER_TOKEN_COOKIES_KEY = "h5_cookie_token";
	
	public static final String TIRPLE_DES_KEY             = "DO3Rz7we8IW5zb2m";
	public static final String DEFAULT_CODE               = "UTF-8";
	public static final String H5_OPEN_ID_COOKIES_KEY = "openid";
	
	

	//商圈
	public static final String DEFAULT_SALT = "51fb";
	public static final int DEFAULT_DIGEST_TIMES = 1024;
	public static final String SHA1 = "SHA-1";
	public static final String TRADE_LOGIN_BUSINESSID = "trade_login_businessId_";

	public static final String CACHEKEY_BORROW_DELIVER_MONEY_LOCK = "ala_bororw_deliver_money_lock";

	public static final String CACHEKEY_BUILD_BOLUOME_ORDER_LOCK = "ala_boluome_build_order_lock";
	//催收系统
	public static final String CONFKEY_COLLECTION_URL = "fbapi.collection.url";

}
