package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfAuthContactsDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月16日 下午2:29:01
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAuthContactsService {
	/**
	 * 增加记录
	 * 
	 * @param afAuthContactsDo
	 * @return
	 */
	int addAuthContacts(List<AfAuthContactsDo> afAuthContactsDos);
}
