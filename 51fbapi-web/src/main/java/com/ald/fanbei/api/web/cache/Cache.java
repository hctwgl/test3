package com.ald.fanbei.api.web.cache;

import java.util.concurrent.locks.ReadWriteLock;

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
	 * 获取
	 * @param key
	 * @return
	 */
	Object getObject(Object key);

	Object removeObject(Object key);

	void clear();

	int getSize();

	ReadWriteLock getReadWriteLock();

}
