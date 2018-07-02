package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketDo;
import com.ald.fanbei.api.biz.service.AfBoluomeRedpacketService;



/**
 * 点亮活动新版ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:27
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeRedpacketService")
public class AfBoluomeRedpacketServiceImpl extends ParentServiceImpl<AfBoluomeRedpacketDo, Long> implements AfBoluomeRedpacketService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeRedpacketServiceImpl.class);
   
    @Resource
    private AfBoluomeRedpacketDao afBoluomeRedpacketDao;

		@Override
	public BaseDao<AfBoluomeRedpacketDo, Long> getDao() {
		return afBoluomeRedpacketDao;
	}
}