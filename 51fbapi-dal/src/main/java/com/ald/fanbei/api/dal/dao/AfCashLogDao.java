/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfCashLogDo;

/**
 * @类描述：
 * @author suweili 2017年3月21日下午5:06:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCashLogDao {
	 /**
	    * 增加记录
	    * @param afCashLogDo
	    * @return
	    */
	    int addCashLog(AfCashLogDo afCashLogDo);
	   /**
	    * 更新记录
	    * @param afCashLogDo
	    * @return
	    */
	    int updateCashLog(AfCashLogDo afCashLogDo);
}
