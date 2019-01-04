package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl.RepayRequestBo;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;

/**
 * 极速贷Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashRepaymentService{
	JsdBorrowCashRepaymentDo getLastByBorrowId(Long borrowId);

    Map<String, Object> repay(RepayRequestBo bo, String bankPayType);

    JsdBorrowCashRepaymentDo getLastRepaymentBorrowCashByBorrowId(Long borrowId);

    void dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String code,String errorMsg);

    void dealRepaymentSucess(String tradeNo, String outTradeNo,String tradeDate);

    JsdBorrowCashRepaymentDo  getByTradeNoXgxy(String tradeNoXgxy);

    List<JsdBorrowCashRepaymentDo>  getByBorrowTradeNoXgxy(String tradeNoXgxy);

    /**
     * 系统内线下还款统一调用接口
     *
     * @param jsdBorrowCashDo
     * @param jsdBorrowLegalOrderCashDo
     * @param totalAmount 还款总额
     * @param repaymentNo 还款流水号
     * @param userId 用户uid
     * @param type 还款类型，对应JsdRepayType枚举
     * @param channel 还款渠道，支付宝，微信，银行卡等
     * @param repayTime 实际发生还款的时间
     * @param orderNo 借款订单的流水号
     * @param dataId 此次线下还款处理的数据唯一标识
     * @param remark 线下还款备注
     */
    void offlineRepay(JsdBorrowCashDo jsdBorrowCashDo, JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo, String totalAmount, String repaymentNo, Long userId, JsdRepayType type, String channel, Date repayTime, String orderNo, String dataId, String remark);

    List<JsdBorrowCashRepaymentDo> getRepayByBorrowId(Long borrowId);

    /**
     * 获取结算系统实收数据
     * @Param list {@link FinaneceDataDo} 对象
     *@return  <code>List<code/>
     *
     * **/
    List<FinaneceDataDo> getRepayData();


    List<JsdBorrowCashRepaymentDo> getWithholdFailRepaymentCashByBorrowIdAndCardNumber(Long borrowId,String cardNumber);

    void  dealWithhold(List<JsdBorrowCashDo> borrowCashDos,String cardType);

    void checkBorrowIsLock(Long userId);

    void lockBorrowList(List<JsdBorrowCashDo> borrowCashDos);

    void lockBorrow(Long userId);

    void unLockBorrow(Long userId);

    BigDecimal getSumRepaymentAmount(String nper);
}
