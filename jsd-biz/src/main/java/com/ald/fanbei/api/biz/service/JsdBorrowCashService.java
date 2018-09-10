package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;

/**
 * 极速贷Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashService extends ParentService<JsdBorrowCashDo, Long>{

    JsdBorrowCashDo getByBorrowNo(String borrowNo);
    
    JsdBorrowCashDo getByTradeNoXgxy(String tradeNoXgxy);

    void checkCanBorrow(Long userId);

    String getCurrentLastBorrowNo(String orderNoPre);
    
    void transUpdate(final JsdBorrowCashDo cashDo, final JsdBorrowLegalOrderDo orderDo, final JsdBorrowLegalOrderCashDo orderCashDo);
    
    BigDecimal getRiskDailyRate(String openId);
    
    /**
     * 计算账单总应还额
     * @param cashDo
     * @param orderCashDo
     * @return
     */
    BigDecimal calcuTotalAmount(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo);
    
    /**
     * 计算未还总金额
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
     * 获取当前的逾期借款
     *
     */
    List<JsdBorrowCashDo> getBorrowCashOverdue(int nowPage, int pageSize);


    /**
     * 获取当前的测试逾期借款
     *
     */
    List<JsdBorrowCashDo> getBorrowCashOverdueByUserIds(String userIds);
}
