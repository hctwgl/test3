package com.ald.fanbei.api.web.h5.api.reward;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
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
        AfSignRewardDo afSignRewardDo = new AfSignRewardDo();
        afSignRewardDo.setIsDelete(0);
        afSignRewardDo.setUserId(context.getUserId());
        afSignRewardDo.setGmtCreate(new Date());
        afSignRewardDo.setGmtModified(new Date());
        afSignRewardDo.setType(SignRewardType.ZERO.getCode());
        afSignRewardDo.setStatus(0);
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("SIGN_COEFFICIENT");
        if(afResourceDo == null || numberWordFormat.isNumeric(afResourceDo.getValue())){
            return new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        if(afSignRewardService.isExist(afSignRewardDo.getUserId())){
            return new H5HandleResponse(context.getId(), FanbeiExceptionCode.USER_SIGN_EXIST);
        }
        if(userSign(afSignRewardDo,afResourceDo,resp)){
            return new H5HandleResponse(context.getId(), FanbeiExceptionCode.USER_SIGN_FAIL);
        }
        return resp;
    }

    /**
     * 自己签到
     * @param afSignRewardDo
     * @return
     */
    private boolean userSign(AfSignRewardDo afSignRewardDo, final AfResourceDo afResourceDo,H5HandleResponse resp){
        boolean flag = afSignRewardService.checkUserSign(afSignRewardDo.getUserId());
        boolean result;
        String status ;
        if(flag){//多次签到
            //判断是当前周期的第几天
            AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(afSignRewardDo.getUserId());
            final int count = signDays(afSignRewardExtDo,0);
            final BigDecimal rewardAmount = randomNum(afResourceDo.getValue3(),afResourceDo.getValue4());
            afSignRewardDo.setAmount(rewardAmount);
            final AfSignRewardDo rewardDo = afSignRewardDo;
            if(count == 3 || count == 1){
                afSignRewardExtDo.setAmount(rewardAmount);
                final AfSignRewardExtDo signRewardExtDo = afSignRewardExtDo;
                if(count == 1){
                    afSignRewardExtDo.setFirstDayParticipation(new Date());
                }
                status = transactionTemplate.execute(new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus status) {
                        try{
                            afSignRewardService.saveRecord(rewardDo);
                            if(count == 3){
                                AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
                                afUserCouponDo.setUserId(rewardDo.getUserId());
                                afUserCouponDo.setCouponId(Long.parseLong(afResourceDo.getValue5()));
                                afUserCouponDo.setGmtCreate(new Date());
                                afUserCouponDo.setGmtModified(new Date());
                                afUserCouponDo.setSourceType("SIGN_REWARD");
                                afUserCouponDo.setSourceRef("SYS");
                                afUserCouponDo.setStatus("NOUSE");
                                afUserCouponService.addUserCoupon(afUserCouponDo);
                            }
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
            resp.addResponseData("amount",afSignRewardExtDo.getAmount());
        }else {//第一次签到
            BigDecimal rewardAmount = randomNum(afResourceDo.getValue1(),afResourceDo.getValue2());
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
            resp.addResponseData("amount",rewardAmount);
        }

        if(StringUtil.equals(status,"success")){
            result =true;
        }else {
            result =false;
        }
        return result;
    }


    /**
     * 得到这一期签到的天数
     * @param afSignRewardExtDo
     * @param num
     * @return
     */
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


    /**
     * 随机获取min 与 max 之间的值
     * @param min
     * @param max
     * @return
     */
    private BigDecimal randomNum(String min,String max){
        BigDecimal rewardAmount = new BigDecimal(Math.random() * (Double.parseDouble(max) - Double.parseDouble(min)) + min).setScale(2, RoundingMode.HALF_EVEN);
        return rewardAmount;

    }

}
