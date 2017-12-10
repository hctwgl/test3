package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;



/**
 * ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowLegalOrderCashService")
public class AfBorrowLegalOrderCashServiceImpl extends ParentServiceImpl<AfBorrowLegalOrderCashDo, Long> implements AfBorrowLegalOrderCashService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowLegalOrderCashServiceImpl.class);
   
    @Resource
    private AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;

		@Override
	public BaseDao<AfBorrowLegalOrderCashDo, Long> getDao() {
		return afBorrowLegalOrderCashDao;
	}
}