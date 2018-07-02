package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBankcardConfigDao;
import com.ald.fanbei.api.dal.domain.AfBankcardConfigDo;
import com.ald.fanbei.api.biz.service.AfBankcardConfigService;



/**
 * 信用卡绑定及订单支付ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-05-09 10:01:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBankcardConfigService")
public class AfBankcardConfigServiceImpl extends ParentServiceImpl<AfBankcardConfigDo, Long> implements AfBankcardConfigService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBankcardConfigServiceImpl.class);
   
    @Resource
    private AfBankcardConfigDao afBankcardConfigDao;

		@Override
	public BaseDao<AfBankcardConfigDo, Long> getDao() {
		return afBankcardConfigDao;
	}
}