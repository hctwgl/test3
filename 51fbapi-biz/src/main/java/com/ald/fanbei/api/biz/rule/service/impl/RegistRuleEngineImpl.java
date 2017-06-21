package com.ald.fanbei.api.biz.rule.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.biz.service.AfGameChanceService;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：注册获取优惠券规则引擎
 * @author xiaotianjian 2017年2月7日下午3:06:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("registRuleEngine")
public class RegistRuleEngineImpl extends AbstractCouponSceneRuleEngine{

	private static final String REGISTKEY = "regist";
	
	@Override
	protected AfCouponSceneDo getCouponScene(Date now) {
		AfCouponSceneDo activityDo = afCouponSceneDao.getCouponSceneByType(CouponSenceRuleType.REGIST.getCode());
		return checkActivity(activityDo, now);
	}
	
	@Override
	protected Map<String, List<CouponSceneRuleBo>> getRules(Date now, AfCouponSceneDo couponScene) {
		JSONObject jsonStr = JSONObject.parseObject(couponScene.getRuleJson());
		List<CouponSceneRuleBo> registRuleList = JSONObject.parseArray(jsonStr.getString(REGISTKEY), CouponSceneRuleBo.class);
		Map<String,List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
		ruleMap.put(REGISTKEY, registRuleList);
		return ruleMap;
	}

	@Override
	protected Map<String, List<CouponSceneRuleBo>> matchRule(Date now, Map<String, Object> inputData, Map<String, List<CouponSceneRuleBo>> rules, AfCouponSceneDo couponScene) {

//		//邀请人获取一次抓娃娃抽奖机会
//		Long invitor = (Long)inputData.get("invitor");
//		if(invitor != null && invitor > 0l){
//			afGameChanceService.updateInviteChance(invitor);
//		}
		
		List<CouponSceneRuleBo> ruleList = rules.get(REGISTKEY);
		List<CouponSceneRuleBo> matchList = new ArrayList<CouponSceneRuleBo>();
		for(CouponSceneRuleBo item:ruleList){
			matchList.add(item);
		}
		if(CollectionUtil.isEmpty(matchList)){
			return null;
		}
		Map<String,List<CouponSceneRuleBo>> ruleMap = new HashMap<String, List<CouponSceneRuleBo>>();
		ruleMap.put(REGISTKEY, matchList);
		return ruleMap;
	}

	@Override
	protected void exeRule(Map<String, Object> inputData, Map<String, List<CouponSceneRuleBo>> rules) {
		Long userId = (Long)inputData.get("userId");
		List<CouponSceneRuleBo> ruleList = rules.get(REGISTKEY);
		for(CouponSceneRuleBo item:ruleList){
			//邀请人获取优惠券
			if(null != userId){
				this.addUserCoupon(item, userId,CouponSenceRuleType.REGIST,null);
			}
		}
	}

}
