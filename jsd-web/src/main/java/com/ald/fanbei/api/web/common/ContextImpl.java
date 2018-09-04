package com.ald.fanbei.api.web.common;

import com.alibaba.fastjson.JSONObject;

public class ContextImpl implements Context {
	private JSONObject dataMap;

	private String userName;
	
	private String method;

	private Long userId;
	
	private String id;
	
	private Object paramEntity;
	
	private String clientIp;

	private String idNumber;

	private String realName;

	private String openId;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}


	@Override
	public String getRealName() {
		return realName;
	}

	@Override
	public String getIdNumber() {
		return idNumber;
	}

	@Override
	public String getClientIp() {
		return clientIp;
	}

	@Override
	public Object getData(String key) {
		return dataMap.get(key);
	}

	@Override
	public JSONObject getDataMap() {
		return this.dataMap;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	@Override
	public String getMethod() {
		return method;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Object getParamEntity() {
		return paramEntity;
	}
	
	@Override
	public void setParamEntity(Object paramEntity) {
		this.paramEntity = paramEntity;
	}

	private ContextImpl(ContextBuilder builder) {
		this.dataMap = builder.dataMap;
		this.userId = builder.userId;
		this.userName = builder.userName;
		this.method  = builder.method;
		this.id = builder.id;
		this.openId  = builder.openId;
		this.clientIp = builder.clientIp;
		this.idNumber = builder.idNumber;
		this.realName = builder.realName;
	}

	public static class ContextBuilder {

		private JSONObject dataMap;

		private String userName;

		private Long userId;

		private String idNumber;

		private String method;
		
		private String id;
		
		private String clientIp;

		private String realName;

		private String openId;

		public ContextBuilder dataMap(JSONObject dataMap) {
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

		public ContextBuilder userId(Long userId) {
			this.userId = userId;
			return this;
		}
		
		public ContextBuilder method(String method) {
			this.method = method;
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
