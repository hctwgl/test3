package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.query.JsdViewAssetQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdViewAssetDao;
import com.ald.fanbei.api.dal.domain.JsdViewAssetDo;
import com.ald.fanbei.api.biz.service.JsdViewAssetService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 14:22:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdViewAssetService")
public class JsdViewAssetServiceImpl extends ParentServiceImpl<JsdViewAssetDo, Long> implements JsdViewAssetService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdViewAssetServiceImpl.class);
   
    @Resource
    private JsdViewAssetDao jsdViewAssetDao;

		@Override
	public BaseDao<JsdViewAssetDo, Long> getDao() {
		return jsdViewAssetDao;
	}

	@Override
	public List<JsdViewAssetDo> getListByQueryCondition(JsdViewAssetQuery query) {
		return jsdViewAssetDao.getListByQueryCondition(query) ;
	}

	@Override
	public BigDecimal getSumMinAmount(Date gmtCreateStart, Date gmtCreateEnd, String minBorrowTime) {
		return jsdViewAssetDao.getSumMinAmount(gmtCreateStart, gmtCreateEnd, minBorrowTime) ;
	}

	@Override
	public BigDecimal getSumMaxAmount(Date gmtCreateStart, Date gmtCreateEnd, String maxBorrowTime) {
		return jsdViewAssetDao. getSumMaxAmount(gmtCreateStart,gmtCreateEnd, maxBorrowTime) ;
	}

	@Override
	public JsdViewAssetDo getByQueryCondition(JsdViewAssetQuery query) {
		return jsdViewAssetDao.getByQueryCondition(query);
	}

	@Override
	public BigDecimal checkAmount(JsdViewAssetQuery query) {
		return jsdViewAssetDao.checkAmount(query);
	}
}