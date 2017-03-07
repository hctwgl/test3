package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserInvitationDto;

/**
 *@类描述：
 *@author Xiaotianjian 2017年1月19日下午1:51:46
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserService {

	/**
	 * 添加用户
	 * @param afUserDo
	 * @return
	 */
	int addUser(AfUserDo afUserDo);
	
	/**
	 * 根据用户id获取用户信息
	 * @param userId
	 * @return
	 */
	AfUserDo getUserById(Long userId);
	/**
	 * 根据用户邀请码获取用户信息
	 * @param recommendCode 用户邀请码
	 * @return
	 */
	AfUserDo getUserByRecommendCode(String recommendCode);
	/**
	 * 通过用户名获取用户信息
	 *@param id
	 *@return
	 */
	AfUserDo getUserByUserName(String userName);
	
	
	/**
	 * 更新用户消息
	 * @param afUserDo
	 * @return
	 */
	int updateUser(AfUserDo afUserDo);
	
	List<AfUserInvitationDto> getRecommendUserByRecommendId(Long recommendId,Integer start,Integer end);

}
