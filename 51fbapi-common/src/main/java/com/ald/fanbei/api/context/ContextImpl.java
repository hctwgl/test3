package com.ald.fanbei.api.context;

import java.util.Map;

import com.google.common.collect.Maps;

public class ContextImpl implements Context {

	private Map<String, Object> dataMap = Maps.newHashMap();

	private Integer appVersion;

	private String userName;
	
	private String method;

	private Long userId;
	
	private String id;

	private Map<String,Object> systemsMap;
	
	
	@Override
	public Object getData(String key) {
		return dataMap.get(key);
	}

	@Override
	public void setData(String key, Object value) {
		dataMap.put(key, value);
	}

	@Override
	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	@Override
	public Map<String, Object> getDataMap() {
		return this.dataMap;
	}

	public Integer getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(Integer appVersion) {
		this.appVersion = appVersion;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMethod() {
		return method;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	
	public Map<String, Object> getSystemsMap() {
		return systemsMap;
	}

	public void setSystemsMap(Map<String, Object> systemsMap) {
		this.systemsMap = systemsMap;
	}

	private ContextImpl(Builder builder) {
		this.dataMap = builder.dataMap;
		this.userId = builder.userId;
		this.userName = builder.userName;
		this.appVersion = builder.appVersion;
		this.method  = builder.method;
		this.id = builder.id;
		this.systemsMap  = builder.systemsMap;
	}

	public static class Builder {

		private Map<String, Object> dataMap;

		private Integer appVersion;

		private String userName;

		private Long userId;
		
		private String method;
		
		private String id;
		
		private Map<String,Object> systemsMap;

		public Builder dataMap(Map<String, Object> dataMap) {
			this.dataMap = dataMap;
			return this;
		}

		public Builder userName(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder appVersion(Integer appVersion) {
			this.appVersion = appVersion;
			return this;
		}

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public Builder method(String method) {
			this.method = method;
			return this;
		}
		
		public Builder systemsMap(Map<String,Object> systemsMap) {
			this.systemsMap = systemsMap;
			return this;
		}
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public ContextImpl build() {
			return new ContextImpl(this);
		}

	}

}
