package com.ald.jsd.mgr.biz.service;



import java.util.Date;

public interface MgrBorrowCashRenewalService {

    Integer getRenewalAmountByDays( Date startDate,Date endDate);

    int getRenewalPersonsByDays(Integer days);

}