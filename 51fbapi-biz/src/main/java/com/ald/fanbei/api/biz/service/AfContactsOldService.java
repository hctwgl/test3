package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfContactsOldDo;

/**
 * @类现描述：
 * @author fumeiai 2017年4月19日 15:54:56
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfContactsOldService {
	
	/**
	 * 获取用户通讯录信息
	 * @param userId
	 * @return
	 */
	AfContactsOldDo getAfContactsByUserId(Long userId);
}
