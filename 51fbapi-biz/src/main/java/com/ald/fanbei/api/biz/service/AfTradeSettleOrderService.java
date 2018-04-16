package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfTradeSettleOrderDo;

public interface AfTradeSettleOrderService {

	/**
	 * 生成结算单
	 * @param afTradeSettleOrderDo
	 * @return
	 */
	int createSettleOrder(AfTradeSettleOrderDo afTradeSettleOrderDo);
	
	/**
	 * 批量更新提现中结算单状态
	 * @param ids
	 * @param status
	 * @return
	 */
	int batchUpdateSettleOrderStatus(List<Long> ids, String status);
	
	/**
	 * 更新退款中结算单状态
	 * @param afTradeSettleOrderDo
	 * @return
	 */
	public int updateSettleOrder(AfTradeSettleOrderDo afTradeSettleOrderDo);
}
