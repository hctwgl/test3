package com.ald.fanbei.api.biz.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.ActiveRuleEnginer;
import com.ald.fanbei.api.common.enums.Source;

/**
 * 
 * @类描述：规则引擎类
 * @author xiaotianjian 2017年2月7日下午3:08:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("activeRuleEngineUtil")
public class ActiveRuleEngineUtil {
	@Resource
	ActiveRuleEnginer registRuleEngine;
	@Resource
	ActiveRuleEnginer afSigninRuleEngine;
	/**
	 * 注册时执行规则
	 * 
	 *@param userId 用户userId
	 *@param invitor 邀请人userId
	 */
	@Async
	public void regist(Long userId,Source source){
		Map<String,Object> inputData = new HashMap<String,Object>();
		inputData.put("userId", userId);
		registRuleEngine.executeRule(inputData,source);
	}
	@Async
	public void signin(Map<String,Object> inputData,Source source){
		afSigninRuleEngine.executeRule(inputData,source);
	}
	
}
