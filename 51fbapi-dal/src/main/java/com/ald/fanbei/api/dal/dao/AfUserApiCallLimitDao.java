package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserApiCallLimitDo;

/**
 * 
 * 
 * @类描述：用户接口调用限制
 * 
 * @author huyang 2017年4月26日下午3:06:52
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserApiCallLimitDao {
	/**
	 * 增加记录
	 * 
	 * @param afUserApiCallLimitDo
	 * @return
	 */
	int addUserApiCallLimit(AfUserApiCallLimitDo afUserApiCallLimitDo);

	/**
	 * 更新记录
	 * 
	 * @param afUserApiCallLimitDo
	 * @return
	 */
	int updateUserApiCallLimit(AfUserApiCallLimitDo afUserApiCallLimitDo);

	/**
	 * 
	 * @方法说明：根据用户和类型查询
	 * @author huyang
	 * @param userId
	 * @param type
	 * @return
	 */
	AfUserApiCallLimitDo selectByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

	/**
	 * 
	 * @方法说明：请求次数增加
	 * @author huyang
	 * @param userId
	 * @param type
	 */
	void addVisitNum(@Param("userId") Long userId, @Param("type") String type);
}
