package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;

/**
 * Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalOrderCashService extends ParentService<AfBorrowLegalOrderCashDo, Long>{


	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowLegalOrderId(
			Long rid);

	int saveBorrowLegalOrderCash(AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo);

	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowId(Long rid);

	AfBorrowLegalOrderCashDo getLastOrderCashByBorrowId(Long rid);

	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowIdNoStatus(Long rid);

	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByCashNo(String cashNo);

	/**
	 * 计算剩余未还的本金(兼容新版旧版)
	 * @param cashDo
	 * @param orderCashDo
	 * @return
	 */
	BigDecimal calculateRestAmount(long borrowid);
	/**
	 * 计算剩余应还的金额
	 * @param cashDo
	 * @param orderCashDo
	 * @return
	 */
	BigDecimal calculateLegalRestAmount(AfBorrowCashDo cashDo, AfBorrowLegalOrderCashDo orderCashDo);
	
	/**
	 * 检查离线还款的金额是否合法
	 * @param cashDo
	 * @param orderCashDo
	 * @param curRepayAmount
	 * @param outTradeNo
	 */
	void checkOfflineRepayment(AfBorrowCashDo cashDo, AfBorrowLegalOrderCashDo orderCashDo, String curRepayAmount, String outTradeNo);
	
	/**
	 * 检查旧版客户端是否存在新版借钱数据,兼容判断V1和V2版本搭售
	 * @param version
	 * @param borrowId
	 */
	void checkIllegalVersionInvoke(Integer version, Long borrowId);

	int updateLegalOrderCashBalanced(AfBorrowLegalOrderCashDo legalOrderCashDo);

	AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowIdNoClosed(Long rid);
	
}
