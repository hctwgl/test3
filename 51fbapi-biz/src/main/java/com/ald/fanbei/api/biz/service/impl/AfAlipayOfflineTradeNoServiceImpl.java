package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAlipayOfflineTradeNoDao;
import com.ald.fanbei.api.dal.domain.AfAlipayOfflineTradeNoDo;
import com.ald.fanbei.api.biz.service.AfAlipayOfflineTradeNoService;

import java.util.List;
import java.util.Map;


/**
 * 支付宝线下转账流水号ServiceImpl
 * 
 * @author xieqiang
 * @version 1.0.0 初始化
 * @date 2018-03-22 16:42:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAlipayOfflineTradeNoService")
public class AfAlipayOfflineTradeNoServiceImpl extends ParentServiceImpl<AfAlipayOfflineTradeNoDo, Long> implements AfAlipayOfflineTradeNoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAlipayOfflineTradeNoServiceImpl.class);
   
    @Resource
    private AfAlipayOfflineTradeNoDao afAlipayOfflineTradeNoDao;

		@Override
	public BaseDao<AfAlipayOfflineTradeNoDo, Long> getDao() {
		return afAlipayOfflineTradeNoDao;
	}

	public List<Map<String,Object>> getTradeNosByUserId(long userId,String status){
		return afAlipayOfflineTradeNoDao.getTradeNosByUserId(userId,status);
	}
}