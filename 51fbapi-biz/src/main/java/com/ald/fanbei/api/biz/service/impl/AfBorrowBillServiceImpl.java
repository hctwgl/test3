package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowBillDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.dto.AfOverdueBillDto;
import com.ald.fanbei.api.dal.domain.dto.AfOverdueOrderDto;
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
	
	@Resource
	private AfUserBankcardDao bankcardDao;

	@Override
	public List<AfBorrowBillDo> getMonthBillList(AfBorrowBillQuery query) {
		return afBorrowBillDao.getMonthBillList(query);
	}

	@Override
	public BigDecimal getMonthlyBillByStatus(Long userId, int billYear, int billMonth, String status) {
		BigDecimal amount = afBorrowBillDao.getMonthlyBillByStatus(userId, billYear, billMonth, status);
		return amount == null ? BigDecimal.ZERO : amount;
	}

	public BigDecimal getMonthlyBillByStatusNew(Long userId, int billYear, int billMonth, String status){
		BigDecimal amount = afBorrowBillDao.getMonthlyBillByStatusNew(userId, billYear, billMonth, status);
		return amount == null ? BigDecimal.ZERO : amount;
	}

	public BigDecimal getMonthlyBillByStatusNewV1(Long userId, String status){
		BigDecimal amount = afBorrowBillDao.getMonthlyBillByStatusNewV1(userId, status);
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
		return afBorrowBillDao.existMonthRepayingBill(userId, billYear, billMonth)> 0;
	}

	@Override
	public Long getOverduedAndNotRepayBillId(Long borrowId) {
		return afBorrowBillDao.getOverduedAndNotRepayBillId(borrowId);
	}

	@Override
	public AfBorrowBillDo getOverduedAndNotRepayBill(Long borrowId, Long billId) {
		return afBorrowBillDao.getOverduedAndNotRepayBill(borrowId, billId);
	}


	public List<AfBorrowBillDo> getAllBorrowNoPayByUserId(@Param("userId") long userId){
		return afBorrowBillDao.getAllBorrowNoPayByUserId(userId);
	}
	public List<HashMap> getBorrowBillNoPaySumByUserId(@Param("userId") long userId){
		return afBorrowBillDao.getBorrowBillNoPaySumByUserId(userId);
	}

	public  AfBorrowBillDo getTotalMonthlyBillByIds( Long userId, List<Long > ids ){
		return  afBorrowBillDao.getTotalMonthlyBillByIds(userId,ids);
	}
	
	@Override
	public List<AfOverdueOrderDto> getOverdueDataToRiskByBillIds(List<Long> billIds) {
		List<AfOverdueOrderDto> orderList = afBorrowBillDao.getOverdueDataToRiskByBillIds(billIds);
		if (orderList != null && orderList.size() > 0) {
			for (AfOverdueOrderDto order : orderList) {
				if (PayType.COMBINATION_PAY.getCode().equals(order.getPayType())) {
					AfUserBankcardDo bankDo = bankcardDao.getUserBankcardById(order.getBankId());
					if (bankDo != null && bankDo.getRid() != null) {
						order.setBankCard(bankDo.getCardNumber());
						order.setBankName(bankDo.getBankName());
					}
				}
				List<AfOverdueBillDto> billList = afBorrowBillDao.getAfOverdueBillDtoByBillIds(billIds,order.getOrderId());
				if (billList != null && billList.size() > 0) {
					order.setBorrows(billList);
				}
			}
			return orderList;
		}
		return null;
	}

	@Override
	public List<AfOverdueOrderDto> getOverdueDataToRiskByConsumerNo(Long consumerNo) {
		List<AfOverdueOrderDto> orderList = afBorrowBillDao.getOverdueDataToRiskByConsumerNo(consumerNo);
		if (orderList != null && orderList.size() > 0) {
			for (AfOverdueOrderDto order : orderList) {
				if (PayType.COMBINATION_PAY.getCode().equals(order.getPayType())) {
					AfUserBankcardDo bankDo = bankcardDao.getUserBankcardById(order.getBankId());
					if (bankDo != null && bankDo.getRid() != null) {
						order.setBankCard(bankDo.getCardNumber());
						order.setBankName(bankDo.getBankName());
					}
				}
				List<AfOverdueBillDto> billList = afBorrowBillDao.getAfOverdueBillDtoByConsumerNo(order.getOrderId());
				if (billList != null && billList.size() > 0) {
					order.setBorrows(billList);
				}
			}
			return orderList;
		}
		return null;
	}
}
