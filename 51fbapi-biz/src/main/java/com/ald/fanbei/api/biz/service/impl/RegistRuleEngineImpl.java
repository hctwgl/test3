package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.ActivityRuleBo;
import com.ald.fanbei.api.biz.service.AbstractActiveRuleEngine;
import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.common.enums.ActivityRuleType;
import com.ald.fanbei.api.common.enums.Source;
import com.ald.fanbei.api.common.enums.UserCouponSource;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午3:06:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("registRuleEngine")
public class RegistRuleEngineImpl extends AbstractActiveRuleEngine {

	private static final String REGISTKEY = "regist";
	
	@Resource
	AfActivityService hoaActivityService;
	
	@Override
	protected AfActivityDo getActivity(Date now) {
		AfActivityDo activityDo = hoaActivityService.getActivityByType(ActivityRuleType.REGIST.getCode());
		return checkActivity(activityDo, now);
	}
	
	@Override
	protected Map<String,List<ActivityRuleBo>> getRules(Date now,AfActivityDo activityDo) {
		JSONObject jsonStr = JSONObject.parseObject(activityDo.getRuleJson());
		List<ActivityRuleBo> registRuleList = JSONObject.parseArray(jsonStr.getString(REGISTKEY), ActivityRuleBo.class);
		Map<String,List<ActivityRuleBo>> ruleMap = new HashMap<String, List<ActivityRuleBo>>();
		ruleMap.put(REGISTKEY, registRuleList);
		return ruleMap;
	}

	@Override
	protected Map<String,List<ActivityRuleBo>> matchRule(Date now,Map<String,Object> inputData,
			Map<String,List<ActivityRuleBo>> rules,AfActivityDo activityDo) {
		List<ActivityRuleBo> ruleList = rules.get(REGISTKEY);
		List<ActivityRuleBo> matchList = new ArrayList<ActivityRuleBo>();
		for(ActivityRuleBo item:ruleList){
			if(StringUtils.equals(EMPTY_CONDITION, item.getCondition()) && 
					(StringUtils.equals(EMPTY_ENDDATE, item.getEndDate()) || this.checkDate1AfterDate2(item.getEndDate(), now))){
				matchList.add(item);
			}
		}
		if(CollectionUtil.isEmpty(matchList)){
			return null;
		}
		Map<String,List<ActivityRuleBo>> ruleMap = new HashMap<String, List<ActivityRuleBo>>();
		ruleMap.put(REGISTKEY, matchList);
		return ruleMap;
	}
	

	@Override
	protected void exeRule(Map<String,Object> inputData,Map<String,List<ActivityRuleBo>> rules,Source source) {
		Long userId = (Long)inputData.get("userId");
		List<ActivityRuleBo> ruleList = rules.get(REGISTKEY);
		for(ActivityRuleBo item:ruleList){
			this.addUserCoupon(item, userId,UserCouponSource.REGIST,null,null,source);
		}
	}


}
