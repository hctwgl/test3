package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.service.impl.DsedLoanRepaymentServiceImpl;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 都市易贷借款还款表Service
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:45:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanRepaymentService {

    DsedLoanRepaymentDo  getProcessingRepayment(Long loanId,Integer nper);


    DsedLoanRepaymentDo getProcessLoanRepaymentByLoanId(Long loanId);


    /**
     * 计算每期应还的金额
     * @return
     */
    BigDecimal calculateRestAmount(DsedLoanPeriodsDo dsedLoanPeriodsDo);

    Map<String, Object> repay(DsedLoanRepaymentServiceImpl.LoanRepayBo bo, String bankPayType);

    void dealRepaymentSucess(String tradeNo, String outTradeNo);
    void dealRepaymentSucess(String tradeNo, String outTradeNo, final DsedLoanRepaymentDo repaymentDo, String operator, Long collectionRepaymentId, List<HashMap> periodsList);

    void dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String errorMsg);
}
