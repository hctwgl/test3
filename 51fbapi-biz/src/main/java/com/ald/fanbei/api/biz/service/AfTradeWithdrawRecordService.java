package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTradeWithdrawRecordDo;

import java.util.Date;
import java.util.List;

public interface AfTradeWithdrawRecordService extends ParentService<AfTradeWithdrawRecordDo, Long>{

	int dealWithDrawSuccess(long id);

	int dealWithDrawFail(long id);

	/**
	 * 分页查询商圈提现记录
	 *
	 * @param businessId
	 * @param offset
	 * @param limit
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<AfTradeWithdrawRecordDo> withdrawGrid(Long businessId, Integer offset, Integer limit, Date startDate, Date endDate);

	/**
	 * 分页查询商圈提现记录总条数
	 *
	 * @param businessId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Long withdrawGridTotal(Long businessId, Date startDate, Date endDate);

}
