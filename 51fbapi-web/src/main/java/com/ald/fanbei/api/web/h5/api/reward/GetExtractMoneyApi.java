package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    @Override
    public H5HandleResponse process(final Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        final String withdrawType = ObjectUtils.toString(context.getData("withdrawType").toString(),null);
        final Long userId = context.getUserId();
        if(withdrawType != null){
            String status = transactionTemplate.execute(new TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    try{
                        BigDecimal amount = BigDecimal.ZERO;
                        if(StringUtil.equals("0",withdrawType)){
                            amount = new BigDecimal(10);
                        }else if(StringUtil.equals("1",withdrawType)){
                            amount = new BigDecimal(30);
                        }else if(StringUtil.equals("2",withdrawType)){
                            amount = new BigDecimal(50);
                        }else if(StringUtil.equals("3",withdrawType)){
                            amount = new BigDecimal(100);
                        }
                        if(StringUtil.equals("0",withdrawType)){//送10元无门槛优惠券
                            AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
                            afUserCouponDo.setUserId(userId);
                            afUserCouponDo.setCouponId(500l);
                            afUserCouponDo.setGmtCreate(new Date());
                            afUserCouponDo.setGmtModified(new Date());
                            afUserCouponDo.setSourceType("SIGN_REWARD");
                            afUserCouponDo.setSourceRef("SYS");
                            afUserCouponDo.setStatus("NOUSE");
                            afUserCouponService.addUserCoupon(afUserCouponDo);
                        }else{//提现到余额
                            AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
                            afUserAccountDo.setUserId(userId);
                            afUserAccountDo.setRebateAmount(amount);
                            afUserAccountService.updateRebateAmount(afUserAccountDo);
                        }
                        //除去相应的金额amount
                        afSignRewardExtService.extractMoney(userId,amount);
                        return "success";
                    }catch (Exception e){
                        status.setRollbackOnly();
                        return "fail";
                    }
                }
            });
            if(StringUtil.equals(status,"fail")){
                resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.WITHDRAW_FAIL);
            }
        }else {
            resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.CHOOSE_WITHDRAW_TYPE);
        }
        return resp;
    }



}
