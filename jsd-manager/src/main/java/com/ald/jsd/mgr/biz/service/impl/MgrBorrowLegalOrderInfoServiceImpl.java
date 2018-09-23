package com.ald.jsd.mgr.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderInfoDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowLegalOrderInfoService;
import com.ald.jsd.mgr.dal.dao.MgrBorrowLegalOrderInfoDao;


/**
 * ServiceImpl
 *
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-19 14:09:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("mgrBorrowLegalOrderInfoService")
public class MgrBorrowLegalOrderInfoServiceImpl implements MgrBorrowLegalOrderInfoService {

    @Resource
    private MgrBorrowLegalOrderInfoDao mgrBorrowLegalOrderInfoDao;

    @Override
    public JsdBorrowLegalOrderInfoDo getByBorrowId(Long borrowId) {
        return mgrBorrowLegalOrderInfoDao.getByBorrowId(borrowId);
    }

    @Override
    public List<JsdBorrowLegalOrderInfoDo> getInfoByDays(Integer days) {
        return mgrBorrowLegalOrderInfoDao.getInfoByDays(days);
    }
}