/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfCashRecordDo;

/**
 * @类描述：
 * @author suweili 2017年2月23日上午11:16:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCashRecordDao {
	/**
	 * 添加提现记录
	 * @param afCashRecordDo
	 * @return
	 */
	int addCashRecord(AfCashRecordDo afCashRecordDo);

	int updateCashRecord(AfCashRecordDo afCashRecordDo);
	
	AfCashRecordDo getCashRecordById(@Param("id")Long id);
}
