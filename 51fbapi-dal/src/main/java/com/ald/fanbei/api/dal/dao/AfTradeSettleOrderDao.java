/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfTradeSettleOrderDo;

/**
 * @author luoxiao
 *
 */
public interface AfTradeSettleOrderDao {
	int createSettleOrder(AfTradeSettleOrderDo afTradeSettleOrderDo);
	
	int batchUpdateSettleOrderStatus(@Param("items") List<Long> ids, @Param("status") String status);
	
	int updateSettleOrder(AfTradeSettleOrderDo afTradeSettleOrderDo);

}
