package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTaskUserDo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 签到领金币 任务
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 16:02:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskUserService {

    List<AfTaskUserDo> isDailyFinishTaskList(Long userId);

    List<AfTaskUserDo> isNotDailyFinishTaskList(Long userId);


    /**
     * 新增
     * @param afTaskUserDo
     * @return
     */
    int insertTaskUserDo(AfTaskUserDo afTaskUserDo);

    /**
     * 批量新增，返回带有主键ID的列表
     * @param taskUserDoList
     * @return
     */
    List<AfTaskUserDo> batchInsertTaskUserDo(List<AfTaskUserDo> taskUserDoList);

    /**
     * 处理浏览/购物等任务
     * @param userId
     * @param goodsId
     * @param afTaskType
     * @return
     */
    List<AfTaskUserDo> browerAndShoppingHandler(Long userId, Long goodsId, String afTaskType);

    /**
     * 处理其他任务，链接类任务
     * @param userId
     * @param afTaskType
     * @param taskCondition
     * @return
     */
    List<AfTaskUserDo> taskHandler(Long userId, String afTaskType, String taskCondition);

    /**
     * 获取可用的金币数量
     * @param userId
     * @return
     */
    Long getAvailableCoinAmount(Long userId);

    /**
     * 获取昨天金币兑换数量
     * @param userId
     * @return
     */
    Long getYestadayChangedCoinAmount(Long userId);

    /**
     * 获取最近7天收益(cashAmount)
     * @param userId
     * @return
     */
    List<Map<String, Object>> getIncomeOfNearlySevenDays(Long userId);



    /**
     * 查询零钱/金币明细
     * @param userId
     * @param rewardType
     * @return
     */
    List<AfTaskUserDo> getDetailsByUserId(Long userId, Integer rewardType);

    /**
     * 增加零钱明细
     * @param userId
     * @param taskName
     * @param cashAmount
     * @return
     */
    int addTaskUser(Long userId, String taskName, BigDecimal cashAmount);

    /**
     * 判断今日是否已经进行过金币兑换任务
     * @param taskName
     * @return
     */
    AfTaskUserDo getTodayTaskUserDoByTaskName(String taskName, Long userId, Integer rewardType);

    /**
     * 判断用户完成的任务类型是否一致
     * -1:没有完成的任务；-2：有多个任务完成；0：金币；1：现金；2：优惠券
     * @param taskUserDoList
     * @return
     */
    Integer isSameRewardType(List<AfTaskUserDo> taskUserDoList);

    /**
     * 主键ID查询用户任务
     * @param taskUserIdList
     * @return
     */
    List<AfTaskUserDo> getTaskUserListByIds(List<Long> taskUserIdList);

    /**
     * 奖励求和
     * @param taskUserDoList
     * @return
     */
    String getRewardAmount(List<AfTaskUserDo> taskUserDoList, int rewardType);

    /**
     * 获取优惠券金额
     * @param couponIdList
     * @return
     */
    BigDecimal getCouponAmountByIds(List<Long> couponIdList);

    /**
     * 批量更新用户状态
     * @return
     */
    int batchUpdateTaskUserStatus(List<Long> taskUserIdList);

    int updateDailyByTaskNameAndUserId(AfTaskUserDo afTaskUserDo);

    // 累计收益
    BigDecimal getAccumulatedIncome(Long userId);

    //获取此任务用户是否完成
    AfTaskUserDo getTaskUserByTaskIdAndUserId(Long taskId,Long userId);
    //获取此任务用户是否完成(每日任务)
    AfTaskUserDo getTodayTaskUserByTaskIdAndUserId(Long taskId,Long userId);
    //获取此用户每日完成的任务(未领奖和已领奖)
    List<AfTaskUserDo> getIsDailyTaskListByUserId(Long userId);
    //获取此用户非每日完成的任务(未领奖和已领奖)
    List<AfTaskUserDo> getIsNotDailyTaskListByUserId(Long userId);

}
