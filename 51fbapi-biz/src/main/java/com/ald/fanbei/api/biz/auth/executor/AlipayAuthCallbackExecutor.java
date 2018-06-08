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
import com.ald.fanbei.api.biz.service.AfUserAccountService;
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
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
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
    private AfUserAuthService afUserAuthService;

    @Resource
    private AfUserAuthStatusService afUserAuthStatusService;

    @Resource
    private AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    private AfAuthRaiseStatusService afAuthRaiseStatusService;

    @Resource
    private AfUserAccountService afUserAccountService;

    @Resource
    private RiskUtil riskUtil;

    private Logger logger = LoggerFactory.getLogger(AlipayAuthCallbackExecutor.class);

    @Override
    public void execute(AuthCallbackBo authCallbackBo) {
	logger.info("start alipay auth callback execute");
	String consumerNo = authCallbackBo.getConsumerNo();
	Long userId = Long.parseLong(consumerNo);

	AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
	afUserAuthDo.setUserId(userId);
	if (StringUtils.equals(authCallbackBo.getResult(), RiskAuthStatus.SUCCESS.getCode())) {
	    // 首先初始化提额状态
	    afAuthRaiseStatusService.initRaiseStatus(userId, AuthType.ALIPAY.getCode());
	    // 认证通过，更新支付宝认证状态
	    afUserAuthDo.setAlipayStatus("Y");
	    afUserAuthDo.setRiskStatus("Y");
	    afUserAuthDo.setGmtAlipay(new Date());
	    afUserAuthService.updateUserAuth(afUserAuthDo);
	    // 认证成功,向风控发起提额申请
	    AfUserAuthDo afUserAuthInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
	    String basicStatus = afUserAuthInfo.getBasicStatus();
	    // 根据强风控状态判断提额场景
	    AfAuthRaiseStatusDo afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.CASH.getName(), AuthType.ALIPAY.getCode(), userId);
	    if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.CASH.getName(), AuthType.ALIPAY.getCode(), afUserAuthDo.getGmtAlipay())) {
		if (StringUtils.equals("Y", basicStatus)) {
		    RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId), new String[] { RiskScene.ALIPAY_XJD_PASS.getCode() }, RiskSceneType.XJD.getCode());
		    // 提额成功
		    if (respBo != null && respBo.isSuccess()) {
			String raiseStatus = respBo.getData().getResults()[0].getResult();
			if (StringUtils.equals(RiskRaiseResult.PASS.getCode(), raiseStatus)) {
			    String amount = respBo.getData().getAmount();
			    String totalAmount = respBo.getData().getTotalAmount();
			    // 更新小贷额度
			    AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
			    afUserAccountDo.setUserId(userId);
			    afUserAccountDo.setAuAmount(new BigDecimal(amount));
			    afUserAccountService.updateUserAccount(afUserAccountDo);
			    // 更新总额度
			    AfUserAccountSenceDo totalAccountSenceDo = afUserAccountSenceService.buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), totalAmount);
			    afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);
			    AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.ALIPAY.getCode(), LoanType.CASH.getCode(), "Y", new BigDecimal(amount), new Date());
			    // 提额成功，记录提额状态
			    afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);

			} else {
			    AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.ALIPAY.getCode(), LoanType.CASH.getCode(), "F", BigDecimal.ZERO, new Date());
			    // 提额失败，记录提额状态
			    afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			}
		    }

		} else if (StringUtils.equals("N", basicStatus)) {
		    RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId), new String[] { RiskScene.ALIPAY_XJD_UNPASS.getCode() }, RiskSceneType.XJD.getCode());
		    // 提额成功
		    if (respBo != null && respBo.isSuccess()) {
			String raiseStatus = respBo.getData().getResults()[0].getResult();
			if (StringUtils.equals(RiskRaiseResult.PASS.getCode(), raiseStatus)) {
			    String amount = respBo.getData().getAmount();
			    String totalAmount = respBo.getData().getTotalAmount();
			    // 更新小贷额度
			    AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
			    afUserAccountDo.setUserId(userId);
			    afUserAccountDo.setAuAmount(new BigDecimal(amount));
			    afUserAccountService.updateUserAccount(afUserAccountDo);
			    // 更新总额度
			    AfUserAccountSenceDo totalAccountSenceDo = afUserAccountSenceService.buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), totalAmount);
			    afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);
			    AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.ALIPAY.getCode(), LoanType.CASH.getCode(), "Y", new BigDecimal(amount), new Date());
			    // 提额成功，记录提额状态
			    afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);

			} else {
			    AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.ALIPAY.getCode(), LoanType.CASH.getCode(), "F", BigDecimal.ZERO, new Date());
			    // 提额失败，记录提额状态
			    afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			}
		    }
		}
	    }
	    // 获取白领贷强风控状态
	    AfUserAuthStatusDo bldAuthDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId, LoanType.BLD_LOAN.getCode());
	    // 如果白领贷强风控通过，调起白领贷款提额度场景
	    if (bldAuthDo != null && StringUtils.equals("Y", bldAuthDo.getStatus())) {
		try {
		    afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.LOAN_TOTAL.getName(), AuthType.ALIPAY.getCode(), userId);
		    if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.CASH.getName(), AuthType.ALIPAY.getCode(), afUserAuthDo.getGmtAlipay())) {
			RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId), new String[] { RiskScene.ALIPAY_BLD.getCode() }, RiskSceneType.BLD.getCode());
			// 提额成功
			if (respBo != null && respBo.isSuccess()) {
			    // 获取提额结果
			    String raiseStatus = respBo.getData().getBldResults()[0].getResult();
			    if (StringUtils.equals(RiskRaiseResult.PASS.getCode(), raiseStatus)) {
				String bldAmount = respBo.getData().getBldAmount();
				String totalAmount = respBo.getData().getTotalAmount();
				AfUserAccountSenceDo bldAccountSenceDo = afUserAccountSenceService.buildAccountScene(userId, LoanType.BLD_LOAN.getCode(), bldAmount);
				AfUserAccountSenceDo totalAccountSenceDo = afUserAccountSenceService.buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), totalAmount);

				afUserAccountSenceService.saveOrUpdateAccountSence(bldAccountSenceDo);
				afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);

				AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.ALIPAY.getCode(), LoanType.BLD_LOAN.getCode(), "Y", new BigDecimal(bldAmount), new Date());
				// 提额成功，记录提额状态
				afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			    } else {
				AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.ALIPAY.getCode(), LoanType.BLD_LOAN.getCode(), "F", BigDecimal.ZERO, new Date());
				// 提额成功，记录提额状态
				afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			    }

			}
		    }
		} catch (Exception e) {
		    logger.error("raise amount fail =>{}", e.getMessage());
		}
	    }

	    afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.ONLINE.getName(), AuthType.ALIPAY.getCode(), userId);
	    if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.ONLINE.getName(), AuthType.ALIPAY.getCode(), afUserAuthDo.getGmtAlipay())) {
		// 线上分期提额
		afUserAccountSenceService.raiseOnlineQuato(userId, SceneType.ONLINE.getName(), RiskScene.ALIPAY_ONLINE.getCode(), RiskSceneType.ONLINE.getCode(), AuthType.ALIPAY.getCode());
	    }
	} else {
	    // 更新认证状态为失败
	    afUserAuthDo.setAlipayStatus("N");
	    afUserAuthService.updateUserAuth(afUserAuthDo);
	}
    }

}
