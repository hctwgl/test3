package com.ald.fanbei.api.web.cache.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.ald.fanbei.api.web.cache.Cache;
import com.ald.fanbei.api.web.cache.task.CacheTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
/**
 * 定时刷新缓存实现类
 * @author rongbo
 *
 */
public class ScheduledCache implements Cache {


	private Map<Object, Object> cache = Maps.newConcurrentMap();
	
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
	
	private List<CacheTask> tasks = Lists.newArrayList();
	
	private long refreshInterval; // 单位：分钟
	
	
	@PostConstruct
	public void init() {
		for(CacheTask task : tasks) {
			task.setCache(this);
			scheduler.scheduleAtFixedRate(task, 0, task.getUpdateInterval(), TimeUnit.MINUTES);
		}
	}
	
	@Override
	public void putObject(Object key, Object value) {
		cache.put(key, value);
	}

	@Override
	public Object getObject(Object key) {
		return cache.get(key);
	}

	@Override
	public Object removeObject(Object key) {
		return cache.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public int getSize() {
		return cache.entrySet().size();
	}


	public List<CacheTask> getTasks() {
		return tasks;
	}


	public void setTasks(List<CacheTask> tasks) {
		this.tasks = tasks;
	}

	public long getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	
	
	
}
