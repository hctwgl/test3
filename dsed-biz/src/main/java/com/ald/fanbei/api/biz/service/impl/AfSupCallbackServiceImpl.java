package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfSupCallbackDao;
import com.ald.fanbei.api.dal.domain.AfSupCallbackDo;
import com.ald.fanbei.api.biz.service.AfSupCallbackService;

/**
 * 游戏充值ServiceImpl
 * 
 * @author 高继斌_temple
 * @version 1.0.0 初始化
 * @date 2017-11-24 16:09:31 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afSupCallbackService")
public class AfSupCallbackServiceImpl extends ParentServiceImpl<AfSupCallbackDo, Long> implements AfSupCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(AfSupCallbackServiceImpl.class);

    @Resource
    private AfSupCallbackDao afSupCallbackDao;

    @Override
    public BaseDao<AfSupCallbackDo, Long> getDao() {
	return afSupCallbackDao;
    }

    @Override
    public AfSupCallbackDo getCompleteByOrderNo(String orderNo) {

	return afSupCallbackDao.getCompleteByOrderNo(orderNo);
    }
}