package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.service.impl.ParentServiceImpl;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowLegalOrderRepaymentService;
import com.ald.jsd.mgr.dal.dao.MgrBorrowLegalOrderRepaymentDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 极速贷ServiceImpl
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("mgrBorrowLegalOrderRepaymentService")
public class MgrBorrowLegalOrderRepaymentServiceImpl extends ParentServiceImpl<JsdBorrowLegalOrderRepaymentDo, Long> implements MgrBorrowLegalOrderRepaymentService {
    @Resource
    private MgrBorrowLegalOrderRepaymentDao mgrBorrowLegalOrderRepaymentDao;

    @Override
    public BaseDao<JsdBorrowLegalOrderRepaymentDo, Long> getDao() {
        return mgrBorrowLegalOrderRepaymentDao;
    }

    @Override
    public List<JsdBorrowLegalOrderRepaymentDo> getBorrowCashByDays(Integer days) {
        return mgrBorrowLegalOrderRepaymentDao.getBorrowCashByDays(days);
    }

}