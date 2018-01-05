package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.dal.dao.AfUserAccountSenceDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;

/**
 * 额度拆分多场景分期额度记录ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:57:51 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afUserAccountSenceService")
public class AfUserAccountSenceServiceImpl extends ParentServiceImpl<AfUserAccountSenceDo, Long> implements AfUserAccountSenceService {

    private static final Logger logger = LoggerFactory.getLogger(AfUserAccountSenceServiceImpl.class);

    @Resource
    private AfUserAccountSenceDao afUserAccountSenceDao;

    @Override
    public BaseDao<AfUserAccountSenceDo, Long> getDao() {
	return afUserAccountSenceDao;
    }

    @Override
    public int updateUserSceneAuAmount(String scene, Long userId, BigDecimal auAmount) {

	return afUserAccountSenceDao.updateUserSceneAuAmount(scene, userId, auAmount);
    }

    @Override
    public AfUserAccountSenceDo getByUserIdAndScene(String scene, Long userId) {

	return afUserAccountSenceDao.getByUserIdAndScene(scene, userId);
    }
}