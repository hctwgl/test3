package com.ald.jsd.mgr.biz.service;


import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

public interface MgrBorrowCashRenewalService {

    BigDecimal getRenewalAmountByDays( Date startDate,Date endDate);

    int getRenewalPersonsByDays(Integer days);

}