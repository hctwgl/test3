package com.ald.fanbei.api.biz.service;

import java.util.Map;

import com.ald.fanbei.api.biz.service.impl.AfBorrowLegalRepaymentServiceImpl.RepayBo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;

/**
 * Service
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalRepaymentService{
    Map<String, Object> repay(RepayBo bo,String bankPayType);

	void offlineRepay(AfBorrowLegalOrderCashDo orderCashDo, String borrowNo,
					  String repayType, String repayTime, String repayAmount,
					  String restAmount, String outTradeNo, String isBalance,String repayCardNum,String operator,String isAdmin);
	
	void dealRepaymentSucess(String tradeNo, String outTradeNo);
	void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfRepaymentBorrowCashDo repaymentDo, final AfBorrowLegalOrderRepaymentDo orderRepaymentDo, String operator, final String isBalance);

	void dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String errorMsg);
}
