package com.ald.jsd.mgr.dal.dao;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 极速贷Dao
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06jsdBorrowCashDao
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrBorrowCashDao extends BaseDao<JsdBorrowCashDo, Long> {

    List<JsdBorrowCashDo> getBorrowCashByDays(Integer days);

    List<JsdBorrowCashDo> getBorrowCashLessThanDays(Integer days);

    List<JsdBorrowCashDo> getBorrowCashBetweenStartAndEnd(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    /**
     * 获取当前申请借款人数
     *
     * @param days
     * @return
     */
    int getApplyBorrowCashByDays(Integer days);

    int getApplyBorrowCashBetweenStartAndEnd(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    int getUserNumByBorrowDays(Integer days);

    int getUserNumBetweenStartAndEnd(@Param("startDate") Date startDate,@Param("endDate") Date endDate);

    BigDecimal getAmountByDays(Integer days);

}
