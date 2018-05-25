package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
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
    AfTaskService afTaskService;
    @Resource
    AfTaskUserService afTaskUserService;

    @Override
    public H5HandleResponse process(final Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        String isDailyUpdate = ObjectUtils.toString(context.getData("isDailyUpdate").toString(),null);
        String taskName = ObjectUtils.toString(context.getData("taskName").toString(),null);
        Long taskId = Long.parseLong(context.getData("taskId").toString());
//        Long taskId = NumberUtil.objToLongDefault(context.getData("taskId"),0l);
        Long userId = context.getUserId();
        int count = 0;
        if(!StringUtil.equals(taskName, Constants.BROWSE_TASK_NAME)){
            AfTaskDo afTaskDo = afTaskService.getTaskByTaskId(taskId);
            if(null == afTaskDo){
                return new H5HandleResponse(context.getId(), FanbeiExceptionCode.TASK_NOT_EXIST);
            }
        }
        AfTaskUserDo afTaskUserDo = new AfTaskUserDo();
        afTaskUserDo.setTaskId(taskId);
        afTaskUserDo.setUserId(userId);
        afTaskUserDo.setGmtModified(new Date());
        afTaskUserDo.setStatus(1);
        afTaskUserDo.setRewardTime(new Date());
        if(isDailyUpdate != null){
            if(StringUtil.equals(isDailyUpdate,"1")){//每日任务
                count = afTaskUserService.updateDailyByTaskIdAndUserId(afTaskUserDo);
            }else if(StringUtil.equals(isDailyUpdate,"0")){//非每日任务
                count = afTaskUserService.updateNotDailyByTaskIdAndUserId(afTaskUserDo);
            }
            if(count<1){
                return new H5HandleResponse(context.getId(), FanbeiExceptionCode.RECEIVE_REWARD_FAIL);
            }
        }else{
            return  new H5HandleResponse(context.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        return resp;
    }



}
