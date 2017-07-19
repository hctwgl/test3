package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTradeOrderDao;
import com.ald.fanbei.api.dal.domain.AfTradeOrderDo;
import com.ald.fanbei.api.biz.service.AfTradeOrderService;



/**
 * 商圈订单扩展表ServiceImpl
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTradeOrderService")
public class AfTradeOrderServiceImpl extends ParentServiceImpl<AfTradeOrderDo, Long> implements AfTradeOrderService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTradeOrderServiceImpl.class);
   
    @Resource
    private AfTradeOrderDao afTradeOrderDao;

		@Override
	public BaseDao<AfTradeOrderDo, Long> getDao() {
		return afTradeOrderDao;
	}
}