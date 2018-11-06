package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.util.CommonUtil;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdUserDo;



/**
 * 极速贷用户信息ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:43:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdUserService")
public class JsdUserServiceImpl extends ParentServiceImpl<JsdUserDo, Long> implements JsdUserService {
   
    @Resource
    private JsdUserDao jsdUserDao;

		@Override
	public BaseDao<JsdUserDo, Long> getDao() {
		return jsdUserDao;
	}

	@Override
	public JsdUserDo getByOpenId(String openId) {
		return jsdUserDao.getByOpenId(openId);
	}

	@Override
	public int updateUser(JsdUserDo userDo) {
		return jsdUserDao.updateUser(userDo);
	}

	@Override
	public JsdUserDo getUserInfo(String mobile) {
		if(jsdUserDao.getUserInfo(mobile)==null){
			return null;
		}
		return jsdUserDao.getUserInfo(mobile);
	}
}