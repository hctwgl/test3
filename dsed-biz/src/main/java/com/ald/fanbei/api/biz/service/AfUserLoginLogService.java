package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:13:37
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserLoginLogService {

	/**
	 * 增加用户登陆记录
	 *@param HoaUserLoginLogDo
	 *@return
	 */
	int addUserLoginLog(AfUserLoginLogDo logDo);
	

	/**
	 * 通过用户名获取登录次数
	 * @param userName
	 * @return
	 */
	int getCountByUserName(String userName);
	
	/**
	 * 获取用户最后一次登陆相关信息
	 * @param userName
	 * @return
	 */
	AfUserLoginLogDo getUserLastLoginInfo(String userName);


	long getCountByUserNameAndResultTrue(String userName);

	long getCountByUserNameAndResultSupermanTrue(String userName);


}