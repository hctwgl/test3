package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTaskDao;
import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.biz.service.AfTaskService;

import java.util.ArrayList;
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

    @Override
	public List<AfTaskDto> getTaskListByUserIdAndUserLevel( String userLevel){
		return afTaskDao.getTaskListByUserIdAndUserLevel(userLevel);
	}

	@Override
    public AfTaskDo getTaskByTaskId(Long taskId){
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

}