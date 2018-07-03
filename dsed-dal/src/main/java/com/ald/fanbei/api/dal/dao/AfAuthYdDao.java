package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfAuthYdDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月15日 下午12:01:39
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAuthYdDao {
	
	/**
	 * 增加有盾认证结果明细
	 * @param AfAuthYdDo
	 * @return
	 */
	int addAuthYd(AfAuthYdDo afAuthYdDo);

}