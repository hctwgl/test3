package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月15日 下午2:50:01
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAuthDao {
	/**
	 * 增加用户授权
	 * @param afUserAuthDo
	 * @return
	 */
	int addUserAuth(AfUserAuthDo afUserAuthDo);
	
	/**
	 * 更新用户授权
	 * @param afUserAuthDo
	 * @return
	 */
	int updateUserAuth(AfUserAuthDo afUserAuthDo);
	
	/**
	 * 更新运营商认证状态为认证中-此更新比较特殊单独方法处理
	 * @param afUserAuthDo
	 * @return
	 */
	int updateUserAuthMobileStatusWait(AfUserAuthDo afUserAuthDo);
	
	/**
	 * 获取用户认证信息
	 * @param userId
	 * @return
	 */
	public AfUserAuthDo getUserAuthInfoByUserId(Long userId);
	
	/**
	 * 获取Ivs_status is Y 的数量
	 * @return
	 */
	int getUserAuthCountWithIvs_statusIsY();
	
	/**
	 * 获取Ivs_status is Y 的数据
	 * @return
	 */
	List<AfUserAuthDo> getUserAuthListWithIvs_statusIsY(AfUserAuthQuery query);
}
