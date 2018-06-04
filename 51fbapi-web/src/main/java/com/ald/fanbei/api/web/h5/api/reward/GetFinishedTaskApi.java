package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 展示已完成任务
 * @author cfp
 * @类描述：签到领金币
 */
@NeedLogin
@Component("getFinishedTaskApi")
public class GetFinishedTaskApi implements H5Handle {

    @Resource
    AfTaskService afTaskService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfTaskUserService afTaskUserService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        List<Long> isDailyList = new ArrayList<Long>();
        List<Long> isNotDailyList = new ArrayList<Long>();
        List<AfTaskUserDo> isDailyTaskList = new ArrayList<AfTaskUserDo>();
        List<AfTaskUserDo> isNotDailyTaskList =	new ArrayList<AfTaskUserDo>();
        Long userId = context.getUserId();
        List<Long> taskIds = new ArrayList<Long>();
        List<AfTaskDto> finalTaskList = new ArrayList<>();

        isDailyTaskList = afTaskUserService.isDailyFinishTaskList(userId);
        isNotDailyTaskList = afTaskUserService.isNotDailyFinishTaskList(userId);
        isDailyTaskList.addAll(isNotDailyTaskList);

        for (AfTaskUserDo afTaskUserDo : isDailyTaskList){
            taskIds.add(afTaskUserDo.getTaskId());
        }
        if(taskIds.size()>0){
            finalTaskList = afTaskService.getTaskByTaskIds(taskIds);
        }
        AfTaskUserDo taskUserDo = afTaskUserService.getTodayTaskUserDoByTaskName(Constants.BROWSE_TASK_NAME,userId);
        if(null != taskUserDo){
            if(StringUtil.equals(taskUserDo.getStatus().toString(),"1")){
                AfTaskDto afTaskDto = new AfTaskDto();
                afTaskDto.setTaskName(Constants.BROWSE_TASK_NAME);
                finalTaskList.add(afTaskDto);
            }
        }
        resp.addResponseData("taskList",finalTaskList);
        return resp;
    }



}
