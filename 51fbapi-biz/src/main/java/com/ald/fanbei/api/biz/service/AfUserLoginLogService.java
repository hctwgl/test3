/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
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
}
