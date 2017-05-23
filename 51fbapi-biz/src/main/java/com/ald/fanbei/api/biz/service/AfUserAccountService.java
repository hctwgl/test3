package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.dto.AfLimitDetailDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.query.AfLimitDetailQuery;
import com.ald.fanbei.api.dal.domain.query.AfUserAccountQuery;

/**
 * 
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月19日下午4:04:58
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountService {

	/**
	 * 获取账户信息
	 * 
	 * @param userId
	 * @return
	 */
	AfUserAccountDo getUserAccountByUserId(Long userId);

	/**
	 * 添加用户余额账户信息
	 * 
	 * @param accountDo
	 * @return
	 */
	int addUserAccount(AfUserAccountDo accountDo);

	/**
	 * 修改账户信息
	 * 
	 * @param accountDo
	 * @return
	 */
	int updateUserAccount(AfUserAccountDo afUserAccountDo);

	/**
	 * 获取用户账号关联信息
	 * 
	 * @param userId
	 * @return
	 */
	AfUserAccountDto getUserAndAccountByUserId(Long userId);

	/**
	 * 增加记录
	 * 
	 * @param afUserAccountLogDo
	 * @return
	 */
	int addUserAccountLog(AfUserAccountLogDo afUserAccountLogDo);

	/**
	 * 获取明细列表
	 * 
	 * @param query
	 * @return
	 */
	List<AfLimitDetailDto> getLimitDetailList(AfLimitDetailQuery query);

	int getUserAccountCountWithHasRealName();

	List<AfUserAccountDto> getUserAndAccountListWithHasRealName(AfUserAccountQuery query);

	/**
	 * 代付失败处理
	 * 
	 * @param merPriv
	 * @return
	 */
	int dealUserDelegatePayError(String merPriv, Long result);

	/**
	 * 获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	AfUserAccountDto getUserInfoByUserId(Long userId);

	/**
	 * 更新用户实名和身份证
	 * 
	 * @param accountDo
	 */
	void updateUserAccountRealNameAndIdNumber(AfUserAccountDto accountDo);
}
