package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;

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
	
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Resource
	AfBorrowDao afBorrowDao;
	
	@Override
	public AfUserAccountDo getUserAccountByUserId(Long userId) {
		return afUserAccountDao.getUserAccountInfoByUserId(userId);
	}

	@Override
	public int addUserAccount(AfUserAccountDo accountDo) {
		return afUserAccountDao.addUserAccount(accountDo);
	}
	
	@Override
	public int updateUserAccount(AfUserAccountDo afUserAccountDo) {
		return afUserAccountDao.updateUserAccount(afUserAccountDo);
	}

	@Override
	public AfUserAccountDto getUserAndAccountByUserId(Long userId) {
		return afUserAccountDao.getUserAndAccountByUserId(userId);
	}

}
