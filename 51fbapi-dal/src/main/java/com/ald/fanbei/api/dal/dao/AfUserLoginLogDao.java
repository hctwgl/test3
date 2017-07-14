package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:15:18
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserLoginLogDao {
	
	/**
	 * 增加用户登陆记录
	 *@param afUserLoginLogDo
	 *@return
	 */
	int addUserLoginLog(AfUserLoginLogDo afUserLoginLogDo);
	
	int getCountByUserName(@Param("userName")String userName);
	
}
