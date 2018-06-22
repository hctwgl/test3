package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;

import java.util.List;

import java.util.HashMap;

import java.math.BigDecimal;
import java.util.List;

/**
 * 都市易贷借款期数表Service
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:44:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanPeriodsService extends ParentService<DsedLoanPeriodsDo, Long>{
    /**
     * 通过编号查询借款信息
     * @param loanNo
     * @author chefeipeng
     * @return
     */
    DsedLoanPeriodsDo getLoanPeriodsByLoanNoAndNper(String loanNo,int nper);

    List<DsedLoanPeriodsDto>  getLoanOverdue(int nowPage, int pageSize);

    /**
     * 获取逾期数据的数量
     *
     * @return
     */
    int getLoanOverdueCount();

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

    DsedLoanPeriodsDo getLoanPeriodsByLoanNo(String loanNo);

    List<DsedLoanPeriodsDo> getNoRepayListByLoanId(Long rid);

    DsedLoanPeriodsDo getOneByLoanId(Long loanId);

}
