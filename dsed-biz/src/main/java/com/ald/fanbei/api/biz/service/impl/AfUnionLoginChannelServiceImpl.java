package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUnionLoginChannelDao;
import com.ald.fanbei.api.dal.domain.AfUnionLoginChannelDo;
import com.ald.fanbei.api.biz.service.AfUnionLoginChannelService;



/**
 * '联合登录渠道信息表ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-20 15:38:24
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUnionLoginChannelService")
public class AfUnionLoginChannelServiceImpl extends ParentServiceImpl<AfUnionLoginChannelDo, Long> implements AfUnionLoginChannelService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUnionLoginChannelServiceImpl.class);
   
    @Resource
    private AfUnionLoginChannelDao afUnionLoginChannelDao;

		@Override
	public BaseDao<AfUnionLoginChannelDo, Long> getDao() {
		return afUnionLoginChannelDao;
	}
}