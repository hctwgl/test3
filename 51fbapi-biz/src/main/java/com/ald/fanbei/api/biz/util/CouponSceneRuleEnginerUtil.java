package com.ald.fanbei.api.biz.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.CouponSceneRuleEnginer;

/**
 * 
 * @类描述：规则引擎类
 * @author xiaotianjian 2017年2月7日下午3:08:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("couponSceneRuleEnginerUtil")
public class CouponSceneRuleEnginerUtil {
	
	@Resource
	private CouponSceneRuleEnginer authRealnameRuleEngine; 
	@Resource
	private CouponSceneRuleEnginer registRuleEngine;
	@Resource
	private CouponSceneRuleEnginer signinRuleEngine;
	@Resource
	private CouponSceneRuleEnginer creditAuthRuleEngine;
	
	/**
	 * 注册时执行规则
	 * 
	 *@param userId 用户userId
	 *@param invitor 邀请人userId
	 */
	@Async
	public void regist(Long userId,Long invitor){
		Map<String,Object> inputData = new HashMap<String,Object>();
		inputData.put("userId", userId);
		inputData.put("invitor", invitor);
		registRuleEngine.executeRule(inputData);
	}
	
	/**
	 * 签到
	 * @param userId
	 */
	@Async
	public void signin(Long userId){
		Map<String,Object> inputData = new HashMap<String,Object>();
		inputData.put("userId", userId);
		signinRuleEngine.executeRule(inputData);
	}
	
	/**
	 * 实名认证
	 * @param userId
	 * @param inviterId
	 */
	@Async
	public void realNameAuth(Long userId,Long inviterId){
		Map<String,Object> inputData = new HashMap<String,Object>();
		inputData.put("userId", userId);
		inputData.put("inviterId", inviterId);
		authRealnameRuleEngine.executeRule(inputData);
	}
	
	/**
	 * 首次过强风控发送优惠劵和现金
	 * @param userId
	 */
	public void creditAuth(Long userId){
		Map<String,Object> inputData = new HashMap<String,Object>();
		inputData.put("userId", userId);
		creditAuthRuleEngine.executeRule(inputData);
	}
}
