package com.ald.fanbei.api.biz.rule.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.dal.dao.AfInterestFreeRulesDao;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年6月4日下午8:34:01
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afInterestFreeRulesService")
public class AfInterestFreeRulesServiceImpl implements
		AfInterestFreeRulesService {

	@Resource
	AfInterestFreeRulesDao afInterestFreeRulesDao;
	
	@Override
	public AfInterestFreeRulesDo getById(Long id) {
		return afInterestFreeRulesDao.getById(id);
	}

}
