package com.ald.fanbei.api.dal.dao;


import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo;
/**
 * 
 * @类描述：
 * @author chenfangfang 2017年7月24日下午 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAppealLogDao {
	
	AfUserAppealLogDo getLatestByUserId(@Param("userId")Long userId);

	int insert(AfUserAppealLogDo params);

	int update(AfUserAppealLogDo params);
	 
}
