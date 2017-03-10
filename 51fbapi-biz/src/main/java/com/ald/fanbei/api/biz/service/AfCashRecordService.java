/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfCashRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

/**
 * @类描述：
 * @author suweili 2017年2月23日上午11:24:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCashRecordService {
	/**
	 * 增加提现记录
	 * @param afCashRecordDo
	 * @return
	 */
	
	int addCashRecord(AfCashRecordDo afCashRecordDo,AfUserBankcardDo card);

}
