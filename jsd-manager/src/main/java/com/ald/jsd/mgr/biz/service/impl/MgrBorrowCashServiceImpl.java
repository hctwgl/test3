package com.ald.jsd.mgr.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashService;
import com.ald.jsd.mgr.dal.dao.MgrBorrowCashDao;


/**
 * 极速贷ServiceImpl
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("mgrBorrowCashService")
public class MgrBorrowCashServiceImpl implements MgrBorrowCashService {

    @Resource
    MgrBorrowCashDao mgrBorrowCashDao;

    @Override
    public List<JsdBorrowCashDo> getBorrowCashByDays(Integer days) {
        return mgrBorrowCashDao.getBorrowCashByDays(days);
    }

    @Override
    public List<JsdBorrowCashDo> getPlanRepayBorrowCashByDays(Integer days) {
        return mgrBorrowCashDao.getBorrowCashByDays(days);
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashLessThanDays(Integer days) {
        return mgrBorrowCashDao.getBorrowCashLessThanDays(days);
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashBetweenStartAndEnd(Date startDate, Date endDate) {
        return mgrBorrowCashDao.getBorrowCashBetweenStartAndEnd(startDate,endDate);
    }

    @Override
    public List<JsdBorrowCashDo> getPlanRepaymentBorrowCashBetweenStartAndEnd(Date startDate, Date endDate) {
        return mgrBorrowCashDao.getPlanRepaymentBorrowCashBetweenStartAndEnd(startDate,endDate);
    }

    @Override
    public int getArrivalBorrowCashPerByDays(Integer days) {
        return mgrBorrowCashDao.getArrivalBorrowCashPerByDays(days);
    }

    @Override
    public int getApplyBorrowCashSuPerByDays(Integer days) {
        return mgrBorrowCashDao.getApplyBorrowCashSuPerByDays(days);
    }

    @Override
    public int getApplyBorrowCashPerByDays(Integer days) {
        return mgrBorrowCashDao.getApplyBorrowCashPerByDays(days);
    }

    @Override
    public BigDecimal getPlanRepaymentCashAmountByDays(Integer days) {
        return mgrBorrowCashDao.getPlanRepaymentCashAmountByDays(days);
    }

    @Override
    public int getArrivalBorrowCashBetweenStartAndEnd(Date startDate, Date endDate) {
        return mgrBorrowCashDao.getArrivalBorrowCashBetweenStartAndEnd(startDate,endDate);
    }

    @Override
    public BigDecimal getPlanRepaymentCashAmountBetweenStartAndEnd(Date startDate, Date endDate) {
        return mgrBorrowCashDao.getPlanRepaymentCashAmountBetweenStartAndEnd(startDate,endDate);
    }

    @Override
    public int getApplyBorrowCashSuPerBetweenStartAndEnd(Date startDate, Date endDate) {
        return mgrBorrowCashDao.getApplyBorrowCashSuPerBetweenStartAndEnd(startDate,endDate);
    }

    @Override
    public int getApplyBorrowCashPerBetweenStartAndEnd(Date startDate, Date endDate) {
        return mgrBorrowCashDao.getApplyBorrowCashPerBetweenStartAndEnd(startDate,endDate);
    }

    @Override
    public int getUserNumByBorrowDays(Integer days) {
        return mgrBorrowCashDao.getUserNumByBorrowDays(days);
    }

    @Override
    public int getUserNumBetweenStartAndEnd(Date startDate, Date endDate) {
        return mgrBorrowCashDao.getUserNumBetweenStartAndEnd(startDate,endDate);
    }

    @Override
    public int getPlanRepaymentCashAmountByDays(Date startDate, Date endDate) {
        return 0;
    }

    @Override
    public BigDecimal getAmountByDays(Integer days) {
        return mgrBorrowCashDao.getAmountByDays(days);
    }

}
