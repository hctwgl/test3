package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfCommitRecordDo;

/**
 * 
 * @类描述：
 * @author fumeiai 2017年4月24日下午18:37:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCommitRecordService {

	/**
	 * 添加提交记录
	 * @param 
	 * @return
	 */
	int addRecord(AfCommitRecordDo afCommitRecordDo);

}
