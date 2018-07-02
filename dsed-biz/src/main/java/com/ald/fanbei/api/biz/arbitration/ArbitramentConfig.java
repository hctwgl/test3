package com.ald.fanbei.api.biz.arbitration;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * 从arbitration.properties获取信息
 * 
 * @Title： 
 * @Author： alvin.wyh
 * @Date： 2017年3月10日 上午8:12:04
 * @Description：
 */
public class ArbitramentConfig {
	
private static ResourceBundle bundle = ResourceBundle.getBundle("arbitrament");
	
	/**
	 * 通过key获取其值
	 * @param key
	 * @return
	 */
	public static String getString(String key){
		return bundle.getString(key);
	}
	
	/**
	 * 通过key和参数值获取其值
	 * @param key
	 * @param param
	 * @return
	 */
	public static String getProperties(final String key, String param){
		
		String msg = getString(key);
		return MessageFormat.format(msg, param);
	}
	
	/**
	 * 通过key和多个参数值获取其值
	 * @param key
	 * @param params
	 * @return
	 */
	public static String getProperties(final String key, Object[] params){
		
		String msg = getString(key);
		return MessageFormat.format(msg, params);
	}

}
