package com.ald.fanbei.api.biz.service;

import java.util.Map;

import com.ald.fanbei.api.common.enums.Source;

/**
 * 
 * @类描述：优惠券、优惠活动规则引起
 * @author xiaotianjian 2017年2月7日下午1:25:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface ActiveRuleEnginer {
	/**
	 * 
	 * @param inputData 输入参数
	 * @param source 来源
	 */
	void executeRule(Map<String,Object> inputData,Source source);
}
