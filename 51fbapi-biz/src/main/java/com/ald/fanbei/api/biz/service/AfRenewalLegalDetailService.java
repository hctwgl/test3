package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

/**  
 * @Description: 
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年12月11日
 */
public interface AfRenewalLegalDetailService {

	/**
	 * 收取手续费+利息+滞纳金，并创建续期记录
	 * 
	 * @return
	 */
	Map<String, Object> createLegalRenewal(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo, Integer appVersion);

	public long dealLegalRenewalSucess(String outTradeNo, String tradeNo);

	public long dealLegalRenewalFail(final String outTradeNo, final String tradeNo,String errorMsg);

	AfRenewalDetailDo getLastRenewalDetailByBorrowId(Long rid);

}
