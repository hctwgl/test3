package com.ald.jsd.mgr.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.impl.ParentServiceImpl;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.jsd.mgr.biz.service.MgrOperatorService;
import com.ald.jsd.mgr.dal.dao.MgrOperatorDao;
import com.ald.jsd.mgr.dal.domain.MgrOperatorDo;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-18 10:34:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("mgrOperatorService")
public class MgrOperatorServiceImpl extends ParentServiceImpl<MgrOperatorDo, Long> implements MgrOperatorService {
	
    @Resource
    private MgrOperatorDao mgrOperatorDao;

		@Override
	public BaseDao<MgrOperatorDo, Long> getDao() {
		return mgrOperatorDao;
	}

	@Override
	public MgrOperatorDo getByUsername(String username) {
		// TODO
		return null;
	}
	
}