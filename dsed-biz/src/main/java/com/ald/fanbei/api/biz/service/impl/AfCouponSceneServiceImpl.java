package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.biz.service.AfCouponSceneService;
import com.ald.fanbei.api.common.enums.CouponScene;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.dal.dao.AfCouponSceneDao;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:42:32
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afCouponSceneService")
public class AfCouponSceneServiceImpl implements AfCouponSceneService {

	@Resource
	AfCouponSceneDao afCouponSceneDao;
	
	@Override
	public AfCouponSceneDo getCouponSceneByType(String type) {
		return afCouponSceneDao.getCouponSceneByType(type);
	}

	@Override
	public List<CouponSceneRuleBo> getRules(String type,String key) {
		AfCouponSceneDo activityDo = afCouponSceneDao.getCouponSceneByType(type);
		JSONObject signInRule = JSONObject.parseObject(activityDo.getRuleJson());
		String signIn = signInRule.getString(key);
		return JSONObject.parseArray(signIn, CouponSceneRuleBo.class);
	}
	@Override
	public List<CouponSceneRuleBo> getRules(CouponScene scene) {
		AfCouponSceneDo afCouponSceneDo = afCouponSceneDao.getCouponSceneByType(scene.getType());
		JSONObject signInRule = JSONObject.parseObject(afCouponSceneDo.getRuleJson());
		String signIn = signInRule.getString(scene.getKey());
		return JSONObject.parseArray(signIn, CouponSceneRuleBo.class);
	}
	@Override
	public List<Long> getCounponIds(CouponScene scene) {
		List<CouponSceneRuleBo> rules = getRules(scene);
		List<Long> couponIds = null;
		if (CollectionUtils.isNotEmpty(rules)) {
			couponIds = CollectionConverterUtil.convertToListFromList(rules, new Converter<CouponSceneRuleBo, Long>() {
				@Override
				public Long convert(CouponSceneRuleBo source) {
					return source.getCouponId();
				}
			});
		}
		return couponIds;
	}

	
}
