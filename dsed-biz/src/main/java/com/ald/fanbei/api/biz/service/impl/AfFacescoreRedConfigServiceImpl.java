package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfFacescoreRedConfigDao;
import com.ald.fanbei.api.dal.domain.AfFacescoreRedConfigDo;
import com.ald.fanbei.api.biz.service.AfFacescoreRedConfigService;



/**
 * 颜值测试红包配置表实体类ServiceImpl
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-12 16:36:56
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afFacescoreRedConfigService")
public class AfFacescoreRedConfigServiceImpl extends ParentServiceImpl<AfFacescoreRedConfigDo, Long> implements AfFacescoreRedConfigService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfFacescoreRedConfigServiceImpl.class);
   
    @Resource
    private AfFacescoreRedConfigDao afFacescoreRedConfigDao;

		@Override
	public BaseDao<AfFacescoreRedConfigDo, Long> getDao() {
		return afFacescoreRedConfigDao;
	}

		@Override
		public List<AfFacescoreRedConfigDo> findAll() {
			return afFacescoreRedConfigDao.findAll();
		}
}