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
        List<Long> finishedList = new ArrayList<Long>();
        List<Long> notFinishedList = new ArrayList<Long>();
        List<AfTaskDto> finalTaskList = new ArrayList<AfTaskDto>();

        String level = afUserAuthService.signRewardUserLevel(context.getUserId());

        List<AfTaskDto> taskList = afTaskService.getTaskListByUserIdAndUserLevel(level);
        for(AfTaskDo afTaskDo : taskList){
            if(afTaskDo.getIsDailyUpdate().equals("1")){
                isDailyList.add(afTaskDo.getRid());
            }else{
                isNotDailyList.add(afTaskDo.getRid());
            }
        }
        if(isDailyList != null){
            isDailyTaskList = afTaskUserService.isDailyTaskList(context.getUserId(),isDailyList);
        }
        if(isNotDailyList != null){
            isNotDailyTaskList = afTaskUserService.isNotDailyTaskList(context.getUserId(),isNotDailyList);
        }
        isDailyTaskList.addAll(isNotDailyTaskList);
        for(AfTaskUserDo taskUserDo : isDailyTaskList){
            if(StringUtil.equals(taskUserDo.getStatus().toString(),"0")){
                notFinishedList.add(taskUserDo.getTaskId());
            }else{
                finishedList.add(taskUserDo.getTaskId());
            }
        }
        for(Long id : finishedList){
            for(AfTaskDto afTaskDo : taskList){
                if(id == afTaskDo.getRid()){
                    finalTaskList.add(afTaskDo);
                }
                break;
            }
        }
        resp.addResponseData("taskList",finalTaskList);
        return resp;
    }



}
