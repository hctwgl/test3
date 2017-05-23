package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfAuthZmDo;

/**
 * @类现描述：芝麻授权dao
 * @author chenjinhu 2017年2月17日 下午2:03:24
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAuthZmDao {

	/**
	 * 增加记录
	 * 
	 * @param afAuthZmDo
	 * @return
	 */
	int addAuthZm(AfAuthZmDo afAuthZmDo);
}
