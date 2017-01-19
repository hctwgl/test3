package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午7:45:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountDao {
	
	/**
	 * 获取账户信息
	 * @param userId
	 * @return
	 */
	AfUserAccountDo getUserAccountInfoByUserId(@Param("userId")Long userId);
	/**
	 * 添加余额账户
	 * @param accountDo
	 * @return
	 */
	int insertUserAccount(AfUserAccountDo afUserAccountDo);
	
	/**
	 * 修改账户信息
	 * @param accountDo
	 * @return
	 */
	int updateUserAccount(AfUserAccountDo afUserAccountDo);
	
}
