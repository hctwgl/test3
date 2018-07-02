package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Map;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

public interface AfOrderCombinationPayService {
    Map<String, Object> combinationPay(final Long userId, final String orderNo, AfOrderDo orderInfo,
		String tradeNo, Map<String, Object> resultMap, Boolean isSelf, Map<String, Object> virtualMap,
		BigDecimal bankAmount, AfBorrowDo borrow, RiskVerifyRespBo verybo, AfUserBankcardDo cardInfo, 
		String bankChannel);
}
