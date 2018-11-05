package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ald.fanbei.api.biz.bo.JsdProctocolBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.dto.JsdCashDto;
import com.ald.fanbei.api.dal.domain.dto.LoanDto;
import com.ald.fanbei.api.dal.query.LoanQuery;
import com.alibaba.fastjson.JSONArray;

/**
 * 极速贷Service
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashService extends ParentService<JsdBorrowCashDo, Long> {

    JsdBorrowCashDo getByRenewalNo(String renewalNo);

    JsdBorrowCashDo getByBorrowNo(String borrowNo);

    JsdBorrowCashDo getByTradeNoXgxy(String tradeNoXgxy);

    void checkCanBorrow(Long userId, BigDecimal amount);

    String getCurrentLastBorrowNo(String orderNoPre);

    void transUpdate(final JsdBorrowCashDo cashDo, final JsdBorrowLegalOrderDo orderDo, final JsdBorrowLegalOrderCashDo orderCashDo);

    BigDecimal getRiskDailyRate(String openId);

    /**
     * 计算账单总应还额
     *
     * @param cashDo
     * @param orderCashDo
     * @return
     */
    BigDecimal calcuTotalAmount(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo);

    /**
     * 计算未还总金额
     *
     * @param cashDo
     * @param orderCashDo
     * @return
     */
    BigDecimal calcuUnrepayAmount(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo);

    void resolve(TrialBeforeBorrowBo bo);

    void dealBorrowSucc(Long cashId, String outTradeNo);

    void dealBorrowFail(Long cashId, String outTradeNo, String failMsg);

    void dealBorrowFail(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderDo orderDo, JsdBorrowLegalOrderCashDo orderCashDo, String failMsg);

    /**
     * 获取逾期数据的数量
     *
     * @return
     */
    int getBorrowCashOverdueCount();


    /**
     * 获取还款日在当前以前
     *
     * @return
     */
    int getBorrowCashByBeforeTodayCount(Date todayLast);


    /**
     * 获取当前的逾期借款
     */
    List<JsdBorrowCashDo> getBorrowCashOverdue(int nowPage, int pageSize);


    /**
     * 获取当前的逾期借款
     */
    List<JsdBorrowCashDo> getBorrowCashByBeforeToday(int nowPage, int pageSize, Date todayLast);

    /**
     * 获取当前的测试逾期借款
     */
    List<JsdBorrowCashDo> getBorrowCashOverdueByUserIds(String userIds);


    /*-------------------------------------------------------管理后台------------------------------------------------------------------------------*/

    /**
     * 借款审批列表
     *
     * @param query
     * @return
     */
    List<LoanDto> getReviewLoanList(LoanQuery query);

    /**
     * 借款审批统计
     *
     * @return
     */
    HashMap<String, BigDecimal> getReviewLoanStatistics();

    /**
     * 借款统计
     *
     * @return
     */
    HashMap<String, BigDecimal> getLoanStatistics();

    /**
     * 还款计划统计
     *
     * @return
     */
    HashMap<String, BigDecimal> getRepayStatistics();

    Boolean updateReviewStatusByXgNo(JSONArray jsonArray);

    /**
     * 借款列表
     *
     * @param query
     * @return
     */
    List<LoanDto> getLoanList(LoanQuery query);

    /**
     * 还款计划列表
     *
     * @param query
     * @return
     */
    List<LoanDto> getRepayList(LoanQuery query);

    /**
     * 获取借款相关协议(赊销)
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    List<JsdProctocolBo> getBorrowProtocols(String openId, String tradeNoXgxy, String previewJsonStr);

    /**
     * 获取搭售代买协议(赊销)
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    List<JsdProctocolBo> getAgencyProtocols(String openId, String tradeNoXgxy, String previewJsonStr);

    /**
     * 获取续期协议(赊销)
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    List<JsdProctocolBo> getRenewalProtocols(String openId, String tradeNoXgxy, String previewJsonStr);

    /**
     * 获取借款相关协议(plus)
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    List<JsdProctocolBo> getBorrowPlusProtocols(String openId, String tradeNoXgxy, String previewJsonStr);

    /**
     * 获取续期协议(plus)
     *
     * @param openId
     * @param tradeNoXgxy
     * @param previewJsonStr
     * @return
     */
    List<JsdProctocolBo> getRenewalPlusProtocols(String openId, String tradeNoXgxy, String previewJsonStr);

    /*-------------------------------------------------------第三方接口-----------------------------------------------------------------------------*/

    /**
     * 如果最近一笔借款为处理中状态，则再向回追溯一条finish的记录.否则查最新的借款为finish的一条记录
     * @param userName
     * @return
     */
    List<JsdBorrowCashDo> getBorrowCashsInfos(Long userId);

    JsdCashDto getGoodsInfoByBorrowNo(String borrowNo);
}
