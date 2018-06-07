package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.TaskSecType;
import com.ald.fanbei.api.common.enums.TaskType;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.AfTaskDao;
import com.ald.fanbei.api.dal.domain.AfTaskDo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * 分类运营位配置ServiceImpl
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 14:44:04
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTaskService")
public class AfTaskServiceImpl  implements AfTaskService {

    private static final Logger logger = LoggerFactory.getLogger(AfTaskServiceImpl.class);

    @Resource
    private AfTaskDao afTaskDao;
    @Resource
    AfTaskUserService afTaskUserService;
    @Resource
    AfTaskBrowseGoodsService afTaskBrowseGoodsService;
    @Resource
    private AfUserAuthService afUserAuthService;

    @Resource
    private AfUserAuthStatusService afUserAuthStatusService;

    @Resource
    private AfOrderService afOrderService;
    @Resource
    BizCacheUtil bizCacheUtil;

    @Override
	public List<AfTaskDto> getTaskListByUserIdAndUserLevel( List<Integer> userLevel){
		return afTaskDao.getTaskListByUserIdAndUserLevel(userLevel);
	}

    @Override
    public AfTaskDo getTaskByTaskId(Long taskId) {
        return afTaskDao.getTaskByTaskId(taskId);
    }

    @Override
    public List<AfTaskDto> getTaskInfo(List<Integer> level, Long userId,String push,HashMap<String,Object> hashMap){
        List<Long> notFinishedList = new ArrayList<Long>();
        List<AfTaskDto> finalTaskList = new ArrayList<AfTaskDto>();
        List<AfTaskDto> taskList = afTaskDao.getTaskListByUserIdAndUserLevel(level);
        //每日任务完成但是未领奖的任务
        List<AfTaskUserDo> isDailyTaskList = new ArrayList<>();
        //非每日任务完成但是未领奖的任务
        List<AfTaskUserDo> isNotDailyTaskList = new ArrayList<>();
        //每日任务完成但是已领奖的任务
        List<AfTaskUserDo> isDailyFinishTaskList = new ArrayList<>();
        //非每日任务完成但是已领奖的任务
        List<AfTaskUserDo> isNotDailyFinishTaskList = new ArrayList<>();
        //每日任务(包括已完成和未领奖)
        List<AfTaskUserDo> dailyTaskLists = afTaskUserService.getIsDailyTaskListByUserId(userId);
        for(AfTaskUserDo task : dailyTaskLists){
            if(StringUtil.equals(task.getStatus().toString(),"0")){
                isDailyTaskList.add(task);
            }else{
                isDailyFinishTaskList.add(task);
            }
        }
        //非每日任务(包括已完成和未领奖)
        List<AfTaskUserDo> nDailyTaskLists = afTaskUserService.getIsNotDailyTaskListByUserId(userId);
        for(AfTaskUserDo task : nDailyTaskLists){
            if(StringUtil.equals(task.getStatus().toString(),"0")){
                isNotDailyTaskList.add(task);
            }else{
                isNotDailyFinishTaskList.add(task);
            }
        }

        //完成但是未领奖的任务
        isDailyTaskList.addAll(isNotDailyTaskList);
        for(AfTaskUserDo taskUserDo : isDailyTaskList){
            notFinishedList.add(taskUserDo.getTaskId());
        }
        //完成但是已领奖的任务
        isDailyFinishTaskList.addAll(isNotDailyFinishTaskList);
        //每日浏览任务(特色处理)
        //若每日浏览任务已完成但是未领奖,则排序到未领奖第一个
        AfTaskUserDo taskUserDo = afTaskUserService.getTodayTaskUserDoByTaskName(Constants.BROWSE_TASK_NAME,userId, null);
        boolean taskBrowseFlag = true;
        if(null != taskUserDo){
            if(StringUtil.equals(taskUserDo.getStatus().toString(),"0")){
                AfTaskDto afTaskDto = new AfTaskDto();
                afTaskDto.setFinishTaskCondition(3);
                afTaskDto.setSumTaskCondition(3);
                afTaskDto.setReceiveReward("N");
                afTaskDto.setIsDailyUpdate(1);
                afTaskDto.setTaskName(Constants.BROWSE_TASK_NAME);
                afTaskDto.setIsDailyUpdate(1);
                finalTaskList.add(afTaskDto);
            }
        }else{
            taskBrowseFlag = false;
        }
        //给完成但是为领奖的任务进行标识处理
        if(notFinishedList.size()>0){
            List<AfTaskDto> afTaskDtos = afTaskDao.getTaskByTaskIds(notFinishedList);
            for (AfTaskDto afTaskDto : afTaskDtos){
                if(StringUtil.equals(afTaskDto.getTaskSecType(), TaskSecType.quantity.getCode())){
                    afTaskDto.setFinishTaskCondition(Integer.parseInt(afTaskDto.getTaskCondition()));
                    afTaskDto.setSumTaskCondition(Integer.parseInt(afTaskDto.getTaskCondition()));
                }else{
                    afTaskDto.setFinishTaskCondition(1);
                    afTaskDto.setSumTaskCondition(1);
                }
                afTaskDto.setReceiveReward("N");
                finalTaskList.add(afTaskDto);
            }
        }
        //若每日浏览任务未完成,则排序到未完成第一个
        if(!taskBrowseFlag){
            int countToday = afTaskBrowseGoodsService.countBrowseGoodsToday(userId);
            AfTaskDto afTaskDto = new AfTaskDto();
            afTaskDto.setFinishTaskCondition(countToday);
            afTaskDto.setSumTaskCondition(3);
            afTaskDto.setIsDailyUpdate(1);
            afTaskDto.setTaskName(Constants.BROWSE_TASK_NAME);
            afTaskDto.setIsDailyUpdate(1);
            finalTaskList.add(afTaskDto);
        }
        //将已完成的任务去重
        for(AfTaskDto afTaskDo : taskList){
            boolean flag = true;
            //用户已完成的任务
            isDailyFinishTaskList.addAll(isDailyTaskList);
            for(AfTaskUserDo afTaskUserDo : isDailyFinishTaskList){
                if(afTaskUserDo.getTaskId() == afTaskDo.getRid()){
                    flag = false;
                    break;
                }
            }
            if(flag){
                if(StringUtil.equals(afTaskDo.getTaskType(), TaskType.verified.getCode())){
                    if(StringUtil.equals(hashMap.get("realnameStatus")+"","Y")){
                        if(StringUtil.equals(afTaskDo.getReceiveReward(),"N")){
                            finalTaskList.add(afTaskDo);
                        }
                        continue;
                    }
                    afTaskDo.setFinishTaskCondition(0);
                    afTaskDo.setSumTaskCondition(1);
                }else if(StringUtil.equals(afTaskDo.getTaskType(), TaskType.strong_risk.getCode())){
                    if (hashMap != null) {
                        if (StringUtils.equals("Y", hashMap.get("status")+"")) {
                            if(StringUtil.equals(afTaskDo.getReceiveReward(),"N")){
                                finalTaskList.add(afTaskDo);
                            }
                            continue;
                        }
                    }
                    afTaskDo.setFinishTaskCondition(0);
                    afTaskDo.setSumTaskCondition(1);
                }else if(StringUtil.equals(afTaskDo.getTaskType(), TaskType.push.getCode())){
                    if(StringUtil.equals(push, "Y")){
                        if(StringUtil.equals(afTaskDo.getReceiveReward(),"N")){
                            finalTaskList.add(afTaskDo);
                        }
                        continue;
                    }
                    afTaskDo.setFinishTaskCondition(0);
                    afTaskDo.setSumTaskCondition(1);
                }else if(StringUtil.equals(afTaskDo.getTaskSecType(), TaskSecType.quantity.getCode())){
                    afTaskDo.setFinishTaskCondition(afOrderService.getSignFinishOrderCount(userId,afTaskDo.getTaskBeginTime()));
                    afTaskDo.setSumTaskCondition(Integer.parseInt(afTaskDo.getTaskCondition()));
                }else {
                    afTaskDo.setFinishTaskCondition(0);
                    afTaskDo.setSumTaskCondition(1);
                }
                finalTaskList.add(afTaskDo);
            }
        }


        return finalTaskList;
    }

