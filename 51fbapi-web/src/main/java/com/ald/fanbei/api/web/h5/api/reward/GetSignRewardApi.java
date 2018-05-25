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
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
        if(!userSign(afSignRewardDo,afResourceDo,resp)){
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
        String status = "" ;
        if(flag){//多次签到
            //判断是当前周期的第几天
            AfSignRewardExtDo afSignRewardExtDo = afSignRewardExtService.selectByUserId(afSignRewardDo.getUserId());
            StringBuffer days = afSignRewardService.supplementSign(afSignRewardExtDo,0);
            String str[] = days.toString().split(",");
            int count = 0;
            if(StringUtil.equals(days.toString(),"")){
                count = str.length;
            }else{
                count = str.length+1;
            }
            final BigDecimal rewardAmount = randomNum(afResourceDo.getValue3(),afResourceDo.getValue4());
            afSignRewardDo.setAmount(rewardAmount);
            final AfSignRewardDo rewardDo = afSignRewardDo;
            if(count == 1){
                afSignRewardExtDo.setAmount(rewardAmount);
                final AfSignRewardExtDo signRewardExtDo = afSignRewardExtDo;
                afSignRewardExtDo.setFirstDayParticipation(new Date());
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
            }else {
                sortStr(str);
                int maxCount = maxCount(str);
                Date before = DateUtil.formatDateToYYYYMMdd(afSignRewardExtDo.getFirstDayParticipation());
                Date after = DateUtil.formatDateToYYYYMMdd(new Date());
                days.append(",").append(DateUtil.getNumberOfDatesBetween(before,after)+1);
                String arrayStr[] = days.toString().split(",");
                sortStr(arrayStr);
                int newMaxCount = maxCount(arrayStr);
                if(count >= 5 && count < 7){
                    //给予连续5天的奖励
                    if(maxCount < 5 && newMaxCount == 5){
                       status = fiveOrSevenSignDays(afSignRewardExtDo,rewardAmount,rewardDo,afResourceDo);
                    }
                }else if(count >= 7 && count< 10){
                    //给予连续5天的奖励
                    if(maxCount < 5 && newMaxCount == 5){
                        status = fiveOrSevenSignDays(afSignRewardExtDo,rewardAmount,rewardDo,afResourceDo);
                    }else if(maxCount < 5 && newMaxCount == 7){//给予连续5天和7天的奖励
                        afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(2)).setScale(2,RoundingMode.HALF_EVEN));
                        status = fiveOrSevenSignDays(afSignRewardExtDo,afSignRewardExtDo.getAmount(),rewardDo,afResourceDo);
                    }else if(maxCount >= 5 && newMaxCount == 7){//给予连续7天的奖励
                        afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(2)).setScale(2,RoundingMode.HALF_EVEN));
                        status = tenSignDays(rewardDo,afSignRewardExtDo);
                    }
                }else if(count == 10){
                    //给予连续7天和10天的奖励
                    if(maxCount < 7){
                        afSignRewardExtDo.setAmount(rewardAmount.multiply(new BigDecimal(4)).setScale(2,RoundingMode.HALF_EVEN));
                        status = tenSignDays(rewardDo,afSignRewardExtDo);
                    }
                }else {//给予普通签到的奖励
                    afSignRewardExtDo.setAmount(rewardAmount);
                    status = tenSignDays(rewardDo,afSignRewardExtDo);
                }
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

    public void sortStr(String[] str){
        for (int sx=0; sx<str.length-1; sx++) {
             for (int i=0; i<str.length-1-sx; i++) {
                 if (Integer.parseInt(str[i]) > Integer.parseInt(str[i+1]) ) {
                     // 交换数据
                     String temp = str[i];
                     str[i] = str[i+1];
                     str[i+1] = temp;
                 }
             }
         }
    }


    private  int maxCount(String[] nums) {
        int count = 0;
        int maxCount = 0;
        for (int i = 0; i < nums.length-1; i++) {
            if(Integer.parseInt(nums[i]) == Integer.parseInt(nums[i+1])-1){
                count++;
            }else {
                if(count > maxCount){
                    maxCount = count;
                }
                count = 0;
            }
        }
        if(count > maxCount){
            maxCount = count;
        }
        return maxCount+1;
    }

            /**
             * 随机获取min 与 max 之间的值
             * @param min
             * @param max
             * @return
             */
    private BigDecimal randomNum(String min,String max){
        Double amount = new BigDecimal(Math.random() * (Double.parseDouble(max) - Double.parseDouble(min)) + Double.parseDouble(min)).doubleValue();
        DecimalFormat dFormat=new DecimalFormat("#.00");
        String yearString=dFormat.format(amount);
        Double temp= Double.valueOf(yearString);
        return new BigDecimal(temp);
    }

    /**
     * 连续5天和7天的奖励 或者 连续5天的奖励
     * @param afSignRewardExtDo
     * @param rewardAmount
     * @param rewardDo
     * @param afResourceDo
     * @return
     */
    private String fiveOrSevenSignDays(AfSignRewardExtDo afSignRewardExtDo ,BigDecimal rewardAmount,final AfSignRewardDo rewardDo,final AfResourceDo afResourceDo){
        afSignRewardExtDo.setAmount(rewardAmount);
        final AfSignRewardExtDo signRewardExtDo = afSignRewardExtDo;
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
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
        return status;
    }

    /**
     * 签到7天 10天 或者 普通签到的奖励
     * @param rewardDo
     * @param signRewardExtDo
     * @return
     */
    private String tenSignDays(final AfSignRewardDo rewardDo,final AfSignRewardExtDo signRewardExtDo){
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
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
        return status;
    }


}
