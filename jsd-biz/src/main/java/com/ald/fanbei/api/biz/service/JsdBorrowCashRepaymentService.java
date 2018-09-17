package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;

import java.util.List;
import java.util.Map;
/**
 * 极速贷Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashRepaymentService{

    /**
     * 获取最近还款编号
     * @param orderNoPre
     * @return
     */
    String getCurrentLastRepayNo(String orderNoPre);

	JsdBorrowCashRepaymentDo getLastByBorrowId(Long borrowId);

    Map<String, Object> repay(JsdBorrowCashRepaymentServiceImpl.BorrowCashRepayBo bo, String bankPayType);

    JsdBorrowCashRepaymentDo getLastRepaymentBorrowCashByBorrowId(Long borrowId);

    void dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String code,String errorMsg);

    void dealRepaymentSucess(String tradeNo, String outTradeNo);

    JsdBorrowCashRepaymentDo  getByTradeNoXgxy(String tradeNoXgxy);

    /**
     * jsd 线下还款
     * @param totalAmount
     * @param repaymentNo
     * @param userId
     * @param type
     * @param repayTime
     * @param orderNo
     */
    void offlineRepay(JsdBorrowCashDo jsdBorrowCashDo, JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo, String totalAmount, String repaymentNo, Long userId, String type, String repayTime, String orderNo,String dataId);


}
