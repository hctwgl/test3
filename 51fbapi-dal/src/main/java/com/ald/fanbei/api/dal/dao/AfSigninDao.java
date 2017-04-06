/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfSigninDo;

/**
 * @类描述：
 * @author suweili 2017年2月7日下午1:41:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSigninDao {

	/**
	 * 插入签到记录
	 * @param afSigninDo
	 * @return
	 */
	int  insertSignin(AfSigninDo afSigninDo);
	
	/**
	 * 修改签到记录
	 * @param afSigninDo
	 * @return
	 */
	int updateSignin(AfSigninDo afSigninDo);
	
	/**
	 * 查询签到记录
	 * @param userId
	 * @return
	 */
	AfSigninDo selectSigninByUserId(@Param("userId")Long userId);
}
