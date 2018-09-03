package com.ald.fanbei.api.web.common;

import java.util.Map;

import com.google.common.collect.Maps;

public class ContextImpl implements Context {

	private Map<String, Object> dataMap = Maps.newHashMap();

	private Integer appVersion;

	private String userName;
	
	private String method;

	private Long userId;
	
	private String id;
	
	private Object paramEntity;
	
	private String clientIp;

	private String idNumber;

	private String realName;

	private String openId;

	private Map<String,Object> systemsMap;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Override
	public String getIdNumber() {
		return idNumber;
	}
	@Override
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	@Override
	public String getRealName() {
		return realName;
	}

	@Override
	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

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
	
	public Object getParamEntity() {
		return paramEntity;
	}

	public void setParamEntity(Object paramEntity) {
		this.paramEntity = paramEntity;
	}

	private ContextImpl(ContextBuilder builder) {
		this.dataMap = builder.dataMap;
		this.userId = builder.userId;
		this.userName = builder.userName;
		this.appVersion = builder.appVersion;
		this.method  = builder.method;
		this.id = builder.id;
		this.systemsMap  = builder.systemsMap;
		this.openId  = builder.openId;
		this.clientIp = builder.clientIp;
		this.idNumber = builder.idNumber;
		this.realName = builder.realName;
	}

	public static class ContextBuilder {

		private Map<String, Object> dataMap;

		private Integer appVersion;

		private String userName;

		private Long userId;

		private String idNumber;

		private String method;
		
		private String id;
		
		private String clientIp;

		private String realName;
		
		private Map<String,Object> systemsMap;

		private String openId;

		public ContextBuilder dataMap(Map<String, Object> dataMap) {
			this.dataMap = dataMap;
			return this;
		}
		public ContextBuilder realName(String realName) {
			this.realName = realName;
			return this;
		}

		public ContextBuilder userName(String userName) {
			this.userName = userName;
			return this;
		}

		public ContextBuilder openId(String openId) {
			this.openId = openId;
			return this;
		}

		public ContextBuilder appVersion(Integer appVersion) {
			this.appVersion = appVersion;
			return this;
		}

		public ContextBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public ContextBuilder method(String method) {
			this.method = method;
			return this;
		}
		
		public ContextBuilder systemsMap(Map<String,Object> systemsMap) {
			this.systemsMap = systemsMap;
			return this;
		}
		
		public ContextBuilder id(String id) {
			this.id = id;
			return this;
		}
		
		public ContextBuilder clientIp(String clientIp) {
			this.clientIp = clientIp;
			return this;
		}
        public ContextBuilder idNumber(String idNumber) {
            this.idNumber = idNumber;
            return this;
        }


        public ContextImpl build() {
			return new ContextImpl(this);
		}

	}

}
