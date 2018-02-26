package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;

/**
 * ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afBorrowLegalOrderCashService")
public class AfBorrowLegalOrderCashServiceImpl extends ParentServiceImpl<AfBorrowLegalOrderCashDo, Long>
		implements AfBorrowLegalOrderCashService {

	@Resource
	private AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;
	@Resource 
	private AfBorrowLegalOrderRepaymentDao afBorrowLegalOrderRepaymentDao;
	@Resource
	private AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
	@Resource
	private AfBorrowLegalOrderService afBorrowLegalOrderService;
	
	@Resource
	GeneratorClusterNo generatorClusterNo;

	@Override
	public BaseDao<AfBorrowLegalOrderCashDo, Long> getDao() {
		return afBorrowLegalOrderCashDao;
	}

	@Override
	public AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowLegalOrderId(Long rid) {
		return afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowLegalOrderId(rid);
	}

	@Override
	public int saveBorrowLegalOrderCash(AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo) {
		String cashNo = generatorClusterNo.geBorrowLegalOrderCashNo(new Date());
		afBorrowLegalOrderCashDo.setCashNo(cashNo);
		return afBorrowLegalOrderCashDao.saveRecord(afBorrowLegalOrderCashDo);
	}

	@Override
	public AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowId(Long borrowId) {
		return afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(borrowId);
	}

	@Override
	public AfBorrowLegalOrderCashDo getLastOrderCashByBorrowId(Long rid) {
		return afBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(rid);
	}

	@Override
	public AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowIdNoStatus(Long rid) {
		return afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowIdNoStatus(rid);
	}

	@Override
	public AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByCashNo(String cashNo) {
		return afBorrowLegalOrderCashDao.getOrderCashByCashNo(cashNo);
	}

	@Override
	public BigDecimal calculateRestAmount(long borrowid) {
		return afBorrowLegalOrderCashDao.calculateRestAmount(borrowid);
	}
	@Override
	public BigDecimal calculateLegalRestAmount(AfBorrowCashDo cashDo, AfBorrowLegalOrderCashDo orderCashDo) {
		BigDecimal restAmount = BigDecimal.ZERO;
		if(cashDo != null) {
			restAmount = BigDecimalUtil.add(restAmount, cashDo.getAmount(),
					cashDo.getOverdueAmount(), cashDo.getSumOverdue(), 
					cashDo.getRateAmount(),cashDo.getSumRate(),
					cashDo.getPoundage(),cashDo.getSumRenewalPoundage())
					.subtract(cashDo.getRepayAmount());
		}
		if(orderCashDo != null) {
			restAmount = BigDecimalUtil.add(restAmount, orderCashDo.getAmount(),
					orderCashDo.getOverdueAmount(), orderCashDo.getSumRepaidOverdue(), 
					orderCashDo.getInterestAmount(),orderCashDo.getSumRepaidInterest(),
					orderCashDo.getPoundageAmount(),orderCashDo.getSumRepaidPoundage())
					.subtract(orderCashDo.getRepaidAmount());
		}
		return restAmount;
	}
	@Override
	public void checkOfflineRepayment(AfBorrowCashDo cashDo, AfBorrowLegalOrderCashDo orderCashDo, String offlineRepayAmount, String outTradeNo) {
		if(afBorrowLegalOrderRepaymentDao.tuchByOutTradeNo(outTradeNo) != null ||
				afRepaymentBorrowCashDao.getRepaymentBorrowCashByTradeNo(null, outTradeNo) != null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_REPEAT_ERROR);
		}
		
		BigDecimal restAmount = calculateLegalRestAmount(cashDo, orderCashDo);
		// 因为有用户会多还几分钱，所以加个安全金额限制，当还款金额 > 用户应还金额+200元 时，返回错误
		if (NumberUtil.objToBigDecimalDivideOnehundredDefault(offlineRepayAmount, BigDecimal.ZERO)
				.compareTo(restAmount.add(BigDecimal.valueOf(200))) > 0) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
		}
	}

	@Override
	public void checkIllegalVersionInvoke(Integer version, Long borrowId) {
		if (version <= 401) {
			Long id = afBorrowLegalOrderCashDao.tuchByBorrowId(borrowId);
			if (id != null) {
				throw new FanbeiException(FanbeiExceptionCode.MUST_UPGRADE_NEW_VERSION_REPAY);
			}
			
			if(afBorrowLegalOrderService.isV2BorrowCash(borrowId)) {
				throw new FanbeiException(FanbeiExceptionCode.MUST_UPGRADE_NEW_VERSION_REPAY);
			}
		}
	}

	@Override
	public int updateLegalOrderCashBalanced(AfBorrowLegalOrderCashDo legalOrderCashDo) {
		return afBorrowLegalOrderCashDao.updateLegalOrderCashBalanced(legalOrderCashDo);
	}

	@Override
	public AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowIdNoClosed(Long rid) {
		return afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowIdNoClosed(rid);
	}
}