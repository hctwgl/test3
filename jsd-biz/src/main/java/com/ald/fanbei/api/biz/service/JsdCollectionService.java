package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;

/**
 * @author CFP
 *	处理催收相关业务
 */
public interface JsdCollectionService {
	
	public void nofityRepayment(JsdBorrowCashRepaymentDo repaymentDo, JsdBorrowLegalOrderRepaymentDo orderRepaymentDo,
			String curOutTradeNo, String borrowNo, String orderId, Long uid, JsdRepayType type);
	
}
