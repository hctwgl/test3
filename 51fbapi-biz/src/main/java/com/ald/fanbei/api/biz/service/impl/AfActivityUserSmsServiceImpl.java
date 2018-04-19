package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfActivityUserSmsDao;
import com.ald.fanbei.api.dal.domain.AfActivityUserSmsDo;
import com.ald.fanbei.api.biz.service.AfActivityUserSmsService;



/**
 * 活动预约信息表ServiceImpl
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-04-13 22:32:58
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afActivityUserSmsService")
public class AfActivityUserSmsServiceImpl extends ParentServiceImpl<AfActivityUserSmsDo, Long> implements AfActivityUserSmsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfActivityUserSmsServiceImpl.class);
   
    @Resource
    private AfActivityUserSmsDao afActivityUserSmsDao;

		@Override
	public BaseDao<AfActivityUserSmsDo, Long> getDao() {
		return afActivityUserSmsDao;
	}
}