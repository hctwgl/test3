package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserRegisterTypeDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserDto;
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
	Long addUser(AfUserDo afUserDo);
	

	/**
	 * 添加用户(区分邀请来源)
	 * @param afUserDo
	 * @return
	 */
	Long toAddUser(AfUserDo afUserDo,String source);
	
	/**
	 * 添加快速登录用户
	 * @param afUserDo
	 * @return
	 */
	int addUser(AfUserDo afUserDo,String type);
	
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

	Long getUserByBorrowCashStatus(Long userId);

	AfUserDo getUserByMobile(String mobile);

	
	/**
	 * 更新用户消息
	 * @param afUserDo
	 * @return
	 */
	int updateUser(AfUserDo afUserDo);
	
	List<AfUserInvitationDto> getRecommendUserByRecommendId(Long recommendId,Integer start,Integer end);

	Long getUserIdByMobile(String mobile);

	/**
	 * 根据用户id批量得到用户电话
	 * @param userId
	 *
	 * @return
	 * **/
	List<String> getUserNameByUserId(List<String> users);

	/**
	 * 修改用户核心信息-手机号，密码等
	 * @param userId
	 * @param newMobile
	 * @param password
	 */
	void updateUserCoreInfo(final Long userId, final String newMobile, final String password);

	AfUserRegisterTypeDo isQuickRegisterUser(Long id);

	int addQuickRegisterUser(AfUserRegisterTypeDo afUserRegisterTypeDo);
	
	/**
	 * 检查请求的支付密码是否正确
	 * @param reqPayPwd
	 * @param userId
	 */
	void checkPayPwd(String reqPayPwd, Long userId);
	AfUserDto getUserInfoByUserId(Long userId);
		Long convertUserNameToUserId(String userName);
}
