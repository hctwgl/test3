package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;

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

    List<DsedLoanPeriodsDto>  getLoanOverdue(int nowPage, int pageSize);

    /**
     * 获取逾期数据的数量
     *
     * @return
     */
    int getLoanOverdueCount();

}
