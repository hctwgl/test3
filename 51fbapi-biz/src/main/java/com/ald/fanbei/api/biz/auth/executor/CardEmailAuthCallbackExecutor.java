package com.ald.fanbei.api.biz.auth.executor;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
import com.ald.fanbei.api.biz.bo.RiskQuotaRespBo;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.enums.RiskAuthStatus;
import com.ald.fanbei.api.common.enums.RiskScene;
import com.ald.fanbei.api.common.enums.RiskSceneType;
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

	@Override
	public void execute(AuthCallbackBo authCallbackBo) {

		String consumerNo = authCallbackBo.getConsumerNo();
		Long userId = Long.parseLong(consumerNo);
		AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
		afUserAuthDo.setUserId(userId);
		if (StringUtils.equals(authCallbackBo.getCode(), RiskAuthStatus.SUCCESS.getCode())) {
			RiskQuotaRespBo respBo = riskUtil.userSupplementQuota(ObjectUtils.toString(userId),
					new String[] { RiskScene.CARDMAIL_XJD_PASS.getCode() }, RiskSceneType.XJD.getCode());
			// 提额成功
			if (respBo != null && respBo.isSuccess()) {
				String amount = respBo.getData().getAmount();
				String totalAmount = respBo.getData().getTotalAmount();
				// 更新小贷额度
				AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
				afUserAccountDo.setUserId(userId);
				afUserAccountDo.setAuAmount(new BigDecimal(amount));
				afUserAccountService.updateUserAccount(afUserAccountDo);
				// 更新总额度
				AfUserAccountSenceDo totalAccountSenceDo = buildAccountScene(userId, "LOAN_TOTAL", totalAmount);
				afUserAccountSenceService.updateById(totalAccountSenceDo);
			}
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
