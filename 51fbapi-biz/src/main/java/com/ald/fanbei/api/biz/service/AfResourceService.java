/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**

 *@类描述：
 *@author Xiaotianjian 2017年1月20日上午10:08:53
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
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
    /**
     * 根据id获取资源配置
     * @param rid
     * @return
     */
    AfResourceDo getResourceByResourceId(Long rid);
    /**
     * 获取type类型的配置信息
     * @param type
     * @return
     */
    AfResourceDo getConfigByTypesAndSecType(String type,String secType);

    
    
	/**
	 * 根据type获取资源列表
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceListByType(String type);
	/**
	 * 根据type获取资源排序列表
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceListByTypeOrderBy(String type);
	/**
	 * 根据type获取资源排序列表
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceHomeListByTypeOrderBy(String type);
	
	
	/**
	 * 根据type获取一个资源信息
	 * @param type
	 * @return
	 */
	AfResourceDo getSingleResourceBytype(String type);
	
	List<AfResourceDo> getOneToManyResourceOrderByBytype(String type);

	  /**借钱模块配置信息
     * 
     * @return
     */
    List<AfResourceDo> selectBorrowHomeConfigByAllTypes();
	
}
