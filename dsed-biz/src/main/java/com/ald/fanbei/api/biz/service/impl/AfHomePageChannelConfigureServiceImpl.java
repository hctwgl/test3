package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfHomePageChannelConfigureDao;
import com.ald.fanbei.api.dal.domain.AfHomePageChannelConfigureDo;
import com.ald.fanbei.api.biz.service.AfHomePageChannelConfigureService;



/**
 * 频道配置表ServiceImpl
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-04-12 17:59:56
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afHomePageChannelConfigureService")
public class AfHomePageChannelConfigureServiceImpl extends ParentServiceImpl<AfHomePageChannelConfigureDo, Long> implements AfHomePageChannelConfigureService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfHomePageChannelConfigureServiceImpl.class);
   
    @Resource
    private AfHomePageChannelConfigureDao afHomePageChannelConfigureDao;

		@Override
	public BaseDao<AfHomePageChannelConfigureDo, Long> getDao() {
		return afHomePageChannelConfigureDao;
	}

		@Override
		public List<AfHomePageChannelConfigureDo> getByChannelId(Long tabId) {
			// TODO Auto-generated method stub
			return afHomePageChannelConfigureDao.getByChannelId(tabId);
		}
}