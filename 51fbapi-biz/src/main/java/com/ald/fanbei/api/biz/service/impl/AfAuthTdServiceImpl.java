package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAuthTdService;
import com.ald.fanbei.api.dal.dao.AfAuthTdDao;
import com.ald.fanbei.api.dal.domain.AfAuthTdDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月16日 上午11:48:07
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afAuthTdService")
public class AfAuthTdServiceImpl implements AfAuthTdService {

	@Resource
	AfAuthTdDao afAuthTdDao;
	
	@Override
	public int addAuthTd(AfAuthTdDo afAuthTdDo) {
		return afAuthTdDao.addAuthTd(afAuthTdDo);
	}

}
