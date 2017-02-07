package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.ActivityRuleBo;
import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.dal.dao.AfActivityDao;
import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:42:32
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("hoaActivityService")
public class AfActivityServiceImpl implements AfActivityService {

	@Resource
	AfActivityDao afActivityDao;
	
	@Override
	public AfActivityDo getActivityByType(String type) {
		return afActivityDao.getActivityByType(type);
	}

	@Override
	public List<ActivityRuleBo> getRules(String type,String key) {
		AfActivityDo activityDo = afActivityDao.getActivityByType(type);
		JSONObject signInRule = JSONObject.parseObject(activityDo.getRuleJson());
		String signIn = signInRule.getString(key);
		return JSONObject.parseArray(signIn, ActivityRuleBo.class);
	}

}
