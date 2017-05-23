/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfTdFraudDo;

/**
 * @类描述：
 * @author suweili 2017年3月31日下午4:57:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTdFraudDao {
	   /**
	    * 增加记录
	    * @param afTdFraudDo
	    * @return
	    */
	    int addTdFraud(AfTdFraudDo afTdFraudDo);
	   /**
	    * 更新记录
	    * @param afTdFraudDo
	    * @return
	    */
	    int updateTdFraud(AfTdFraudDo afTdFraudDo);
}
