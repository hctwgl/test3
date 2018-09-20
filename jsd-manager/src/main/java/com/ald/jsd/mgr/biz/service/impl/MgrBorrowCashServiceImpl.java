package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.service.impl.ParentServiceImpl;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashService;
import com.ald.jsd.mgr.dal.dao.MgrBorrowCashDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


/**
 * 极速贷ServiceImpl
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("mgrBorrowCashService")
public class MgrBorrowCashServiceImpl extends ParentServiceImpl<JsdBorrowCashDo, Long> implements MgrBorrowCashService {

    @Resource
    MgrBorrowCashDao mgrBorrowCashDao;

    @Override
    public BaseDao<JsdBorrowCashDo, Long> getDao() {
        return mgrBorrowCashDao;
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashByDays(Integer days) {
        return mgrBorrowCashDao.getBorrowCashByDays(days);
    }

    @Override
    public List<JsdBorrowCashDo> getBorrowCashLessThanDays(Integer days) {
        return mgrBorrowCashDao.getBorrowCashLessThanDays(days);
    }

    @Override
    public int getApplyBorrowCashByDays(Integer days) {
        return mgrBorrowCashDao.getApplyBorrowCashByDays(days);
    }

    @Override
    public BigDecimal getAmountByDays(Integer days) {
        return mgrBorrowCashDao.getAmountByDays(days);
    }

}
