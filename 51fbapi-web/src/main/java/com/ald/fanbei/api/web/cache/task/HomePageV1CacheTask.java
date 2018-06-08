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
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.redisson.RedissonProxy;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.goods.GetHomeInfoV1Api;
import com.ald.fanbei.api.web.cache.Cache;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * 首页V1版本接口缓存
 * 
 * @author rongbo
 *
 */
public class HomePageV1CacheTask extends AbstractCacheTask {

	private static final String LOCK_NAME = "HOME_PAGE_V1_CACHE_TASK";

	@Resource
	RedissonProxy redissonProxy;

	@Resource
	AfResourceService afResourceService;

	@Resource
	GetHomeInfoV1Api getHomeInfoV1Api;

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
		log.info("update home page v1 cache task start,time =>{}", System.currentTimeMillis());
		// 获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
				Constants.RES_BORROW_CONSUME);
		log.info("home page v1 resource = "+JSON.toJSONString(resource));
		JSONArray array = JSON.parseArray(resource.getValue());
		log.info("home page v1 array = "+JSON.toJSONString(array));
		List<Map<String, Object>> activityInfoList = getHomeInfoV1Api.getHomeActivityList(resource, array);
		log.info("home page v1 activityInfoList = "+JSON.toJSONString(activityInfoList));
		Map<String, Object> moreGoodsInfo = getHomeInfoV1Api.getMoreGoodsInfo(resource, array);
		log.info("home page v1 moreGoodsInfo = "+JSON.toJSONString(moreGoodsInfo));
		// 更新jvm缓存
		cache.putObject(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_ACTIVITY_INFO_LIST.getCode(), activityInfoList);

		cache.putObject(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_MORE_GOODS_INFO.getCode(), moreGoodsInfo);

//		if (lock.tryLock()) {
			
				// 更新redis缓存
				log.info("home page v1 update redis cache start.");
				bizCacheUtil.saveListForever(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_ACTIVITY_INFO_LIST.getCode(),
						activityInfoList);
				bizCacheUtil.saveMapForever(CacheConstants.HOME_PAGE.GET_HOME_INFO_V1_MORE_GOODS_INFO.getCode(),
						moreGoodsInfo);
				log.info("home page v1 update redis cache success.");
				
				//TimeUnit.MINUTES.sleep(cache.getLockInterval());
			} catch (Exception e) {
				log.info("update home page v1 error:"+e);
				e.printStackTrace();
			}/* finally {
				lock.unlock();
			}
		}*/
		log.info("update home page v1 cache task end,time =>{}", System.currentTimeMillis());
	}

}
