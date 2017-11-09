package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfShareUserGoodsDao;
import com.ald.fanbei.api.dal.domain.AfShareUserGoodsDo;
import com.ald.fanbei.api.biz.service.AfShareUserGoodsService;



/**
 * 新人专享ServiceImpl
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-11-02 11:16:29
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afShareUserGoodsService")
public class AfShareUserGoodsServiceImpl extends ParentServiceImpl<AfShareUserGoodsDo, Long> implements AfShareUserGoodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfShareUserGoodsServiceImpl.class);
   
    @Resource
    private AfShareUserGoodsDao afShareUserGoodsDao;

		@Override
	public BaseDao<AfShareUserGoodsDo, Long> getDao() {
		return afShareUserGoodsDao;
	}

		@Override
		public void updateIsBuyById(long parseLong, int i) {
			afShareUserGoodsDao.updateIsBuyById(parseLong,i);
			
		}

		@Override
		public AfShareUserGoodsDo getByUserId(Long userId) {
			// TODO Auto-generated method stub
			return afShareUserGoodsDao.getByUserId(userId);
		}
}