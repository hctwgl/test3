package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.WithdrawType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfSignRewardWithdrawQuery;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 提现
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getExtractMoneyApi")
public class GetExtractMoneyApi implements H5Handle {

    @Resource
    AfSignRewardExtService afSignRewardExtService;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfUserCouponService afUserCouponService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfTaskUserService afTaskUserService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    private BizCacheUtil bizCacheUtil;
    @Resource
    AfCouponService afCouponService;
    @Resource
    AfSignRewardWithdrawService afSignRewardWithdrawService;
    @Resource
    SmsUtil smsUtil;
    ExecutorService pool = Executors.newFixedThreadPool(1);


    @Override
    public H5HandleResponse process(final Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        final Long userId = context.getUserId();
        String key = "GetExtractMoney_lock" + userId;
        if(bizCacheUtil.getObject(key)==null) {
            bizCacheUtil.saveObject(key, new Date(), 5l);
            try {
                final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_PRIZE");
                AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(Constants.SIGN_REWARD_MAX_WITHDRAW);
                final String withdrawType = ObjectUtils.toString(context.getData("withdrawType").toString(), null);

                if (withdrawType != null && null != resourceDo) {
                    BigDecimal todayWithdrawAmount = afSignRewardWithdrawService.getTodayWithdrawAmount();
                    todayWithdrawAmount = (todayWithdrawAmount == null ? new BigDecimal(0) : todayWithdrawAmount);
                    if(StringUtils.isEmpty(resourceDo.getValue())){
                        logger.info("getExtractMoneyApi 没有配置最大可提现金额：{}" + resourceDo.getValue());
                        resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
                        return resp;
                    }
                    else{
                        BigDecimal configedMaxAmount = new BigDecimal(resourceDo.getValue());
                    }

                    logger.info("getExtractMoneyApi todayWithdrawAmount={},configMaxAmount={}", todayWithdrawAmount, resourceDo.getValue());

                    if (todayWithdrawAmount.compareTo(new BigDecimal(resourceDo.getValue())) >= 0) {
                        // 发送预警短信
                        Runnable process = new AysSendSms(todayWithdrawAmount);
                        pool.execute(process);

                        return new H5HandleResponse(context.getId(), FanbeiExceptionCode.WITHDRAW_OVER);
                    }

                    String status = transactionTemplate.execute(new TransactionCallback<String>() {
                        @Override
                        public String doInTransaction(TransactionStatus status) {
                            try {
                                AfSignRewardWithdrawDo afSignRewardWithdrawDo = new AfSignRewardWithdrawDo();
                                afSignRewardWithdrawDo.setGmtCreate(new Date());
                                afSignRewardWithdrawDo.setGmtModified(new Date());
                                BigDecimal amount = BigDecimal.ZERO;
                                if (StringUtil.equals(WithdrawType.ZERO.getCode(), withdrawType)) {
                                    amount = new BigDecimal(afResourceDo.getValue1());
                                    afSignRewardWithdrawDo.setWithdrawType(0);
                                } else if (StringUtil.equals(WithdrawType.ONE.getCode(), withdrawType)) {
                                    amount = new BigDecimal(afResourceDo.getValue2());
                                    afSignRewardWithdrawDo.setWithdrawType(1);
                                } else if (StringUtil.equals(WithdrawType.TWO.getCode(), withdrawType)) {
                                    amount = new BigDecimal(afResourceDo.getValue3());
                                    afSignRewardWithdrawDo.setWithdrawType(2);
                                } else if (StringUtil.equals(WithdrawType.THREE.getCode(), withdrawType)) {
                                    amount = new BigDecimal(afResourceDo.getValue4());
                                    afSignRewardWithdrawDo.setWithdrawType(3);
                                }
                                if (StringUtil.equals(WithdrawType.ZERO.getCode(), withdrawType)) {//送10元无门槛优惠券
                                    AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
                                    AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(afResourceDo.getValue5()));
                                    if (afCouponDo != null) {
                                        if (StringUtil.equals(afCouponDo.getExpiryType(), "D")) {
                                            afUserCouponDo.setGmtStart(new Date());
                                            afUserCouponDo.setGmtEnd(DateUtil.addDays(new Date(), afCouponDo.getValidDays()));
                                        } else if (StringUtil.equals(afCouponDo.getExpiryType(), "R")) {
                                            afUserCouponDo.setGmtStart(afCouponDo.getGmtStart());
                                            afUserCouponDo.setGmtEnd(afCouponDo.getGmtEnd());
                                        }
                                    }
                                    afUserCouponDo.setUserId(userId);
                                    afUserCouponDo.setCouponId(Long.parseLong(afResourceDo.getValue5()));
                                    afUserCouponDo.setGmtCreate(new Date());
                                    afUserCouponDo.setGmtModified(new Date());
                                    afUserCouponDo.setSourceType("SIGN_REWARD");
                                    afUserCouponDo.setSourceRef("SYS");
                                    afUserCouponDo.setStatus("NOUSE");
                                    afUserCouponService.addUserCoupon(afUserCouponDo);
                                } else {//提现到余额
                                    AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
                                    afUserAccountDo.setUserId(userId);
                                    afUserAccountDo.setRebateAmount(amount);
                                    afUserAccountService.updateRebateAmount(afUserAccountDo);

                                    // add by luoxiao for 边逛边赚，增加零钱明细
                                    afTaskUserService.addTaskUser(userId, UserAccountLogType.WITHDRAW_TO_REBATE.getName(), amount);
                                    // end by luoxiao
                                }
                                //除去相应的金额amount
                                afSignRewardExtService.extractMoney(userId, amount);
                                afSignRewardWithdrawDo.setWithdrawAmount(amount);
                                afSignRewardWithdrawDo.setUserId(userId);
                                afSignRewardWithdrawService.saveRecord(afSignRewardWithdrawDo);
                                return "success";
                            } catch (Exception e) {
                                status.setRollbackOnly();
                                return "fail";
                            }
                        }
                    });
                    if (StringUtil.equals(status, "fail")) {
                        resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.WITHDRAW_FAIL);
                    }
                } else {
                    resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.CHOOSE_WITHDRAW_TYPE);
                }
                return resp;
            } finally {
                bizCacheUtil.delCache(key);
            }
        }
        return resp;
    }

    class AysSendSms implements Runnable{
        private BigDecimal todayWithdrawAmount;

        public AysSendSms(BigDecimal todayWithdrawAmount){
            this.todayWithdrawAmount = todayWithdrawAmount;
        }

        @Override
        public void run() {
            logger.info("aysSendSms start..");
            try {
                int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                String smsKey = "sendSignRewardWithdrawWarn" + currentHour;
                Integer warnHour = (Integer) bizCacheUtil.getObject(smsKey);
                if (null == warnHour) {
                    warnHour = currentHour;
                    bizCacheUtil.saveObject(smsKey, warnHour, Constants.SECOND_OF_AN_HOUR_INT);
                    logger.info("aysSendSms start, warnHour={}", warnHour);
                    String[] arg = {"18917116090", "15868156133", "15505719987", "17376569906", "13157183226"};
                    for (String mobile : arg) {
                        smsUtil.sendSignRewardWithdrawWarn(mobile, todayWithdrawAmount);
                    }
                }

            } catch (Exception e) {
                logger.error("aysSendSms error, ", e);
            }
            logger.info("aysSendSms end..");
        }
    }

}


