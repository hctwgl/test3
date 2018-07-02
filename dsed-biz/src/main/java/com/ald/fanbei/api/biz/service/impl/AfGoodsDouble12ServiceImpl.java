package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfGoodsDouble12Dao;
import com.ald.fanbei.api.dal.domain.AfGoodsDouble12Do;
import com.ald.fanbei.api.biz.service.AfGoodsDouble12Service;



/**
 * 双十二ServiceImpl
 * 
 * @author yanghailong_temple
 * @version 1.0.0 初始化
 * @date 2017-11-17 11:28:44
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afGoodsDouble12Service")
public class AfGoodsDouble12ServiceImpl extends ParentServiceImpl<AfGoodsDouble12Do, Long> implements AfGoodsDouble12Service {
	
    private static final Logger logger = LoggerFactory.getLogger(AfGoodsDouble12ServiceImpl.class);
   
    @Resource
    private AfGoodsDouble12Dao afGoodsDouble12Dao;

		@Override
	public BaseDao<AfGoodsDouble12Do, Long> getDao() {
		return afGoodsDouble12Dao;
	}

		@Override
		public List<AfGoodsDouble12Do> getAfGoodsDouble12List() {
			// TODO Auto-generated method stub
			return afGoodsDouble12Dao.getAfGoodsDouble12List();
		}

		@Override
		public List<AfGoodsDouble12Do> getByGoodsId(Long goodsId) {
			// TODO Auto-generated method stub
			return afGoodsDouble12Dao.getByGoodsId(goodsId);
		}

		@Override
		public void updateCountById(Long goodsId) {
			// TODO Auto-generated method stub
			afGoodsDouble12Dao.updateCountById(goodsId);
		}
}