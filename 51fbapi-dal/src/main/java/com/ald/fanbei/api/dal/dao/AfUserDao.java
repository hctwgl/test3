package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserInvitationDto;

/**
 *@类描述：
 *@author Xiaotianjian 2017年1月19日下午3:09:06
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserDao {

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
	AfUserDo getUserById(@Param("userId")Long userId);
	/**
	 * 根据用户邀请码获取用户信息
	 * @param recommendCode
	 * @return
	 */
	AfUserDo getUserByRecommendCode(@Param("recommendCode")String recommendCode);
	
	/**
	 * 更新用户消息
	 * @param afUserDo
	 * @return
	 */
	int updateUser(AfUserDo afUserDo);
	
	/**
	 * 通过用户名获取用户信息
	 *@param id
	 *@return
	 */
	AfUserDo getUserByUserName(@Param("userName")String userName);

	/**
	 * 通过用户名获取用户信息
	 *@param id
	 *@return
	 */
	AfUserDo getUserByMobile(@Param("mobile")String mobile);
	/**
	 * 获取该用户邀请的用户
	 * @param recommendId
	 * @return
	 */
	List<AfUserInvitationDto> getRecommendUserByRecommendId(@Param("recommendId")Long recommendId,@Param("start")Integer start,@Param("end")Integer end);

	Long getUserIdByMobile(@Param("mobile")String mobile);

	/**
	 * 根据用户id批量得到用户电话
	 * @param userId
	 *
	 * @return
	 * **/
	List<String> getUserNameByUserId(List<String> users);

	/**
	 * 根据用户id查询邀请码
	 * @param userId
	 * @return
	 */
	String getUserRecommendCode(@Param("userId")long userId);
}
