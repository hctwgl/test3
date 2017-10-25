/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;


/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年6月4日下午8:33:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfInterestFreeRulesService {
	
	/**
	 * 根据id获取免息规则
	 * @param id
	 * @return
	 */
	AfInterestFreeRulesDo getById(Long id);
	    
}
