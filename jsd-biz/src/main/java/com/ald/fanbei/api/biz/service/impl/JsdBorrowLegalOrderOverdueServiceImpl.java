package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderOverdueDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderOverdueDo;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderOverdueService;



/**
 * 极速贷ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowLegalOrderOverdueService")
public class JsdBorrowLegalOrderOverdueServiceImpl extends ParentServiceImpl<JsdBorrowLegalOrderOverdueDo, Long> implements JsdBorrowLegalOrderOverdueService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBorrowLegalOrderOverdueServiceImpl.class);
   
    @Resource
    private JsdBorrowLegalOrderOverdueDao jsdBorrowLegalOrderOverdueDao;

		@Override
	public BaseDao<JsdBorrowLegalOrderOverdueDo, Long> getDao() {
		return jsdBorrowLegalOrderOverdueDao;
	}
}