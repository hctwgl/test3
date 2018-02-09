package com.ald.fanbei.api.biz.auth.executor;

import java.math.BigDecimal;

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
import com.ald.fanbei.api.common.enums.RiskScene;
import com.ald.fanbei.api.common.enums.RiskSceneType;
import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;

/**
 * 支付宝认证回调处理类
 * 
 * @author rongbo
 *
 */
@Component("alipayAuthCallbackExecutor")
public class AlipayAuthCallbackExecutor implements Executor {

	@Resource
	AfUserAuthService afUserAuthService;

	@Resource
	AfUserAuthStatusService afUserAuthStatusService;

	@Resource
	AfUserAccountSenceService afUserAccountSenceService;

	@Resource
	AfAuthRaiseStatusService afAuthRaiseStatusService;

	@Resource
	RiskUtil riskUtil;

	private Logger logger = LoggerFactory.getLogger(AlipayAuthCallbackExecutor.class);

	@Override
	public void execute(AuthCallbackBo authCallbackBo) {

		String consumerNo = authCallbackBo.getConsumerNo();
		Long userId = Long.parseLong(consumerNo);
		if (StringUtils.equals(authCallbackBo.getCode(), RiskAuthStatus.SUCCESS.getCode())) {
			// 认证成功,向风控发起提额申请
			AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
			String basicStatus = afUserAuthDo.getBasicStatus();
			// 根据强风控状态判断提额场景
			if (StringUtils.equals("Y", basicStatus)) {
				// RiskSceneType.ALIPAY_XJD_PASS场景
			} else if (StringUtils.equals("N", basicStatus)) {

			}
			// 获取白领贷强风控状态
			AfUserAuthStatusDo bldAuthDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,
					LoanType.BLD_LOAN.getCode());

			// 如果白领贷强风控通过，调起白领贷款提额度场景
			if (bldAuthDo != null && StringUtils.equals("Y", bldAuthDo.getStatus())) {
				try {
					RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId),
							new String[] { RiskScene.ALIPAY_BLD.getCode() }, RiskSceneType.BLD.getCode());
					// 提额成功
					if (respBo != null && respBo.isSuccess()) {
						String bldAmount = respBo.getData().getBldAmount();
						String totalAmount = respBo.getData().getTotalAmount();
						AfUserAccountSenceDo bldAccountSenceDo = buildAccountScene(userId, LoanType.BLD_LOAN.getCode(),
								bldAmount);
						AfUserAccountSenceDo totalAccountSenceDo = buildAccountScene(userId, "LOAN_TOTAL", totalAmount);

						afUserAccountSenceService.updateById(bldAccountSenceDo);
						afUserAccountSenceService.updateById(totalAccountSenceDo);

						AfAuthRaiseStatusDo raiseStatusDo = buildAuthRaiseStatusDo(userId, AuthType.ALIPAY.getCode(),
								LoanType.BLD_LOAN.getCode(), "Y");
						// 提额成功，记录提额状态
						afAuthRaiseStatusService.saveRecord(raiseStatusDo);

					}
				} catch (Exception e) {
					logger.error("raise amount fail =>{}", e.getMessage());
				}

			}
		} else {
			// 更新认证状态为失败
			AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
			afUserAuthDo.setUserId(userId);
			afUserAuthDo.setAlipayStatus("N");
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

	private AfAuthRaiseStatusDo buildAuthRaiseStatusDo(Long userId, String authType, String prdType,
			String raiseStatus) {
		AfAuthRaiseStatusDo raiseStatusDo = new AfAuthRaiseStatusDo();
		raiseStatusDo.setAuthType(authType);
		raiseStatusDo.setPrdType(prdType);
		raiseStatusDo.setUserId(userId);
		raiseStatusDo.setRaiseStatus(raiseStatus);
		return raiseStatusDo;
	}

}
