package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfGoodsDoubleEggsDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsDo;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsService;



/**
 * 双蛋活动ServiceImpl
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-12-07 13:40:05
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afGoodsDoubleEggsService")
public class AfGoodsDoubleEggsServiceImpl extends ParentServiceImpl<AfGoodsDoubleEggsDo, Long> implements AfGoodsDoubleEggsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfGoodsDoubleEggsServiceImpl.class);
   
    @Resource
    private AfGoodsDoubleEggsDao afGoodsDoubleEggsDao;

		@Override
	public BaseDao<AfGoodsDoubleEggsDo, Long> getDao() {
		return afGoodsDoubleEggsDao;
	}

		@Override
		public AfGoodsDoubleEggsDo getByGoodsId(Long goodsId) {
			
			return afGoodsDoubleEggsDao.getByGoodsId(goodsId);
		}

		@Override
		public List<AfGoodsDoubleEggsDo> getAvalibleGoodsList() {
			return afGoodsDoubleEggsDao.getAvalibleGoodsList();
		}
}