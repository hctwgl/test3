package com.ald.fanbei.api.biz.rule.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSchemeService;
import com.ald.fanbei.api.dal.dao.AfSchemeDao;
import com.ald.fanbei.api.dal.domain.AfSchemeDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年6月6日下午1:52:17
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afSchemeService")
public class AfSchemeServiceImpl implements AfSchemeService {
	
	@Resource
	AfSchemeDao afSchemeDao;
	
	@Override
	public AfSchemeDo getSchemeById(Long id) {
		return afSchemeDao.getSchemeById(id);
	}

}
