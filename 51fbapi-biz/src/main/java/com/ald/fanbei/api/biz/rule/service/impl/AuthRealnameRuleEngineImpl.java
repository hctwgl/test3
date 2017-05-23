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

/**
 *@类现描述：实名认证完成匹配优惠券规则
 *
 *@author chenjinhu 2017年2月17日 下午8:04:08
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authRealnameRuleEngine")
public class AuthRealnameRuleEngineImpl extends AbstractCouponSceneRuleEngine {

	private static final String AUTHNAMEKEY = "authname";
	
	@Override
	protected AfCouponSceneDo getCouponScene(Date now) {
		AfCouponSceneDo activityDo = afCouponSceneDao.getCouponSceneByType(CouponSenceRuleType.AUTHNAME.getCode());
		return checkActivity(activityDo, now);
	}

	@Override
	protected Map<String, List<CouponSceneRuleBo>> getRules(Date now, AfCouponSceneDo couponScene) {
		JSONObject jsonStr = JSONObject.parseObject(couponScene.getRuleJson());
		List<CouponSceneRuleBo> registRuleList = JSONObject.parseArray(jsonStr.getString(AUTHNAMEKEY), CouponSceneRuleBo.class);
		Map<String,List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
		ruleMap.put(AUTHNAMEKEY, registRuleList);
		return ruleMap;
	}

	@Override
	protected Map<String, List<CouponSceneRuleBo>> matchRule(Date now, Map<String, Object> inputData, Map<String, List<CouponSceneRuleBo>> rules, AfCouponSceneDo couponScene) {
		List<CouponSceneRuleBo> ruleList = rules.get(AUTHNAMEKEY);
		List<CouponSceneRuleBo> matchList = new ArrayList<CouponSceneRuleBo>();
		for(CouponSceneRuleBo item:ruleList){
			matchList.add(item);
		}
		if(CollectionUtil.isEmpty(matchList)){
			return null;
		}
		Map<String,List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
		ruleMap.put(AUTHNAMEKEY, matchList);
		return ruleMap;
	}

	@Override
	protected void exeRule(Map<String, Object> inputData, Map<String, List<CouponSceneRuleBo>> rules) {
		Long userId = (Long)inputData.get("userId");
		Long inviterId = (Long)inputData.get("inviterId");//邀请人用户id
		List<CouponSceneRuleBo> ruleList = rules.get(AUTHNAMEKEY);
		for(CouponSceneRuleBo item:ruleList){
			//邀请人获取优惠券
			this.addUserCoupon(item, inviterId,CouponSenceRuleType.AUTHNAME,userId+"");
		}
	}

}
