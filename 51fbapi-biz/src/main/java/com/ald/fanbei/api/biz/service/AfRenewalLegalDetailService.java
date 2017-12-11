package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

public interface AfRenewalLegalDetailService {

	/**
	 * 收取手续费+利息+滞纳金，并创建续期记录
	 * 
	 * @return
	 */
	Map<String, Object> createLegalRenewal(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo, Integer appVersion);

	public long dealLegalRenewalSucess(String outTradeNo, String tradeNo);

	public long dealLegalRenewalFail(final String outTradeNo, final String tradeNo,String errorMsg);


}
