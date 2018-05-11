package com.ald.fanbei.api.web.h5.api.reward;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSignRewardExtService;
import com.ald.fanbei.api.biz.service.AfSignRewardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSignRewardDo;
import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;


/**
 * 分享成功 领取 奖励
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getSignRewardApi")
public class GetSignRewardApi implements H5Handle {

    @Resource
    AfSignRewardService afSignRewardService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    AfSignRewardExtService afSignRewardExtService;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfUserCouponService afUserCouponService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        Integer signType = NumberUtil.objToIntDefault(context.getData("signType"),null);
        Long friendUserId = NumberUtil.objToLongDefault(context.getData("friendUserId"),null);
        AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
        afSignRewardDo.setUserId(context.getUserId());
        afSignRewardDo.setGmtCreate(new Date());
        afSignRewardDo.setGmtModified(new Date());
        afSignRewardDo.setType(signType);
        afSignRewardDo.setStatus(0);
        afSignRewardDo.setFriendUserId(friendUserId);
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("SIGN_COEFFICIENT");
        if(afResourceDo == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
            return new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        if(signType == 0){
            if(afSignRewardService.isExist(afSignRewardDo.getUserId())){
                return new H5HandleResponse(context.getId(), FanbeiExceptionCode.USER_SIGN_EXIST);
            }
            if(userSign(afSignRewardDo,afResourceDo)){
                return new H5HandleResponse(context.getId(), FanbeiExceptionCode.USER_SIGN_FAIL);
            }


        }else if(signType == 1){

        }else if(signType == 2){

        }else {
            resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }

        return resp;
    }

    /**
     * 自己签到
     * @param afSignRewardDo
     * @return
     */
    private boolean userSign(AfSignRewardDo afSignRewardDo, final AfResourceDo afResourceDo){
        boolean flag = afSignRewardService.checkUserSign(afSignRewardDo.getUserId());
        boolean result =true;
        String status = "success";
        if(flag){//多次签到
            //判断是当前周期的第几天
            AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(afSignRewardDo.getUserId());
            int count = signDays(afSignRewardExtDo,0);
            final BigDecimal rewardAmount = new BigDecimal(Math.random() * (Double.parseDouble(afResourceDo.getValue3()) - Double.parseDouble(afResourceDo.getValue4())) + afResourceDo.getValue3()).setScale(2, RoundingMode.HALF_EVEN);
            afSignRewardDo.setAmount(rewardAmount);
            final AfSignRewardDo rewardDo = afSignRewardDo;
            if(count == 3){
                afSignRewardExtDo.setAmount(rewardAmount);
                final AfSignRewardExtDo signRewardExtDo = afSignRewardExtDo;
                status = transactionTemplate.execute(new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus status) {
                        try{
                            afSignRewardService.saveRecord(rewardDo);
                            AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
                            afUserCouponDo.setUserId(rewardDo.getUserId());
                            afUserCouponDo.setCouponId(Long.parseLong(afResourceDo.getValue5()));
                            afUserCouponDo.setGmtCreate(new Date());
                            afUserCouponDo.setGmtModified(new Date());
                            afUserCouponDo.setSourceType("SIGN_REWARD");
                            afUserCouponDo.setSourceRef("SYS");
                            afUserCouponDo.setStatus("NOUSE");
                            afUserCouponService.addUserCoupon(afUserCouponDo);
                            afSignRewardExtService.increaseMoney(signRewardExtDo);
                            return "success";
                        }catch (Exception e){
                            status.setRollbackOnly();
                            return "fail";
                        }
                    }
                });
            }else {
                if(count == 6){
                    afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(2)).setScale(2,RoundingMode.HALF_EVEN));

                }else if(count == 10){
                    afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(3)).setScale(2,RoundingMode.HALF_EVEN));
                }
                final AfSignRewardExtDo signRewardExtDo = afSignRewardExtDo;
                status = transactionTemplate.execute(new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus status) {
                        try{
                            afSignRewardService.saveRecord(rewardDo);
                            afSignRewardExtService.increaseMoney(signRewardExtDo);
                            return "success";
                        }catch (Exception e){
                            status.setRollbackOnly();
                            return "fail";
                        }
                    }
                });
            }
        }else {//第一次签到
            BigDecimal rewardAmount = new BigDecimal(Math.random() * (Double.parseDouble(afResourceDo.getValue1()) - Double.parseDouble(afResourceDo.getValue2())) + afResourceDo.getValue2()).setScale(2, RoundingMode.HALF_EVEN);
            afSignRewardDo.setAmount(rewardAmount);
            final AfSignRewardDo rewardDo = afSignRewardDo;
            status = transactionTemplate.execute(new TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    try{
                        AfSignRewardExtDo afSignRewardExtDo = new AfSignRewardExtDo();
                        afSignRewardExtDo.setUserId(rewardDo.getUserId());
                        afSignRewardExtDo.setGmtModified(new Date());
                        afSignRewardExtDo.setFirstDayParticipation(new Date());
                        afSignRewardExtDo.setAmount(rewardDo.getAmount());
                        afSignRewardService.saveRecord(rewardDo);
                        afSignRewardExtService.updateSignRewardExt(afSignRewardExtDo);
                        return "success";
                    }catch (Exception e){
                        status.setRollbackOnly();
                        return "fail";
                    }
                }
            });
        }
        if(StringUtil.equals(status,"success")){
            result =true;
        }else {
            result =false;
        }
        return result;
    }

    /**
     * 帮签
     * @param afSignRewardDo
     * @return
     */
    private boolean friendSign(AfSignRewardDo afSignRewardDo){

        return true;
    }

    /**
     * 补签
     * @param afSignRewardDo
     * @return
     */
    private boolean supplementSign(AfSignRewardDo afSignRewardDo){
        return true;
    }


    private int signDays(AfSignRewardExtDo afSignRewardExtDo,int num){
        int countDays = 0;
        boolean flag = true;
        Date date = afSignRewardExtDo.getFirstDayParticipation();
        int cycle = afSignRewardExtDo.getCycleDays();
        Date startTime;
        Date endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.formatDateToYYYYMMdd(date));
        while(flag){
            num ++;
            calendar.add(Calendar.DAY_OF_MONTH,(new BigDecimal(num-1).multiply(new BigDecimal(cycle))).intValue());
            startTime = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH,cycle-1);
            endTime = calendar.getTime();
            if((startTime.getTime() <= DateUtil.formatDateToYYYYMMdd(new Date()).getTime()) && (endTime.getTime() >= DateUtil.formatDateToYYYYMMdd(new Date()).getTime())){
                if(startTime.getTime() == DateUtil.formatDateToYYYYMMdd(new Date()).getTime()){
                    afSignRewardExtDo.setFirstDayParticipation(new Date());
                    afSignRewardExtDo.setGmtModified(new Date());
                    afSignRewardExtService.updateSignRewardExt(afSignRewardExtDo);
                }
                flag = false;
                countDays = afSignRewardService.sumSignDays(afSignRewardExtDo.getUserId(),startTime);
            }else{
                signDays(afSignRewardExtDo,num);
            }
        }
        return countDays;
    }

}
