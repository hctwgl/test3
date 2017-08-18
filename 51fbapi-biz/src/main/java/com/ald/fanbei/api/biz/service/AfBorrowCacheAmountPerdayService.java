/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;

/**
 * @author chenjinhu
 *
 */
public interface AfBorrowCacheAmountPerdayService {
	
	/**
	 * 增加记录
	 * @param borrowCacheAmountPerdayDo
	 * @return
	 */
	int addBorrowCacheAmountPerday(AfBorrowCacheAmountPerdayDo borrowCacheAmountPerdayDo);
	
	/**
	 * 更新借款金额
	 * @param borrowCacheAmountPerdayDo
	 * @return
	 */
	int updateBorrowCacheAmount(AfBorrowCacheAmountPerdayDo borrowCacheAmountPerdayDo);
	
	/**
	 * 获取当天的总金额
	 * @param day
	 * @return
	 */
	AfBorrowCacheAmountPerdayDo getSigninByDay(Integer day);
}
