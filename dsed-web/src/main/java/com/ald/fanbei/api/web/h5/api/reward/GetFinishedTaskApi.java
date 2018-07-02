package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
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
    AfCouponService afCouponService;
    @Resource
    AfTaskUserService afTaskUserService;


    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        List<AfTaskUserDo> isDailyTaskList = new ArrayList<AfTaskUserDo>();
        List<AfTaskUserDo> isNotDailyTaskList =	new ArrayList<AfTaskUserDo>();
        Long userId = context.getUserId();
        List<Long> taskIds = new ArrayList<Long>();
        List<AfTaskDto> finalTaskList = new ArrayList<>();
        //每日任务(完成的)
        isDailyTaskList = afTaskUserService.isDailyFinishTaskList(userId);
        //非每日任务(完成的)
        isNotDailyTaskList = afTaskUserService.isNotDailyFinishTaskList(userId);
        //已完成的任务
        isDailyTaskList.addAll(isNotDailyTaskList);

        for (AfTaskUserDo afTaskUserDo : isDailyTaskList){
            taskIds.add(afTaskUserDo.getTaskId());
        }
        //排除失效的任务
        if(taskIds.size()>0){
            finalTaskList = afTaskService.getTaskByTaskIds(taskIds);
        }
        //每日浏览3个商品任务(特殊处理)
        AfTaskUserDo taskUserDo = afTaskUserService.getTodayTaskUserDoByTaskName(Constants.BROWSE_TASK_NAME,userId, null);
        if(null != taskUserDo){
            if(StringUtil.equals(taskUserDo.getStatus().toString(),"1")){
                AfTaskDto afTaskDto = new AfTaskDto();
                afTaskDto.setTaskName(Constants.BROWSE_TASK_NAME);
                finalTaskList.add(afTaskDto);
            }
        }
        for (AfTaskDto afTaskDto :finalTaskList){
            if(StringUtil.equals(afTaskDto.getRewardType()+"","0")){
                afTaskDto.setRewardName("成功获得"+afTaskDto.getCoinAmount()+"金币");
            }else if(StringUtil.equals(afTaskDto.getRewardType()+"","1")){
                afTaskDto.setRewardName("成功获得"+afTaskDto.getCashAmount()+"元");
            }else if(StringUtil.equals(afTaskDto.getRewardType()+"","2")){
                AfCouponDo afCouponDo = afCouponService.getCouponById(afTaskDto.getCouponId());
                afTaskDto.setRewardName("成功获得"+afCouponDo.getAmount()+"元优惠券");
            }
        }
        resp.addResponseData("taskList",finalTaskList);
        return resp;
    }



}
