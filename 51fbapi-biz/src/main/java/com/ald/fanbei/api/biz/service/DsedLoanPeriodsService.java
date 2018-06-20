package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;

import java.util.HashMap;

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
    DsedLoanPeriodsDo getLoanPeriodsByLoanNo(String loanNo,int nper);

}
