/**
 * 
 */
package com.ald.fanbei.api.biz.rule.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：符合连续签到规则
 * @author suweili 2017年2月8日上午11:21:37
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("signinRuleEngine")
public class SigninRuleEngineImpl extends AbstractCouponSceneRuleEngine {

	private static final String SIGNINKEY = "signin";

	@Override
	protected AfCouponSceneDo getCouponScene(Date now) {
		AfCouponSceneDo activityDo = afCouponSceneDao.getCouponSceneByType(CouponSenceRuleType.SIGNIN.getCode());
		return checkActivity(activityDo, now);
	}
	
	@Override
	protected Map<String, List<CouponSceneRuleBo>> getRules(Date now, AfCouponSceneDo activityDo) {
		JSONObject jsonStr = JSONObject.parseObject(activityDo.getRuleJson());
		List<CouponSceneRuleBo> registRuleList = JSONObject.parseArray(jsonStr.getString(SIGNINKEY), CouponSceneRuleBo.class);
		Map<String,List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
		ruleMap.put(SIGNINKEY, registRuleList);
		return ruleMap;
	}


	@Override
	protected Map<String, List<CouponSceneRuleBo>> matchRule(Date now, Map<String, Object> inputData,Map<String, List<CouponSceneRuleBo>> rules, AfCouponSceneDo activityDo) {
		List<CouponSceneRuleBo> ruleList = rules.get(SIGNINKEY);
		List<CouponSceneRuleBo> matchList = new ArrayList<CouponSceneRuleBo>();
		for(CouponSceneRuleBo item:ruleList){

			matchList.add(item);

		}
		if(CollectionUtil.isEmpty(matchList)){
			return null;
		}
		Map<String,List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
		ruleMap.put(SIGNINKEY, matchList);
		return ruleMap;
	}

	
	@Override
	protected void exeRule(Map<String, Object> inputData, Map<String, List<CouponSceneRuleBo>> rules) {
		Long userId = (Long)inputData.get("userId");
		List<CouponSceneRuleBo> ruleList = rules.get(SIGNINKEY);
		for(CouponSceneRuleBo item:ruleList){
			this.addUserCoupon(item, userId,CouponSenceRuleType.SIGNIN,null);
		}
	}


}
