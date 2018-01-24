package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserProfileDao;
import com.ald.fanbei.api.dal.domain.AfUserProfileDo;
import com.ald.fanbei.api.biz.service.AfUserProfileService;



/**
 * 用户关联账号ServiceImpl
 * 
 * @author xieqiang
 * @version 1.0.0 初始化
 * @date 2018-01-24 16:04:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserProfileService")
public class AfUserProfileServiceImpl extends ParentServiceImpl<AfUserProfileDo, Long> implements AfUserProfileService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserProfileServiceImpl.class);
   
    @Resource
    private AfUserProfileDao afUserProfileDao;

		@Override
	public BaseDao<AfUserProfileDo, Long> getDao() {
		return afUserProfileDao;
	}
}