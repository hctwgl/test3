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
    AfOrderService afOrderService;
    @Resource
    AfUserAuthStatusService afUserAuthStatusService;
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
        String loyalUsers;
        String ordinaryUser;
        String specialUser;
        String newUser;
        int count = afOrderService.getFinishOrderCount(context.getUserId());
        //是否是忠实用户(count超过二次)
        if(count>1){
            loyalUsers = "Y";
        }else{
            loyalUsers = "N";
        }
        //是否是购物一次用户(count等于1次)
        if(count==1){
            ordinaryUser = "Y";
        }else{
            ordinaryUser = "N";
        }

        //消费分期强风控是否通过用户
        String onLicneStatus = riskOnline(context.getUserId());

        //消费分期强风控是否通过用户而且未购物
        if(StringUtil.equals("Y",onLicneStatus) && count == 0){
            specialUser = "Y";
        }else{
            specialUser = "N";
        }

        //是否是新用户
        if(count>0 || StringUtil.equals("Y",onLicneStatus)){
            newUser = "N";
        }else{
            AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
            if(userAuthDo != null){
                if(userAuthDo.getGmtFaces() == null && StringUtil.equals("N",userAuthDo.getBankcardStatus())
                        && userAuthDo.getGmtRealname() == null && StringUtil.equals("N",userAuthDo.getRealnameStatus())
                        && StringUtil.equals("N",userAuthDo.getFacesStatus()) ){
                    newUser = "Y";
                }else {
                    newUser = "N";
                }
            }else {
                newUser = "N";
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append("'").append("0").append("',");
        if(newUser.equals("Y")){
            sb.append("'").append("1").append("',");
        }
        if(onLicneStatus.equals("Y")){
            sb.append("'").append("2").append("',");
        }
        if(ordinaryUser.equals("Y")){
            sb.append("'").append("3").append("',");
        }
        if(loyalUsers.equals("Y")){
            sb.append("'").append("4").append("',");
        }
        if(specialUser.equals("Y")){
            sb.append("'").append("5").append("',");
        }
        sb.deleteCharAt(sb.length()-1);
        List<AfTaskDto> taskList = afTaskService.getTaskListByUserIdAndUserLevel(sb.toString());
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
            if(StringUtil.isBlank(taskUserDo.getCashAmount().toString()) && StringUtil.isBlank(taskUserDo.getCoinAmount().toString())
                    && StringUtil.isBlank(taskUserDo.getCouponId().toString())){
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


    private String riskOnline(Long userId){
        String flag ;
        AfUserAuthStatusDo authStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,"ONLINE");
        if(authStatusDo != null){
            if(authStatusDo.getStatus().equals("Y")){
                flag = "Y";
            }else{
                flag = "N";
            }
        }else{
            flag = "N";
        }
        return flag;
    }

}
