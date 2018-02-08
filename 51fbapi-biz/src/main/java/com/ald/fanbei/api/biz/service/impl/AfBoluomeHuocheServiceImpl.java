package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeHuocheDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeHuocheDo;
import com.ald.fanbei.api.biz.service.AfBoluomeHuocheService;



/**
 * 菠萝觅订单详情ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-02-02 16:34:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeHuocheService")
public class AfBoluomeHuocheServiceImpl extends ParentServiceImpl<AfBoluomeHuocheDo, Long> implements AfBoluomeHuocheService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeHuocheServiceImpl.class);
   
    @Resource
    private AfBoluomeHuocheDao afBoluomeHuocheDao;

		@Override
	public BaseDao<AfBoluomeHuocheDo, Long> getDao() {
		return afBoluomeHuocheDao;
	}
}