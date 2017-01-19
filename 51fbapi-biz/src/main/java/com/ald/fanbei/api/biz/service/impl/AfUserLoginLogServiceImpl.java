/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.dal.dao.AfUserLoginLogDao;
import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:13:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("afUserLoginLogService")
public class AfUserLoginLogServiceImpl extends BaseService implements AfUserLoginLogService {
	
	@Resource
	AfUserLoginLogDao afUserLoginLogDao;
	
	@Override
	public int addUserLoginLog(AfUserLoginLogDo logDo) {
		int result = 0;
		try {
			result = afUserLoginLogDao.addUserLoginLog(logDo);
		} catch (Exception e) {
			logger.error("insert user_login_log error,msg=" + e);
		}
		return result;
	}
}
