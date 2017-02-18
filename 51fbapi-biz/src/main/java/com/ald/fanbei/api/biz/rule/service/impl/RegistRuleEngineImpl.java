package com.ald.fanbei.api.biz.rule.service.impl;

import org.springframework.stereotype.Service;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午3:06:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("registRuleEngine")
public class RegistRuleEngineImpl {

//	private static final String REGISTKEY = "regist";
//
//	@Resource
//	AfCouponSceneService hoaActivityService;
//
//	@Override
//	protected AfCouponSceneDo getActivity(Date now) {
//		AfCouponSceneDo activityDo = hoaActivityService.getActivityByType(CouponSenceRuleType.REGIST.getCode());
//		return checkActivity(activityDo, now);
//	}
//
//	@Override
//	protected Map<String, List<CouponSceneRuleBo>> getRules(Date now, AfCouponSceneDo activityDo) {
//		JSONObject jsonStr = JSONObject.parseObject(activityDo.getRuleJson());
//		List<CouponSceneRuleBo> registRuleList = JSONObject.parseArray(jsonStr.getString(REGISTKEY), CouponSceneRuleBo.class);
//		Map<String, List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
//		ruleMap.put(REGISTKEY, registRuleList);
//		return ruleMap;
//	}
//
//	@Override
//	protected Map<String, List<CouponSceneRuleBo>> matchRule(Date now, Map<String, Object> inputData, Map<String, List<CouponSceneRuleBo>> rules, AfCouponSceneDo activityDo) {
//		List<CouponSceneRuleBo> ruleList = rules.get(REGISTKEY);
//		List<CouponSceneRuleBo> matchList = new ArrayList<CouponSceneRuleBo>();
//		for (CouponSceneRuleBo item : ruleList) {
//			if (StringUtils.equals(EMPTY_CONDITION, item.getCondition())) {
//				matchList.add(item);
//			}
//		}
//		if (CollectionUtil.isEmpty(matchList)) {
//			return null;
//		}
//		Map<String, List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
//		ruleMap.put(REGISTKEY, matchList);
//		return ruleMap;
//	}
//
//	@Override
//	protected void exeRule(Map<String, Object> inputData, Map<String, List<CouponSceneRuleBo>> rules, Source source) {
//		Long userId = (Long) inputData.get("userId");
//		List<CouponSceneRuleBo> ruleList = rules.get(REGISTKEY);
//		for (CouponSceneRuleBo item : ruleList) {
//			this.addUserCoupon(item, userId, CouponSenceRuleType.REGIST, null, null, source);
//		}
//	}

}
