package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserDrawService;
import com.ald.fanbei.api.dal.dao.AfUserDrawDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUserDrawDo;
import com.ald.fanbei.api.dal.domain.dto.UserDrawInfo;

/**
 * 年会抽奖ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2017-12-27 16:31:00 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afUserDrawService")
public class AfUserDrawServiceImpl extends ParentServiceImpl<AfUserDrawDo, Long> implements AfUserDrawService {

    private static final Logger logger = LoggerFactory.getLogger(AfUserDrawServiceImpl.class);

    @Resource
    private AfUserDrawDao afUserDrawDao;

    @Override
    public BaseDao<AfUserDrawDo, Long> getDao() {
	return afUserDrawDao;
    }

    @Override
    public AfUserDrawDo getByPhone(String phone) {

	return afUserDrawDao.getByPhone(phone);
    }

    @Override
    public List<AfUserDrawDo> getByPhoneAndStatus(String phone, Integer status) {

	return afUserDrawDao.getByPhoneAndStatus(phone, status);
    }

    @Override
    public List<UserDrawInfo> getByStatus(Integer status) {

	return afUserDrawDao.getByStatus(status);
    }

    @Override
    public int updateWinUserStatus(Integer status, List<UserDrawInfo> userList) {

	return afUserDrawDao.updateWinUserStatus(status, userList);
    }

    @Override
    public AfUserDrawDo getByOpenId(String openId) {
	return afUserDrawDao.getByOpenId(openId);
    }
}