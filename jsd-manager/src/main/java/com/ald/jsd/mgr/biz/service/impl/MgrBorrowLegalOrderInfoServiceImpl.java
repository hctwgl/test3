package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.service.impl.ParentServiceImpl;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderInfoDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowLegalOrderInfoService;
import com.ald.jsd.mgr.dal.dao.MgrBorrowLegalOrderInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * ServiceImpl
 *
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-19 14:09:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("mgrBorrowLegalOrderInfoService")
public class MgrBorrowLegalOrderInfoServiceImpl extends ParentServiceImpl<JsdBorrowLegalOrderInfoDo, Long> implements MgrBorrowLegalOrderInfoService {

    private static final Logger logger = LoggerFactory.getLogger(MgrBorrowLegalOrderInfoServiceImpl.class);

    @Resource
    private MgrBorrowLegalOrderInfoDao mgrBorrowLegalOrderInfoDao;

    @Override
    public BaseDao<JsdBorrowLegalOrderInfoDo, Long> getDao() {
        return mgrBorrowLegalOrderInfoDao;
    }

    @Override
    public JsdBorrowLegalOrderInfoDo getByBorrowId(Long borrowId) {
        return mgrBorrowLegalOrderInfoDao.getByBorrowId(borrowId);
    }

    @Override
    public List<JsdBorrowLegalOrderInfoDo> getInfoByDays(Integer days) {
        return mgrBorrowLegalOrderInfoDao.getInfoByDays(days);
    }
}