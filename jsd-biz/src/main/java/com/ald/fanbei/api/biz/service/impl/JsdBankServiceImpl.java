package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBankDao;
import com.ald.fanbei.api.dal.domain.JsdBankDo;
import com.ald.fanbei.api.biz.service.JsdBankService;



/**
 * 极速贷银行卡信息ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-23 09:40:17
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBankService")
public class JsdBankServiceImpl extends ParentServiceImpl<JsdBankDo, Long> implements JsdBankService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBankServiceImpl.class);
   
    @Resource
    private JsdBankDao jsdBankDao;

		@Override
	public BaseDao<JsdBankDo, Long> getDao() {
		return jsdBankDao;
	}
}