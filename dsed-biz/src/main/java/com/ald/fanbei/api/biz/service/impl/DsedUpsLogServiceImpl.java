package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.DsedUpsLogService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedUpsLogDao;
import com.ald.fanbei.api.dal.domain.DsedUpsLogDo;



/**
 * ups打款记录ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:49:29
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedUpsLogService")
public class DsedUpsLogServiceImpl extends ParentServiceImpl<DsedUpsLogDo, Long> implements DsedUpsLogService {
	
    @Resource
    private DsedUpsLogDao dsedUpsLogDao;

		@Override
	public BaseDao<DsedUpsLogDo, Long> getDao() {
		return dsedUpsLogDao;
	}
}