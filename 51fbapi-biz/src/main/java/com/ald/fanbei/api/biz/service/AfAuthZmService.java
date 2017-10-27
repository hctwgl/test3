package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfAuthZmDo;

/**
 * @类现描述：芝麻信用
 * @author chenjinhu 2017年2月17日 下午2:04:40
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAuthZmService {
	/**
	 * 增加记录
	 * 
	 * @param afAuthZmDo
	 * @return
	 */
	int addAuthZm(AfAuthZmDo afAuthZmDo);
}
