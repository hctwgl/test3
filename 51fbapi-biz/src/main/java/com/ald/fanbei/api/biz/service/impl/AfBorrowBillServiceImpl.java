package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.barlyClearance.AllBarlyClearanceBo;
import com.ald.fanbei.api.biz.bo.barlyClearance.AllBarlyClearanceDetailBo;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
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

	@Resource
	AfUserOutDayDao afUserOutDayDao;
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

	@Override
	public int getOverduedMonthByUserId(Long userId) {
		return afBorrowBillDao.getOverduedMonthByUserId(userId);
	}

	@Override
	public Date getLastPayDayByUserId(Long userId) {
		return afBorrowBillDao.getLastPayDayByUserId(userId);
	}

	@Override
	public BigDecimal getUserBillMoneyByQuery(AfBorrowBillQuery query) {
		return afBorrowBillDao.getUserBillMoneyByQuery(query);
	}

	@Override
	public List<AfBorrowBillDo> getUserBillListByQuery(AfBorrowBillQuery query) {
		return afBorrowBillDao.getUserBillListByQuery(query);
	}

	@Override
	public int countBillByQuery(AfBorrowBillQuery query) {
		return afBorrowBillDao.countBillByQuery(query);
	}

	@Override
	public List<AfBorrowBillDto> getBillListByQuery(AfBorrowBillQuery query) {
		return afBorrowBillDao.getBillListByQuery(query);
	}

	@Override
	public BigDecimal getUserOverdeuInterestByQuery(AfBorrowBillQuery query) {
		return afBorrowBillDao.getUserOverdeuInterestByQuery(query);
	}

	@Override
	public BigDecimal getInterestByBorrowId(Long borrowId) {
		return afBorrowBillDao.getInterestByBorrowId(borrowId);
	}

	@Override
	public BigDecimal getOverdueInterestByBorrowId(Long borrowId) {
		return afBorrowBillDao.getOverdueInterestByBorrowId(borrowId);
	}

	@Override
	public List<AfBorrowBillDo> getUserAllMonthBill(Long userId, int page,int pageSize) {
		int begin = (page - 1) * pageSize;
		return afBorrowBillDao.getUserAllMonthBill(userId,begin,pageSize);
	}


	/**
	 * 全部结清
	 * @param userId
	 * @return
	 */
	public List<AllBarlyClearanceBo> getAllClear(Long userId){
		List<AfBorrowBillDo> list = afBorrowBillDao.getBorrowBillList("N",userId);
		List<HashMap> mapLsit = new ArrayList<HashMap>();
		AfUserOutDayDo afUserOutDayDo = afUserOutDayDao.getUserOutDayByUserId(userId);
		int outDay = 10,payDay = 20;
		if(afUserOutDayDo !=null){
			outDay = afUserOutDayDo.getOutDay();
			payDay = afUserOutDayDo.getPayDay();
		}
		Date out_day = getDay(outDay);
		Date pay_day = getDay(payDay);

		HashMap<Long,Boolean> m = new HashMap<Long,Boolean>();
		List<AllBarlyClearanceBo> l = new ArrayList<AllBarlyClearanceBo>();
		for(AfBorrowBillDo afBorrowBillDo :list) {
			AllBarlyClearanceBo allBarlyClearanceBo = getAllBarlyBo(l, afBorrowBillDo.getBorrowId());
			if (allBarlyClearanceBo == null) {
				allBarlyClearanceBo.setBorrowId(afBorrowBillDo.getBorrowId());
				allBarlyClearanceBo.setNper(afBorrowBillDo.getNper());
				allBarlyClearanceBo.setTitle(afBorrowBillDo.getName());
				List<AllBarlyClearanceDetailBo> detailList = new ArrayList<AllBarlyClearanceDetailBo>();
				allBarlyClearanceBo.setDetailList(detailList);
				l.add(allBarlyClearanceBo);
			}
			if (afBorrowBillDo.getIsOut().intValue() == 1) {
				//逾期
				BigDecimal amount = allBarlyClearanceBo.getAmount().add(afBorrowBillDo.getBillAmount());
				allBarlyClearanceBo.setAmount(amount);
				allBarlyClearanceBo.setMinAdnMaxNper(afBorrowBillDo.getBillNper());
				AllBarlyClearanceDetailBo allBarlyClearanceDetailBo = new AllBarlyClearanceDetailBo();
				allBarlyClearanceDetailBo.setNper(afBorrowBillDo.getBillNper());
				allBarlyClearanceDetailBo.setAmount(afBorrowBillDo.getBillAmount());
				allBarlyClearanceDetailBo.setFree(false);
				allBarlyClearanceDetailBo.setStatus(1);
				allBarlyClearanceDetailBo.setOverdue(afBorrowBillDo.getOverdueStatus().equals("Y")?1:0); //逾期
				allBarlyClearanceDetailBo.setPoundAmount(afBorrowBillDo.getPoundageAmount().add(afBorrowBillDo.getInterestAmount()));
				allBarlyClearanceDetailBo.setInterest(afBorrowBillDo.getOverdueInterestAmount().add(afBorrowBillDo.getOverduePoundageAmount()));
				List<AllBarlyClearanceDetailBo> detailList = allBarlyClearanceBo.getDetailList();
				detailList.add(allBarlyClearanceDetailBo);

			} else {
				//未出
				boolean needPlus = false;
				if (m.containsKey(afBorrowBillDo.getBorrowId())) {
					needPlus = true;
				}
				if (!needPlus) {
					if (afBorrowBillDo.getBillNper().intValue() == 1) {
						needPlus = true;
						m.put(afBorrowBillDo.getBorrowId(), true);
					}
				}
				if (!needPlus) {
					Calendar c = Calendar.getInstance();
					c.set(Calendar.HOUR_OF_DAY, 0);
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
					if (pay_day.compareTo(c.getTime()) < 0) {
						m.put(afBorrowBillDo.getBorrowId(), true);
						needPlus = true;
					}
				}

				BigDecimal amount = needPlus? allBarlyClearanceBo.getAmount().add(afBorrowBillDo.getBillAmount()):allBarlyClearanceBo.getAmount().add(afBorrowBillDo.getPrincipleAmount());
				allBarlyClearanceBo.setAmount(amount);
				allBarlyClearanceBo.setMinAdnMaxNper(afBorrowBillDo.getBillNper());
				AllBarlyClearanceDetailBo allBarlyClearanceDetailBo = new AllBarlyClearanceDetailBo();
				allBarlyClearanceDetailBo.setNper(afBorrowBillDo.getBillNper());
				allBarlyClearanceDetailBo.setAmount(afBorrowBillDo.getBillAmount());
				allBarlyClearanceDetailBo.setFree(needPlus ? false : true);
				allBarlyClearanceDetailBo.setStatus(0);
				allBarlyClearanceDetailBo.setOverdue(afBorrowBillDo.getOverdueStatus().equals("Y") ? 1 : 0);
				allBarlyClearanceDetailBo.setPoundAmount(afBorrowBillDo.getPoundageAmount().add(afBorrowBillDo.getInterestAmount()));
				allBarlyClearanceDetailBo.setInterest(afBorrowBillDo.getOverdueInterestAmount().add(afBorrowBillDo.getOverduePoundageAmount()));
				List<AllBarlyClearanceDetailBo> detailList = allBarlyClearanceBo.getDetailList();
				detailList.add(allBarlyClearanceDetailBo);

			}
		}
		return  l;
	}

	private AllBarlyClearanceBo getAllBarlyBo(List<AllBarlyClearanceBo> list,Long borrowId){
		AllBarlyClearanceBo allBarlyClearanceBo = null;
		for(AllBarlyClearanceBo allBarlyClearanceBo1: list){
			if(allBarlyClearanceBo1.getBorrowId().intValue() == borrowId.intValue()){
				allBarlyClearanceBo = allBarlyClearanceBo1;
				break;
			}
		}
		return allBarlyClearanceBo;
	}



	/**
	 * 单个订单结清
	 * @param user
	 * @param billId
	 * @return
	 */
	public HashMap getOrderClear(Long user,Long billId){
		HashMap map = new HashMap();
		AfBorrowBillDo afBorrowBillDo = afBorrowBillDao.getBorrowBillById(billId);
		return map;
	}

	private Date getDay(int day){
		Calendar out = Calendar.getInstance();
		out.set(Calendar.DAY_OF_MONTH,day);
		out.set(Calendar.HOUR_OF_DAY,0);
		out.set(Calendar.MINUTE,0);
		out.set(Calendar.SECOND,0);
		return out.getTime();
	}
}
