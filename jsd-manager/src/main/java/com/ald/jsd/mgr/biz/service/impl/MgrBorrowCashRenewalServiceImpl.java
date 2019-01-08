package com.ald.jsd.mgr.biz.service.impl;

import com.ald.jsd.mgr.biz.service.MgrBorrowCashRenewalService;
import com.ald.jsd.mgr.dal.dao.MgrBorrowCashRenewalDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service("mgrBorrowCashRenewalService")
public class MgrBorrowCashRenewalServiceImpl implements MgrBorrowCashRenewalService {

    @Resource
    private MgrBorrowCashRenewalDao mgrBorrowCashRenewalDao;


    @Override
    public BigDecimal getRenewalAmountByDays(Date startDate, Date endDate) {
        return mgrBorrowCashRenewalDao.getRenewalAmountByDays(startDate,endDate);
    }

    @Override
    public int getRenewalPersonsByDays(Integer days) {
        return mgrBorrowCashRenewalDao.getRenewalPersonsByDays(days);
    }
}