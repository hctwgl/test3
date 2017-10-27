package com.ald.fanbei.api.common.util;

import java.util.List;
import java.util.Map;

/**
 * 
 *@类描述：集合工具类
 *@author 陈金虎 2017年1月16日 下午11:39:50
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CollectionUtil {
	
	/**
	 * To judge whether the List is empty
	 * if the list is null or the size of this list is 0, then return true; 
	 * @param list
	 * @return
	 */
	public static <T> boolean isEmpty(List<T> list) {
		if (list == null || list.size() < 1 ) {
			return true;
		}
		return false;
	}

	/**
	 * To judge whether the List is not empty
	 * if the list is not null or the size of this list > 0, then return true; 
	 * @param list
	 * @return
	 */ 
	public static <T> boolean isNotEmpty(List<T> list) {
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * To judge whether the map is empty
	 * if the list is null or the size of this list is 0, then return true; 
	 * @param <K>
	 * @param list
	 * @return
	 */
	public static <K, V> boolean isEmpty(Map<K, V> maps) {
		if (maps == null || maps.size() < 1 ) {
			return true;
		}
		return false;
	}

	/**
	 * To judge whether the map is not empty
	 * if the list is not null or the size of this list > 0, then return true; 
	 * @param list
	 * @return
	 */ 
	public static <K, V> boolean isNotEmpty(Map<K, V> maps) {
		if (maps != null && maps.size() > 0) {
			return true;
		}
		return false;
	}
}