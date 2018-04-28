package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfRecycleGoodsDao;
import com.ald.fanbei.api.dal.domain.AfRecycleGoodsDo;
import com.ald.fanbei.api.biz.service.AfRecycleGoodsService;



/**
 * 回收商品表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-28 14:00:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afRecycleGoodsService")
public class AfRecycleGoodsServiceImpl extends ParentServiceImpl<AfRecycleGoodsDo, Long> implements AfRecycleGoodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfRecycleGoodsServiceImpl.class);
   
    @Resource
    private AfRecycleGoodsDao afRecycleGoodsDao;

		@Override
	public BaseDao<AfRecycleGoodsDo, Long> getDao() {
		return afRecycleGoodsDao;
	}
}