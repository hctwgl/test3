package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserAuthDo;

/**
 * @类现描述：
 * @author chenjinhu 2017年2月15日 下午3:03:56
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAuthService {
	
	/**
	 * 增加记录
	 * @param afUserAuthDo
	 * @return
	 */
	int addUserAuth(AfUserAuthDo afUserAuthDo);

	/**
	 * 更新记录
	 * @param afUserAuthDo
	 * @return
	 */
	int updateUserAuth(AfUserAuthDo afUserAuthDo);
}
