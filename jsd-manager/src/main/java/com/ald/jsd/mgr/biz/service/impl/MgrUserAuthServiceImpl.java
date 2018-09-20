package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.service.impl.ParentServiceImpl;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.UserAuthDto;
import com.ald.fanbei.api.dal.query.UserAuthQuery;
import com.ald.jsd.mgr.dal.dao.MgrUserAuthDao;
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
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("mgrUserAuthService")
public class MgrUserAuthServiceImpl extends ParentServiceImpl<JsdUserAuthDo, Long> implements MgrUserAuthService {

    private static final Logger logger = LoggerFactory.getLogger(MgrUserAuthServiceImpl.class);

    @Resource
    private MgrUserAuthDao mgrUserAuthDao;

    @Override
    public BaseDao<JsdUserAuthDo, Long> getDao() {
        return mgrUserAuthDao;
    }

    @Override
    public List<UserAuthDto> getListUserAuth(UserAuthQuery query) {
        return mgrUserAuthDao.getListUserAuth(query);
    }

    @Override
    public int getPassPersonNumByStatusAndDays(String status, Integer days) {
        return mgrUserAuthDao.getPassPersonNumByStatusAndDays(status, days);
    }

    @Override
    public JsdUserAuthDo getByUserId(Long userId) {
        return mgrUserAuthDao.getByUserId(userId);
    }
}