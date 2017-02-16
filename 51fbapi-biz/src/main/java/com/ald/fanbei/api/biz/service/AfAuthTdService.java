package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfAuthTdDo;

/**
 * @类现描述：同盾认证
 * @author chenjinhu 2017年2月16日 上午11:47:33
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAuthTdService {
	/**
	 * 增加记录
	 * 
	 * @param afAuthTdDo
	 * @return
	 */
	int addAuthTd(AfAuthTdDo afAuthTdDo);
}
