package com.ald.fanbei.api.biz.auth.executor;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
import com.ald.fanbei.api.biz.service.AfAuthRaiseStatusService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.enums.AuthType;
import com.ald.fanbei.api.common.enums.LoanType;
import com.ald.fanbei.api.common.enums.RiskAuthStatus;
import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
/**
 * 支付宝认证回调处理类
 * @author rongbo
 *
 */
@Component("alipayAuthCallbackExecutor")
public class AlipayAuthCallbackExecutor implements Executor{
	
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Resource
	AfUserAuthStatusService afUserAuthStatusService;
	
	@Resource
	AfAuthRaiseStatusService afAuthRaiseStatusService;
	
	@Resource
	RiskUtil riskUtil;
	
	@Override
	public void execute(AuthCallbackBo authCallbackBo) {
		
		String consumerNo = authCallbackBo.getConsumerNo();
		Long userId = Long.parseLong(consumerNo);
		
		AfAuthRaiseStatusDo raiseStatusDo = new AfAuthRaiseStatusDo();
		raiseStatusDo.setAuthType(AuthType.ALIPAY.getCode());
		raiseStatusDo.setPrdType(LoanType.BLD_LOAN.getCode());
		raiseStatusDo.setUserId(userId);
		
		if(StringUtils.equals(authCallbackBo.getCode(), RiskAuthStatus.SUCCESS.getCode())){
			// 认证成功,向风控发起提额申请
			AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
			
			String basicStatus = afUserAuthDo.getBasicStatus();
			// 根据强风控状态判断提额场景
			if(StringUtils.equals("Y", basicStatus)) {
				// RiskSceneType.ALIPAY_XJD_PASS场景
				
			} else if (StringUtils.equals("N", basicStatus)){
				
			}
			
		} else {
			
		}
	}

}
