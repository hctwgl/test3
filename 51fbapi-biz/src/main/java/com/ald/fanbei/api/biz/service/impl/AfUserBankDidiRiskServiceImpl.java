package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserBankDidiRiskDao;
import com.ald.fanbei.api.dal.domain.AfUserBankDidiRiskDo;
import com.ald.fanbei.api.biz.service.AfUserBankDidiRiskService;



/**
 * 滴滴风控绑卡信息ServiceImpl
 * 
 * @author xiaotianjian
 * @version 1.0.0 初始化
 * @date 2017-08-14 13:41:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserBankDidiRiskService")
public class AfUserBankDidiRiskServiceImpl extends ParentServiceImpl<AfUserBankDidiRiskDo, Long> implements AfUserBankDidiRiskService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserBankDidiRiskServiceImpl.class);
   
    @Resource
    private AfUserBankDidiRiskDao afUserBankDidiRiskDao;

		@Override
	public BaseDao<AfUserBankDidiRiskDo, Long> getDao() {
		return afUserBankDidiRiskDao;
	}
}