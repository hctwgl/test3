package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;

/**
 * 都市易贷借款还款表Dao
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:45:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanRepaymentDao extends BaseDao<DsedLoanRepaymentDo, Long> {

    DsedLoanRepaymentDo  getProcessingRepayment(@Param("loanId") Long loanId, @Param("nper") Integer nper);

    DsedLoanRepaymentDo getProcessLoanRepaymentByLoanId(@Param("loanId") Long loanId);

    DsedLoanRepaymentDo getRepayByTradeNo(@Param("tradeNo") String tradeNo);

    int getCurrDayRepayErrorTimesByUser(@Param("userId") Long userId);

    DsedLoanRepaymentDo getLoanRepaymentByLoanId(@Param("loanId")Long loanId);

}
