package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAssetSideOperaLogDao;
import com.ald.fanbei.api.dal.domain.AfAssetSideOperaLogDo;
import com.ald.fanbei.api.biz.service.AfAssetSideOperaLogService;



/**
 * 资金方调用爱上街业务日志ServiceImpl
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:48:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAssetSideOperaLogService")
public class AfAssetSideOperaLogServiceImpl extends ParentServiceImpl<AfAssetSideOperaLogDo, Long> implements AfAssetSideOperaLogService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAssetSideOperaLogServiceImpl.class);
   
    @Resource
    private AfAssetSideOperaLogDao afAssetSideOperaLogDao;

		@Override
	public BaseDao<AfAssetSideOperaLogDo, Long> getDao() {
		return afAssetSideOperaLogDao;
	}
}