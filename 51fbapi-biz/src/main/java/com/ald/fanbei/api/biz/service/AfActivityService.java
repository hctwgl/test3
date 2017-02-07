package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.biz.bo.ActivityRuleBo;
import com.ald.fanbei.api.dal.domain.AfActivityDo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:37:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityService {
	/**
	 * 根据活动类型获取活动
	 *@param type
	 *@return
	 */
	AfActivityDo getActivityByType(String type);
	
	
	/**
	 * 根据活动类型获取Rules
	 *@param type
	 *@return
	 */
	List<ActivityRuleBo> getRules(String type, String key);
}
