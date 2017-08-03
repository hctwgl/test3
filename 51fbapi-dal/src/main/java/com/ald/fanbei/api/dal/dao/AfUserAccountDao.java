package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.query.AfUserAccountQuery;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午7:45:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountDao {

	/**
	 * 获取账户信息
	 * 
	 * @param userId
	 * @return
	 */
	AfUserAccountDo getUserAccountInfoByUserId(@Param("userId") Long userId);

	/**
	 * 添加余额账户
	 * 
	 * @param accountDo
	 * @return
	 */
	int addUserAccount(AfUserAccountDo afUserAccountDo);

	/**
	 * 修改账户信息,有相关的加减
	 * 
	 * @param accountDo
	 * @return
	 */
	int updateUserAccount(AfUserAccountDo afUserAccountDo);

	/**
	 * 只按照传参进行修改值
	 * 
	 * @param accountDo
	 * @return
	 */
	int updateOriginalUserAccount(AfUserAccountDo afUserAccountDo);

	/**
	 * 获取账户关联信息
	 * 
	 * @param userId
	 * @return
	 */
	AfUserAccountDto getUserAndAccountByUserId(@Param("userId") Long userId);

	int getUserAccountCountWithHasRealName();

	List<AfUserAccountDto> getUserAndAccountListWithHasRealName(AfUserAccountQuery query);

	/**
	 * 获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	AfUserAccountDto getUserInfoByUserId(Long userId);

	void updateUserAccountRealNameAndIdNumber(AfUserAccountDto accountDo);
	
	/***
	 * 获取身份证号是否被其他人注册过
	 * @param citizenId 身份证号
	 * @param userId 用户主键
	 * @return 被其他人使用过的次数
	 */
	Integer getCountByIdNumer(@Param("citizenId")String citizenId,@Param("userId") Long userId);

	/**
	 * 获取账户信息
	 * @param userName
	 * @return
	 */
	AfUserAccountDo getUserAccountInfoByUserName(@Param("userName") String userName);
}
