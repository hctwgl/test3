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

    List<AfTaskUserDo> isDailyTaskList(@Param("userId") Long userId);

    List<AfTaskUserDo> isNotDailyTaskList(@Param("userId")Long userId);

    List<AfTaskUserDo> isDailyFinishTaskList(@Param("userId")Long userId);

    List<AfTaskUserDo> isNotDailyFinishTaskList(@Param("userId")Long userId);

    int updateNotDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo);

    int updateDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo);

    int insertTaskUserDo(AfTaskUserDo afTaskUserDo);

    int batchInsertTaskUserDo(List<AfTaskUserDo> taskUserDoList);

    Long getYestadayChangedCoinAmount(@Param("userId") Long userId);

    Long getAvailableCoinAmount(@Param("userId") Long userId);

    List<Map<String, Object>> getIncomeOfNearlySevenDays(@Param("userId") Long userId);

    List<Map<String, Object>> getIncomeCoinOfNearlySevenDays(@Param("userId")Long userId);

    List<AfTaskUserDo> getDetailsByUserId(@Param("userId") Long userId, @Param("rewardType") Integer rewardType);

    AfTaskUserDo getTodayTaskUserDoByTaskName(@Param("taskName") String taskName, @Param("userId") Long userId);

    List<AfTaskUserDo> getTaskUserListByIds(@Param("taskUserIdList") List<Long> taskUserIdList);

    BigDecimal getCouponAmountByIds(@Param("couponIdList") List<Long> couponIdList);

    int batchUpdateTaskUserStatus(@Param("taskUserIdList") List<Long> taskUserIdList);

    int updateDailyByTaskNameAndUserId(AfTaskUserDo afTaskUserDo);

    BigDecimal getAccumulatedIncome(Long userId);

}
