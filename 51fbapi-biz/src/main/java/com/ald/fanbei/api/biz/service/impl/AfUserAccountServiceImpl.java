/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:05:08
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAccountService")
public class AfUserAccountServiceImpl implements AfUserAccountService {

	@Resource
	AfUserAccountDao afUserAccountDao;
	
	@Override
	public AfUserAccountDo getUserAccountByUserId(Long userId) {
		return afUserAccountDao.getUserAccountInfoByUserId(userId);
	}

	@Override
	public int addUserAccount(AfUserAccountDo accountDo) {
		return afUserAccountDao.insertUserAccount(accountDo);
	}

}
