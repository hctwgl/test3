package com.ald.fanbei.api.web.cache.task;

import com.ald.fanbei.api.web.cache.Cache;

public class HomePageV1CacheTask extends AbstractCacheTask{
	

	@Override
	public void updateCache(Cache cache) {
		
		log.info("home page v1 cache task start,time =>{}",System.currentTimeMillis());
		
		
		
		
		log.info("home page v1 cache task end,time =>{}",System.currentTimeMillis());
	}
	

}
