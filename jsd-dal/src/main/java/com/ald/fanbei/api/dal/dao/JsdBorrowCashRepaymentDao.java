package com.ald.fanbei.api.dal.dao;

import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;
import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;

import java.util.List;

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

    /**
     * @param tradeNo 可选参数
     * @param repaymentId
     * @return
     */
    int status2Process(@Param("tradeNo") String tradeNo, @Param("repaymentId") Long repaymentId);


    JsdBorrowCashRepaymentDo getByTradeNo(@Param("tradeNo") String tradeNo);

    JsdBorrowCashRepaymentDo getByTradeNoXgxy(@Param("tradeNoXgxy")String tradeNoXgxy);

    JsdBorrowCashRepaymentDo getByTradeNoOut(@Param("tradeNoUps") String tradeNoUps);

    List<JsdBorrowCashRepaymentDo> getByBorrowTradeNoXgxy(@Param("tradeNoXgxy")String tradeNoXgxy);

    List<JsdBorrowCashRepaymentDo> getRepayByBorrowId(@Param("borrowId")Long borrowId);

    /**
     * 获取结算系统实收数据
     * @Param list {@link FinaneceDataDo} 对象
     *@return  <code>List<code/>
     *
     * **/
    List<FinaneceDataDo> getRepayData();

}
