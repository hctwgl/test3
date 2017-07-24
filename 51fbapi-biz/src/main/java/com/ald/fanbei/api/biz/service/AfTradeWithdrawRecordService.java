package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTradeWithdrawRecordDo;

public interface AfTradeWithdrawRecordService extends ParentService<AfTradeWithdrawRecordDo, Long>{

	int dealWithDrawSuccess(long id);

	int dealWithDrawFail(long id);

}
