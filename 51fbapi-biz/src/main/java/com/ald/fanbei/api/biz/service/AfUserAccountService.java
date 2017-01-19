package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:04:58
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountService {
	
	/**
	 * 获取账户信息
	 * @param userId
	 * @return
	 */
	AfUserAccountDo getUserAccountByUserId(Long userId);
	
	/**
	 * 添加用户余额账户信息
	 * @param accountDo
	 * @return
	 */
	int addUserAccount(AfUserAccountDo accountDo);
	

}
