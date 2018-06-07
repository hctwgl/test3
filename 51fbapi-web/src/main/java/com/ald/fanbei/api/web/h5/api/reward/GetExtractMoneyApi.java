package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;


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

    @Override
    public H5HandleResponse process(final Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        final Long userId = context.getUserId();
        String key = "GetExtractMoney_lock" + userId;
        if(bizCacheUtil.getObject(key)==null) {
            bizCacheUtil.saveObject(key, new Date(), 5l);
            try {
                final AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("REWARD_PRIZE");
                final String withdrawType = ObjectUtils.toString(context.getData("withdrawType").toString(), null);
                if (withdrawType != null) {
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




}
