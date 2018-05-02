package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfHomePageChannelDao;
import com.ald.fanbei.api.dal.domain.AfHomePageChannelDo;
import com.ald.fanbei.api.biz.service.AfHomePageChannelService;



/**
 * 首页频道表ServiceImpl
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-04-12 17:58:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afHomePageChannelService")
public class AfHomePageChannelServiceImpl extends ParentServiceImpl<AfHomePageChannelDo, Long> implements AfHomePageChannelService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfHomePageChannelServiceImpl.class);
   
    @Resource
    private AfHomePageChannelDao afHomePageChannelDao;

		@Override
	public BaseDao<AfHomePageChannelDo, Long> getDao() {
		return afHomePageChannelDao;
	}

		@Override
		public List<AfHomePageChannelDo> getListOrderBySortDesc() {
			// TODO Auto-generated method stub
			return afHomePageChannelDao.getListOrderBySortDesc();
		}
}