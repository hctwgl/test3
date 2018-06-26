package com.ald.fanbei.api.context;

import java.util.Map;
/**
 * Http请求Context
 * @author Jiang Rongbo
 *
 */
public interface Context {

	public Object getData(String key);

	public void setData(String key, Object value);

	public void setDataMap(Map<String, Object> dataMap);

	public Map<String, Object> getDataMap();

	public Integer getAppVersion();

	public void setAppVersion(Integer appVersion);

	public String getUserName();

	public void setUserName(String userName);

	public Long getUserId();

	public void setUserId(Long userId);
	
	public String getMethod();

	public void setMethod(String method);
	
	public String getId();

	public void setId(String id);
	
	public Map<String, Object> getSystemsMap() ;

	public void setSystemsMap(Map<String, Object> systemsMap);
	
	public Object getParamEntity();

	public void setParamEntity(Object paramEntity);
	
	public String getClientIp();

	public void setClientIp(String clientIp);

	public String getIdNumber();

	public void setIdNumber(String idNumber);
}
