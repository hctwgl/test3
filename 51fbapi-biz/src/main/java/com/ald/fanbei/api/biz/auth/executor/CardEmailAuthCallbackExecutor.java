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

/**
 * 信用卡认证回调处理类
 * 
 * @author rongbo
 *
 */
@Component("cardEmailAuthCallbackExecutor")
public class CardEmailAuthCallbackExecutor implements Executor {

    @Resource
    RiskUtil riskUtil;

    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    AfUserAuthService afUserAuthService;

    @Resource
    AfAuthRaiseStatusService afAuthRaiseStatusService;

    Logger logger = LoggerFactory.getLogger(CardEmailAuthCallbackExecutor.class);

    @Override
    public void execute(AuthCallbackBo authCallbackBo) {
	logger.info("start card mail auth callback execute");
	String consumerNo = authCallbackBo.getConsumerNo();
	Long userId = Long.parseLong(consumerNo);
	AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
	afUserAuthDo.setUserId(userId);
	if (StringUtils.equals(authCallbackBo.getResult(), RiskAuthStatus.SUCCESS.getCode())) {
	    // 初始化提额状态
	    afAuthRaiseStatusService.initCreditRaiseStatus(userId, AuthType.CARDEMAIL.getCode());
	    afUserAuthDo.setCreditStatus("Y");
	    afUserAuthDo.setRiskStatus("Y");
	    afUserAuthDo.setGmtCredit(new Date());
	    afUserAuthService.updateUserAuth(afUserAuthDo);

	    AfAuthRaiseStatusDo afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.CASH.getName(), AuthType.CARDEMAIL.getCode(), userId);
	    if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.CASH.getName(), AuthType.CARDEMAIL.getCode(), afUserAuthDo.getGmtCredit())) {
		RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId), new String[] { RiskScene.CARDMAIL_XJD_PASS.getCode() }, RiskSceneType.XJD.getCode());
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
			AfUserAccountSenceDo totalAccountSenceDo = buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), totalAmount);
			afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);
			AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.CARDEMAIL.getCode(), LoanType.CASH.getCode(), "Y", new BigDecimal(amount), new Date());
			// 提额成功，记录提额状态
			afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);

		    } else {
			AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.CARDEMAIL.getCode(), LoanType.CASH.getCode(), "F", BigDecimal.ZERO, new Date());
			// 提额失败，记录提额状态
			afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
		    }
		}
	    }

	    afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.ONLINE.getName(), AuthType.CARDEMAIL.getCode(), userId);
	    if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.ONLINE.getName(), AuthType.CARDEMAIL.getCode(), afUserAuthDo.getGmtCredit())) {
		// 线上分期提额
		afUserAccountSenceService.raiseOnlineQuato(userId, SceneType.ONLINE.getName(), RiskScene.CARDMAIL_ONLINE.getCode(), RiskSceneType.ONLINE.getCode(), AuthType.CARDEMAIL.getCode());
	    }
	} else {
	    afUserAuthDo.setCreditStatus("N");
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
