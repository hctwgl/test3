package com.ald.fanbei.api.common;


/**
 * 
 *@类Constants.java 的实现描述：常量累
 *@author 陈金虎 2017年1月16日 下午11:17:10
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Constants {
    
    public static final long   SECOND_OF_TEN_MINITS                   = 10 * 60l;
    public static final long   SECOND_OF_HALF_HOUR                    = 30 * 60l;
    public static final long   SECOND_OF_AN_HOUR                      = 60 * 60l;
    public static final long   SECOND_OF_ONE_DAY                      = 24 * 60 * 60l;
    public static final long   SECOND_OF_ONE_WEEK                     = 90 * 24 * 60 * 60l;
    
    public static final int   MINITS_OF_2HOURS                        = 120;
    public static final int   MINITS_OF_HALF_HOUR                     = 30;
    
    public static final int   MONTH_OF_YEAR                    		  = 12;
//    
    public static final String REQ_PARAM_NODE_SYSTEM                  ="system";
    public static final String REQ_PARAM_NODE_METHOD                  ="method";
    public static final String REQ_PARAM_NODE_PARAMS                  ="params";
    public static final String REQ_SYS_NODE_ID                        ="id";
    public static final String REQ_SYS_NODE_VERSION                   ="appVersion";
    public static final String REQ_SYS_NODE_USERNAME                  ="userName";
    public static final String REQ_SYS_NODE_SIGN                      ="sign";
    public static final String REQ_SYS_NODE_TIME                      ="time";
    public static final String REQ_SYS_NODE_NETTYPE                   ="netType";
    
    public static final String REQ_SYS_NODE_ID_FOR_QQ                 ="id";
    public static final String REQ_SYS_NODE_VERSION_FOR_QQ            ="appversion";
    public static final String REQ_SYS_NODE_USERNAME_FOR_QQ           ="username";
    public static final String REQ_SYS_NODE_SIGN_FOR_QQ               ="sign";
    public static final String REQ_SYS_NODE_TIME_FOR_QQ               ="time";
    public static final String REQ_SYS_NODE_NETTYPE_FOR_QQ            ="nettype";
    
    
    
    public static final String DEFAULT_ENCODE                        = "UTF-8";
    public static final String SWITCH_ON                              = "1";
    public static final String SWITCH_OFF                             = "0";
    public static final String YES                                    = "yes";
    public static final String NO                                     = "no";
    public static final String COMMA                                  = ",";
    public static final String DEFAULT_YEAR                       	  = "year";
    public static final String DEFAULT_MONTH                       	  = "month";
    public static final String DEFAULT_NPER                       	  = "nper";
    public static final String DEFAULT_RATE                       	  = "rate";
    
    public static final Long   INVITE_START_VALUE                     = 1679625L;
    public static final String INVELOMENT_TYPE_TEST                   = "test";
    public static final String INVELOMENT_TYPE_ONLINE                 = "online";
    
    public static final double DEFAULT_CHARGE_MIN 					  = 1;
    public static final double DEFAULT_CHARGE_MAX 					  = 50;
    public static final int DEFAULT_CASH_DEVIDE					  	  = 2;
    public static final String DEFAULT_BORROW_CASH_NAME				  = "现金借款";
    public static final String DEFAULT_MOBILE_CHARGE_NAME			  = "手机充值";
    public static final String DEFAULT_BORROW_PURPOSE				  = "借款";
    public static final String DEFAULT_CASH_PURPOSE				  	  = "提现";
    public static final String DEFAULT_REFUND_PURPOSE				  = "退款";
    public static final String DEFAULT_PAY_PURPOSE				  	  = "付款";
    public static final String DEFAULT_WX_PAY_NAME					  = "微信";
    public static final String DEFAULT_USER_ACCOUNT					  = "账户余额";
    public static final String DEFAULT_SERVICE_PHONE				  =	"0571-88193918";
    public static final String DEFAULT_REPAYMENT_NAME				  =	"主动还款-";
    public static final String DEFAULT_BRAND_SHOP					  = "品牌订单支付";
    public static final String DEFAULT_REPAYMENT_NAME_BORROW_CASH	  =	"借钱还款";

    //cache keys
    public static final String CACHEKEY_USER_TOKEN                    = "user_token$";
    public static final String CACHEKEY_APK_NEWEST_VERSION            = "apk_newest_version$";
    public static final String CACHEKEY_BORROW_CASH                   = "borrow_cash$";
    public static final String CACHEKEY_BORROW_CONSUME                = "borrow_consume$";
    
    
    //res type
    public static final String RES_APP_QRCODE_URL                 	  = "APP_QRCODE_URL";
    public static final String RES_APP_QRCODE_GENERATE_URL            = "APP_QRCODE_GENERATE_URL";
    public static final String RES_APP_LOGIN_FAILED_LOCK_HOUR         = "APP_LOGIN_FAILED_LOCK_HOUR";
    public static final String RES_APP_CALL_CENTER_MESSAGE         	  = "APP_CALL_CENTER_MESSAGE";
    public static final String RES_APP_CALL_CENTER_MESSAGE_QQ    	  = "QQ";
    public static final String RES_APP_CALL_CENTER_MESSAGE_WX    	  = "WX";
    public static final String RES_APP_CALL_CENTER_MESSAGE_ONLINE     = "ONLINE";
    public static final String RES_APP_CALL_CENTER_MESSAGE_TEL    	  = "TEL";
    public static final String RES_APP_CALL_CENTER_MESSAGE_QQGROUP    = "QQGROUP";
    public static final String RES_THIRD_GOODS_REBATE_RATE			  = "THIRD_GOODS_REBATE_RATE";
    public static final String RES_BORROW_CASH_RATE_DESC			  = "BORROW_CASH_RATE_DESC";
    public static final String RES_BORROW_CONSUME					  = "BORROW_CONSUME";
    public static final String RES_BORROW_CASH					  	  = "BORROW_CASH";
    public static final String RES_BORROW_RATE				  	  	  = "BORROW_RATE";
    public static final String RES_BRAND_SHOP				  	  	  = "BRAND_SHOP";
    public static final String RES_CREDIT_SCORE_AMOUNT				  = "CREDIT_SCORE_AMOUNT";
    public static final String RES_CREDIT_SCORE				  	  	  = "CREDIT_SCORE";
    //免审核信用分
    public static final String RES_DIRECT_TRANS_CREDIT_SCORE		  = "DIRECT_TRANS_CREDIT_SCORE";
    
    //config key
    public static final String CONFKEY_CHECK_SIGN_SWITCH              = "fbapi.check.sign.switch";
    public static final String CONFKEY_JPUSH_APPKEY                   = "fbapi.jpush.appkey";
    public static final String CONFKEY_JPUSH_SECRET                   = "fbapi.jpush.secret";
    public static final String CONFKEY_AES_KEY                        = "fbapi.aes.password";
    public static final String CONFKEY_INVELOMENT_TYPE                = "fbapi.inveloment.type";
    public static final String CONFKEY_ZHIMA_APPID                    = "fbapi.zhima.appid";
    public static final String CONFKEY_ZHIMA_PUBKEY                   = "fbapi.zhima.public.key";
    public static final String CONFKEY_ZHIMA_PRIKEY                   = "fbapi.zhima.private.key";
    public static final String CONFKEY_TONGDUN_PARTNER_HOST           = "fbapi.tongdun.partner.host";
    public static final String CONFKEY_TONGDUN_PARTNER_CODE           = "fbapi.tongdun.partner.code";
    public static final String CONFKEY_TONGDUN_PARTNER_KEY            = "fbapi.tongdun.partner.key";
    public static final String CONFKEY_TONGDUN_APP_NAME               = "fbapi.tongdun.app.name";
    public static final String CONFKEY_YOUDUN_HOST       		      = "fbapi.youdun.host";
    public static final String CONFKEY_YOUDUN_PUBKEY                  = "fbapi.youdun.pubkey";
    public static final String CONFKEY_YOUDUN_SECURITYKEY             = "fbapi.youdun.securitykey";
    public static final String CONFKEY_YOUDUN_NOTIFY                  = "fbapi.youdun.notify";
    public static final String CONFKEY_TAOBAO_BCDS_URL			      = "fbapi.taobao.bcds.url";
    public static final String CONFKEY_TAOBAO_BCDS_APPID			  = "fbapi.taobao.bcds.appid";
    public static final String CONFKEY_TAOBAO_BCDS_SECRET			  = "fbapi.taobao.bcds.secret";
    public static final String CONFKEY_SMS_DHST_PASSWORD			  = "fbapi.sms.dhst.password";
    public static final String CONFKEY_TAOBAO_TBK_ITEM_GET_FIELDS	  = "taobao.tbk.item.get.fields";
    public static final String CONFKEY_TAOBAO_TAE_ITEM_LIST_FIELDS	  = "taobao.tae.item.list.fields";
    public static final String CONFKEY_TAOBAO_TAE_ITEM_DETAIL_GET_FIELDS = "taobao.tae.item.detail.get.fields";
    public static final String CONFKEY_TAOBAO_ICON_COMMON_LOCATION	  = "http://img02.taobaocdn.com/bao/uploaded/";
    public static final String CACHEKEY_ORDERNO_LOCK				  = "ala_order_lock";
    public static final String CACHEKEY_ORDERNO    				  	  = "ala_order";
    public static final String CACHEKEY_BORROWNO_LOCK				  = "ala_borrow_no_lock";
    public static final String CACHEKEY_BORROWNO    				  = "ala_borrow_no";
    public static final String CACHEKEY_REPAYNO_LOCK				  = "ala_repay_no_lock";
    public static final String CACHEKEY_REPAYNO    				      = "ala_repay_no";
    public static final String CONFIG_KEY_LOCK_TRY_TIMES              = "fbapi.sync.lock.try.times";
    public static final String CONFKEY_KXG_PASSWORD					  = "fbapi.kxg.password";
    public static final String CONFKEY_KXG_PAY_PASSWORD				  = "fbapi.kxg.pay.password";
    public static final String CONFKEY_KXG_KEY				  		  = "fbapi.kxg.key";
    public static final String CONFKEY_BILL_CREATE_TIME 			  = "fbapi.bill.create.time";
    public static final String CONFKEY_BILL_REPAY_TIME				  = "fbapi.bill.repay.time";
    //发送邮箱
    public static final String EMAIL_SEND_USERNAME            = "fbapi.email.username";
    public static final String EMAIL_SEND_PWD       = "fbapi.email.pwd";
    
    public static final String CONFKEY_WX_APPID				  		 = "fbapi.wx.appid";
    public static final String CONFKEY_WX_MCHID						 = "fbapi.wx.mchid";
    public static final String CONFKEY_WX_KEY						 = "fbapi.wx.key";
    public static final String CONFKEY_WX_CERTPATH					 = "fbapi.wx.certpath";
    public static final String CONFKEY_NOTIFY_HOST					 = "fbapi.notify.host";
    public static final String CONFKEY_RISK_URL					 	 = "fbapi.risk.url";
    public static final String CONFKEY_UPS_URL						 = "fbapi.ups.url";
    //菠萝觅
    public static final String CONFKEY_BOLUOME_APPKEY                = "fbapi.boluome.appkey";
    public static final String CONFKEY_BOLUOME_SECRET                = "fbapi.boluome.secret";
    public static final String CONFKEY_BOLUOME_API_URL               = "fbapi.boluome.api.url";
    public static final String CONFKEY_BOLUOME_PUSH_PAY_URL          = "fbapi.boluome_push_pay_url";
    public static final String CONFKEY_BOLUOME_PUSH_REFUND_URL       = "fbapi.boluome.push_refund_url";
    
}
