package com.ald.fanbei.api.common;

/**
 *
 * @类Constants.java 的实现描述：常量累
 * @author 陈金虎 2017年1月16日 下午11:17:10
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Constants {

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
	public static final int ONE_YEAY_DAYS = 360;

	public static final int MINITS_OF_2HOURS = 120;
	public static final int MINITS_OF_HALF_HOUR = 30;
	public static final int MINITS_OF_SIXTY = 1;//1分钟
	public static final int MONTH_OF_YEAR = 12;

	public static final String DEFAULT_ENCODE = "UTF-8";

	public static final String FILE_CLOUD_PATH = "http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/";
	public static final String INVELOMENT_TYPE_TEST = "test";
	public static final String INVELOMENT_TYPE_ONLINE = "online";
	public static final String INVELOMENT_TYPE_PRE_ENV = "pre";

	public static final String DEFAULT_BORROW_PURPOSE = "借款";
	public static final String DEFAULT_PAY_PURPOSE = "付款";
	public static final String DEFAULT_REPAYMENT_NAME_BORROW_CASH = "主动还款";
	public static final String DEFAULT_REPAYMENT_NAME_BORROW_RECYCLE = "主动支付";
	public static final String DEFAULT_RENEWAL_NAME_BORROW_CASH = "续费支付";
	public static final String DEFAULT_WITHHOLD_NAME_BORROW_CASH = "代扣还款";

	public static final int DEFAULT_DIGEST_TIMES = 1024;
	public static final String SHA1 = "SHA-1";
	public static final String NOTICE_FAIL_COUNT = "5"; //通知机制失败次数
	

	// -------------- CACHE KEY ZONE start ---------------
	public static final String CACHEKEY_APPLY_BORROW_CASH_LOCK = "jsd_apply_borrow_cash_lock";
	public static final String CACHEKEY_REPAYCASHNO_LOCK = "jsd_repay_cash_no_lock";
	public static final String CACHEKEY_REPAYNO_LOCK = "jsd_repay_no_lock";
	public static final String CACHEKEY_BORROWNO_LOCK = "jsd_borrow_no_lock";
	public static final String CACHEKEY_ORDERNO_LOCK = "jsd_order_lock";
	public static final String CACHEKEY_BORROWCASHNO_LOCK = "jsd_borrow_cash_no_lock";
	
	public static final String CACHEKEY_BORROW_NO = "jsd_borrow_no";
	public static final String CACHEKEY_RENEWAL_NO = "jsd_renewal_no";
	public static final String CACHEKEY_ORDER_NO = "jsd_order_no";
	public static final String CACHEKEY_ORDER_CASH_NO = "jsd_order_cash_no";
	public static final String CACHEKEY_REPAY_NO = "jsd_repay_no";
	public static final String CACHEKEY_OFFLINE_REPAY_NO = "jsd_offline_repay_no";
	
	public static final String CACHEKEY_RISK_LAYER_RATE = "CACHEKEY_RISK_LAYER_RATE";
	public static final String CACHEKEY_BORROW_CURRDAY_ALLAMOUNT = "JSD_BORROW_CURRDAY_ALLAMOUNT";
	// -------------- CACHE KEY ZONE end ---------------
	
	
	// --------------- CONF KEY ZONE start ---------------
	public static final String CONFKEY_INVELOMENT_TYPE = "fbapi.inveloment.type";
	public static final String CONFKEY_LOCK_TRY_TIMES = "fbapi.sync.lock.try.times";
	
	public static final String CONFKEY_NOTIFY_HOST = "fbapi.notify.host";
	public static final String CONFKEY_SECURITY_OVERDUE_TASK = "security.overdue.task";
	public static final String CONFKEY_TASK_ACTIVE_HOST = "jsd.task.active.host";
	
	public static final String CONFKEY_UPS_URL = "fbapi.ups.url";
	public static final String CONFKEY_UPS_PREFFIX = "jsd.ups.preffix";
	public static final String CONFKEY_UPS_MERNO = "jsd.ups.merno";
	public static final String CONFKEY_UPS_PRIVATE_KEY = "jsd.ups.private.key";

	public static final String CONFKEY_XGXY_HOST = "jsd.xgxy.host";
	public static final String CONFKEY_XGXY_AES_PASSWORD = "jsd.xgxy.aes.password";
	public static final String CONFKEY_XGXY_APP_ID = "jsd.xgxy.app.id";
	
	public static final String CONFKEY_COLLECTION_TOKEN = "jsd.collection.token";
	public static final String CONFKEY_COLLECTION_COMPANYID = "jsd.collection.companyId";
	public static final String CONFKEY_COLLECTION_REPORT_URL = "jsd.collection.report.url";// 收发系统
	public static final String CONFKEY_COLLECTION_URL = "jsd.collection.url";// 催收系统
	// --------------- CONF KEY ZONE end ---------------
	
	
	/**
	 * ---------------- RESOURCE 配置名称
	 */
	public static final String JSD_CONFIG = "JSD_CONFIG";
	public static final String JSD_RATE_INFO = "JSD_RATE_INFO";
	public static final String JSD_CONFIG_REVIEW_MODE = "JSD_CONFIG_REVIEW_MODE";


}
