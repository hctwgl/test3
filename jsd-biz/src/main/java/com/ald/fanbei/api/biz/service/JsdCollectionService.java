package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl.RepayDealBo;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;

/**
 * @author CFP
 *	处理催收相关业务
 */
public interface JsdCollectionService {
	
	public void nofityRisk(RepayDealBo repayDealBo, JsdBorrowCashRepaymentDo repaymentDo, JsdBorrowLegalOrderRepaymentDo orderRepaymentDo, JsdRepayType type,String dataId);
	
}
