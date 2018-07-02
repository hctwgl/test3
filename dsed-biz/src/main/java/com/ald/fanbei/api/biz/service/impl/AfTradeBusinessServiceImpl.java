package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfTradeBusinessService;
import com.ald.fanbei.api.dal.dao.AfTradeBusinessDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商圈商户表ServiceImpl
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:40:39 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afTradeBusinessService")
public class AfTradeBusinessServiceImpl extends ParentServiceImpl<AfTradeBusinessDo, Long> implements AfTradeBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(AfTradeBusinessServiceImpl.class);

	@Resource
	private AfTradeBusinessDao afTradeBusinessDao;

	@Override
	public BaseDao<AfTradeBusinessDo, Long> getDao() {
		return afTradeBusinessDao;
	}

	@Override
	public AfTradeBusinessDo getByName(String username) {
		return afTradeBusinessDao.getByName(username);
	}
}