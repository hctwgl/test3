package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowRecycleGoodsDao;
import com.ald.fanbei.api.dal.domain.AfBorrowRecycleGoodsDo;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleGoodsService;



/**
 * 回收商品表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-28 14:08:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowRecycleGoodsService")
public class AfBorrowRecycleGoodsServiceImpl extends ParentServiceImpl<AfBorrowRecycleGoodsDo, Long> implements AfBorrowRecycleGoodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowRecycleGoodsServiceImpl.class);
   
    @Resource
    private AfBorrowRecycleGoodsDao afBorrowRecycleGoodsDao;

		@Override
	public BaseDao<AfBorrowRecycleGoodsDo, Long> getDao() {
		return afBorrowRecycleGoodsDao;
	}
}