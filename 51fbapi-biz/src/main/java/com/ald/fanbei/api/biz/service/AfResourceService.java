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
	 * 根据type获取资源列表
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceListByType(String type);
	
	/**
	 * 根据type获取一个资源信息
	 * @param type
	 * @return
	 */
	AfResourceDo getSingleResourceBytype(String type);
	
	
}
