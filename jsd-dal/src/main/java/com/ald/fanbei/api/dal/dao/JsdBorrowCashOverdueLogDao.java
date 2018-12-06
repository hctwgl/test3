package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashOverdueLogDo;
import com.ald.fanbei.api.dal.domain.dto.JsdBorrowCashOverdueLogDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-04 11:10:19
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashOverdueLogDao extends BaseDao<JsdBorrowCashOverdueLogDo, Long> {

    int getBorrowCashOverDueLogByNow(@Param("borrowId") String borrowId, @Param("now") Date now);


    List<JsdBorrowCashOverdueLogDto> getListCashOverdueLogByBorrowId(@Param("borrowId") Long borrowId, @Param("payTime") Date PayTime);

    List<JsdBorrowCashOverdueLogDto> getListOrderCashOverdueLogByBorrowId(@Param("borrowId") Long borrowId, @Param("payTime") Date PayTime);

    List<JsdBorrowCashOverdueLogDo> getListCashOverdueLog(@Param("payTime")Date payTime);
}
