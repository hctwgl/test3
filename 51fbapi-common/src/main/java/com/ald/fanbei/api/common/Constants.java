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
    
    public static final Long   INVITE_START_VALUE                     = 1679625L;
    public static final String INVELOMENT_TYPE_TEST                   = "test";
    public static final String INVELOMENT_TYPE_ONLINE                 = "online";
    
    
    //cache keys
    public static final String CACHEKEY_USER_TOKEN                    = "user_token$";
    public static final String CACHEKEY_APK_NEWEST_VERSION            = "apk_newest_version$";
    
    
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
    
    
    //config key
    public static final String CONFKEY_CHECK_SIGN_SWITCH              = "fbapi.check.sign.switch";
    public static final String CONFKEY_JPUSH_APPKEY                   = "fbapi.jpush.appkey";
    public static final String CONFKEY_JPUSH_SECRET                   = "fbapi.jpush.secret";
    public static final String CONFKEY_AES_KEY                        = "fbapi.aes.password";
    public static final String CONFKEY_INVELOMENT_TYPE                = "fbapi.inveloment.type";
    public static final String CONFKEY_ZHIMA_APPID                    = "fbapi.zhima.appid";
    public static final String CONFKEY_ZHIMA_PUBKEY                   = "fbapi.zhima.public.key";
    public static final String CONFKEY_ZHIMA_PRIKEY                   = "fbapi.zhima.private.key";
    public static final String CONFKEY_TONGDUN_PARTNER_CODE           = "fbapi.tongdun.partner.code";
    public static final String CONFKEY_TONGDUN_PARTNER_KEY            = "fbapi.tongdun.partner.key";
    public static final String CONFKEY_TONGDUN_APP_NAME               = "fbapi.tongdun.app.name";
    public static final String CONFKEY_TAOBAO_BCDS_URL			      = "fbapi.taobao.bcds.url";
    public static final String CONFKEY_TAOBAO_BCDS_APPID			  = "fbapi.taobao.bcds.appid";
    public static final String CONFKEY_TAOBAO_BCDS_SECRET			  = "fbapi.taobao.bcds.secret";
    public static final String CONFKEY_SMS_USERID          			  = "fbapi.sms.userid";
    public static final String CONFKEY_SMS_PASSWORD					  = "fbapi.sms.password";
    public static final String CONFKEY_TAOBAO_TBK_ITEM_GET_FIELDS	  = "taobao.tbk.item.get.fields";
    public static final String CONFKEY_TAOBAO_TAE_ITEM_LIST_FIELDS	  = "taobao.tae.item.list.fields";
    
    
}
