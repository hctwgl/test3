package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.dal.dao.AfUserVirtualAccountDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUserVirtualAccountDo;



/**
 * '虚拟商品额度记录ServiceImpl
 * 
 * @author xiaotianjian
 * @version 1.0.0 初始化
 * @date 2017-07-08 14:16:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserVirtualAccountService")
public class AfUserVirtualAccountServiceImpl extends ParentServiceImpl<AfUserVirtualAccountDo, Long> implements AfUserVirtualAccountService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserVirtualAccountServiceImpl.class);
   
    @Resource
    private AfUserVirtualAccountDao afUserVirtualAccountDao;

		@Override
	public BaseDao<AfUserVirtualAccountDo, Long> getDao() {
		return afUserVirtualAccountDao;
	}
}