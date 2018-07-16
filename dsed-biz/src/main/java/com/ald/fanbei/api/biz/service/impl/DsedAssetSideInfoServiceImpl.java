package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedAssetSideInfoDao;
import com.ald.fanbei.api.dal.domain.DsedAssetSideInfoDo;
import com.ald.fanbei.api.biz.service.DsedAssetSideInfoService;



/**
 * 信用卡绑定及订单支付ServiceImpl
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-07-16 11:48:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedAssetSideInfoService")
public class DsedAssetSideInfoServiceImpl extends ParentServiceImpl<DsedAssetSideInfoDo, Long> implements DsedAssetSideInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedAssetSideInfoServiceImpl.class);
   
    @Resource
    private DsedAssetSideInfoDao dsedAssetSideInfoDao;

		@Override
	public BaseDao<DsedAssetSideInfoDo, Long> getDao() {
		return dsedAssetSideInfoDao;
	}
}