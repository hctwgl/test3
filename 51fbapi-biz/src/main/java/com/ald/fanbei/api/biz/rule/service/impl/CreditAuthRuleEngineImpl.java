package com.ald.fanbei.api.biz.rule.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.alibaba.fastjson.JSONObject;

@Component("creditAuthRuleEngine")
public class CreditAuthRuleEngineImpl extends AbstractCouponSceneRuleEngine {

	
	private static final String CREDITAUTHKEY = "creditauth";
	
	@Override
	protected AfCouponSceneDo getCouponScene(Date now) {
		AfCouponSceneDo activityDo = afCouponSceneDao.getCouponSceneByType(CouponSenceRuleType.CREDITAUTH.getCode());
		return checkActivity(activityDo, now);
	}
	
	@Override
	protected Map<String, List<CouponSceneRuleBo>> getRules(Date now,
			AfCouponSceneDo couponScene) {
		JSONObject jsonStr = JSONObject.parseObject(couponScene.getRuleJson());
		List<CouponSceneRuleBo> registRuleList = JSONObject.parseArray(jsonStr.getString(CREDITAUTHKEY), CouponSceneRuleBo.class);
		Map<String,List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
		ruleMap.put(CREDITAUTHKEY, registRuleList);
		return ruleMap;
	}
	
	

	@Override
	protected Map<String, List<CouponSceneRuleBo>> matchRule(Date now,
			Map<String, Object> inputData,
			Map<String, List<CouponSceneRuleBo>> rules,
			AfCouponSceneDo couponScene) {
		
		List<CouponSceneRuleBo> ruleList = rules.get(CREDITAUTHKEY);
		List<CouponSceneRuleBo> matchList = new ArrayList<CouponSceneRuleBo>();
		for(CouponSceneRuleBo item:ruleList){
			matchList.add(item);
		}
		if(CollectionUtil.isEmpty(matchList)){
			return null;
		}
		Map<String,List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
		ruleMap.put(CREDITAUTHKEY, matchList);
		return ruleMap;
	}
	

	@Override
	protected void exeRule(Map<String, Object> inputData,
			Map<String, List<CouponSceneRuleBo>> rules) {
		Long userId = (Long)inputData.get("userId");
		List<CouponSceneRuleBo> ruleList = rules.get(CREDITAUTHKEY);
		for(CouponSceneRuleBo item:ruleList){
			// 根据规则发放优惠劵
			if(null != userId){
				this.addUserCoupon(item, userId,CouponSenceRuleType.CREDITAUTH,null);
			}
		}
	}

}


