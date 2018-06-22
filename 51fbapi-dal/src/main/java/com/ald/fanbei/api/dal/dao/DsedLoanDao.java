package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.DsedLoanDo;

import java.util.Date;
import java.util.List;

/**
 * 借款Dao
 * 
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:48:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanDao extends BaseDao<DsedLoanDo, Long> {

    int getLoanOverdueCount(Date nowTime);


    List<DsedLoanDo> getLoanOverdue(int beginIndex, int pageSize,Date nowTime);

    int updateByLoanId(DsedLoanDo loanDo);

    DsedLoanDo getByUserId(Long userId);

    DsedLoanDo getByLoanNo(String loanNo);
}
