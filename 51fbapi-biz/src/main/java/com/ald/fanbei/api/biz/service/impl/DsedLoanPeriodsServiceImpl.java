package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;
import com.ald.fanbei.api.biz.service.DsedLoanProductService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.DsedLoanProductDao;
import com.ald.fanbei.api.dal.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanPeriodsDao;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import static com.ald.fanbei.api.biz.service.impl.AfLoanPeriodsServiceImpl.MAX_DAY_NO;


/**
 * 都市易贷借款期数表ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:44:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedLoanPeriodsService")
public class DsedLoanPeriodsServiceImpl extends ParentServiceImpl<DsedLoanPeriodsDo, Long> implements DsedLoanPeriodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedLoanPeriodsServiceImpl.class);

	public static final BigDecimal DAYS_OF_YEAR = BigDecimal.valueOf(360);
	public static final BigDecimal DAYS_OF_MONTH = BigDecimal.valueOf(30);
	public static final int MAX_DAY_NO = 28;
    @Resource
    private DsedLoanPeriodsDao dsedLoanPeriodsDao;

    @Resource
	private DsedLoanProductService dsedLoanProductService;


		@Override
	public BaseDao<DsedLoanPeriodsDo, Long> getDao() {
		return dsedLoanPeriodsDao;
	}

	@Override
	public List<DsedLoanPeriodsDto> getLoanOverdue(int nowPage, int pageSize) {
		return dsedLoanPeriodsDao.getLoanOverdue(new Date(), nowPage,  pageSize );
	}

	@Override
	public int getLoanOverdueCount() {
		Date date = new Date(System.currentTimeMillis());
		Date bengin = DateUtil.getStartOfDate(date);
		return dsedLoanPeriodsDao.getLoanOverdueCount(bengin);
	}

	/**
	 * 通过编号查询借款信息
	 * @param loanNo
	 * @author
	 * @returnchefeipeng
	 */
	@Override
	public DsedLoanPeriodsDo getLoanPeriodsByLoanNoAndNper(String loanNo,int nper){
		return dsedLoanPeriodsDao.getLoanPeriodsByLoanNoAndNper(loanNo,nper);
	}

	@Override
	public List<Object> resolvePeriods(BigDecimal amount, Long userId, int periods, String loanNo, String prdType) {
		DsedLoanRateDo loanRateDo = dsedLoanProductService.getByPrdTypeAndNper(prdType, periods+"");

		BigDecimal interestRate = new BigDecimal(loanRateDo.getInterestRate());
		BigDecimal poundageRate = new BigDecimal(loanRateDo.getPoundageRate());
		BigDecimal overdueRate = new BigDecimal(loanRateDo.getOverdueRate());
		BigDecimal layRate = interestRate.add(poundageRate);
		BigDecimal userLayDailyRate = layRate.divide(DAYS_OF_YEAR, 10, RoundingMode.HALF_UP);

		BigDecimal interestRatio = interestRate.divide(layRate, 10, RoundingMode.HALF_UP);

		List<Object> result = new ArrayList<Object>();

		// 设贷款额为a，月利率为i，年利率为I，还款月数为n，每月还款额为b，还款利息总和为Y
		BigDecimal a = amount;
		BigDecimal i = userLayDailyRate.multiply(DAYS_OF_MONTH);
		int n = periods;
		// 月均总还款:b ＝ a×i×（1＋i）^n÷〔（1＋i）^n－1〕
		BigDecimal totalFeePerPeriod = a.multiply(i)
				.multiply( (i.add(BigDecimal.ONE)).pow(n) )
				.divide( (i.add(BigDecimal.ONE)).pow(n).subtract(BigDecimal.ONE) , 2, RoundingMode.HALF_UP);
		// 还款总额:n * b
		BigDecimal totalFee = totalFeePerPeriod.multiply( BigDecimal.valueOf(periods) );
		// 支付总利息:n * b - a
		BigDecimal totalIncome = totalFee.subtract(a).setScale(2, RoundingMode.HALF_UP);

		BigDecimal totalInterestFee = totalIncome.multiply(interestRatio).setScale(2, RoundingMode.HALF_UP);
		BigDecimal totalServiceFee = totalIncome.subtract(totalInterestFee).setScale(2, RoundingMode.HALF_UP);
		result.add(DsedLoanDo.gen(userId, loanNo, prdType, periods, poundageRate, interestRate, overdueRate, userLayDailyRate,
				amount, totalServiceFee, totalInterestFee));

		BigDecimal capitalPerPeriod;//每期本金
		BigDecimal incomePerPeriod;	//每期总利息
		BigDecimal interestFeePerPeriod;
		BigDecimal serviceFeePerPeriod;
		for(int j = 1; j<=periods; j++) {// 计算每期详细数据
			// 第n月还款利息为：＝（a×i－b）×（1＋i）^（n－1）＋b
			incomePerPeriod = ( a.multiply(i).subtract(totalFeePerPeriod) )
					.multiply( BigDecimal.ONE.add(i).pow(j-1) )
					.add(totalFeePerPeriod).setScale(2, RoundingMode.HALF_UP);
			capitalPerPeriod = totalFeePerPeriod.subtract(incomePerPeriod);

			interestFeePerPeriod = incomePerPeriod.multiply(interestRatio).setScale(2, RoundingMode.HALF_UP);
			serviceFeePerPeriod = incomePerPeriod.subtract(interestFeePerPeriod).setScale(2, RoundingMode.HALF_UP);

			// 计算还款时间
			Date gmtPlanRepay = new Date();
			if (j != 1){
				gmtPlanRepay = DateUtil.setDayZeroTime(new Date());//非第一期时间修改为23:59:59
			}
			gmtPlanRepay = DateUtil.addMonths(gmtPlanRepay, j);
			int today = DateUtil.getTodayNoInMonth(gmtPlanRepay);
			if(today > MAX_DAY_NO) {
				gmtPlanRepay = DateUtil.setDayNoInMonth(gmtPlanRepay, MAX_DAY_NO);
			}

			result.add(DsedLoanPeriodsDo.gen(userId, loanNo, prdType, periods, j,
					capitalPerPeriod, serviceFeePerPeriod, interestFeePerPeriod, gmtPlanRepay));
		}

		return result;
	}

	@Override
	public DsedLoanPeriodsDo getLoanPeriodsByLoanNo(String loanNo){
		return dsedLoanPeriodsDao.getLoanPeriodsByLoanNo(loanNo);
	}

	@Override
	public List<DsedLoanPeriodsDo> getNoRepayListByLoanId(Long rid){
		return dsedLoanPeriodsDao.getNoRepayListByLoanId(rid);
	}

	@Override
	public DsedLoanPeriodsDo getOneByLoanId(Long loanId) {
		return dsedLoanPeriodsDao.getOneByLoanId(loanId);
	}




}