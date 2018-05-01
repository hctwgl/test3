package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowRecycleOrderDao;
import com.ald.fanbei.api.dal.domain.AfBorrowRecycleOrderDo;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleOrderService;



/**
 * 回收商品借款关联记录ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-05-01 11:00:24
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowRecycleOrderService")
public class AfBorrowRecycleOrderServiceImpl extends ParentServiceImpl<AfBorrowRecycleOrderDo, Long> implements AfBorrowRecycleOrderService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowRecycleOrderServiceImpl.class);
   
    @Resource
    private AfBorrowRecycleOrderDao afBorrowRecycleOrderDao;

		@Override
	public BaseDao<AfBorrowRecycleOrderDo, Long> getDao() {
		return afBorrowRecycleOrderDao;
	}
}