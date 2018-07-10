package com.ald.fanbei.api.common;

import java.math.BigDecimal;

/**
 *
 * @类Constants.java 的实现描述：常量累
 * @author 陈金虎 2017年1月16日 下午11:17:10
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Constants {
	public static final String XGXY_REQ_CODE = "200";

	public static final long SECOND_OF_TEN_MINITS = 10 * 60l;
	public static final long SECOND_OF_ONE_MINITS = 60l;
	public static final long SECOND_OF_FIVE_MINITS = 5 * 60l;
	public static final long SECOND_OF_TEN = 10l;//10秒
	public static final long SECOND_OF_FIFTEEN = 15l;//15秒
	public static final long SECOND_OF_THREE = 30l;// 30秒
	public static final long SECOND_OF_HALF_HOUR = 30 * 60l;
	public static final long SECOND_OF_AN_HOUR = 60 * 60l;
	public static final long SECOND_OF_ONE_DAY = 24 * 60 * 60l;
	public static final long SECOND_OF_HALF_DAY = 12 * 60 * 60l;
	public static final int SECOND_OF_HALF_HOUR_INT = 30 * 60;
	public static final int SECOND_OF_AN_HOUR_INT = 60 * 60;

	public static final long MINITS_OF_FIVE = 5 * 60l; // 5分钟
	public static final long SECOND_OF_ONE_WEEK = 7 * 24 * 60 * 60l;
	public static final long SECOND_OF_ONE_MONTH = 30 * 24 * 60 * 60l;

	public static final int MINITS_OF_2HOURS = 120;
	public static final int MINITS_OF_HALF_HOUR = 30;
	public static final int MINITS_OF_SIXTY = 1;//1分钟
	public static final int MONTH_OF_YEAR = 12;
	
	public static final long MINITS_OF_TWO = 60*2;//5分钟

	//51公积金token失效时间
	public static final long SECOND_OF_ONE_HALF_HOUR =90 * 60l;//一个半小时

	// 商品详情图片信息包含数据总计部分
	public static final int GOODSDETAIL_PIC_PARTS = 3;
	// 自营商品超时支付时间-小时
	public static final int SELFSUPPORT_PAY_TIMEOUT_HOUR = 1;
	public static final BigDecimal DECIMAL_MONTH_OF_YEAR = new BigDecimal(Constants.MONTH_OF_YEAR);
	// 订单下单到截止支付的时间间隔小时
	public static final int ORDER_PAY_TIME_LIMIT = 24;//改为8小时
	// 申请售后时间限制要求
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
	public static final String OUT_DATETIME = "outdatetime";
	public static final String PAY_DATETIME = "paydatetime";
	public static final String DEFAULT_NPER = "nper";
	public static final String DEFAULT_RATE = "rate";
	/** 免息期数json key **/
	public static final String DEFAULT__FREENPER = "freeNper";
	/** 每期还款金额位数 **/
	public static final int HALFUP_DIGIT = 2;
	/** 是否免息是否还款状态--免息 **/
	public static final String ISFREEINTEREST_Y = "Y";
	/** 是否免息是否还款状态--不免息 **/
	public static final String ISFREEINTEREST_N = "N";
	/** 三方调用 ，例如还款等调用时，事务内出现自定义异常时，向上返回map对应key值 **/
	public static final String THIRD_REQ_EXCEP_KEY = "fanbei_third_reqexp_key";

	// 用户未完成订单
	public static final String NO_FINISH_ORDER = "NO_FINISH_ORDER";
	public static final String FIRST_TIME = "FIRST_TIME";

	public static final String CONF_KEY_INVELOMENT_TYPE = "fbapi.inveloment.type";
	public static final String FILE_CLOUD_PATH = "http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/";
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
	public static final String DEFAULT_LOAN_PURPOSE = "贷款";
	public static final String DEFAULT_CASH_PURPOSE = "提现";
	public static final String DEFAULT_REFUND_PURPOSE = "退款";
	public static final String DEFAULT_PAY_PURPOSE = "付款";
	public static final String DEFAULT_WX_PAY_NAME = "微信";
	public static final String DEFAULT_ZFB_PAY_NAME = "支付宝";
	public static final String DEFAULT_OFFLINE_PAY_NAME = "线下还款";
	public static final String DEFAULT_USER_ACCOUNT = "账户余额";
	public static final String DEFAULT_BANK_PAY = "银行卡";
	public static final String DEFAULT_SERVICE_PHONE = "0571-88193918";
	public static final String DEFAULT_REPAYMENT_NAME = "主动还款-账单ID:";
	public static final String DEFAULT_BRAND_SHOP = "品牌订单支付";
	public static final String DEFAULT_SELFSUPPORT_SHOP = "自营商品支付";

	public static final String DEFAULT_REPAYMENT_NAME_BORROW_CASH = "主动还款";
	public static final String DEFAULT_REPAYMENT_NAME_BORROW_RECYCLE = "主动支付";

	public static final String BORROW_REPAYMENT_NAME_OFFLINE = "线下还款";
	public static final String COLLECTION_BORROW_REPAYMENT_NAME_OFFLINE = "催收线下还款";
	public static final String BORROW_REPAYMENT_NAME_AUTO = "代扣付款";
	public static final String BORROW_REPAYMENT_RECYCLE_NAME_AUTO = "代扣支付";

	public static final String DEFAULT_RENEWAL_NAME_BORROW_CASH = "续费支付";
	// 支付失败信息
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
	public static final String CACHEKEY_GAME_INFO = "game_info$";
	public static final String CACHEKEY_GAMECONF_INFO = "gameconf_info$";
	public static final String CACHEKEY_LATESTAWARD_LIST = "latest_award_list$";
	public static final String CACHEKEY_LATEST_GAMEERSULT_LIST = "latest_gameresult_list$";
	public static final String CACHEKEY_COUPON_INFO = "coupon$";
	public static final String CACHEKEY_BORROW_CONSUME_OVERDUE = "borrow_consume_overdue$";
	public static final String CACHEKEY_USER_CONTACTS = "user_contacts$";
	public static final String CACHEKEY_USER_LOGIN_WIFI_MAC = "login_wifi_mac$";
	public static final String CACHEKEY_PROMOTIONCHANBELPOINT = "promotion_channel_point$";
	public static final String CACHEKEY_CHANNEL_IMG_CODE_PREFIX = "channel_img_code$";
	public static final String CACHEKEY_CHANGE_MOBILE = "change_mobile";
	public static final String CACHEKEY_REDRAIN_MISC = "redrain_misc";
	public static final String CACHEKEY_REDRAIN_COUNTERS = "redrain_counters";
	public static final String CACHEKEY_REDRAIN_SINK = "redrain_sink";
	// 实名认证缓存key前缀
	public static final String CACHEKEY_REAL_AUTH_CITIZEN_CARD_PREFFIX = "real_auth_citizen_card$";
	public static final String CACHEKEY_REAL_AUTH_REAL_NAME_PREFFIX = "real_auth_real_name$";
	public static final String CACHEKEY_REAL_AUTH_PASS_PREFFIX = "real_auth_pass$";
	public static final String CACHEKEY_REAL_AUTH_MOBILE_INFO 				= "real_auth_mobile_info";

	// 查询用户是否结果款，1代表已借款
	public static final String HAVE_BORROWED = "have_borrowed$";
	public static final String BOLUOME_LOGIN_ERROR_TIMES = "boluome_login_error_times$";

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
	public static final String RES_BORROW_CONSUME_VIP = "BORROW_CONSUME_VIP";
	public static final String RES_BORROW_CONSUME_OVERDUE = "BORROW_CONSUME_OVERDUE";
	public static final String RES_BORROW_CASH = "BORROW_CASH";
	public static final String RES_BORROW_RATE = "BORROW_RATE";
	public static final String RES_BORROW_CASH_RANGE = "BORROW_CASH_RANGE";
	public static final String RES_BORROW_CASH_LIMIT_AMOUNT = "BORROW_CASH_LIMIT_AMOUNT";
	public static final String RES_BORROW_TRADE = "BORROW_TRADE";
	public static final String RES_BORROW_CASH_POUNDAGE_RATE = "RES_BORROW_CASH_POUNDAGE_RATE";
	public static final String RES_BORROW_CASH_POUNDAGE_TIME = "RES_BORROW_CASH_POUNDAGE_TIME";
	public static final String GOODS_DETAIL_RECYCLE_FLAG = "GOODS_DETAIL_RECYCLE_FLAG";

	public static final String RES_BRAND_SHOP = "BRAND_SHOP";
	public static final String RES_CREDIT_SCORE_AMOUNT = "CREDIT_SCORE_AMOUNT";
	public static final String RES_CREDIT_SCORE = "CREDIT_SCORE";
	public static final String APPLY_BRROW_CASH_WHITE_LIST = "APPLY_BRROW_CASH_WHITE_LIST";
	public static final String RES_REFUND_RATE = "REFUND_RATE";
	public static final String RES_BORROW_CASH_POUNDAGE = "BORROW_CASH_POUNDAGE";
	public static final String RENEWAL_CAPITAL_RATE = "RENEWAL_CAPITAL_RATE";
	public static final String RES_APP_POP_IMAGE = "APP_POP_IMAGE"; // 首页弹窗配置
	public static final String RES_GAME_AWARD_OF_CATCH_DOLL = "GAME_AWARD_OF_CATCH_DOLL"; // 抓娃娃游戏发奖
	public static final String RES_GAME_CATCH_DOLL_CLIENT_RATE = "GAME_CATCH_DOLL_CLIENT_RATE";// 抓娃娃游戏客户端抓中概率
	public static final String RES_OLD_USER_ID = "OLD_USER_ID";
	// risk eventType
	public static final String EVENT_FINANCE_LIMIT = "event_finance_limit";
	public static final String EVENT_FINANCE_COUNT = "event_finance_count";
	public static final String EVENT_FINANCE_LIMIT_WEAK = "event_finance_limit_weak";
	public static final String EVENT_LOGIN_SYN = "event_login_syn";
	public static final String EVENT_LOGIN_ASY = "event_login_asy";
	public static final String EVENT_RIGISTER_ASY = "event_register_asy";

	public static final String RES_GAME_AWARD_COUNT_LIMIT = "GAME_AWARD_COUNT_LIMIT";// 游戏中奖数量总限制
	// 续期天数限制
	public static final String RES_RENEWAL_DAY_LIMIT = "RENEWAL_DAY_LIMIT";
	// 允许续期的天数
	public static final String RES_ALLOW_RENEWAL_DAY = "ALLOW_RENEWAL_DAY";
	// 新版允许续期的天数
	public static final String RES_ALLOW_RENEWAL_DAY_NEW = "ALLOW_RENEWAL_DAY_NEW";
	// 续期的距离预计还款日的最小天数差
	public static final String RES_BETWEEN_DUEDATE = "BETWEEN_DUEDATE";
	// 未还款金额限制，只有在未还款金额大于这个金额时才能续期
	public static final String RES_AMOUNT_LIMIT = "AMOUNT_LIMIT";
	// 央行基准利率
	public static final String RES_BASE_BANK_RATE = "BASE_BANK_RATE";
	// 借钱最高倍数
	public static final String RES_BORROW_CASH_BASE_BANK_DOUBLE = "BORROW_CASH_BASE_BANK_DOUBLE";
	// 提现开关
	public static final String RES_CASH_RECORD_SWITCH = "CASH_RECORD_SWITCH";
	// 信用卡认证
	public static final String RES_CREDIT_CARD_SWITCH = "CREDIT_CARD_SWITCH";
	// 为了审核定义字段
	public static final String RES_IS_FOR_AUTH = "IS_FOR_AUTH";

	// 免审核信用分
	public static final String RES_DIRECT_TRANS_CREDIT_SCORE = "DIRECT_TRANS_CREDIT_SCORE";
	// 风控
	public static final String REGIST_TONGDUN_SWITCH = "regist.tongdun.switch";
	// 每月还款日期
	public static final String RES_REPAYMENT_DATE = "REPAYMENT_DATE";

	// 客服电话
	public static final String RES_COMSUMER_PHONE = "CONSUMER_PHONE";


	//白领贷相关信息
	public static final String WHITE_AGREEMENT = "WHITE_AGREEMENT";
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
	public static final String CONFKEY_TAOBAO_TBK_SHOP_GET_FIELDS = "taobao.tbk.shop.get.fields";
	public static final String CONFKEY_TAOBAO_TBK_ITEM_GET = "taobao.tbk.item.info.get";

	public static final String CONFKEY_TAOBAO_TAE_ITEM_LIST_FIELDS = "taobao.tae.item.list.fields";
	public static final String CONFKEY_TAOBAO_TAE_ITEM_DETAIL_GET_FIELDS = "taobao.tae.item.detail.get.fields";
	public static final String CONFKEY_TAOBAO_ICON_COMMON_LOCATION = "http://img02.taobaocdn.com/bao/uploaded/";
	public static final String CACHEKEY_BORROWCASHNO_LOCK = "fbapi_borrow_cash_no_lock";
	public static final String CACHEKEY_BORROWCASHNO = "fbapi_borrow_cash_no";
	public static final String CACHEKEY_REPAYCASHNO_LOCK = "fbapi_repay_cash_no_lock";
	public static final String CACHEKEY_REPAYCASHNO = "fbapi_repay_cash_no";
	public static final String CACHEKEY_RISKLOGIN = "fbapi_risk_login_no";
	public static final String CACHEKEY_ORDERNO_LOCK = "dsed_order_lock";
	public static final String CACHEKEY_ORDERNO = "dsed_order";
	public static final String CACHEKEY_BORROWNO_LOCK = "dsed_borrow_no_lock";
	public static final String CACHEKEY_BORROWNO = "dsed_borrow_no";
	public static final String CACHEKEY_REPAYNO_LOCK = "dsed_repay_no_lock";
	public static final String CACHEKEY_REPAYNO = "dsed_repay_no";
	public static final String CACHEKEY_REFUND_NO_LOCK = "dsed_refund_no_lock";
	public static final String CACHEKEY_REFUND_NO = "dsed_brefund_no";

	public static final String CONFIG_KEY_LOCK_TRY_TIMES = "fbapi.sync.lock.try.times";
	public static final String CONFKEY_KXG_URL_CHARGE = "fbapi.kxg.url.charge";
	public static final String CONFKEY_KXG_PASSWORD = "fbapi.kxg.password";
	public static final String CONFKEY_KXG_PAY_PASSWORD = "fbapi.kxg.pay.password";
	public static final String CONFKEY_KXG_KEY = "fbapi.kxg.key";
	public static final String CONFKEY_BILL_CREATE_TIME = "fbapi.bill.create.time";
	public static final String CONFKEY_BILL_REPAY_TIME = "fbapi.bill.repay.time";

	public static final String CACHEKEY_ORDER_PAY_NO_LOCK = "dsed_order_pay_no_lock";
	public static final String CACHEKEY_ORDER_PAY_NO = "dsed_order_pay_no";

	// redis -- key
	public static final String RESERVATION_IPHONEX_RESERVATION_COUNT = "reservation_count";
	public static final String RESERVATION_IPHONEX_COUPON1_COUNT = "coupon1_count_";
	public static final String RESERVATION_IPHONEX_COUPON2_COUNT = "coupon2_count_";
	public static final String RESERVATION_IPHONEX_COUPON3_COUNT = "coupon3_count_";
	public static final String RESERVATION_IPHONEX_COUPON_STATUS = "coupon_status_";
	public static final String RESERVATION_IPHONEX_COUPON_ID = "coupon_id";
	public static final String RESERVATION_IPHONEX_SUM_COUNT = "sum_coupon_count_";

	//wrong paypwd times and the previous wrong time.
	public static final String CACHKEY_WRONG_INPUT_PAYPWD_TIMES = "wrong_input_paypwd_times";
	public static final String CACHKEY_THE_LAST_WRONG_PAYPWD_TIME = "the_last_wrong_paypwd_time";

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
	public static final String CONFKEY_XGXY_URL = "desd.xgxy.url";
	public static final String CONFKEY_XGXY_AES_PASSWORD = "dsed.aes.password";

	public static final String CONFKEY_BKL_URL = "fbapi.bkl.url";
	public static final String CONFKEY_ADMIN_URL = "fbapi.admin.url";
	public static final String CONFKEY_BKL_ACCESS_TOKEN = "fbapi.bkl.access.token";

	// 菠萝觅
	public static final String CONFKEY_BOLUOME_APPKEY = "fbapi.boluome.appkey";
	public static final String CONFKEY_BOLUOME_SECRET = "fbapi.boluome.secret";
	public static final String CONFKEY_BOLUOME_API_URL = "fbapi.boluome.api.url";
	public static final String CONFKEY_BOLUOME_SERVER_API_URL = "fbapi.boluome.server.api.url";

	public static final String CONFKEY_BOLUOME_PUSH_PAY_URL = "fbapi.boluome.push.pay.url";
	public static final String CONFKEY_BOLUOME_PUSH_REFUND_URL = "fbapi.boluome.push.refund.url";
	public static final String CONFKEY_BOLUOME_ORDER_SEARCH_URL = "fbapi.boluome.order.search.url";
	public static final String CONFKEY_BOLUOME_ORDER_CANCEL_URL = "fbapi.boluome.order.cancel.url";
	public static final String CONFKEY_BOLUOME_COUPON_URL = "fbapi.boluome.coupon.url";
	public static final String CONFKEY_BOLUOME_ORDER_URL = "fbapi.boluome.api.order.url";

	public static final String CONFKEY_BOLUOME_API_ORDER_URL = "fbapi.boluome.api.order.url";

	// 依图
	public static final String CONFKEY_YITU_URL = "fbapi.yitu.url";
	public static final String CONFKEY_YITU_ID = "fbapi.yitu.id";
	public static final String CONFKEY_YITU_KEY = "fbapi.yitu.key";
	public static final String CONFKEY_YITU_DEFINED_CONTENT = "fbapi.yitu.definedContent";
	public static final String CONFKEY_YITU_PEM_PATH = "fbapi.yitu.pemPath";
	public static final String CACHEKEY_YITU_FACE_SIMILARITY = "yitu_face_similarity$";

	// face++
	public static final String CONFKEY_FACE_PLUS_ID_CARD_URL = "fbapi.face.plus.id.card.url";
	public static final String CONFKEY_FACE_PLUS_FACE_LIVING_URL = "fbapi.face.plus.face.living.url";
	public static final String CONFKEY_FACE_PLUS_APPKEY = "fbapi.face.plus.appkey";
	public static final String CONFKEY_FACE_PLUS_SECRET = "fbapi.face.plus.secret";

	// 三方接口调用限制
	public static final String API_CALL_LIMIT = "API_CALL_LIMIT";

	// 图片验证码
	public static final String IMAGE_CODE_COOKIE_NAME = "IMAGE_CODE_NAME";
	public static final String IMAGE_CODE_COOKIE_PASSWORD = "IMAGE_VERIFY_CODE_PASSWORD";

	public static final String VIRTUAL_CODE = "virtualCode";
	public static final String VIRTUAL_AMOUNT = "amount";
	public static final String VIRTUAL_TOTAL_AMOUNT = "totalAmount";
	public static final String VIRTUAL_RECENT_DAY = "recentDay";
	public static final String VIRTUAL_CHECK = "virtualCheck";
	public static final String VIRTUAL_CHECK_NAME = "virtualCheckName";
	public static final String VIRTUAL_DAY_AMOUNT = "dayAmount";

	// 商品AES解密的password
	public static final String TRADE_AES_DECRYPT_PASSWORD = "trade";
	public static final String DSED_AES_PASSWORD = "dsed.aes.password";

	// 人脸识别类型
	public static final String FACE_TYPE = "FACE_TYPE";
	// 实名认证修改姓名开关
	public static final String SWITCH = "SWITCH";

	// H5用户以及token cookie
	public static final String H5_USER_NAME_COOKIES_KEY = "userName";
	public static final String H5_USER_TOKEN_COOKIES_KEY = "token";

	public static final String H5_CACHE_USER_NAME_COOKIES_KEY = "h5_cookie_userName";
	public static final String H5_CACHE_USER_TOKEN_COOKIES_KEY = "h5_cookie_token";

	public static final String TIRPLE_DES_KEY = "DO3Rz7we8IW5zb2m";
	public static final String DEFAULT_CODE = "UTF-8";
	public static final String H5_OPEN_ID_COOKIES_KEY = "openid";

	// 商圈
	public static final String DEFAULT_SALT = "51fb";
	public static final int DEFAULT_DIGEST_TIMES = 1024;
	public static final String SHA1 = "SHA-1";
	public static final String TRADE_LOGIN_BUSINESSID = "trade_login_businessId_";

	/** ---------------
	 * 缓存Key常量区域
	 * ---------------- */
	public static final String CACHEKEY_BORROW_DELIVER_MONEY_LOCK = "dsed_bororw_deliver_money_lock";
	public static final String CACHEKEY_BUILD_BOLUOME_ORDER_LOCK = "dsed_boluome_build_order_lock";
	public static final String CACHEKEY_APPLY_BORROW_CASH_LOCK = "dsed_apply_borrow_cash_lock";
	public static final String CACHEKEY_APPLY_STRONG_RISK_LOCK = "dsed_apply_strong_risk_lock";
	public static final String CACHEKEY_APPLY_BLD_RISK_LOCK = "dsed_apply_bld_risk_lock";
	public static final String CACHEKEY_APPLY_RENEWAL_LOCK = "cachekey_apply_renewal_lock";
	// 催收系统
	public static final String CONFKEY_COLLECTION_URL = "fbapi.collection.url";
	// 借贷超市签到锁
	public static final String CACHEKEY_LOAN_SUPERMARKET_SIGN_LOCK = "dsed_loan_supermarket_sign_lock$";
	// 借贷超市签到领奖锁
	public static final String CACHEKEY_LOAN_SUPERMARKET_SIGN_AWARD_LOCK = "dsed_loan_supermarket_sign_award_lock$";
	public static final String CACHEKEY_USER_LAY_DAILY_RATE="USER_LAY_DAILY_RATE_";


	// 借钱抽取
	public static final String BORROWCASH_ACTIVITYS_TYPR = "BORROWCASH_ TO_DRAW";
	public static final String BORROWCASH_ACTIVITYS_SECTYPR = "BORROWCASH_ACTIVITYS";

	// 联合登陆相关 现金超人
	public static final String UNIONLOGIN_XJCR_SECRET = "fbapi.unionlogin.xjcr.secret";
	// 联合登陆相关 借点钱
	public static final String UNIONLOGIN_JDQ_SECRET = "fbapi.unionlogin.jdq.secret";

	// 第三方自建开关_是否是爬取商品
	public static final String THIRD_GOODS_TYPE = "third_goods";

	public static final String THIRD_GOODS_IS_WORM_SECTYPE = "third_goods_is_worm";

	// 双十一砍价活动
	public static final String CACHKEY_CUT_PRICE_LOCK = "dsed_cut_price_lock";
	public static final String CACHKEY_WX_TOKEN_LOCK = "dsed_wx_token_lock";
	public static final String CONFKEY_WX_SECRET = "fbadmin.wx.pub.appid";
	//逛逛惊喜返礼金
	public static final String GG_SURPRISE_LOCK = "gg_suprise_lock";
	public static final String GG_COUPON_LOCK = "gg_coupon_lock";
	//自营商城返利
	public static final String SELFSUPPORT_REBATE = "selfsupport_rebate";

	// 双十二秒杀抢券活动
	public static final String CACHKEY_BUY_GOODS_LOCK = "dsed_buy_goods_lock";
	public static final String CACHKEY_GET_COUPON_LOCK = "dsed_get_coupon_lock";
	public static final String CACHKEY_DOUBLE_USER = "double_user";

	// 借钱费率配置相关
	public static final String BORROW_RATE = "BORROW_RATE";
	public static final String BORROW_CASH_POUNDAGE = "BORROW_CASH_POUNDAGE";
	public static final String BORROW_CASH_OVERDUE_POUNDAGE = "BORROW_CASH_OVERDUE_POUNDAGE";
	public static final String BORROW_CASH_INFO_LEGAL = "BORROW_CASH_INFO_LEGAL";
	public static final String BORROW_CASH_INFO_LEGAL_NEW = "BORROW_CASH_INFO_LEGAL_NEW";
	// 首页滚动条
	public static final String HOMEPAGE_TOP_SCROLLBAR = "HOMEPAGE_TOP_SCROLLBAR";
	public static final String BORROW_TOP_SCROLLBAR = "H5_URL";

	// 代扣
	public static final String WITH_HOLD_SWITCH = "WITH_HOLD_SWITCH";
	// 宜信阿福RC4秘钥
	public static final String YIXIN_AFU_PASSWORD = "fbapi.yixinafu.password";
	// 宜信阿福查询用户逾期信息redis中的key前缀
	public static final String YIXIN_AFU_SEARCH_KEY = "yxafu_";

	//	sup游戏充值
	public static final String CONFKEY_SUP_BUSINESS_ID = "fbapi.sup.business.id";
	public static final String CONFKEY_SUP_BUSINESS_KEY = "fbapi.sup.business.key";
	public static final String CONFKEY_SUP_ORDER_DETAILS = "fbapi.sup.order.details";

	public static final int ONE_YEAY_DAYS = 360;
	//爱上街与资产方对接时的平台标识
	public static final String ASSET_SIDE_FANBEI_FLAG = "51fanbei";
	public static final String ASSET_SIDE_EDSPAY_FLAG = "edspay";
	//资产方查询用户借款及逾期信息redis中的key前缀
	public static final String ASSET_SIDE_SEARCH_USER_KEY="assetside_";
	//资产包
	public static final String CACHEKEY_ASSETPACKAGE_LOCK = "dsed_asset_package_lock";
	public static final String CACHEKEY_ASSETPACKAGE_LOCK_VALUE = "dsed_asset_package_lock_value";
	public static final Integer AVG_BORROWCASH_AMOUNT = 1400;//现金贷平均每单金额
	public static final Integer AVG_BORROW_AMOUNT = 200;//消费分期平均每单金额
	public static final Integer AVG_LOAN_AMOUNT = 5000;//白领贷平均每单金额
	public static final String TRADE_CODE_INFO_DEFAULT_KEY = "default";

	public static final String CACHEKEY_COMPLETEORDER_LOCK = "cachekey_completeorder_lock";
	public static final String CACHEKEY_COMPLETEORDER_LOCK_VALUE = "cachekey_completeorder_lock_value";

	//公信宝认证的密钥
	public static final String AUTH_GXB_APPID = "auth.gxb.appid";
	public static final String AUTH_GXB_APPSECURITY = "auth.gxb.appsecurity";
	//51公积金认证
	public static final String AUTH_51FUND_TOKEN = "auth_51fund_token";
	public static final String CONFKEY_NEWFUND_APPKEY = "fbapi.newfund.appkey";
	public static final String CONFKEY_NEWFUND_SECRET = "fbapi.newfund.secret";

	//快递鸟配置
	public static final String KDNIAO_BUSINESSID = "fbadmin.kdniao.businessid";
	public static final String KDNIAO_APIKEY = "fbadmin.kdniao.apikey";


	public static final String ORDER_PAY_ORDER_ID = "order_pay_orderId:";

	//resource 类型
	public static final String USER_SHARE_INFO = "USER_SHARE_INFO";

	public static final String USER_SHARE_INFO_CONFIGURE = "USER_SHARE_INFO_CONFIGURE";
	//resource 类型
	public static final String DEFAULT_AVATAR = "DEFAULT_AVATAR";

	//轮播图
	public static final String PERSONAL_CENTER_BANNER = "PERSONAL_CENTER_BANNER";

	//设备黑名单
	public static final String DEVICE_UUID_BLACK = "DEVICE_UUID_BLACK";


	//访问H5域名
	public static final String H5_REQUEST_URI = "H5_REQUEST_URI";



	public static final String 	FACE_GAME_RED_CONFIG = "face_game_red_config";
	public static final String ORDER_MOBILE_VERIFY_SET = "ORDER_MOBILE_VERIFY_SET";

	public static final String ORDER_MOBILE_VERIFY_QUESTION_SET = "ORDER_MOBILE_VERIFY_QUESTION_SET";

	public static final String CACHKEY_CUT_LEASE_LOCK = "dsed_cut_lease_lock";

	/**
     * 用户租房分期利率
     */
    public static final String BORROW_TENEMENT_RATE = "BORROW_TENEMENT_RATE";

    /**
     * 默认用户租房分期利率（分期利率均为0）
     */
    public static final String BORROW_TENEMENT_RATE_DEFAULT = "BORROW_TENEMENT_RATE_DEFAULT";

	// 租房分期期数
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;
    public static final int SEVEN = 7;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final int TEN = 10;
    public static final int ELEVEN = 11;
    public static final int TWELVE = 12;

    /**
     * 线下商圈租房业务类型
     */
    public static final String ORDER_TYPE_TENEMENT = "TENEMENT";
    //芝麻认证引导弹窗缓存处理对应key标识
  	public static final String ZM_AUTH_POP_GUIDE_CACHE_KEY = "zm_auth_pop_guide_";

	/**
	 * 三周年庆典秒杀活动名称前缀
	 */
	public static final String TAC_SEC_KILL_ACTIVITY_NAME = "TAC_SEC_KILL_ACTIVITY_NAME";

	/**
	 * 三周年庆典秒杀活动每日开始时间（小时）
	 */
	public static final String TAC_SEC_KILL_ACTIVITY_START_TIME = "TAC_SEC_KILL_ACTIVITY_START_TIME";

	/**
	 * 三周年庆典活动
	 */
	public static final String TAC_ACTIVITY = "TAC_ACTIVITY";

	/**
	 * 预售商品定金支付成功发送短信
	 */
	public static final String SMS_TEMPLATE = "SMS_TEMPLATE";
	public static final String SMS_ACTIVITY_RESERVATION_GOODS = "SMS_ACTIVITY_RESERVATION_GOODS";

	/**
	 * 预售商品活动
	 */
	public static final String ACTIVITY_RESERVATION_GOODS = "ACTIVITY_RESERVATION_GOODS";

	//乐享生活节
	public static final String ENJOYLIFE_ACTIVITY_GOODSINFO = "ENJOYLIFE_ACTIVITY_GOODSINFO";
	public static final String ACTIVITY_INFO_GOODSID = "ACTIVITY_INFO_GOODSID";

	// 商品详情服务配置
	public static final String COUPON_SALESERVICE = "COUPON_SALESERVICE";

    public static final String BORROW_FINANCE = "borrow";
    //软弱风控订单特殊标识
    public static final String SOFT_WEAK_VERIFY_ORDER_NO_FLAG = "SWV";

    public static final String BROWSE = "browse";
    public static final String SHOPPING = "shopping";
    public static final String SHARE = "share";
    public static final String VERIFIED = "verified";
    public static final String STRONG_RISK = "strong_risk";
    public static final String LOAN_MARKET_ACCESS = "loan_market_access";

	/**
	 * 未领取
	 */
	public static final Integer TASK_USER_REWARD_STATUS_0 = 0;

	/**
	 * 已领取，明细中默认领取状态
	 */
    public static final Integer TASK_USER_REWARD_STATUS_1 = 1;

	/**
	 * 金币已经自动兑换成零钱
	 */
	public static final Integer TASK_USER_REWARD_STATUS_3 = 3;

	/**
	 * 浏览商品数量任务的配置
	 */
	public static final String BROWSE_TASK = "BROWSE_TASK";

	/**
	 * 浏览商品数量任务的名称
	 */
	public static final String BROWSE_TASK_NAME = "每日浏览商品赚金币";

	/**
	 * 金币兑换任务名称
	 */
	public static final String TASK_COIN_CHANGE_TO_CASH_NAME = "金币兑换零钱";

	/**
	 * 边逛边赚奖励类型
	 */
	public static final int REWARD_TYPE_COIN = 0;
	public static final int REWARD_TYPE_CASH = 1;
	public static final int REWARD_TYPE_COUPON = 2;

	public static final String SIGN_DATE = "SIGN_DATE";

	/**
	 * 签到奖励每日最大提现配置
	 */
	public static final String SIGN_REWARD_MAX_WITHDRAW = "SIGN_REWARD_MAX_WITHDRAW";

	//通知失败次数
	public static final String NOTICE_FAIL_COUNT = "5";
}
