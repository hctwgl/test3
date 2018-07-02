package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfFundSideAccountDao;
import com.ald.fanbei.api.dal.domain.AfFundSideAccountDo;
import com.ald.fanbei.api.biz.service.AfFundSideAccountService;



/**
 * '资金方账户资金信息表ServiceImpl
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:54:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afFundSideAccountService")
public class AfFundSideAccountServiceImpl extends ParentServiceImpl<AfFundSideAccountDo, Long> implements AfFundSideAccountService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfFundSideAccountServiceImpl.class);
   
    @Resource
    private AfFundSideAccountDao afFundSideAccountDao;

	@Override
	public BaseDao<AfFundSideAccountDo, Long> getDao() {
		return afFundSideAccountDao;
	}
}