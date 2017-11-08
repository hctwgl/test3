package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfShareGoodsDao;
import com.ald.fanbei.api.dal.domain.AfShareGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfShareGoodsDto;
import com.ald.fanbei.api.biz.service.AfShareGoodsService;



/**
 * 新人专享ServiceImpl
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-11-02 11:16:29
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afShareGoodsService")
public class AfShareGoodsServiceImpl extends ParentServiceImpl<AfShareGoodsDo, Long> implements AfShareGoodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfShareGoodsServiceImpl.class);
   
    @Resource
    private AfShareGoodsDao afShareGoodsDao;

		@Override
	public BaseDao<AfShareGoodsDo, Long> getDao() {
		return afShareGoodsDao;
	}


	@Override
	public List<AfShareGoodsDo> getAllGoodsList() {
		return afShareGoodsDao.getAllGoodsList();
	}


		@Override
		public List<AfShareGoodsDto> getShareGoods() {
			// TODO Auto-generated method stub
			return afShareGoodsDao.getShareGoods();
		}


		@Override
		public Integer getCountByGoodsId(Long goodsId) {
			// TODO Auto-generated method stub
			return afShareGoodsDao.getCountByGoodsId(goodsId);
		}

}