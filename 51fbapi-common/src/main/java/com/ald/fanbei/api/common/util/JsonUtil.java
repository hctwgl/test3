/**
 * 
 */
package com.ald.fanbei.api.common.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月4日下午2:57:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class JsonUtil {
	public static String toJSONString(Object jsonObj) {
		return JSON.toJSONString(jsonObj);
	}

	public static <T> T toJavaBean(String json, Class<T> clazz) {
		Map<String, Object> objMap = (Map<String, Object>) JSON.parse(json);
		
		T bean = null;
		try {
			bean = clazz.newInstance();
			
			
			
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return bean;
	}
}
