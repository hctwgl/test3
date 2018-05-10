package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类运营位配置Dao
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 16:02:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskUserDao  {

    List<AfTaskUserDo> isDailyTaskList(@Param("userId") Long userId, @Param("list")List<Long> list);

    List<AfTaskUserDo> isNotDailyTaskList(@Param("userId")Long userId,@Param("list")List<Long> list);

    int updateNotDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo);

    int updateDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo);

}
