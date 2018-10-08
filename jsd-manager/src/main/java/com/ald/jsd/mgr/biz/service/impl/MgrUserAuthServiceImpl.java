package com.ald.jsd.mgr.biz.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.UserAuthDto;
import com.ald.fanbei.api.dal.query.UserAuthQuery;
import com.ald.jsd.mgr.dal.dao.MgrUserAuthDao;


/**
 * ServiceImpl
 *
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("mgrUserAuthService")
public class MgrUserAuthServiceImpl implements MgrUserAuthService {

    @Resource
    private MgrUserAuthDao mgrUserAuthDao;

    @Override
    public List<UserAuthDto> getListUserAuth(UserAuthQuery query) {
        return mgrUserAuthDao.getListUserAuth(query);
    }

    @Override
    public int getPassPersonNumByStatusAndDays(String status, Integer days) {
        return mgrUserAuthDao.getPassPersonNumByStatusAndDays(status, days);
    }

    @Override
    public int getPassPersonNumByStatusBetweenStartAndEnd(String status, Date startDate, Date endDate) {
        return mgrUserAuthDao.getPassPersonNumByStatusBetweenStartAndEnd(status,startDate,endDate);
    }

    @Override
    public int getPassPersonNumByStatusEqualDays(String status, Integer days) {
        return mgrUserAuthDao.getPassPersonNumByStatusEqualDays(status, days);
    }

    @Override
    public JsdUserAuthDo getByUserId(Long userId) {
        return mgrUserAuthDao.getByUserId(userId);
    }
}