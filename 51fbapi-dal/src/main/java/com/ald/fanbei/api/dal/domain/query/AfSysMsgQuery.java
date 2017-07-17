package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfSysMsgDo;

/**
 * 
 *@类描述：AfSysMsgQuery
 *@author 何鑫 2017年1月19日  20:11:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSysMsgQuery extends Page<AfSysMsgDo>{

	private static final long serialVersionUID = 4240569485632231081L;

	private Long	userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
