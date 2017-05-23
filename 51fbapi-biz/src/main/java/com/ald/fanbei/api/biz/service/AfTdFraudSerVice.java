/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTdFraudDo;

/**
 * @类描述：
 * @author suweili 2017年3月31日下午5:01:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTdFraudSerVice {
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
