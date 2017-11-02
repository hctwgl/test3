package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAuthYdService;
import com.ald.fanbei.api.dal.dao.AfAuthYdDao;
import com.ald.fanbei.api.dal.domain.AfAuthYdDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月15日 下午12:02:51
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afAuthYdService")
public class AfAuthYdServiceImpl implements AfAuthYdService {

	@Resource
	AfAuthYdDao afAuthYdDao;
	
	@Override
	public int addAuthYd(AfAuthYdDo afAuthYdDo) {
		return afAuthYdDao.addAuthYd(afAuthYdDo);
	}

}
