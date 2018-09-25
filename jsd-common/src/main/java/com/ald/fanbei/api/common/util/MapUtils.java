package com.ald.fanbei.api.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;

/**
 * @author ZJF
 */
public class MapUtils {

	/**
	 * 将字符串转为Map对象
	 * @param value
	 * 传进去的值 key,value 必须成对
	 * @return
	 */
	public static Map<Object, Object> objsAsMap(Object... value) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		if (value != null && value.length > 0) {
			for (int i = 0; i < value.length; i += 2) {
				map.put(value[i], value[i + 1]);
			}
		}
		return map;
	}

	/**
	 * 构造键值为String的Map对象
	 * @param value
	 * 传进去的值 key,value 必须成对
	 * @return
	 */
	public static Map<String, String> cstStrKeyMap(Object... value) {
		Map<String, String> map = new HashMap<String, String>();
		if (value != null && value.length > 0) {
			for (int i = 0; i < value.length; i += 2) {
				map.put(String.valueOf(value[i]), String.valueOf(value[i + 1]));
			}
		}
		return map;
	}

	/**
	 * delMapValue: 从map中删除多个键值<br/>
	 * @param srcMap
	 * 要操作的map
	 * @param keys
	 * 要删除的key值逗号分隔 如：id,id1,id2
	 * @return
	 */
	public static Map<String, Object> delValue(Map<String, Object> srcMap, String keys) {
		if (srcMap != null && srcMap.size() > 0) {
			if (keys != null && !keys.isEmpty()) {
				for (String s : keys.split(",")) {
					srcMap.remove(s);
				}
			}
		}
		return srcMap;
	}

	/**
	 * JSONObject 转 HashMap
	 * @param o
	 * @return
	 */
	public static Map<String, Object> jsonToMap(JSONObject o) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (o != null) {
			for (Entry<String, Object> e : o.entrySet()) {
				map.put(e.getKey(), e.getValue());
			}
		}
		return map;
	}

}
