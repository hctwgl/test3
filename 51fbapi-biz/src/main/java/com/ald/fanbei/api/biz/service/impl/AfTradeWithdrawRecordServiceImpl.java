package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfTradeWithdrawRecordService;
import com.ald.fanbei.api.common.enums.TradeOrderStatus;
import com.ald.fanbei.api.common.enums.TradeWithdrawRecordStatus;
import com.ald.fanbei.api.dal.dao.AfTradeOrderDao;
import com.ald.fanbei.api.dal.dao.AfTradeWithdrawDetailDao;
import com.ald.fanbei.api.dal.dao.AfTradeWithdrawRecordDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfTradeWithdrawDetailDo;
import com.ald.fanbei.api.dal.domain.AfTradeWithdrawRecordDo;

@Service("afTradeWithdrawRecordService")
public class AfTradeWithdrawRecordServiceImpl extends ParentServiceImpl<AfTradeWithdrawRecordDo, Long> implements AfTradeWithdrawRecordService {

	@Resource
	private AfTradeWithdrawRecordDao afTradeWithdrawRecordDao;
	@Resource
	private AfTradeOrderDao afTradeOrderDao;
	@Resource
	private AfTradeWithdrawDetailDao afTradeWithdrawDetailDao;
	
	@Override
	public BaseDao<AfTradeWithdrawRecordDo, Long> getDao() {
		return afTradeWithdrawRecordDao;
	}

	@Override
	public int dealWithDrawSuccess(long id) {
		AfTradeWithdrawRecordDo recordDo = new AfTradeWithdrawRecordDo();
		recordDo.setId(id);
		recordDo.setStatus(TradeWithdrawRecordStatus.TRANSED.getCode());
		afTradeWithdrawRecordDao.updateById(recordDo);
		
		List<AfTradeWithdrawDetailDo> detailList = afTradeWithdrawDetailDao.getByRecordId(id);
		List<Long> ids = new ArrayList<>();
		for (AfTradeWithdrawDetailDo detail: detailList) {
			ids.add(detail.getId());
		}
		afTradeOrderDao.updateStatusByIds(ids, TradeOrderStatus.EXTRACT.getCode());
		return 0;
	}

	
	@Override
	public int dealWithDrawFail(long id) {
		AfTradeWithdrawRecordDo recordDo = new AfTradeWithdrawRecordDo();
		recordDo.setId(id);
		recordDo.setStatus(TradeWithdrawRecordStatus.CLOSED.getCode());
		afTradeWithdrawRecordDao.updateById(recordDo);
		
		List<AfTradeWithdrawDetailDo> detailList = afTradeWithdrawDetailDao.getByRecordId(id);
		List<Long> ids = new ArrayList<>();
		for (AfTradeWithdrawDetailDo detail: detailList) {
			ids.add(detail.getId());
		}
		afTradeOrderDao.updateStatusByIds(ids, TradeOrderStatus.NEW.getCode());
		return 0;
	}
}
