package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.dal.dao.AfDeUserGoodsDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfDeUserGoodsQuery;

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
	public List<AfDeUserGoodsDto> getAfDeUserGoogsList(AfDeUserGoodsQuery queryGoods) {
		    // TODO Auto-generated method stub
		return afDeUserGoodsDao.getAfDeUserGoogsList(queryGoods);
	}

	@Override
	public AfDeUserGoodsInfoDto getUserGoodsInfo(AfDeUserGoodsDo afDeUserGoodsDo) {
		    // TODO Auto-generated method stub
	    	return afDeUserGoodsDao.getUserGoodsInfo(afDeUserGoodsDo);
	}

    @Override
    public AfDeUserGoodsDo getUserGoodsPrice(Long userId, Long goodsPriceId) {

	return afDeUserGoodsDao.getUserGoodsPrice(userId, goodsPriceId);
    }

    @Override
    public int updateIsBuyById(Long id, Integer isBuy) {

	return afDeUserGoodsDao.updateIsBuyById(id, isBuy);
    }

    @Override
    public AfDeUserGoodsInfoDto getUserCutPrice(AfDeUserGoodsDo afDeUserGoodsDo) {
	// TODO Auto-generated method stub
	return afDeUserGoodsDao.getUserCutPrice(afDeUserGoodsDo);
    }
}