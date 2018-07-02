package com.ald.fanbei.api.biz.auth.executor;


import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
import com.ald.fanbei.api.biz.bo.RiskQuotaRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 冒泡认证回调处理
 * @author
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("bubbleAuthCallbackExecutor")
public class BubbleAuthCallbackExecutor implements Executor {
    private Logger logger = LoggerFactory.getLogger(BubbleAuthCallbackExecutor.class);


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



    @Override
    public void execute(AuthCallbackBo authCallbackBo) {
        logger.info("start bubble auth callback execute");
        String consumerNo = authCallbackBo.getConsumerNo();
        Long userId = Long.parseLong(consumerNo);

        AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
        afUserAuthDo.setUserId(userId);
        if (StringUtils.equals(authCallbackBo.getResult(), RiskAuthStatus.SUCCESS.getCode())) {
            // 首先初始化提额状态
            afAuthRaiseStatusService.initRaiseStatus(userId, AuthType.BUBBLE.getCode());
            // 认证通过，更冒泡认证状态
            afUserAuthDo.setBubbleStatus("Y");
            afUserAuthDo.setRiskStatus("Y");
            afUserAuthDo.setGmtBubble(new Date());
            afUserAuthService.updateUserAuth(afUserAuthDo);
            // 认证成功,向风控发起提额申请
            AfUserAuthDo afUserAuthInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
            String basicStatus = afUserAuthInfo.getBasicStatus();
            // 根据强风控状态判断提额场景
            AfAuthRaiseStatusDo afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.CASH.getName(), AuthType.BUBBLE.getCode(), userId);
            if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.CASH.getName(), AuthType.BUBBLE.getCode(), afUserAuthDo.getGmtBubble())) {
                if (StringUtils.equals("Y", basicStatus)) {
                    RiskQuotaRespBo respBo = riskUtil.userReplenishQuota(ObjectUtils.toString(userId), new String[] { RiskScene.BUBBLE_XJD_PASS.getCode() }, RiskSceneType.XJD.getCode());
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
                            AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.BUBBLE.getCode(), LoanType.CASH.getCode(), "Y", new BigDecimal(amount), new Date());
                            // 提额成功，记录提额状态
                            afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);

                        } else {
                            AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.BUBBLE.getCode(), LoanType.CASH.getCode(), "F", BigDecimal.ZERO, new Date());
                            // 提额失败，记录提额状态
                            afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
                        }
                    }

                } else if (StringUtils.equals("N", basicStatus)) {
                    RiskQuotaRespBo respBo = riskUtil.userReplenishQuota(ObjectUtils.toString(userId), new String[] { RiskScene.BUBBLE_XJD_UNPASS.getCode() }, RiskSceneType.XJD.getCode());
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
                            afUserAccountSenceService.updateById(totalAccountSenceDo);
                            AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.BUBBLE.getCode(), LoanType.CASH.getCode(), "Y", new BigDecimal(amount), new Date());
                            // 提额成功，记录提额状态
                            afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);

                        } else {
                            AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, AuthType.BUBBLE.getCode(), LoanType.CASH.getCode(), "F", BigDecimal.ZERO, new Date());
                            // 提额失败，记录提额状态
                            afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);
                        }
                    }
                }
            }
            afAuthRaiseStatusDo = afAuthRaiseStatusService.getByPrdTypeAndAuthType(SceneType.ONLINE.getName(), AuthType.BUBBLE.getCode(), userId);
            if (afUserAuthService.getAuthRaiseStatus(afAuthRaiseStatusDo, SceneType.ONLINE.getName(), AuthType.BUBBLE.getCode(), afUserAuthDo.getGmtBubble())) {
                // 线上分期提额
                afUserAccountSenceService.raiseOnlineQuatoForBuddle(userId, SceneType.ONLINE.getName(), RiskScene.BUBBLE_ONLINE.getCode(), RiskSceneType.ONLINE.getCode(), AuthType.BUBBLE.getCode());
            }
        } else {
            // 更新认证状态为失败
            afUserAuthDo.setBubbleStatus("N");
            afUserAuthService.updateUserAuth(afUserAuthDo);
        }
    }


}
