package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJipiaoPassengerDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeJipiaoPassengerDo;
import com.ald.fanbei.api.biz.service.AfBoluomeJipiaoPassengerService;



/**
 * 菠萝觅订单详情ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-02-02 16:34:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeJipiaoPassengerService")
public class AfBoluomeJipiaoPassengerServiceImpl extends ParentServiceImpl<AfBoluomeJipiaoPassengerDo, Long> implements AfBoluomeJipiaoPassengerService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeJipiaoPassengerServiceImpl.class);
   
    @Resource
    private AfBoluomeJipiaoPassengerDao afBoluomeJipiaoPassengerDao;

		@Override
	public BaseDao<AfBoluomeJipiaoPassengerDo, Long> getDao() {
		return afBoluomeJipiaoPassengerDao;
	}
}