package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdOfflineOverdueRemoveDao;
import com.ald.fanbei.api.dal.domain.JsdOfflineOverdueRemoveDo;
import com.ald.fanbei.api.biz.service.JsdOfflineOverdueRemoveService;



/**
 * ServiceImpl
 * 
 * @author yinxiangyu
 * @version 1.0.0 初始化
 * @date 2018-10-13 17:46:16
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdOfflineOverdueRemoveService")
public class JsdOfflineOverdueRemoveServiceImpl extends ParentServiceImpl<JsdOfflineOverdueRemoveDo, Long> implements JsdOfflineOverdueRemoveService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdOfflineOverdueRemoveServiceImpl.class);
   
    @Resource
    private JsdOfflineOverdueRemoveDao jsdOfflineOverdueRemoveDao;

		@Override
	public BaseDao<JsdOfflineOverdueRemoveDo, Long> getDao() {
		return jsdOfflineOverdueRemoveDao;
	}

	@Override
	public JsdOfflineOverdueRemoveDo getInfoByoverdueLogId(String overdueLogId) {
		return jsdOfflineOverdueRemoveDao.getInfoByoverdueLogId(overdueLogId);
	}
}