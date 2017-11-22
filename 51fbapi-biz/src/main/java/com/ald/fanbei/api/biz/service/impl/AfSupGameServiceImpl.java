package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfSupGameDao;
import com.ald.fanbei.api.dal.domain.AfSupGameDo;
import com.ald.fanbei.api.biz.service.AfSupGameService;



/**
 * 新人专享ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-22 13:57:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afSupGameService")
public class AfSupGameServiceImpl extends ParentServiceImpl<AfSupGameDo, Long> implements AfSupGameService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfSupGameServiceImpl.class);
   
    @Resource
    private AfSupGameDao afSupGameDao;

		@Override
	public BaseDao<AfSupGameDo, Long> getDao() {
		return afSupGameDao;
	}
}