package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类运营位配置Dao
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 14:44:04
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskDao {

    List<AfTaskDto> getTaskListByUserIdAndUserLevel(@Param("userLevel")String userLevel);

    AfTaskDo getTaskByTaskId(@Param("taskId")Long taskId);

    List<AfTaskDto> getTaskListByTaskTypeAndUserLevel(@Param("taskType") String taskType, @Param("userLevelList") List<Integer> userLevelList, @Param("taskContition") String taskContition);

}
