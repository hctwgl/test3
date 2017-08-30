package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAuthZmService;
import com.ald.fanbei.api.dal.dao.AfAuthZmDao;
import com.ald.fanbei.api.dal.domain.AfAuthZmDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月17日 下午2:05:25
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afAuthZmService")
public class AfAuthZmServiceImpl implements AfAuthZmService {

	@Resource
	AfAuthZmDao afAuthZmDao;
	
	@Override
	public int addAuthZm(AfAuthZmDo afAuthZmDo) {
		return afAuthZmDao.addAuthZm(afAuthZmDo);
	}

}
