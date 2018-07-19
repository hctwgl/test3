package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.ald.fanbei.api.biz.service.impl.DsedLoanRepaymentServiceImpl;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;

/**
 * 都市易贷借款还款表Service
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:45:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanRepaymentService{

    DsedLoanRepaymentDo  getProcessingRepayment(Long loanId,Integer nper);


    DsedLoanRepaymentDo getProcessLoanRepaymentByLoanId(Long loanId);


    /**
     * 计算每期应还的金额
     * @return
     */
    BigDecimal calculateRestAmount(DsedLoanPeriodsDo dsedLoanPeriodsDo);

    void repay(DsedLoanRepaymentServiceImpl.LoanRepayBo bo, String bankPayType);

    void dealRepaymentSucess(String tradeNo, String outTradeNo);
    void dealRepaymentSucess(String tradeNo, String outTradeNo, final DsedLoanRepaymentDo repaymentDo, String operator, Long collectionRepaymentId, List<HashMap> periodsList);

    void dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String errorMsg);

    /**
     * 计算提前还款应还的金额
     * @return
     */
    BigDecimal calculateAllRestAmount(Long rid);

    /**
     * 计算提前还款可以减免的金额
     * @return
     */
    BigDecimal getDecreasedAmount(String loanNo,Long userId);

    DsedLoanRepaymentDo getById(Long id);

    public HashMap<String,String> buildData(DsedLoanRepaymentDo repaymentDo);

    String getCurrentLastRepayNo(String orderNoPre);

    /**
     * 都市e贷 线下还款
     * @param totalAmount
     * @param repaymentNo
     * @param userId
     * @param type
     * @param repayTime
     * @param orderNo
     * @param list
     */
    void offlineRepay(String loanNo,Long loanId,String totalAmount,String repaymentNo,Long userId,String type,String repayTime,String orderNo,List<DsedLoanPeriodsDo> list);


}
