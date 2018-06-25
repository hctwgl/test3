package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.dsed.DsedApplyLoanBo;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;

import java.util.Date;
import java.util.List;

import java.math.BigDecimal;

/**
 * 借款Service
 *
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:48:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanService extends ParentService<DsedLoanDo, Long> {

    int updateByLoanId(DsedLoanDo loanDo);

    /**
     * 执行借贷
     *
     * @param bo
     */
    void doLoan(DsedApplyLoanBo bo);

    DsedLoanDo resolveLoan(BigDecimal amount, Long userId, int periods, String loanNo, String prdType);

    /**
     * 借贷成功回调
     *
     * @param loanId
     * @param loanNo
     * @param tradeNoOut
     */
    void dealLoanSucc(Long loanId, String tradeNoOut);

    /**
     * 借贷失败回调
     *
     * @param loanId
     * @param loanNo
     * @param tradeNoOut
     */
    void dealLoanFail(Long loanId, String tradeNoOut, String msgOut);

    /**
     * 获取用户白领贷分层日利率,缓存1小时
     *
     * @param userId
     * @param prdType 对应product表中的prdType字段值
     * @return
     * @deprecated 暂时写死：年化利率 0.36
     */
    BigDecimal getUserLayDailyRate(Long userId, String prdType);

    DsedLoanDo selectById(Long loanId);

    DsedLoanDo getByLoanNo(String loanNo);

    DsedLoanDo getByUserId(Long userId);

    /**
     * 根据用户id和产品类型获取最新一条
     *
     * @author wangli
     * @date 2018/4/14 12:13
     */
    DsedLoanDo getLastByUserIdAndPrdType(Long userId, String prdType);

}
