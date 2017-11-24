package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowBillDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
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
	private AfUserOutDayDao afUserOutDayDao;
	
	@Resource
	private AfUserBankcardDao bankcardDao;
	
	@Resource
    TransactionTemplate transactionTemplate;

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
	public int countNotPayOverdueBill(Long userId) {
		return afBorrowBillDao.countNotPayOverdueBill(userId);
	}

	private void updateBorrowBills(long userId, int outDay, int oldOutDay,int payDay) {

		List<AfBorrowBillDo> list =  afBorrowBillDao.getNoPayBillByUserId(userId,new Date());
		if(list ==null || list.size() ==0)return;
		AfBorrowBillDo lastOutBill = afBorrowBillDao.getLastOutBill(userId);
		Date now = new Date();

		Map<String, Object> map = getCurrentYearAndMonth(list.get(0).getGmtOutDay(),outDay,payDay);
		int needPlus = 0;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH,outDay);
		if(lastOutBill == null){
			//正常修改

		}
		else{


			//比较出帐日
			int oldYearMonth = getYearMonth(lastOutBill.getGmtOutDay());
			if(oldYearMonth >= (Integer) map.get("OUT")){
				needPlus = needPlus +1;
			}

			if(needPlus == 0) {
				//比较还款日
				int oldYearMonth_pay = getYearMonth(lastOutBill.getGmtPayTime());
				if (oldYearMonth_pay >=(Integer) map.get("PAY")) {
					needPlus = needPlus + 1;
				}
			}
		}
		if(needPlus ==0){
			if(new Date().compareTo((Date)map.get("OUT_TIME"))>=0){
				needPlus = needPlus +1;
			}
		}

		//修改;
		for(AfBorrowBillDo afBorrowBillDo :list){
			Date _now = afBorrowBillDo.getGmtOutDay();
			if(needPlus != 0){
				Calendar datetime = Calendar.getInstance();
				datetime.setTime(_now);
				datetime.add(Calendar.MONTH,1);
				_now = datetime.getTime();
			}
			Map<String, Object> map1 = getCurrentYearAndMonth(_now,outDay,payDay);
			int billYear = afBorrowBillDo.getBillYear();
			int billMonth = afBorrowBillDo.getBillMonth();
			if(needPlus >0){
				billMonth = billMonth +1;
				if(billMonth>12) {
					billMonth =1;
					billYear =billYear+1;
				}
			}
			afBorrowBillDao.updateBillOutDay(afBorrowBillDo.getRid(),(Date) map1.get("OUT_TIME"),(Date)map1.get("PAY_TIME"),billYear,billMonth);
		}

	}

	private Map<String, Object> getCurrentYearAndMonth(Date now,int outDay, int payDay) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 账单日

		int default_out_day = outDay;
		int default_pay_day = payDay;



		int billYear = 0, billMonth = 0;
		String[] billDay = DateUtil.formatDate(now, DateUtil.MONTH_PATTERN).split("-");
		if (billDay.length == 2) {
			billYear = NumberUtil.objToIntDefault(billDay[0], 0);
			billMonth = NumberUtil.objToIntDefault(billDay[1], 0);
		}
		map.put(Constants.DEFAULT_YEAR, billYear);
		map.put(Constants.DEFAULT_MONTH, billMonth);

		Calendar out_datetime = Calendar.getInstance();
		out_datetime.setTime(now);
		//out_datetime.add(Calendar.MONTH,1);
		out_datetime.set(Calendar.DAY_OF_MONTH,default_out_day);
		out_datetime.set(Calendar.HOUR_OF_DAY,0);
		out_datetime.set(Calendar.MINUTE,0);
		out_datetime.set(Calendar.SECOND,0);
		out_datetime.set(Calendar.MILLISECOND,0);

		Calendar pay_datetime = Calendar.getInstance();
		pay_datetime.setTime(now);
		if(default_pay_day < default_out_day) {
			pay_datetime.add(Calendar.MONTH, 1);
		}
		pay_datetime.set(Calendar.DAY_OF_MONTH,default_pay_day);
		pay_datetime.set(Calendar.HOUR_OF_DAY,23);
		pay_datetime.set(Calendar.MINUTE,59);
		pay_datetime.set(Calendar.SECOND,59);
		//pay_datetime.set(Calendar.MILLISECOND,999);

		map.put("OUT",getYearMonth(out_datetime.getTime()));
		map.put("PAY",getYearMonth(pay_datetime.getTime()));
		map.put("OUT_TIME",out_datetime.getTime());
		map.put("PAY_TIME",pay_datetime.getTime());
		return map;
	}

	private Integer getYearMonth(Date date) {
		String[] billDay1 = DateUtil.formatDate(date, DateUtil.MONTH_PATTERN).split("-");
		return Integer.parseInt(billDay1[0]+billDay1[1]);
	}

	@Override
	public int addUserOutDay(final long userId, final int outDay, final int payDay) {
        final int[] returnValue={0};
        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                if(afUserOutDayDao.insertUserOutDay(userId,outDay,payDay)>0){
                    returnValue[0] =afUserOutDayDao.insertUserOutDayLog(userId,outDay);//插入日志
                    updateBorrowBills(userId,outDay,10,payDay);
                }
                return null;
            }
        });
        return returnValue[0];
    }

	@Override
	public int updateUserOutDay(final long userId, final int outDay, final int payDay) {
        final int[] returnValue = {0};
        transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus transactionStatus) {
                if(afUserOutDayDao.updateUserOutDay(userId,outDay,payDay)>0){
                    returnValue[0]  = afUserOutDayDao.insertUserOutDayLog(userId,outDay);//插入日志
                    updateBorrowBills(userId,outDay,10,payDay);
                }
                return null;
            }
        });
        return returnValue[0];
    }
}
