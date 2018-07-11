package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.DsedLoanOverdueLogDo;

import java.util.Date;

/**
 * 都市e贷借款逾期记录表Dao
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:42:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanOverdueLogDao extends BaseDao<DsedLoanOverdueLogDo, Long> {

    int addLoanOverdueLog(DsedLoanOverdueLogDo loanOverdueLogDo);

    DsedLoanOverdueLogDo getLoanOverDueLogByNow(String  periodsId);


}
