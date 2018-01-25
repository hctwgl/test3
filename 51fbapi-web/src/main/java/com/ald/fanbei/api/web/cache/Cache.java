package com.ald.fanbei.api.web.cache;

/**
 * 应用缓存接口
 * 
 * @author rongbo
 *
 */
public interface Cache {

	/**
	 * 向容器中添加缓存
	 * @param key
	 * @param value
	 */
	void putObject(Object key, Object value);

	/**
	 * 获取缓存数据
	 * @param key
	 * @return
	 */
	Object getObject(Object key);

	Object removeObject(Object key);

	void clear();

	int getSize();
	
	public long getLockInterval();

}