    @Override
    public List<AfTaskDo> getNotDailyTaskListByUserId(Long userId, String afTaskType) {
        return afTaskDao.getNotDailyTaskListByUserId(userId, afTaskType);
    }

    @Override
    public List<AfTaskDo> getDailyTaskListByUserId(Long userId, String afTaskType) {
        return afTaskDao.getDailyTaskListByUserId(userId, afTaskType);
    }

    @Override
    public List<Integer> getUserLevelByUserId(Long userId){
        List<Integer> userLevelList = Lists.newArrayList();
        // 所有用户
        userLevelList.add(0);

        // 新注册用户
        AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
        if (userAuthDo != null) {
            if (userAuthDo.getGmtFaces() == null && StringUtil.equals("N", userAuthDo.getBankcardStatus())
                    && userAuthDo.getGmtRealname() == null && StringUtil.equals("N", userAuthDo.getRealnameStatus())
                    && StringUtil.equals("N", userAuthDo.getFacesStatus())) {
                userLevelList.add(1);
            }
        }

        // 用户已购物次数,调用此接口时，已完成一次购物（已收货），所以购物次数需减去1
        int count = afOrderService.getFinishOrderCount(userId);
        count = count - 1;

        AfUserAuthStatusDo authStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId, "ONLINE");
        if (authStatusDo != null) {
            if (StringUtils.equals("Y", authStatusDo.getStatus())) {
                // 消费分期强风控通过用户
                userLevelList.add(2);
                if(0 == count){
                    // 消费分期强风控通过用户并且未购物
                    userLevelList.add(5);
                }
            }
        }

        // 仅购物一次用户
        if(1 == count){
            userLevelList.add(3);
        }
        else if(1 < count){
            userLevelList.add(4);
        }

        return userLevelList;
    }

    @Override
    public List<AfTaskDo> getTaskListByTaskTypeAndUserLevel(String taskType, List<Integer> userLevelList, String taskContition){
        return afTaskDao.getTaskListByTaskTypeAndUserLevel(taskType, userLevelList, taskContition);
    }


    @Override
    public List<AfTaskDto> getTaskByTaskIds(List<Long> taskIds){
        return afTaskDao.getTaskByTaskIds(taskIds);
    }

    @Override
    public List<AfTaskDo> getTaskByTaskDo(AfTaskDo afTaskDo){
        return afTaskDao.getTaskByTaskDo(afTaskDo);
    }

}