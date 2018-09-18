package com.ald.jsd.mgr.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.impl.ParentServiceImpl;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.jsd.mgr.biz.service.MgrRoleService;
import com.ald.jsd.mgr.dal.dao.MgrRoleDao;
import com.ald.jsd.mgr.dal.domain.MgrRoleDo;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-18 10:34:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("mgrRoleService")
public class MgrRoleServiceImpl extends ParentServiceImpl<MgrRoleDo, Long> implements MgrRoleService {
	
    @Resource
    private MgrRoleDao mgrRoleDao;

		@Override
	public BaseDao<MgrRoleDo, Long> getDao() {
		return mgrRoleDao;
	}
}