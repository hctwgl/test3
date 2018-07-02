package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfRecycleTradeDao;
import com.ald.fanbei.api.dal.domain.AfRecycleTradeDo;
import com.ald.fanbei.api.biz.service.AfRecycleTradeService;



/**
 * ydm交易记录ServiceImpl
 * 
 * @author cxk
 * @version 1.0.0 初始化
 * @date 2018-02-27 17:22:29
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afRecycleTradeService")
public class AfRecycleTradeServiceImpl extends ParentServiceImpl<AfRecycleTradeDo, Long> implements AfRecycleTradeService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfRecycleTradeServiceImpl.class);
   
    @Resource
    private AfRecycleTradeDao afRecycleTradeDao;

		@Override
	public BaseDao<AfRecycleTradeDo, Long> getDao() {
		return afRecycleTradeDao;
	}

		@Override
		public AfRecycleTradeDo getLastRecord() {			
			return afRecycleTradeDao.getLastRecord();
		}
}