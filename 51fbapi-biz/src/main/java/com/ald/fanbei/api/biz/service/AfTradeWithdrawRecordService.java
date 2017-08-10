package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTradeWithdrawRecordDo;

import java.util.Date;
import java.util.List;

public interface AfTradeWithdrawRecordService extends ParentService<AfTradeWithdrawRecordDo, Long>{

	int dealWithDrawSuccess(long id);

	int dealWithDrawFail(long id);

	/**
	 * 提现明细时间列表
	 * @param businessId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<String> withdrawGridDate(Long businessId, Date startDate, Date endDate);

	/**
	 * 分页查询商圈提现记录
	 *
	 * @param businessId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<AfTradeWithdrawRecordDo> withdrawGrid(Long businessId, Date startDate, Date endDate);

}
