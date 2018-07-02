package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfViewAssetBorrowCashService;
import com.ald.fanbei.api.dal.dao.AfViewAssetBorrowCashDao;
import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowCashDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowCashQuery;



/**
 * 资产可用债权信息ServiceImpl
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:50:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afViewAssetBorrowCashService")
public class AfViewAssetBorrowCashServiceImpl  implements AfViewAssetBorrowCashService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfViewAssetBorrowCashServiceImpl.class);
   
    @Resource
    private AfViewAssetBorrowCashDao afViewAssetBorrowCashDao;

    /**
	 * 根据条件获取视图明细记录
	 * @param afViewAssetBorrowCashQuery
	 * @return
	 */
    @Override
	public List<AfViewAssetBorrowCashDo> getListByQueryCondition(AfViewAssetBorrowCashQuery afViewAssetBorrowCashQuery){
		return afViewAssetBorrowCashDao.getListByQueryCondition(afViewAssetBorrowCashQuery);
	}
    
    @Override
	public BigDecimal getSumAmount(Date gmtCreateStart, Date gmtCreateEnd) {
		return afViewAssetBorrowCashDao.getSumAmount(gmtCreateStart,gmtCreateEnd);
	}

	@Override
	public BigDecimal getSumMinAmount(Date gmtCreateStart, Date gmtCreateEnd,String minBorrowTime) {
		return afViewAssetBorrowCashDao.getSumMinAmount(gmtCreateStart, gmtCreateEnd,minBorrowTime);
	}

	@Override
	public BigDecimal getSumMaxAmount(Date gmtCreateStart,Date gmtCreateEnd,String maxBorrowTime) {
		return afViewAssetBorrowCashDao.getSumMaxAmount(gmtCreateStart, gmtCreateEnd,maxBorrowTime);
	}

	@Override
	public AfViewAssetBorrowCashDo getByQueryCondition(AfViewAssetBorrowCashQuery query) {
		return afViewAssetBorrowCashDao.getByQueryCondition(query);
	}

	@Override
	public BigDecimal checkAmount(AfViewAssetBorrowCashQuery query) {
		return afViewAssetBorrowCashDao.checkAmount(query);
	}
}