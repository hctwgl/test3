package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 贷款业务Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanPeriodsService extends ParentService<AfLoanPeriodsDo, Long>{
	
	/**
	 * 根据贷款额、期数信息、贷款产品类型 计算贷款详情和各分期详情
	 * @param amount NotNull
	 * @param userId Nullable
	 * @param periods NotNull
	 * @param loanNo Nullable
	 * @param prdType NotNull
	 * @return 
	 * 	index-0：{@link AfLoanDo} 对应贷款总信息 <br/>
	 * 	index-1: {@link AfLoanPeriodsDo} 对应第一期信息<br/> 
	 *  index-2: {@link AfLoanPeriodsDo} 对应第二期信息<br/>
	 *  index-*: {@link AfLoanPeriodsDo} 对应第*期信息
	 */
	List<Object> resolvePeriods(BigDecimal amount, Long userId, int periods, String loanNo, String prdType);
	
	BigDecimal calcuRestAmount(AfLoanPeriodsDo period);

	AfLoanPeriodsDo getLastActivePeriodByLoanId(Long loanId);

	List<AfLoanPeriodsDo> listByLoanId(Long loanId);

	List<AfLoanPeriodsDo> getNoRepayListByLoanId(Long rid);

	List<AfLoanPeriodsDo> listCanRepayPeriods(Long loanId);

	AfLoanPeriodsDo getOneByLoanId(Long loanId);

	List<AfLoanPeriodsDo> getAllLoanPeriodsByLoanId(Long loanId);
	
	/**
	 * 
	 * 
	 * @author wangli
	 * @date 2018/4/20 15:07
	 */
	List<AfLoanPeriodsDo> listUnChargeRepayPeriods(Long loanId);
}
