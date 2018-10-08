package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderInfoService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderInfoDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderInfoDo;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-19 14:09:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowLegalOrderInfoService")
public class JsdBorrowLegalOrderInfoServiceImpl extends ParentServiceImpl<JsdBorrowLegalOrderInfoDo, Long> implements JsdBorrowLegalOrderInfoService {
   
    @Resource
    private JsdBorrowLegalOrderInfoDao jsdBorrowLegalOrderInfoDao;

		@Override
	public BaseDao<JsdBorrowLegalOrderInfoDo, Long> getDao() {
		return jsdBorrowLegalOrderInfoDao;
	}

	@Override
	public JsdBorrowLegalOrderInfoDo getByBorrowId(Long borrowId) {
		return jsdBorrowLegalOrderInfoDao.getByBorrowId(borrowId);
	}
}