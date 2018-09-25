package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdCollectionBorrowService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdCollectionBorrowDao;
import com.ald.fanbei.api.dal.domain.JsdCollectionBorrowDo;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdCollectionBorrowService")
public class JsdCollectionBorrowServiceImpl extends ParentServiceImpl<JsdCollectionBorrowDo, Long> implements JsdCollectionBorrowService {
	
    @Resource
    private JsdCollectionBorrowDao jsdCollectionBorrowDao;

		@Override
	public BaseDao<JsdCollectionBorrowDo, Long> getDao() {
		return jsdCollectionBorrowDao;
	}

	@Override
	public JsdCollectionBorrowDo selectByBorrowId(Long borrowId){
		return jsdCollectionBorrowDao.selectByBorrowId(borrowId);
	}
}