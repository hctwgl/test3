package com.ald.fanbei.api.web.common;

import java.util.Map;
/**
 * Http请求Context
 */
public interface Context {
	public Object getData(String key);

	public Map<String, Object> getDataMap();

	public String getUserName();

	public Long getUserId();
	
	public String getMethod();
	
	public String getId();
	
	public Object getParamEntity();
	public void setParamEntity(Object paramEntity);

	public String getRealName();
	
	public String getIdNumber();

	public String getOpenId();
	
	public String getClientIp();
}
