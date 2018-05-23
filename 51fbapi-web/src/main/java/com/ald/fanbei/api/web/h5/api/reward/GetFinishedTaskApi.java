package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
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
        AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
        String level = afUserAuthService.signRewardUserLevel(userId,userAuthDo);
        List<Long> taskIds = new ArrayList<Long>();

        if(isDailyList.size()>0){
            isDailyTaskList = afTaskUserService.isDailyFinishTaskList(userId);
        }
        if(isNotDailyList.size()>0){
            isNotDailyTaskList = afTaskUserService.isNotDailyFinishTaskList(userId);
        }
        isDailyTaskList.addAll(isNotDailyTaskList);

        for (AfTaskUserDo afTaskUserDo : isDailyTaskList){
            taskIds.add(afTaskUserDo.getTaskId());
        }

        List<AfTaskDto> finalTaskList = afTaskService.getTaskByTaskIds(taskIds);

        resp.addResponseData("taskList",finalTaskList);
        return resp;
    }



}
