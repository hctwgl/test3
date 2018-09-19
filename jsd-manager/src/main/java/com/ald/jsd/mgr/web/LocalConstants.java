package com.ald.jsd.mgr.web;

import java.nio.charset.Charset;

public class LocalConstants {
	/**
	 * 通用常量
	 */
	public final static Charset UTF_8 = Charset.forName("UTF-8");
	public final static String USER_DEFAULT_PWD= "11111111"; 
	
	/**
	 * 业务常量
	 */
	public final static Integer DONATED_COINS_OF_REGISTER = 500;
	public final static Integer DONATED_BANN_TIMES_OF_REGISTER = 0;
	public final static Integer DONATED_TEXT_BANN_WORDS_LIMIT_OF_REGISTER = 20000;
	
	public final static String DEFAULT_PASSWD = "123456";
	
	/**
	 * Session key name
	 */
	public final static String SESS_KEY_CAPTCHA = "CAPTCHA"; //验证码key
	public final static String SESS_KEY_LOGIN_SMS_CODE = "LOGIN_SMS_CODE";
	
	public final static String SESS_KEY_UID = "UID";
	public final static String SESS_KEY_USERNAME = "USERNAME";
	public final static String SESS_KEY_PHONE = "PHONE";
	
	public final static String CACHE_KEY_LOGIN_SMS = "CACHE_KEY_LOGIN_SMS";
	public final static Long CACHE_KEY_LOGIN_SMS_EXPIRE_SECS = 60*30L;
	
	/**
	 * Cookie key name
	 */
	public final static String COOKIE_KEY_AUTO_LOGIN_TOKEN = "AUTO_LOGIN_TOKEN";
	public final static int COOKIE_AUTO_LOGIN_TOKEN_TIMEOUT_IN_SECOND = 60*60*60*24;
	
	/**
	 * 未找到宝贝扣除基础流量币系数
	 */
	public static final float NON_FOUND_DEDUCT_RATE = 0.5F;
}
