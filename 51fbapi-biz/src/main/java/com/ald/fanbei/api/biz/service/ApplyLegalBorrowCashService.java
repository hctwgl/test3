package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.biz.bo.ApplyLegalBorrowCashBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

public interface ApplyLegalBorrowCashService {

	public AfBorrowLegalOrderDo buildBorrowLegalOrder(Long userId, ApplyLegalBorrowCashBo param);

	public AfBorrowCashDo buildBorrowCashDo(AfUserBankcardDo afUserBankcardDo, Long userId,
			AfResourceDo rateInfoDo,  ApplyLegalBorrowCashBo param);

	public void checkLock(String lockKey);

	BigDecimal calculateMaxAmount(BigDecimal usableAmount);

	public void checkAccount(AfUserAccountDo accountDo, AfUserAuthDo authDo);

	public void checkAmount(ApplyLegalBorrowCashBo param, AfResourceDo rateInfoDo);

	public void checkPassword(AfUserAccountDo accountDo, ApplyLegalBorrowCashBo param);

	public void checkBindCard(AfUserAuthDo authDo);

	public void checkAuth(AfUserAuthDo authDo);

	public void checkCanBorrow(AfUserAccountDo accountDo, ApplyLegalBorrowCashBo param);

	public void checkBusi(AfUserAccountDo accountDo, AfUserAuthDo authDo, AfResourceDo rateInfoDo,
			AfUserBankcardDo bankCard,ApplyLegalBorrowCashBo param);

	public void addTodayTotalAmount(int day, BigDecimal amount);

	public void checkBorrowFinish(Long userId);

	public void delegatePay(String consumerNo, String orderNo, String result,
			AfBorrowLegalOrderDo afBorrowLegalOrderDo, AfUserBankcardDo mainCard);

	public void checkRiskRefused(Long userId);

	public void checkCardNotEmpty(AfUserBankcardDo mainCard);

	public void updateBorrowStatus(AfBorrowCashDo cashDo, AfBorrowLegalOrderDo afBorrowLegalOrderDo);

	public Long addBorrowRecord(AfBorrowCashDo afBorrowCashDo, AfBorrowLegalOrderDo afBorrowLegalOrderDo);

	public RiskVerifyRespBo submitRiskReview(Long borrowId, String appType, String ipAddress,
			ApplyLegalBorrowCashBo param, AfUserAccountDo accountDo, Long userId, AfBorrowCashDo afBorrowCashDo,String riskOrderNo);

	public void updateBorrowStatus2Apply(Long borrowId, String riskOrderNo);

	public void checkGenRecordError(Long borrowId);

}
