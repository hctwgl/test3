package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月15日 下午3:09:39
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAuthService")
public class AfUserAuthServiceImpl implements AfUserAuthService {

	@Resource
	AfUserAuthDao afUserAuthDao;
	
	@Override
	public int addUserAuth(AfUserAuthDo afUserAuthDo) {
		return afUserAuthDao.addUserAuth(afUserAuthDo);
	}

	@Override
	public int updateUserAuth(AfUserAuthDo afUserAuthDo) {
		return afUserAuthDao.updateUserAuth(afUserAuthDo);
	}

}
