package com.ald.fanbei.api.web.cache.task;

import com.ald.fanbei.api.web.cache.Cache;

public interface  CacheTask extends Runnable{
	
	void updateCache(Cache cache);
	
	long getUpdateInterval();
	
	public void setCache(Cache cache);
}
