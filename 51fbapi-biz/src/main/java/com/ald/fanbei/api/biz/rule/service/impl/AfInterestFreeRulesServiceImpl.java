package com.ald.fanbei.api.biz.rule.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.AfInterestReduceRulesDo;
import com.ald.fanbei.api.dal.domain.AfInterestReduceSchemeDo;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
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
	
	@Resource
	BizCacheUtil bizCacheUtil;
	 
	@Override
	public AfInterestFreeRulesDo getById(Long id) {
		String cacheKey = "GET_INTEREST_FREE_RULES_BY_ID" + id;
		AfInterestFreeRulesDo freeRulesDo = (AfInterestFreeRulesDo)bizCacheUtil.getObject(cacheKey);
		if(freeRulesDo == null) {
			freeRulesDo = afInterestFreeRulesDao.getById(id);
			bizCacheUtil.saveObject(cacheKey, freeRulesDo);
		}
		return freeRulesDo;
	}

	@Override
	public AfInterestReduceSchemeDo getReduceSchemeByGoodId(Long goodsId, Long brandId, Long catogeryId) {

		AfInterestReduceSchemeDo afInterestReduceSchemeDo = null;
		afInterestReduceSchemeDo = afInterestFreeRulesDao.getReduceSchemeByGoodId(goodsId,null,null);
		if (afInterestReduceSchemeDo == null){
			afInterestReduceSchemeDo=afInterestFreeRulesDao.getReduceSchemeByGoodId(null,brandId,null);
		}
		if (afInterestReduceSchemeDo == null){
			afInterestReduceSchemeDo=afInterestFreeRulesDao.getReduceSchemeByGoodId(null,null,catogeryId);
		}
			return afInterestReduceSchemeDo;

	}

	@Override
	public AfInterestReduceRulesDo getReduceRuleById(Long rid) {
		return afInterestFreeRulesDao.getReduceRuleById(rid);
	}

}
