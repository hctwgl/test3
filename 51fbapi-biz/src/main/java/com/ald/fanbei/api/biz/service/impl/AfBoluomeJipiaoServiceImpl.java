package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJipiaoDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeJipiaoDo;
import com.ald.fanbei.api.biz.service.AfBoluomeJipiaoService;



/**
 * 菠萝觅订单详情ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-02-02 16:34:00
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeJipiaoService")
public class AfBoluomeJipiaoServiceImpl extends ParentServiceImpl<AfBoluomeJipiaoDo, Long> implements AfBoluomeJipiaoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeJipiaoServiceImpl.class);
   
    @Resource
    private AfBoluomeJipiaoDao afBoluomeJipiaoDao;

		@Override
	public BaseDao<AfBoluomeJipiaoDo, Long> getDao() {
		return afBoluomeJipiaoDao;
	}
}