package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserAuthStatusDao;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;

/**
 * 额度拆分多场景认证状体记录ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:58:48 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afUserAuthStatusService")
public class AfUserAuthStatusServiceImpl extends ParentServiceImpl<AfUserAuthStatusDo, Long> implements AfUserAuthStatusService {

    private static final Logger logger = LoggerFactory.getLogger(AfUserAuthStatusServiceImpl.class);

    @Resource
    private AfUserAuthStatusDao afUserAuthStatusDao;

    @Override
    public BaseDao<AfUserAuthStatusDo, Long> getDao() {
	return afUserAuthStatusDao;
    }

    @Override
    public void addOrUpdateAfUserAuthStatus(AfUserAuthStatusDo afUserAuthStatusDo) {
	// TODO Auto-generated method stub
        if(afUserAuthStatusDao.selectAfUserAuthStatusByUserIdAndScene(afUserAuthStatusDo.getUserId(),afUserAuthStatusDo.getScene())>0){
            afUserAuthStatusDao.updateAfUserAuthStatus(afUserAuthStatusDo);
        }else{
            afUserAuthStatusDao.addAfUserAuthStatus(afUserAuthStatusDo);
        }
    }

    @Override
    public AfUserAuthStatusDo selectAfUserAuthStatusByCondition(Long userId, String scene, String status) {
	// TODO Auto-generated method stub
	   return afUserAuthStatusDao.selectAfUserAuthStatusByCondition(userId,scene,status);
    }

    @Override
    public AfUserAuthStatusDo selectAfUserAuthStatusByUserIdAndStatus(Long userId, String status) {
        return afUserAuthStatusDao.selectAfUserAuthStatusByUserIdAndStatus(userId,status);
    }
}