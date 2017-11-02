package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.biz.service.AfCouponSceneService;
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

}
