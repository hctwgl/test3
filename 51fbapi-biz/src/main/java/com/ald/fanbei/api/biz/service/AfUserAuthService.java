package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;

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
	
	/**
	 * 获取用户认证信息
	 * @param userId
	 * @return
	 */
	AfUserAuthDo getUserAuthInfoByUserId(Long userId);
	
	/**
	 * 判断是否可以分期
	 * @param userId
	 * @return
	 */
	String getConsumeStatus(Long userId,Integer appVersion);
	
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
