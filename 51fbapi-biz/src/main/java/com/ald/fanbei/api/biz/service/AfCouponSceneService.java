package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午2:37:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCouponSceneService {
	/**
	 * 根据活动类型获取活动
	 *@param type
	 *@return
	 */
	AfCouponSceneDo getCouponSceneByType(String type);
	
	
	/**
	 * 根据活动类型获取Rules
	 *@param type
	 *@return
	 */
	List<CouponSceneRuleBo> getRules(String type, String key);
}
