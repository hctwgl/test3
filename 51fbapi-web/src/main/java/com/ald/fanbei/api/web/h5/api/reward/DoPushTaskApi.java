package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.AfTaskService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.TaskSecType;
import com.ald.fanbei.api.common.enums.TaskType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 去完成消息推送任务
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("doPushTaskApi")
public class DoPushTaskApi implements H5Handle {

    @Resource
    AfTaskUserService afTaskUserService;
    @Resource
    AfTaskService afTaskService;


    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        try{
            Long userId = context.getUserId();
            AfTaskDo afTaskDo = new AfTaskDo();
            afTaskDo.setTaskType(TaskType.push.getCode());
            afTaskDo.setIsOpen(1);
            List<Long> taskIds = new ArrayList<>();
            List<AfTaskDo> taskDos = afTaskService.getTaskByTaskDo(afTaskDo);
            if(taskDos.size()>0){
                for(AfTaskDo taskDo : taskDos){
                    if(afTaskUserService.getTaskUserByTaskIdAndUserId(taskDo.getRid(),userId) == null){
                        AfTaskUserDo taskUserDo = new AfTaskUserDo();
                        taskUserDo.setGmtCreate(new Date());
                        taskUserDo.setRewardType(taskDo.getRewardType());
                        taskUserDo.setCoinAmount(taskDo.getCoinAmount());
                        taskUserDo.setCashAmount(taskDo.getCashAmount());
                        taskUserDo.setCouponId(taskDo.getCouponId());
                        taskUserDo.setUserId(userId);
                        taskUserDo.setTaskName(taskDo.getTaskName());
                        taskUserDo.setStatus(Constants.TASK_USER_REWARD_STATUS_0);
                        taskUserDo.setTaskId(taskDo.getRid());
                        taskUserDo.setGmtCreate(new Date());
                        taskUserDo.setGmtModified(new Date());
                        afTaskUserService.insertTaskUserDo(taskUserDo);
                        taskIds.add(taskDo.getRid());
                    }
                }
            }
            resp.addResponseData("taskIds",taskIds);
        }catch (Exception e){
            logger.error(" doShareTaskApi error =", e);
        }
        return resp;
    }



}
