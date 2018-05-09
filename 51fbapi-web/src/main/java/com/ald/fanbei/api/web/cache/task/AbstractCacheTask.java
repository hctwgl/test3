package com.ald.fanbei.api.web.cache.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.web.cache.Cache;

public abstract class  AbstractCacheTask implements CacheTask{
	
	private Cache cache;
	
	private long updateInterval;
	
	protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void run() {
		updateCache(cache);
	}

	public abstract void updateCache(Cache cache);

	@Override
	public long getUpdateInterval() {
		return updateInterval;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}
	
}
