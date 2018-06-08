package com.ald.fanbei.api.web.cache.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.redisson.api.RLock;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.redisson.RedissonProxy;
import com.ald.fanbei.api.web.api.goods.GetHomeInfoV2Api;
import com.ald.fanbei.api.web.cache.Cache;

/**
 * 首页V2版本缓存
 * @author rongbo
 *
 */
public class HomePageV2CacheTask extends AbstractCacheTask{
	
	private static final String LOCK_NAME = "HOME_PAGE_V2_CACHE_TASK";

	@Resource
	RedissonProxy redissonProxy;

	@Resource
	AfResourceService afResourceService;

	@Resource
	GetHomeInfoV2Api getHomeInfoV2Api;

	@Resource
	BizCacheUtil bizCacheUtil;

	private RLock lock;
	
	@PostConstruct
	public void init() {
		lock = redissonProxy.getLock(LOCK_NAME);
	}
	
	@Override
	public void updateCache(Cache cache) {
		try {
			log.info("update home page v2 cache task start,time =>{}", System.currentTimeMillis());
			
			List<Map<String, Object>> newGoodsInfoList = getHomeInfoV2Api.getHomePageGoodsCategoryInfoV1();
			List<Map<String, Object>> oldGoodsInfoList = getHomeInfoV2Api.getHomePageGoodsCategoryInfo();

			// 更新jvm缓存
			cache.putObject(CacheConstants.HOME_PAGE.GET_HOME_INFO_V2_GOODS_INFO_FOR_NEW.getCode(), newGoodsInfoList);
			cache.putObject(CacheConstants.HOME_PAGE.GET_HOME_INFO_V2_GOODS_INFO_FOR_OLD.getCode(), oldGoodsInfoList);

//			if (lock.tryLock()) {
				// 更新redis缓存
				bizCacheUtil.saveListForever(CacheConstants.HOME_PAGE.GET_HOME_INFO_V2_GOODS_INFO_FOR_NEW.getCode(),
						newGoodsInfoList);
				bizCacheUtil.saveListForever(CacheConstants.HOME_PAGE.GET_HOME_INFO_V2_GOODS_INFO_FOR_OLD.getCode(),
						oldGoodsInfoList);
				log.info("home page v2 update redis cache success.");
			//}
			log.info("update home page v2 cache task end,time =>{}", System.currentTimeMillis());
			//TimeUnit.MINUTES.sleep(cache.getLockInterval());
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
			lock.unlock();
		}*/
	}
	

}
