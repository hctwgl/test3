package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfTradeSettleOrderService;
import com.ald.fanbei.api.biz.service.AfTradeWithdrawRecordService;
import com.ald.fanbei.api.common.enums.AfTradeSettleOrderStatus;
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
	
	@Resource
	private AfTradeSettleOrderService afTradeSettleOrderService;
	
	@Override
	public BaseDao<AfTradeWithdrawRecordDo, Long> getDao() {
		return afTradeWithdrawRecordDao;
	}

	@Override
	public int dealWithDrawSuccess(long id) {
		logger.info("enter into dealWithDrawSuccess,id={}", id);
		AfTradeWithdrawRecordDo recordDo = new AfTradeWithdrawRecordDo();
		recordDo.setId(id);
		recordDo.setStatus(TradeWithdrawRecordStatus.TRANSED.getCode());
		afTradeWithdrawRecordDao.updateById(recordDo);
		
		List<AfTradeWithdrawDetailDo> detailList = afTradeWithdrawDetailDao.getByRecordId(id);
		List<Long> ids = new ArrayList<>();
		for (AfTradeWithdrawDetailDo detail: detailList) {
			logger.info("dealWithDrawSuccess,id detail:" +  detail.getOrderId());
			ids.add(detail.getOrderId());
		}
		
		// modify by luoxiao 结算单状态变更 on 2018-04-10
//		afTradeOrderDao.updateStatusByIds(ids, TradeOrderStatus.EXTRACT.getCode());
		int count= afTradeSettleOrderService.batchUpdateSettleOrderStatus(ids, AfTradeSettleOrderStatus.EXTRACTED.getCode());
		logger.info("dealWithDrawSuccess,count:" + count);
		logger.info("dealWithDrawSuccess,id={}, end.", id);
		// end by luoxiao 结算单状态变更 on 2018-04-10
		return 0;
	}

	
	@Override
	public int dealWithDrawFail(long id) {
		logger.info("enter into dealWithDrawFail,id={}", id);
		AfTradeWithdrawRecordDo recordDo = new AfTradeWithdrawRecordDo();
		recordDo.setId(id);
		recordDo.setStatus(TradeWithdrawRecordStatus.CLOSED.getCode());
		afTradeWithdrawRecordDao.updateById(recordDo);
		
		List<AfTradeWithdrawDetailDo> detailList = afTradeWithdrawDetailDao.getByRecordId(id);
		List<Long> ids = new ArrayList<>();
		for (AfTradeWithdrawDetailDo detail: detailList) {
			ids.add(detail.getOrderId());
		}
		// modify by luoxiao 结算单状态变更 on 2018-04-10
//		afTradeOrderDao.updateStatusByIds(ids, TradeOrderStatus.NEW.getCode());
		afTradeSettleOrderService.batchUpdateSettleOrderStatus(ids, AfTradeSettleOrderStatus.EXTRACTABLE.getCode());
		logger.info("dealWithDrawFail,id={}, end.", id);
		// end by luoxiao 结算单状态变更 on 2018-04-10
		return 0;
	}

	@Override
	public List<String> withdrawGridDate(Long businessId, Date startDate, Date endDate) {
		return afTradeWithdrawRecordDao.withdrawGridDate(businessId, startDate, endDate);
	}

	@Override
	public List<AfTradeWithdrawRecordDo> withdrawGrid(Long businessId, Date startDate, Date endDate) {
		return afTradeWithdrawRecordDao.withdrawGrid(businessId, startDate, endDate);
	}

}
