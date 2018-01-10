package com.ald.fanbei.api.context;

import java.util.Map;

public interface Context {

	public Object getData(String key);

	public void setData(String key, Object value);

	public void setDataMap(Map<String, Object> dataMap);

	public Map<String, Object> getDataMap();

	public Long getAppVersion();

	public void setAppVersion(Long appVersion);

	public String getUserName();

	public void setUserName(String userName);

	public Long getUserId();

	public void setUserId(Long userId);
	
	public String getMethod();

	public void setMethod(String method);
}
