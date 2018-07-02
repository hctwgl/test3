package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfViewAssetLoanDao;
import com.ald.fanbei.api.dal.domain.AfViewAssetLoanDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetLoanQuery;
import com.ald.fanbei.api.biz.service.AfViewAssetLoanService;



/**
 * 白领带非实时推送债权视图信息ServiceImpl
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-25 13:39:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afViewAssetLoanService")
public class AfViewAssetLoanServiceImpl extends ParentServiceImpl<AfViewAssetLoanDo, Long> implements AfViewAssetLoanService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfViewAssetLoanServiceImpl.class);
   
    @Resource
    private AfViewAssetLoanDao afViewAssetLoanDao;

		@Override
	public BaseDao<AfViewAssetLoanDo, Long> getDao() {
		return afViewAssetLoanDao;
	}

	@Override
	public BigDecimal getSumAmount(Date gmtCreateStart, Date gmtCreateEnd) {
		return afViewAssetLoanDao.getSumAmount(gmtCreateStart,gmtCreateEnd) ;
	}

	@Override
	public List<AfViewAssetLoanDo> getListByQueryCondition(AfViewAssetLoanQuery query) {
		return afViewAssetLoanDao.getListByQueryCondition(query);
	}

	@Override
	public BigDecimal checkAmount(AfViewAssetLoanQuery query) {
		return afViewAssetLoanDao.checkAmount(query);
	}

	@Override
	public AfViewAssetLoanDo getByQueryCondition(AfViewAssetLoanQuery query) {
		return afViewAssetLoanDao.getByQueryCondition(query);
	}
}