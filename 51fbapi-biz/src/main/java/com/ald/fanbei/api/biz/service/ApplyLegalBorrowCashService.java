package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.bean.ApplyLegalBorrowCashParam;

public interface ApplyLegalBorrowCashService {

	public AfBorrowLegalOrderDo buildBorrowLegalOrder(Long userId, ApplyLegalBorrowCashParam param);

	public AfBorrowCashDo buildBorrowCashDo(AfUserBankcardDo afUserBankcardDo, Long userId,
			AfResourceDo rateInfoDo,  ApplyLegalBorrowCashParam param);

	public void checkLock(String lockKey);

	BigDecimal calculateMaxAmount(BigDecimal usableAmount);

	public void checkAccount(AfUserAccountDo accountDo, AfUserAuthDo authDo);

	public void checkAmount(ApplyLegalBorrowCashParam param, AfResourceDo rateInfoDo);

	public void checkPassword(AfUserAccountDo accountDo, ApplyLegalBorrowCashParam param);

	public void checkBindCard(AfUserAuthDo authDo);

	public void checkAuth(AfUserAuthDo authDo);

	public void checkCanBorrow(AfUserAccountDo accountDo, ApplyLegalBorrowCashParam param);

	public void checkBusi(AfUserAccountDo accountDo, AfUserAuthDo authDo, AfResourceDo rateInfoDo,
			AfUserBankcardDo bankCard,ApplyLegalBorrowCashParam param);

	public void addTodayTotalAmount(int day, BigDecimal amount);

	public void checkBorrowFinish(Long userId);

	public void delegatePay(String consumerNo, String orderNo, String result,
			AfBorrowLegalOrderDo afBorrowLegalOrderDo, AfUserBankcardDo mainCard);

	public void checkRiskRefused(Long userId);

	public void checkCardNotEmpty(AfUserBankcardDo mainCard);

	public void updateBorrowStatus(AfBorrowCashDo cashDo, AfBorrowLegalOrderDo afBorrowLegalOrderDo);

	public Long addBorrowResult(AfBorrowCashDo afBorrowCashDo, AfBorrowLegalOrderDo afBorrowLegalOrderDo);

	public String getAppName(RequestDataVo requestDataVo);

	public RiskVerifyRespBo submitRiskReview(Long borrowId, String appType, String ipAddress,
			ApplyLegalBorrowCashParam param, AfUserAccountDo accountDo, Long userId, AfBorrowCashDo afBorrowCashDo,String riskOrderNo);

	public String getAppType(RequestDataVo requestDataVo);

}
