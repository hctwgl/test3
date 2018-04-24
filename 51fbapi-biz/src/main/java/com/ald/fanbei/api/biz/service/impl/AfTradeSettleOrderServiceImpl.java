package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfTradeSettleOrderService;
import com.ald.fanbei.api.dal.dao.AfTradeSettleOrderDao;
import com.ald.fanbei.api.dal.domain.AfTradeSettleOrderDo;

@Service("afTradeSettleOrderService")
public class AfTradeSettleOrderServiceImpl implements AfTradeSettleOrderService {
	
	@Resource
	private AfTradeSettleOrderDao afTradeSettleOrderDao;

	@Override
	public int createSettleOrder(AfTradeSettleOrderDo afTradeSettleOrderDo) {
		return afTradeSettleOrderDao.createSettleOrder(afTradeSettleOrderDo);
	}
	
	@Override
	public int batchUpdateSettleOrderStatus(List<Long> ids, String status){
		return afTradeSettleOrderDao.batchUpdateSettleOrderStatus(ids, status);
	}
	
	@Override
	public int updateSettleOrder(AfTradeSettleOrderDo afTradeSettleOrderDo){
		return afTradeSettleOrderDao.updateSettleOrder(afTradeSettleOrderDo);
	}

}
