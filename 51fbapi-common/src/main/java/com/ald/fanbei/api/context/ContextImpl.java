package com.ald.fanbei.api.context;

import java.util.Map;

import com.google.common.collect.Maps;

public class ContextImpl implements Context {

	private Map<String, Object> dataMap = Maps.newHashMap();

	private Long appVersion;

	private String userName;

	private Long userId;

	private String method;

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

	public Long getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(Long appVersion) {
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

	public void setMethod(String method) {
		this.method = method;
	}

	private ContextImpl(Builder builder) {
		this.dataMap = builder.dataMap;
		this.userId = builder.userId;
		this.userName = builder.userName;
		this.appVersion = builder.appVersion;
		this.method  = builder.method;
	}

	public static class Builder {

		private Map<String, Object> dataMap;

		private Long appVersion;

		private String userName;

		private Long userId;
		
		private String method;

		public Builder dataMap(Map<String, Object> dataMap) {
			this.dataMap = dataMap;
			return this;
		}

		public Builder userName(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder appVersion(Long appVersion) {
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

		public ContextImpl build() {
			return new ContextImpl(this);
		}

	}

}
