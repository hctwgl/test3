package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
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
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTaskDao;
import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.biz.service.AfTaskService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Date;
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
    private AfUserAuthService afUserAuthService;

    @Resource
    private AfUserAuthStatusService afUserAuthStatusService;

    @Resource
    private AfOrderService afOrderService;

    @Override
	public List<AfTaskDto> getTaskListByUserIdAndUserLevel( String userLevel){
		return afTaskDao.getTaskListByUserIdAndUserLevel(userLevel);
	}

    @Override
    public AfTaskDo getTaskByTaskId(Long taskId) {
        return afTaskDao.getTaskByTaskId(taskId);
    }

    @Override
    public List<AfTaskDto> getTaskInfo(String level, Long userId){
        List<AfTaskUserDo> isDailyTaskList = new ArrayList<AfTaskUserDo>();
        List<AfTaskUserDo> isNotDailyTaskList =	new ArrayList<AfTaskUserDo>();
        List<Long> isDailyList = new ArrayList<Long>();
        List<Long> isNotDailyList = new ArrayList<Long>();
        List<Long> finishedList = new ArrayList<Long>();
        List<Long> notFinishedList = new ArrayList<Long>();
        List<AfTaskDto> finalTaskList = new ArrayList<AfTaskDto>();
        AfTaskDto taskDto = new AfTaskDto();
        List<AfTaskDto> taskList = afTaskDao.getTaskListByUserIdAndUserLevel(level);

        for(AfTaskDo afTaskDo : taskList){
            if(afTaskDo.getIsDailyUpdate().equals("1")){
                isDailyList.add(afTaskDo.getRid());
            }else{
                isNotDailyList.add(afTaskDo.getRid());
            }
        }
        if(isDailyList != null){
            isDailyTaskList = afTaskUserService.isDailyTaskList(userId,isDailyList);
        }
        if(isNotDailyList != null){
            isNotDailyTaskList = afTaskUserService.isNotDailyTaskList(userId,isNotDailyList);
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
        for(Long id : notFinishedList){
            for(AfTaskDto afTaskDo : taskList){
                if(id == afTaskDo.getRid()){
                    taskDto.setReceiveReward("N");
                    finalTaskList.add(afTaskDo);
                }
                break;
            }
        }
        for(AfTaskDto afTaskDo : taskList){
            boolean flag = true;
            boolean taskFlag = true;
            for(AfTaskUserDo afTaskUserDo : isDailyTaskList){
                if(afTaskUserDo.getTaskId() == afTaskDo.getRid()
                        || (StringUtil.equals(afTaskDo.getIsOpen().toString(),"1") && StringUtil.equals(afTaskDo.getIsDelete(),"0"))){
                    flag = false;
                }
                break;
            }
            if(StringUtil.equals(afTaskDo.getIsOpen().toString(),"0") || StringUtil.equals(afTaskDo.getIsDelete().toString(),"1")
                    || afTaskDo.getTaskBeginTime().getTime() > new Date().getTime() || afTaskDo.getTaskEndTime().getTime() < new Date().getTime() ){
                taskFlag = false;
            }
            if(flag && taskFlag){
                finalTaskList.add(afTaskDo);
            }
        }

        return finalTaskList;
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

        // 用户已购物次数
        int count = afOrderService.getFinishOrderCount(userId);
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
    public List<AfTaskDto> getTaskListByTaskTypeAndUserLevel(String taskType, List<Integer> userLevelList, String taskContition){
        return afTaskDao.getTaskListByTaskTypeAndUserLevel(taskType, userLevelList, taskContition);
    }

}