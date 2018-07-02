package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfViewAssetBorrowDao;
import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowQuery;
import com.ald.fanbei.api.biz.service.AfViewAssetBorrowService;



/**
 * 资产方消费分期债权视图ServiceImpl
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017-12-14 16:59:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afViewAssetBorrowService")
public class AfViewAssetBorrowServiceImpl extends ParentServiceImpl<AfViewAssetBorrowDo, Long> implements AfViewAssetBorrowService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfViewAssetBorrowServiceImpl.class);
   
    @Resource
    private AfViewAssetBorrowDao afViewAssetBorrowDao;

		@Override
	public BaseDao<AfViewAssetBorrowDo, Long> getDao() {
		return afViewAssetBorrowDao;
	}

		@Override
		public BigDecimal getSumAmount(Date gmtCreateStart, Date gmtCreateEnd) {
			return afViewAssetBorrowDao.getSumAmount(gmtCreateStart, gmtCreateEnd);
		}

		@Override
		public List<AfViewAssetBorrowDo> getListByQueryCondition(AfViewAssetBorrowQuery query) {
			return afViewAssetBorrowDao.getListByQueryCondition(query);
		}

		@Override
		public BigDecimal checkAmount(AfViewAssetBorrowQuery query) {
			return afViewAssetBorrowDao.checkAmount(query);
		}

		@Override
		public AfViewAssetBorrowDo getByQueryCondition(AfViewAssetBorrowQuery query) {
			return afViewAssetBorrowDao.getByQueryCondition(query);
		}
}