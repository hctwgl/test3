package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfInterestReduceGoodsDao;
import com.ald.fanbei.api.dal.domain.AfInterestReduceGoodsDo;
import com.ald.fanbei.api.biz.service.AfInterestReduceGoodsService;



/**
 * 降息ServiceImpl
 * 
 * @author qiao
 * @version 1.0.0 初始化
 * @date 2018-03-29 13:41:22
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afInterestReduceGoodsService")
public class AfInterestReduceGoodsServiceImpl extends ParentServiceImpl<AfInterestReduceGoodsDo, Long> implements AfInterestReduceGoodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfInterestReduceGoodsServiceImpl.class);
   
    @Resource
    private AfInterestReduceGoodsDao afInterestReduceGoodsDao;

		@Override
	public BaseDao<AfInterestReduceGoodsDo, Long> getDao() {
		return afInterestReduceGoodsDao;
	}
}