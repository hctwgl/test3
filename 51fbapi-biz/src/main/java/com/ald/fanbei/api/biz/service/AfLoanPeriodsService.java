package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;

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
	
	/**
	 * 统计目标贷款下的全部应还期数的汇总信息
	 * @param loanId
	 * @return Map Keys如下<br/>
	 * 	periodIds Nullable 期数id串，以逗号相连<br/>
	 * 	restAmount Nullable 剩余应还总金额<br/> 
	 * 	gmtPlanRepay Nullable 应还期数的最后一期计划还款时间<br/>
	 */
	Map<String,Object> getTotalRestInfo(Long loanId);
	
	BigDecimal calcuRestAmount(AfLoanPeriodsDo period);

	AfLoanPeriodsDo getLastActivePeriodByLoanId(Long loanId);

	List<AfLoanPeriodsDo> listByLoanId(Long loanId);

	List<AfLoanPeriodsDo> getNoRepayListByLoanId(Long rid);
	
}
