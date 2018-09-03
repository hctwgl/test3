package com.ald.fanbei.api.web.common;

import java.util.Map;
/**
 * Http请求Context
 */
public interface Context {
	public Object getData(String key);
	public void setData(String key, Object value);

	public void setDataMap(Map<String, Object> dataMap);
	public Map<String, Object> getDataMap();

	public String getUserName();
	public void setUserName(String userName);

	public Long getUserId();
	public void setUserId(Long userId);
	
	public String getMethod();
	public void setMethod(String method);
	
	public String getId();
	public void setId(String id);
	
	public Object getParamEntity();
	public void setParamEntity(Object paramEntity);

	public String getRealName();
	public void setRealName(String realName);

	public String getOpenId();
	public void setOpenId(String openId);
}
