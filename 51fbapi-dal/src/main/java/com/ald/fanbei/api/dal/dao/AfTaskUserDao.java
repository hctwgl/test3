package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 分类运营位配置Dao
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 16:02:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskUserDao  {


    List<AfTaskUserDo> isDailyFinishTaskList(@Param("userId")Long userId);

    List<AfTaskUserDo> isNotDailyFinishTaskList(@Param("userId")Long userId);


    int insertTaskUserDo(AfTaskUserDo afTaskUserDo);

    int batchInsertTaskUserDo(List<AfTaskUserDo> taskUserDoList);

    Long getYestadayChangedCoinAmount(@Param("userId") Long userId);

    Long getAvailableCoinAmount(@Param("userId") Long userId);

    List<Map<String, Object>> getIncomeOfNearlySevenDays(@Param("userId") Long userId);


    List<AfTaskUserDo> getDetailsByUserId(@Param("userId") Long userId, @Param("rewardType") Integer rewardType);

    AfTaskUserDo getTodayTaskUserDoByTaskName(@Param("taskName") String taskName, @Param("userId") Long userId, @Param("rewardType") Integer rewardType);

    List<AfTaskUserDo> getTaskUserListByIds(@Param("taskUserIdList") List<Long> taskUserIdList);

    int getCouponAmountByIds(@Param("couponIdList") List<Long> couponIdList);

    int batchUpdateTaskUserStatus(@Param("taskUserIdList") List<Long> taskUserIdList);

    int updateDailyByTaskNameAndUserId(AfTaskUserDo afTaskUserDo);

    int updateNotDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo);

    BigDecimal getAccumulatedIncome(Long userId);

    AfTaskUserDo getTaskUserByTaskIdAndUserId(@Param("taskId")Long taskId,@Param("userId")Long userId);

    AfTaskUserDo getTodayTaskUserByTaskIdAndUserId(@Param("taskId")Long taskId,@Param("userId")Long userId);


    //获取此用户每日完成的任务(未领奖和已领奖)
    List<AfTaskUserDo> getIsDailyTaskListByUserId(@Param("userId")Long userId);
    //获取此用户非每日完成的任务(未领奖和已领奖)
    List<AfTaskUserDo> getIsNotDailyTaskListByUserId(@Param("userId")Long userId);
}
