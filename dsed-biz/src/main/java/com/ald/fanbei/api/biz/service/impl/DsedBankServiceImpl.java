package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedBankDao;
import com.ald.fanbei.api.dal.domain.DsedBankDo;
import com.ald.fanbei.api.biz.service.DsedBankService;



/**
 * 信用卡绑定及订单支付ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:36:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedBankService")
public class DsedBankServiceImpl extends ParentServiceImpl<DsedBankDo, Long> implements DsedBankService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedBankServiceImpl.class);
   
    @Resource
    private DsedBankDao dsedBankDao;

		@Override
	public BaseDao<DsedBankDo, Long> getDao() {
		return dsedBankDao;
	}

	@Override
	public DsedBankDo getBankByName(String name) {
		return dsedBankDao.getBankByName(name);
	}
}