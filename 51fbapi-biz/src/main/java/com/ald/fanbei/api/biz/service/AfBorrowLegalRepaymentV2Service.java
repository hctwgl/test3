package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.biz.service.impl.AfBorrowLegalRepaymentV2ServiceImpl.RepayBo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;

/**
 * Service
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalRepaymentV2Service extends ParentService<AfRepaymentBorrowCashDo, Long>{
	void repay(RepayBo bo);
	
	void offlineRepay(AfBorrowCashDo cashDo, String borrowNo, 
			String repayType, String repayTime, String repayAmount,
			String restAmount, String outTradeNo, String isBalance);
	
	void dealRepaymentSucess(String tradeNo, String outTradeNo);
	void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfRepaymentBorrowCashDo repaymentDo);
	
	void dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String errorMsg);
	
	/**
	 * 计算剩余应还的金额
	 * @param cashDo
	 * @param orderCashDo
	 * @return
	 */
	BigDecimal calculateRestAmount(AfBorrowCashDo cashDo);
}
