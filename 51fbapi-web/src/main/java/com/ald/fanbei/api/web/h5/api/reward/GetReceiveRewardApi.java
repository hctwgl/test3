package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
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
import java.util.Calendar;
import java.util.Date;


/**
 * 完成任务领取奖励
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getReceiveRewardApi")
public class GetReceiveRewardApi implements H5Handle {

    @Resource
    AfUserCouponService afUserCouponService;
    @Resource
    AfTaskUserService afTaskUserService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfUserAccountService afUserAccountService;

    @Override
    public H5HandleResponse process(final Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        final String isDailyUpdate = ObjectUtils.toString(context.getData("isDailyUpdate").toString(),null);
        final String taskName = ObjectUtils.toString(context.getData("taskName").toString(),null);
        final Long taskId = NumberUtil.objToLongDefault(context.getData("taskId"),0l);
        final Long userId = context.getUserId();
        Calendar now = Calendar.getInstance();
        AfTaskUserDo afTaskUserDo = new AfTaskUserDo();
        afTaskUserDo.setTaskId(taskId);
        afTaskUserDo.setUserId(userId);
        afTaskUserDo.setGmtModified(new Date());
        afTaskUserDo.setStatus(1);
        afTaskUserDo.setRewardTime(new Date());
        afTaskUserDo.setTaskName(taskName);
        String key = "";
        String status = "";
        if(isDailyUpdate != null){
            if(StringUtil.equals(isDailyUpdate,"1")){//每日任务
                if(StringUtil.equals(taskName, Constants.BROWSE_TASK_NAME)){//每日浏览3个商品任务(特殊处理)
                    key = userId+":"+now.get(Calendar.DAY_OF_MONTH)+":"+Constants.BROWSE_TASK_NAME;
                    //幂等性(防止领取奖励多次领取)
                    if(null == bizCacheUtil.getObject(key)){
                        bizCacheUtil.saveObject(key,new Date(),Constants.SECOND_OF_ONE_DAY);
                        status = getStatus(taskId,userId,taskName,isDailyUpdate);
                    }else {
                        status = "success";
                    }
                }else{
                    key = userId+":"+now.get(Calendar.DAY_OF_MONTH)+":"+taskId;
                    if(null == bizCacheUtil.getObject(key)){
                        bizCacheUtil.saveObject(key,new Date(),Constants.SECOND_OF_ONE_DAY);
                        status = getStatus(taskId,userId,taskName,isDailyUpdate);
                    }else {
                        status = "success";
                    }
                }
            }else if(StringUtil.equals(isDailyUpdate,"0")){//非每日任务
                key = userId+":"+now.get(Calendar.DAY_OF_MONTH)+":"+taskId;
                if(null == bizCacheUtil.getObject(key)){
                    bizCacheUtil.saveObjectForever(key,new Date());
                    status = getStatus(taskId,userId,taskName,isDailyUpdate);
                }else {
                    status = "success";
                }
            }
            if(StringUtil.equals(status,"fail")){
                bizCacheUtil.delCache(key);
                return new H5HandleResponse(context.getId(), FanbeiExceptionCode.RECEIVE_REWARD_FAIL);
            }
        }else{
            return  new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        return resp;
    }


    private String getStatus (final Long taskId,final Long userId,final String taskName,final String isDailyUpdate){
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                    AfTaskUserDo afTaskUserDo = new AfTaskUserDo();
                    afTaskUserDo.setTaskId(taskId);
                    afTaskUserDo.setUserId(userId);
                    afTaskUserDo.setGmtModified(new Date());
                    afTaskUserDo.setStatus(1);
                    afTaskUserDo.setRewardTime(new Date());
                    afTaskUserDo.setTaskName(taskName);
                    if(StringUtil.equals(isDailyUpdate,"1")){
                        afTaskUserService.updateDailyByTaskNameAndUserId(afTaskUserDo);
                    }else{
                        afTaskUserService.updateNotDailyByTaskIdAndUserId(afTaskUserDo);
                    }
                    AfTaskUserDo taskUserDo = new AfTaskUserDo();
                    if(StringUtil.equals(isDailyUpdate,"1")){
                        if(StringUtil.equals(taskName, Constants.BROWSE_TASK_NAME)) {//每日浏览3个商品任务(特殊处理)
                            taskUserDo = afTaskUserService.getTodayTaskUserDoByTaskName(taskName,userId, null);
                        }else {
                            taskUserDo = afTaskUserService.getTodayTaskUserByTaskIdAndUserId(taskId,userId);
                        }
                    }else if(StringUtil.equals(isDailyUpdate,"0")){
                        taskUserDo = afTaskUserService.getTaskUserByTaskIdAndUserId(taskId,userId);
                    }
                    if(taskUserDo.getRewardType() == 1){
                        AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
                        afUserAccountDo.setRebateAmount(taskUserDo.getCashAmount());
                        afUserAccountDo.setUserId(userId);
                        afUserAccountService.updateRebateAmount(afUserAccountDo);
                    }else if(taskUserDo.getRewardType() == 2){
                        AfUserCouponDo afUserCouponDo = new AfUserCouponDo();
                        afUserCouponDo.setUserId(userId);
                        afUserCouponDo.setCouponId(taskUserDo.getCouponId());
                        afUserCouponDo.setGmtCreate(new Date());
                        afUserCouponDo.setGmtModified(new Date());
                        afUserCouponDo.setSourceType("SIGN_REWARD");
                        afUserCouponDo.setSourceRef("SYS");
                        afUserCouponDo.setStatus("NOUSE");
                        afUserCouponService.addUserCoupon(afUserCouponDo);
                    }
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
