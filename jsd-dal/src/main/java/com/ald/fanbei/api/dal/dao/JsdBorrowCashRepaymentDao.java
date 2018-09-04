package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import org.apache.ibatis.annotations.Param;

/**
 * 极速贷Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashRepaymentDao extends BaseDao<JsdBorrowCashRepaymentDo, Long> {

	JsdBorrowCashRepaymentDo getLastByBorrowId(Long borrowId);


    /**
     * 获取最近还款编号
     * @param orderNoPre
     * @return
     */
     String getCurrentLastRepayNo(String orderNoPre);

    JsdBorrowCashRepaymentDo getLastRepaymentBorrowCashByBorrowId(@Param("borrowId") Long borrowId);


    JsdBorrowCashRepaymentDo getRepaymentByPayTradeNo(@Param("repayNo")String repayNo);


    /**
     * 更新记录
     *
     * @param jsdBorrowCashRepaymentDo
     * @return
     */
    int updateRepaymentBorrowCash(JsdBorrowCashRepaymentDo jsdBorrowCashRepaymentDo);


    /**
     * @param tradeNo 可选参数
     * @param repaymentId
     * @return
     */
    int status2Process(@Param("tradeNo") String tradeNo, @Param("repaymentId") Long repaymentId);


    JsdBorrowCashRepaymentDo getRepayByTradeNo(@Param("tradeNo") String tradeNo);

    JsdBorrowCashRepaymentDo getByRepayNo(@Param("repayNo")String repayNo);



}
