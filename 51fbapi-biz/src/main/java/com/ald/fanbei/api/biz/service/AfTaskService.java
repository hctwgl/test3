package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;

import java.util.List;

/**
 * 分类运营位配置Service
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 14:44:04
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskService {

    List<AfTaskDto> getTaskListByUserIdAndUserLevel(String userLevel);

    AfTaskDo getTaskByTaskId(Long taskId);

    /**
     * 根据用户ID获取用户层级
     * @param userId
     * @return
     */
    List<Integer> getUserLevelByUserId(Long userId);

    /**
     * 根据任务类型与用户层级获取任务集合
     * @param taskType
     * @param userLevelList
     * @return
     */
    List<AfTaskDo> getTaskListByTaskTypeAndUserLevel(String taskType, List<Integer> userLevelList, String taskContition);
    List<AfTaskDto> getTaskInfo(String level, Long userId,String push,AfUserAuthDo userAuthDo,AfUserAuthStatusDo authStatusDo);

    List<AfTaskDo> getNotDailyTaskListByUserId(Long userId, String afTaskType);

    List<AfTaskDo> getDailyTaskListByUserId(Long userId, String afTaskType);

    List<AfTaskDto> getTaskByTaskIds(List<Long> taskIds);

}
