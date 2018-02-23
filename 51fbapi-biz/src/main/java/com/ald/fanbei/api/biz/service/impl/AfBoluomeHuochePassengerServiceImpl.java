package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeHuochePassengerDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeHuochePassengerDo;
import com.ald.fanbei.api.biz.service.AfBoluomeHuochePassengerService;



/**
 * 菠萝觅订单详情ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-02-02 16:34:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeHuochePassengerService")
public class AfBoluomeHuochePassengerServiceImpl extends ParentServiceImpl<AfBoluomeHuochePassengerDo, Long> implements AfBoluomeHuochePassengerService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeHuochePassengerServiceImpl.class);
   
    @Resource
    private AfBoluomeHuochePassengerDao afBoluomeHuochePassengerDao;

		@Override
	public BaseDao<AfBoluomeHuochePassengerDo, Long> getDao() {
		return afBoluomeHuochePassengerDao;
	}
}