package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowBillDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月17日下午21:52:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowBillService")
public class AfBorrowBillServiceImpl implements AfBorrowBillService {

	@Resource
	private AfBorrowBillDao afBorrowBillDao;

	@Override
	public List<AfBorrowBillDo> getMonthBillList(AfBorrowBillQuery query) {
		return afBorrowBillDao.getMonthBillList(query);
	}

	@Override
	public BigDecimal getMonthlyBillByStatus(Long userId, int billYear, int billMonth, String status) {
		BigDecimal amount = afBorrowBillDao.getMonthlyBillByStatus(userId, billYear, billMonth, status);
		return amount == null ? BigDecimal.ZERO : amount;
	}

	@Override
	public List<AfBorrowTotalBillDo> getUserFullBillList(Long userId) {
		return afBorrowBillDao.getUserFullBillList(userId);
	}

	@Override
	public AfBorrowBillDo getBorrowBillById(Long rid) {
		return afBorrowBillDao.getBorrowBillById(rid);
	}

	@Override
	public AfBorrowBillDo getTotalMonthlyBillByUserId(Long userId, int billYear, int billMonth) {
		return afBorrowBillDao.getTotalMonthlyBillByUserId(userId, billYear, billMonth);
	}

	@Override
	public AfBorrowBillDo getBillAmountByIds(String ids) {
		return afBorrowBillDao.getBillAmountByIds(StringUtil.splitToList(ids, ","));
	}
	/**
	 * update by fumeiai 在af_borrow_bill表里增加了coupon_amount(优惠减免),jfb_amount(集分宝抵扣),rebate_amount(返利抵扣)
	 * 在还款成功的时候，将优惠值平分到每个账单里
	 */
	@Override
	public int updateBorrowBillStatusByIds(String ids, String status, Long repaymentId, BigDecimal couponAmount, BigDecimal jfbAmount, BigDecimal rebateAmount) {
		List<String> idsList = StringUtil.splitToList(ids, ",");
		BigDecimal billNum = new BigDecimal(idsList.size());
		BigDecimal couponAmountAvg = couponAmount.divide(billNum, 0, BigDecimal.ROUND_HALF_EVEN);
		BigDecimal jfbAmountAvg = couponAmount.divide(billNum, 0, BigDecimal.ROUND_HALF_EVEN);
		BigDecimal rebateAmountAvg = couponAmount.divide(billNum, 0, BigDecimal.ROUND_HALF_EVEN);
		int resultCode = 1;
		for (int i = 0; i < idsList.size(); i++) {
			if (i == idsList.size() - 1) {
				int flag = afBorrowBillDao.updateBorrowBillStatusById(idsList.get(i), status, repaymentId, couponAmount.subtract(couponAmountAvg.multiply(new BigDecimal(idsList.size() - 1))), 
						jfbAmount.subtract(jfbAmountAvg.multiply(new BigDecimal(idsList.size() - 1))), rebateAmount.subtract(rebateAmountAvg.multiply(new BigDecimal(idsList.size() - 1))));
				resultCode = resultCode * flag;
			} else {
				int flag = afBorrowBillDao.updateBorrowBillStatusById(idsList.get(i), status, repaymentId, couponAmountAvg, jfbAmountAvg, rebateAmountAvg);
				resultCode = resultCode * flag;
			}
		}
		// return afBorrowBillDao.updateBorrowBillStatusByIds(StringUtil.splitToList(ids, ","), status, repaymentId);
		return resultCode;
	}

	@Override
	public int getUserMonthlyBillNotpayCount(int year, int month, Long userId) {
		return afBorrowBillDao.getUserMonthlyBillNotpayCount(year, month, userId);
	}

	@Override
	public int updateTotalBillStatus(int year, int month, Long userId, String status) {
		return afBorrowBillDao.updateTotalBillStatus(year, month, userId, status);
	}

	@Override
	public AfBorrowBillDo getBillAmountByCashIds(String ids) {
		return afBorrowBillDao.getBillAmountByCashIds(StringUtil.splitToList(ids, ","));
	}

	@Override
	public AfBorrowBillDto getBorrowBillDtoById(Long rid) {
		return afBorrowBillDao.getBorrowBillDtoById(rid);
	}

	@Override
	public int getUserMonthlyBillTotalCount(int year, int month, Long userId) {
		return afBorrowBillDao.getUserMonthlyBillTotalCount(year, month, userId);
	}

	@Override
	public BigDecimal getBorrowBillByBorrowId(Long borrowId) {
		return afBorrowBillDao.getBorrowBillByBorrowId(borrowId);
	}

	@Override
	public List<AfBorrowBillDo> getAllBorrowBillByBorrowId(Long borrowId) {
		return afBorrowBillDao.getAllBorrowBillByBorrowId(borrowId);
	}

	@Override
	public int getBorrowBillWithNoPayByUserId(Long userId) {
		return afBorrowBillDao.getBorrowBillWithNoPayByUserId(userId);
	}
	
	@Override
	public int getPaidBillNumByBorrowId(Long borrowId) {
		return afBorrowBillDao.getPaidBillNumByBorrowId(borrowId);
	}

	@Override
	public BigDecimal getSumIncomeByBorrowId(Long borrowId) {
		return afBorrowBillDao.getSumIncomeByBorrowId(borrowId);
	}

	@Override
	public Long getSumOverdueDayByBorrowId(Long borrowId) {
		return afBorrowBillDao.getSumOverdueDayByBorrowId(borrowId);
	}

	@Override
	public int getSumOverdueCountByBorrowId(Long borrowId) {
		return afBorrowBillDao.getSumOverdueCountByBorrowId(borrowId);
	}

	@Override
	public int updateBorrowBillStatusByBillIdsAndStatus(List<Long> billIds, String status) {
		return afBorrowBillDao.updateBorrowBillStatusByBillIdsAndStatus(billIds, status);
	}

	@Override
	public List<AfBorrowBillDo> getBorrowBillByIds(List<Long> billIdList) {
		return afBorrowBillDao.getBillListByIds(billIdList);
	}

	@Override
	public boolean existMonthRepayingBill(Long userId, Integer billYear, Integer billMonth) {
		return afBorrowBillDao.existMonthRepayingBill(userId, billYear, billMonth)> 1;
	}	
	
}
