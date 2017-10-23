package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.dal.dao.AfDeGoodsDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.UserDeGoods;

/**
 * 双十一砍价ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:19 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afDeGoodsService")
public class AfDeGoodsServiceImpl extends ParentServiceImpl<AfDeGoodsDo, Long> implements AfDeGoodsService {

    private static final Logger logger = LoggerFactory.getLogger(AfDeGoodsServiceImpl.class);

    @Resource
    private AfDeGoodsDao afDeGoodsDao;

    @Override
    public BaseDao<AfDeGoodsDo, Long> getDao() {
	return afDeGoodsDao;
    }

    @Override
    public List<UserDeGoods> getUserDeGoodsList(Long userId) {

	return afDeGoodsDao.getUserDeGoodsList(userId);
    }
}