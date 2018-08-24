package com.ald.fanbei.api.biz.service.impl;

import java.util.HashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdUserBankcardDao;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;



/**
 * 极速贷用户银行卡信息ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-23 09:40:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdUserBankcardService")
public class JsdUserBankcardServiceImpl extends ParentServiceImpl<JsdUserBankcardDo, Long> implements JsdUserBankcardService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdUserBankcardServiceImpl.class);
   
    @Resource
    private JsdUserBankcardDao jsdUserBankcardDao;

		@Override
	public BaseDao<JsdUserBankcardDo, Long> getDao() {
		return jsdUserBankcardDao;
	}

	@Override
	public JsdUserBankcardDo getByBankNo(String bankNo) {
		return jsdUserBankcardDao.getByBankNo(bankNo);
	}
}