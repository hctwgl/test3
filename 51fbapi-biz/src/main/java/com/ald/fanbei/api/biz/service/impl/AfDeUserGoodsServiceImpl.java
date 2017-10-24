package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfDeUserGoodsDao;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;

/**
 * 双十一砍价ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afDeUserGoodsService")
public class AfDeUserGoodsServiceImpl extends ParentServiceImpl<AfDeUserGoodsDo, Long> implements AfDeUserGoodsService {

    private static final Logger logger = LoggerFactory.getLogger(AfDeUserGoodsServiceImpl.class);

    @Resource
    private AfDeUserGoodsDao afDeUserGoodsDao;

    @Override
    public BaseDao<AfDeUserGoodsDo, Long> getDao() {
	return afDeUserGoodsDao;
    }

    @Override
    public AfDeUserGoodsDo getUserGoodsPrice(Long userId, Long goodsPriceId) {

	return afDeUserGoodsDao.getUserGoodsPrice(userId, goodsPriceId);
    }

    @Override
    public int updateIsBuyById(Long id, Integer isBuy) {

	return afDeUserGoodsDao.updateIsBuyById(id, isBuy);
    }
}