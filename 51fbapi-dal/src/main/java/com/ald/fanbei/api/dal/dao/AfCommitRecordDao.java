package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfCommitRecordDo;


/**
 * @类现描述：提交记录dao
 * @author fumeiai 2017年4月24日 上午18:58:38
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCommitRecordDao {

	/**
	 * 增加记录
	 * @param 
	 * @return
	 */
	public int addRecord(AfCommitRecordDo afCommitRecordDo);
	
	/**
	 * 根据Type和relate_Id获取记录
	 * @param 
	 * @return
	 */
	AfCommitRecordDo getRecordByTypeAndRelateId(String relate_id);
}
