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
import com.ald.fanbei.api.common.enums.RaiseStatus;
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
 * 公积金认证回调处理类
 * 
 * @author rongbo
 *
 */
@Component("fundAuthCallbackExecutor")
public class FundAuthCallbackExecutor implements Executor {

    @Resource
    AfUserAuthService afUserAuthService;

    @Resource
    AfUserAuthStatusService afUserAuthStatusService;

    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    AfAuthRaiseStatusService afAuthRaiseStatusService;

    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    RiskUtil riskUtil;

    private Logger logger = LoggerFactory.getLogger(AlipayAuthCallbackExecutor.class);

    @Override
    public void execute(AuthCallbackBo authCallbackBo) {
	logger.info("start fund auth callback execute");
	String consumerNo = authCallbackBo.getConsumerNo();
	Long userId = Long.parseLong(consumerNo);

	AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
	afUserAuthDo.setUserId(userId);
	if (StringUtils.equals(authCallbackBo.getResult(), RiskAuthStatus.SUCCESS.getCode())) {
	    // 首先初始化提额状态
	    afAuthRaiseStatusService.initRaiseStatus(userId, AuthType.FUND.getCode());
	    // 认证通过，更新支付宝认证状态
	    afUserAuthDo.setFundStatus("Y");
	    afUserAuthDo.setRiskStatus("Y");
	    afUserAuthDo.setGmtFund(new Date());
	    afUserAuthService.updateUserAuth(afUserAuthDo);
	    AfAuthRaiseStatusDo afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.CASH.getName(), AuthType.FUND.getCode(), userId);
	    if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.CASH.getName(), AuthType.FUND.getCode(), afUserAuthDo.getGmtFund())) {
		// 认证成功,向风控发起提额申请
		AfUserAuthDo afUserAuthInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
		String basicStatus = afUserAuthInfo.getBasicStatus();
		// 根据强风控状态判断提额场景
		if (StringUtils.equals("Y", basicStatus)) {
		    RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId), new String[] { RiskScene.FUND_XJD_PASS.getCode() }, RiskSceneType.XJD.getCode());
		    // 提额接口调用成功
		    if (respBo != null && respBo.isSuccess()) {
			// 获取提额结果
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
			    AfUserAccountSenceDo totalAccountSenceDo = buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), totalAmount);
			    afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);

			    AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.FUND.getCode(), LoanType.CASH.getCode(), "Y", new BigDecimal(amount), new Date());
			    // 提额成功，记录提额状态
			    afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			} else {
			    AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.FUND.getCode(), LoanType.CASH.getCode(), "F", BigDecimal.ZERO, new Date());
			    // 提额成功，记录提额状态
			    afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			}

		    }

		} else if (StringUtils.equals("N", basicStatus)) {
		    RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId), new String[] { RiskScene.FUND_XJD_UNPASS.getCode() }, RiskSceneType.XJD.getCode());
		    // 提额成功
		    if (respBo != null && respBo.isSuccess()) {
			// 获取提额结果
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
			    AfUserAccountSenceDo totalAccountSenceDo = buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), totalAmount);
			    afUserAccountSenceService.updateById(totalAccountSenceDo);
			    AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.FUND.getCode(), LoanType.CASH.getCode(), "Y", new BigDecimal(amount), new Date());
			    // 提额成功，记录提额状态
			    afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			} else {
			    AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.FUND.getCode(), LoanType.CASH.getCode(), "F", BigDecimal.ZERO, new Date());
			    // 提额成功，记录提额状态
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
		    afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.LOAN_TOTAL.getName(), AuthType.FUND.getCode(), userId);
		    if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.CASH.getName(), AuthType.FUND.getCode(), afUserAuthDo.getGmtFund())) {
			RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId), new String[] { RiskScene.FUND_BLD.getCode() }, RiskSceneType.BLD.getCode());
			// 提额成功
			if (respBo != null && respBo.isSuccess()) {
			    // 获取提额结果
			    String raiseStatus = respBo.getData().getBldResults()[0].getResult();
			    if (StringUtils.equals(RiskRaiseResult.PASS.getCode(), raiseStatus)) {
				String bldAmount = respBo.getData().getBldAmount();
				String totalAmount = respBo.getData().getTotalAmount();
				AfUserAccountSenceDo bldAccountSenceDo = buildAccountScene(userId, LoanType.BLD_LOAN.getCode(), bldAmount);
				AfUserAccountSenceDo totalAccountSenceDo = buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), totalAmount);

				afUserAccountSenceService.saveOrUpdateAccountSence(bldAccountSenceDo);
				afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);
				AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.FUND.getCode(), LoanType.BLD_LOAN.getCode(), "Y", new BigDecimal(bldAmount), new Date());
				// 提额成功，记录提额状态
				afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			    } else {
				AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.FUND.getCode(), LoanType.BLD_LOAN.getCode(), "F", BigDecimal.ZERO, new Date());
				// 提额成功，记录提额状态
				afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
			    }
			}
		    }
		} catch (Exception e) {
		    logger.error("raise amount fail =>{}", e.getMessage());
		}
	    }

	    afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.ONLINE.getName(), AuthType.FUND.getCode(), userId);
	    if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.ONLINE.getName(), AuthType.FUND.getCode(), afUserAuthDo.getGmtFund())) {
		// 线上分期提额
		afUserAccountSenceService.raiseOnlineQuato(userId, SceneType.ONLINE.getName(), RiskScene.FUND_ONLINE.getCode(), RiskSceneType.ONLINE.getCode(), AuthType.FUND.getCode());
	    }
	} else {
	    // 更新认证状态为失败
	    afUserAuthDo.setFundStatus("N");
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
