package com.ald.jsd.mgr.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.impl.ParentServiceImpl;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.jsd.mgr.biz.service.MgrRoleMenuService;
import com.ald.jsd.mgr.dal.dao.MgrRoleMenuDao;
import com.ald.jsd.mgr.dal.domain.MgrRoleMenuDo;



/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-18 10:34:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("mgrRoleMenuService")
public class MgrRoleMenuServiceImpl extends ParentServiceImpl<MgrRoleMenuDo, Long> implements MgrRoleMenuService {
	
    @Resource
    private MgrRoleMenuDao mgrRoleMenuDao;

		@Override
	public BaseDao<MgrRoleMenuDo, Long> getDao() {
		return mgrRoleMenuDao;
	}
}