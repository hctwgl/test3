package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedUserBankcardDao;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.biz.service.DsedUserBankcardService;



/**
 * 都市E贷用户绑定的银行卡ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:50
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedUserBankcardService")
public class DsedUserBankcardServiceImpl extends ParentServiceImpl<DsedUserBankcardDo, Long> implements DsedUserBankcardService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedUserBankcardServiceImpl.class);
   
    @Resource
    private DsedUserBankcardDao dsedUserBankcardDao;

		@Override
	public BaseDao<DsedUserBankcardDo, Long> getDao() {
		return dsedUserBankcardDao;
	}
}