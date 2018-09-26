package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashRepaymentService;
import com.ald.jsd.mgr.dal.dao.MgrBorrowCashRepaymentDao;
import com.ald.jsd.mgr.dal.dao.MgrBorrowLegalOrderRepaymentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 极速贷ServiceImpl
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("mgrBorrowCashRepaymentService")
public class MgrBorrowCashRepaymentServiceImpl implements MgrBorrowCashRepaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MgrBorrowCashRepaymentServiceImpl.class);

    @Resource
    private MgrBorrowCashRepaymentDao mgrBorrowCashRepaymentDao;
    @Resource
    private MgrBorrowLegalOrderRepaymentDao mgrBorrowLegalOrderRepaymentDao;

    @Override
    public List<JsdBorrowCashRepaymentDo> getByBorrowTradeNoXgxy(String tradeNoXgxy) {
        return null;
    }

    @Override
    public List<JsdBorrowCashRepaymentDo> getBorrowCashRepayByDays(Integer days) {
        return mgrBorrowCashRepaymentDao.getBorrowCashRepayByDays(days);
    }

    @Override
    public List<JsdBorrowCashRepaymentDo> getBorrowCashRepayBetweenStartAndEnd(Date startDate, Date endDate) {
        return mgrBorrowCashRepaymentDao.getBorrowCashRepayBetweenStartAndEnd(startDate,endDate);
    }
}
