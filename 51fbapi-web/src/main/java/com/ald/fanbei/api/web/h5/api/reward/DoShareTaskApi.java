package com.ald.fanbei.api.web.h5.api.reward;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.TaskSecType;
import com.ald.fanbei.api.common.enums.TaskType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
        try{
            String taskCondition = ObjectUtils.toString(context.getData("taskCondition"),null);
            Long userId = context.getUserId();
            List<Long> taskIds = new ArrayList<>();
            AfTaskDo afTaskDo = new AfTaskDo();
            afTaskDo.setTaskCondition(taskCondition);
            afTaskDo.setTaskType(TaskType.share.getCode());
            afTaskDo.setTaskSecType(TaskSecType.activity.getCode());
            afTaskDo.setIsOpen(1);
            List<AfTaskDo> taskDos = afTaskService.getTaskByTaskDo(afTaskDo);
            if(taskDos.size()>0){
                for(AfTaskDo taskDo : taskDos){
                    if(afTaskUserService.getTaskUserByTaskIdAndUserId(taskDo.getRid(),userId) == null){
                        AfTaskUserDo taskUserDo = new AfTaskUserDo();
                        taskUserDo.setGmtCreate(new Date());
                        taskUserDo.setRewardType(afTaskDo.getRewardType());
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
