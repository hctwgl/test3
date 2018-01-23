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

public class ScheduledCache implements Cache {


	private Map<Object, Object> cache = Maps.newConcurrentMap();
	
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
	
	private List<CacheTask> tasks = Lists.newArrayList();
	
	
	@PostConstruct
	public void init() {
		for(CacheTask task : tasks) {
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
	
}
