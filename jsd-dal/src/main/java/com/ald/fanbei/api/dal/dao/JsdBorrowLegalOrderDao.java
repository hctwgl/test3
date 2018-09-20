package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 极速贷Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowLegalOrderDao extends BaseDao<JsdBorrowLegalOrderDo, Long> {

    String getCurrentLastOrderNo(Date currentDate);

    JsdBorrowLegalOrderDo getLastOrderByBorrowId(@Param("borrowId") Long borrowId);

    List<JsdBorrowLegalOrderDo> getBorrowOrdersByBorrowId(@Param("borrowId")Long borrowId);

    JsdBorrowLegalOrderDo getLastOrderByBorrowIdAndStatus(@Param("borrowId") Long borrowId);
}
