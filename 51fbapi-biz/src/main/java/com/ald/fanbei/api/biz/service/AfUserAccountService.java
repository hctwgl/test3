package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;

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
	
	/**
	 * 修改账户信息
	 * @param accountDo
	 * @return
	 */
	int updateUserAccount(AfUserAccountDo afUserAccountDo);
	
	/**
	 * 获取用户账号关联信息
	 * @param userId
	 * @return
	 */
	AfUserAccountDto getUserAndAccountByUserId(Long userId);
	
	/**
	 * 用户取现处理
	 * @param userDto
	 * @param money
	 * @return
	 */
	int dealCashApply(AfUserAccountDto userDto,BigDecimal money);
	
}
