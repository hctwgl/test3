package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardTypeDao;
import com.ald.fanbei.api.dal.domain.AfUserBankcardTypeDo;
import com.ald.fanbei.api.biz.service.AfUserBankcardTypeService;



/**
 * 信用卡绑定及订单支付ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-05-09 10:01:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserBankcardTypeService")
public class AfUserBankcardTypeServiceImpl extends ParentServiceImpl<AfUserBankcardTypeDo, Long> implements AfUserBankcardTypeService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserBankcardTypeServiceImpl.class);
   
    @Resource
    private AfUserBankcardTypeDao afUserBankcardTypeDao;

		@Override
	public BaseDao<AfUserBankcardTypeDo, Long> getDao() {
		return afUserBankcardTypeDao;
	}
}