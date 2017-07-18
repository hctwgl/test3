/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfSchemeDo;




/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年6月6日下午1:51:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSchemeDao {
	
	/**
	 * 根据id获取优惠计划
	 * @param id
	 * @return
	 */
	AfSchemeDo getSchemeById(@Param("id")Long id);
}
