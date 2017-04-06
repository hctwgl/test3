package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfOrderPushLogDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月30日上午11:12:10
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderPushLogDao {

	 /**
	    * 增加记录
	    * @param afOrderPushLogDo
	    * @return
	    */
	    int addOrderPushLog(AfOrderPushLogDo afOrderPushLogDo);
	   /**
	    * 更新记录
	    * @param afOrderPushLogDo
	    * @return
	    */
	    int updateOrderPushLog(AfOrderPushLogDo afOrderPushLogDo);
}
