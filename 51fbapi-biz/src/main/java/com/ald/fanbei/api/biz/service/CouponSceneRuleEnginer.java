package com.ald.fanbei.api.biz.service;

import java.util.Map;


/**
 * 
 *@类现描述：获券场景匹配规则
 *@author chenjinhu 2017年2月17日 下午5:09:10
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface CouponSceneRuleEnginer {
	/**
	 * 
	 * @param inputData 输入参数
	 * @param source 来源
	 */
	void executeRule(Map<String,Object> inputData);
}
