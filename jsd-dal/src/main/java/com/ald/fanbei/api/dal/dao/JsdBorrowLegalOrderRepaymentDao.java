package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;

import org.apache.ibatis.annotations.Param;

/**
 * 极速贷Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowLegalOrderRepaymentDao extends BaseDao<JsdBorrowLegalOrderRepaymentDo, Long> {


    JsdBorrowLegalOrderRepaymentDo getLastByBorrowId(@Param("borrowId") Long borrowId);


    JsdBorrowLegalOrderRepaymentDo  getBorrowLegalOrderRepaymentByTradeNo(@Param("tradeNo")String tradeNo);

    /**
     * 更新记录
     * @param jsdBorrowLegalOrderRepaymentDo
     * @return
     */
    int updateBorrowLegalOrderRepayment(JsdBorrowLegalOrderRepaymentDo jsdBorrowLegalOrderRepaymentDo);


	JsdBorrowLegalOrderRepaymentDo getNewOrderRepaymentByBorrowId(Long borrowId);

	int updateStatus(@Param("tradeNo")String tradeNo);

    JsdBorrowLegalOrderRepaymentDo getByRepayNo(@Param("repayNo") String VARCHAR);

}
