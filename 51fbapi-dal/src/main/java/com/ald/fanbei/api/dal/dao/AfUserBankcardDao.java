package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

/**
 *@类现描述：
 *@author hexin 2017年2月18日 下午17:27:22
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserBankcardDao {

	/**
	 * 获取用户主卡信息
	 * @param userId
	 * @return
	 */
	AfUserBankcardDo getUserMainBankcardByUserId(@Param("userId")Long userId);
}
