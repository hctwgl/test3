package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.TaskType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


/**
 * 去完成分享任务
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("doShareTaskApi")
public class DoShareTaskApi implements H5Handle {

    @Resource
    AfTaskUserService afTaskUserService;
    @Resource
    AfTaskService afTaskService;


    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        List<AfTaskUserDo> toAddTaskUserList = Lists.newArrayList();
        try {
            String taskCondition = ObjectUtils.toString(context.getData("taskCondition"),null);
            Long userId = context.getUserId();
            // 获取用户能参加的活动
            List<Integer> userLevelList = afTaskService.getUserLevelByUserId(userId);
            List<AfTaskDo> taskList = afTaskService.getTaskListByTaskTypeAndUserLevel(TaskType.share.getCode(), userLevelList, taskCondition);
            if (null != taskList && !taskList.isEmpty()) {
                List<AfTaskDo> notDailyTaskUserList = afTaskService.getNotDailyTaskListByUserId(userId, TaskType.share.getCode());
                List<AfTaskDo> taskUserCompleteList = Lists.newArrayList();
                if (null != notDailyTaskUserList && !notDailyTaskUserList.isEmpty()) {
                    taskUserCompleteList.addAll(notDailyTaskUserList);
                }
                if (!taskUserCompleteList.isEmpty()) {
                    Iterator<AfTaskDo> iter = taskList.iterator();
                    while(iter.hasNext()){
                        if(taskUserCompleteList.contains(iter.next())){
                            iter.remove();
                        }
                    }
                }
                if (!taskList.isEmpty()) {
                    AfTaskUserDo taskUserDo;
                    for (AfTaskDo taskDo : taskList) {
                        taskUserDo = buildTaskUserDo(taskDo, userId);
                        toAddTaskUserList.add(taskUserDo);
                    }
                    for(AfTaskUserDo afTaskUserDo : toAddTaskUserList){
                        afTaskUserDo.setRid(afTaskUserService.insertTaskUserDo(afTaskUserDo));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("taskHandler failed, ", e);
        }
        resp.addResponseData("taskIds",buildTaskUserIds(toAddTaskUserList));
        return resp;
    }

    /**
     * 生成完成任务IDs
     * @param taskUserDoList
     * @return
     */
    public String buildTaskUserIds(List<AfTaskUserDo> taskUserDoList) {
        if(null == taskUserDoList || taskUserDoList.isEmpty()){
            return null;
        }
        StringBuffer idStringBuffer = new StringBuffer();
        for (AfTaskUserDo tempTaskUser : taskUserDoList) {
            idStringBuffer.append(tempTaskUser.getRid()).append(",");
        }
        idStringBuffer.deleteCharAt(idStringBuffer.length() - 1);
        return idStringBuffer.toString();
    }

    /**
     * 构造taskUserDo 对象
     * @param taskDo
     * @param userId
     * @return
     */
    public AfTaskUserDo buildTaskUserDo(AfTaskDo taskDo, Long userId){
        AfTaskUserDo taskUserDo = new AfTaskUserDo();
        int rewardType = taskDo.getRewardType();
        taskUserDo.setRewardType(rewardType);
        if(0 == rewardType){
            taskUserDo.setCoinAmount(taskDo.getCoinAmount());
        } else if(1 == rewardType){
            taskUserDo.setCashAmount(taskDo.getCashAmount());
        } else{
            taskUserDo.setCouponId(taskDo.getCouponId());
        }
        taskUserDo.setTaskId(taskDo.getRid());
        taskUserDo.setTaskName(taskDo.getTaskName());
        taskUserDo.setUserId(userId);
        taskUserDo.setGmtCreate(new Date());
        taskUserDo.setStatus(Constants.TASK_USER_REWARD_STATUS_0);
        return taskUserDo;
    }



}
