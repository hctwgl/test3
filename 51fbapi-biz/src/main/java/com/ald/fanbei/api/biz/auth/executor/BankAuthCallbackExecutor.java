package com.ald.fanbei.api.biz.auth.executor;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
import com.ald.fanbei.api.biz.bo.RiskQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfAuthRaiseStatusService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.enums.AuthType;
import com.ald.fanbei.api.common.enums.LoanType;
import com.ald.fanbei.api.common.enums.RiskAuthStatus;
import com.ald.fanbei.api.common.enums.RiskRaiseResult;
import com.ald.fanbei.api.common.enums.RiskScene;
import com.ald.fanbei.api.common.enums.RiskSceneType;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;

/**
 * 网银认证回调处理类
 * 
 * @author rongbo
 *
 */
@Component("bankAuthCallbackExecutor")
public class BankAuthCallbackExecutor implements Executor {

	@Resource
	AfUserAuthService afUserAuthService;

	@Resource
	AfUserAuthStatusService afUserAuthStatusService;

	@Resource
	AfUserAccountSenceService afUserAccountSenceService;

	@Resource
	RiskUtil riskUtil;

	@Resource
	AfAuthRaiseStatusService afAuthRaiseStatusService;
	
	Logger logger = LoggerFactory.getLogger(BankAuthCallbackExecutor.class);

	@Override
	public void execute(AuthCallbackBo authCallbackBo) {
		logger.info("start bank auth callback execute");
		String consumerNo = authCallbackBo.getConsumerNo();
		Long userId = Long.parseLong(consumerNo);
		AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
		afUserAuthDo.setUserId(userId);
		if (StringUtils.equals(authCallbackBo.getResult(), RiskAuthStatus.SUCCESS.getCode())) {
			// 初始化提额状态
			afAuthRaiseStatusService.initOnlinebankRaiseStatus(userId,AuthType.BANK.getCode());
			
			// 认证成功
			afUserAuthDo.setOnlinebankStatus("Y");
			afUserAuthDo.setRiskStatus("Y");
			afUserAuthDo.setGmtOnlinebank(new Date());
			afUserAuthService.updateUserAuth(afUserAuthDo);
			// 获取白领贷强风s控状态
			AfUserAuthStatusDo bldAuthDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,
					LoanType.BLD_LOAN.getCode());

			// 如果白领贷强风控通过，调起白领贷款提额度场景
			if (bldAuthDo != null && StringUtils.equals("Y", bldAuthDo.getStatus())) {
				RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId),
						new String[] { RiskScene.BANK_BLD.getCode() }, RiskSceneType.BLD.getCode());
				// 提额成功
				if (respBo != null && respBo.isSuccess()) {
					
					String raiseStatus = respBo.getData().getBldResults()[0].getResult();
					if (StringUtils.equals(RiskRaiseResult.PASS.getCode(), raiseStatus)) {
						String bldAmount = respBo.getData().getBldAmount();
						String totalAmount = respBo.getData().getTotalAmount();
						AfUserAccountSenceDo bldAccountSenceDo = buildAccountScene(userId, LoanType.BLD_LOAN.getCode(),
								bldAmount);
						AfUserAccountSenceDo totalAccountSenceDo = buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), totalAmount);

						afUserAccountSenceService.saveOrUpdateAccountSence(bldAccountSenceDo);
						afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);
						
						AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.BANK.getCode(),
								LoanType.BLD_LOAN.getCode(), "Y",BigDecimal.ZERO,new Date());
						// 提额成功，记录提额状态
						afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
					}
				}
			}	
		} else {
			// 认证失败
			afUserAuthDo.setOnlinebankStatus("N");
			afUserAuthService.updateUserAuth(afUserAuthDo);
		}
	}

	private AfUserAccountSenceDo buildAccountScene(Long userId, String loanType, String amount) {
		AfUserAccountSenceDo bldAuthStatusDo = new AfUserAccountSenceDo();
		bldAuthStatusDo.setUserId(userId);
		bldAuthStatusDo.setScene(loanType);
		bldAuthStatusDo.setAuAmount(new BigDecimal(amount));
		return bldAuthStatusDo;
	}

}
