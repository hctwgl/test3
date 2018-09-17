package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.MgrOperatorDao;
import com.ald.fanbei.api.dal.domain.MgrOperatorDo;
import com.ald.fanbei.api.biz.service.MgrOperatorService;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-17 15:22:24
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("mgrOperatorService")
public class MgrOperatorServiceImpl extends ParentServiceImpl<MgrOperatorDo, Long> implements MgrOperatorService {
	
    private static final Logger logger = LoggerFactory.getLogger(MgrOperatorServiceImpl.class);
   
    @Resource
    private MgrOperatorDao mgrOperatorDao;

		@Override
	public BaseDao<MgrOperatorDo, Long> getDao() {
		return mgrOperatorDao;
	}
}