/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * @author suweili
 *
 */
public interface AfResourceService {
	/**
	 * 获取首页配置信息
	 * @param allTypes
	 * @return
	 */
	List<AfResourceDo> getHomeConfigByAllTypes();
	
	/**
	 * 获取type类型的配置信息
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getConfigByTypes(String type);

}
