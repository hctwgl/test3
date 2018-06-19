package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedCodeRecordDao;
import com.ald.fanbei.api.dal.domain.DsedCodeRecordDo;
import com.ald.fanbei.api.biz.service.DsedCodeRecordService;



/**
 * 都市易贷短信验证码记录ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:41:19
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedCodeRecordService")
public class DsedCodeRecordServiceImpl extends ParentServiceImpl<DsedCodeRecordDo, Long> implements DsedCodeRecordService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedCodeRecordServiceImpl.class);
   
    @Resource
    private DsedCodeRecordDao dsedCodeRecordDao;

		@Override
	public BaseDao<DsedCodeRecordDo, Long> getDao() {
		return dsedCodeRecordDao;
	}
}