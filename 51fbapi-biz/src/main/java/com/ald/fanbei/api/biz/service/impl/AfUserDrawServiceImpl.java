package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserDrawDao;
import com.ald.fanbei.api.dal.domain.AfUserDrawDo;
import com.ald.fanbei.api.biz.service.AfUserDrawService;



/**
 * 年会抽奖ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2017-12-27 16:31:00
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserDrawService")
public class AfUserDrawServiceImpl extends ParentServiceImpl<AfUserDrawDo, Long> implements AfUserDrawService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserDrawServiceImpl.class);
   
    @Resource
    private AfUserDrawDao afUserDrawDao;

		@Override
	public BaseDao<AfUserDrawDo, Long> getDao() {
		return afUserDrawDao;
	}
}