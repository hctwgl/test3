package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfTaskSecType;
import com.ald.fanbei.api.common.enums.AfTaskType;
import com.ald.fanbei.api.dal.domain.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.AfTaskUserDao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 分类运营位配置ServiceImpl
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 16:02:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTaskUserService")
public class AfTaskUserServiceImpl implements AfTaskUserService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTaskUserServiceImpl.class);
   
    @Resource
    private AfTaskUserDao afTaskUserDao;

    @Resource
    private AfTaskService afTaskService;

    @Resource
    private AfGoodsService afGoodsService;

    @Override
	public List<AfTaskUserDo> isDailyTaskList(Long userId, List<Long> list){
    	return afTaskUserDao.isDailyTaskList(userId,list);
	}

	@Override
	public List<AfTaskUserDo> isNotDailyTaskList(Long userId, List<Long> list){
		return afTaskUserDao.isNotDailyTaskList(userId,list);
	}

	@Override
	public int updateNotDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo){
		return afTaskUserDao.updateNotDailyByTaskIdAndUserId(afTaskUserDo);
	}

	@Override
	public int updateDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo){
		return afTaskUserDao.updateDailyByTaskIdAndUserId(afTaskUserDo);
	}

	@Override
	public int insertTaskUserDo(AfTaskUserDo afTaskUserDo){
		return afTaskUserDao.insertTaskUserDo(afTaskUserDo);
	}

	@Override
	public int batchInsertTaskUserDo(List<AfTaskUserDo> taskUserDoList){
		return afTaskUserDao.batchInsertTaskUserDo(taskUserDoList);
	}

	@Override
	public boolean browerAndShoppingHandler(Long userId, Long goodsId, String afTaskType){
		logger.info("afTaskUser start..");
		boolean result = false;
		try {
			// 获取用户能参加的活动
			List<Integer> userLevelList = afTaskService.getUserLevelByUserId(userId);
			List<AfTaskDo> taskList = afTaskService.getTaskListByTaskTypeAndUserLevel(afTaskType, userLevelList, null);

			if(null != taskList && !taskList.isEmpty()) {
				// 获取商品ID、商品品牌ID、商品分类ID
				AfGoodsDo goodsDo = afGoodsService.getGoodsById(goodsId);
				Long brandId = goodsDo.getBrandId();
				Long categoryId = goodsDo.getCategoryId();

				// 非每日更新,用户已完成任务
				List<AfTaskDo> notDailyTaskUserList = afTaskService.getNotDailyTaskListByUserId(userId, afTaskType);

				// 每日更新任务，用户今日完成任务
				List<AfTaskDo> dailyTaskUserList = afTaskService.getDailyTaskListByUserId(userId, afTaskType);

				// 用户完成的任务
				List<AfTaskDo> taskUserCompleteList = Lists.newArrayList();
				if (null != notDailyTaskUserList && !notDailyTaskUserList.isEmpty()) {
					taskUserCompleteList.addAll(notDailyTaskUserList);
				}

				if (null != dailyTaskUserList && !dailyTaskUserList.isEmpty()) {
					taskUserCompleteList.addAll(dailyTaskUserList);
				}

				if (!taskUserCompleteList.isEmpty()) {
					taskList.removeAll(taskUserCompleteList);
				}

				List<AfTaskUserDo> taskUserDoList = Lists.newArrayList();
				AfTaskUserDo taskUserDo;
				if(!taskList.isEmpty()){
					for (AfTaskDo taskDo : taskList) {
						if (StringUtils.equals(AfTaskSecType.CATEGORY.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), goodsId)) {
								taskUserDo = buildTaskUserDo(taskDo, userId);
								taskUserDoList.add(taskUserDo);
							}
						} else if (StringUtils.equals(AfTaskSecType.BRAND.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), brandId)) {
								taskUserDo = buildTaskUserDo(taskDo, userId);
								taskUserDoList.add(taskUserDo);
							}

						} else if (StringUtils.equals(AfTaskSecType.COMMODITY.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), categoryId)) {
								taskUserDo = buildTaskUserDo(taskDo, userId);
								taskUserDoList.add(taskUserDo);
							}
						}
					}

					if (!taskUserDoList.isEmpty()) {
						batchInsertTaskUserDo(taskUserDoList);
						result = true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("afTaskUser failed, ", e);
		}

		logger.info("afTaskUser end..");
		return result;
	}

	@Override
	public boolean taskHandler(Long userId, String afTaskType, String taskCondition) {
		logger.info("afTaskUser start..");
		boolean result = false;
		try {
			// 获取用户能参加的活动
			List<Integer> userLevelList = afTaskService.getUserLevelByUserId(userId);
			List<AfTaskDo> taskList = afTaskService.getTaskListByTaskTypeAndUserLevel(afTaskType, userLevelList, taskCondition);
			if (null != taskList && !taskList.isEmpty()) {
				List<AfTaskDo> notDailyTaskUserList = afTaskService.getNotDailyTaskListByUserId(userId, afTaskType);
				List<AfTaskDo> dailyTaskUserList = null;

				// 实名认证、强风控通过没有每日更新任务
				if (!StringUtils.equals(AfTaskType.VERIFIED.getCode(), afTaskType) || !StringUtils.equals(AfTaskType.STRONG_RISK.getCode(), afTaskType)) {
					dailyTaskUserList = afTaskService.getDailyTaskListByUserId(userId, afTaskType);
				}

				List<AfTaskDo> taskUserCompleteList = Lists.newArrayList();
				if (null != notDailyTaskUserList && !notDailyTaskUserList.isEmpty()) {
					taskUserCompleteList.addAll(notDailyTaskUserList);
				}

				if (null != dailyTaskUserList && !dailyTaskUserList.isEmpty()) {
					taskUserCompleteList.addAll(dailyTaskUserList);
				}

				if (!taskUserCompleteList.isEmpty()) {
					taskList.removeAll(taskUserCompleteList);
				}

				if (!taskList.isEmpty()) {
					AfTaskUserDo taskUserDo;
					List<AfTaskUserDo> taskUserDoList = Lists.newArrayList();
					for (AfTaskDo taskDo : taskList) {
						taskUserDo = buildTaskUserDo(taskDo, userId);
						taskUserDoList.add(taskUserDo);
					}

					if (!taskUserDoList.isEmpty()) {
						batchInsertTaskUserDo(taskUserDoList);
						result = true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("afTaskUser failed, ", e);
		}
		logger.info("afTaskUser end..");
		return result;
	}

	@Override
	public Long getAvailableCoinAmount(Long userId){
    	return afTaskUserDao.getAvailableCoinAmount(userId);
	}

	@Override
	public Long getYestadayChangedCoinAmountList(Long userId) {
		return afTaskUserDao.getYestadayChangedCoinAmountList(userId);
	}

	@Override
	public List<Map<String, Object>> getIncomeOfNearlySevenDays(Long userId){
    	return afTaskUserDao.getIncomeOfNearlySevenDays(userId);
	}

	public List<AfTaskUserDo> getDetailsByUserId(Long userId, Integer rewardType){
		return afTaskUserDao.getDetailsByUserId(userId,rewardType);
	}

	@Override
	public int addTaskUser(Long userId, String taskName, BigDecimal cashAmount){
    	AfTaskUserDo afTaskUserDo = new AfTaskUserDo();
		afTaskUserDo.setTaskName(taskName);
		afTaskUserDo.setStatus(Constants.TASK_USER_REWARD_STATUS_1);
		afTaskUserDo.setTaskId(-1l);
		afTaskUserDo.setUserId(userId);
		afTaskUserDo.setRewardType(1);
		afTaskUserDo.setCashAmount(cashAmount);
		afTaskUserDo.setGmtCreate(new Date());
		afTaskUserDo.setRewardTime(new Date());

		return afTaskUserDao.insertTaskUserDo(afTaskUserDo);
	}

	@Override
	public AfTaskUserDo getYestadayTaskUserDoByTaskName(String taskName) {
		return afTaskUserDao.getYestadayTaskUserDoByTaskName(taskName);
	}

	/**
	 * 是否匹配任务ID
	 * @param taskCondition  任务条件
	 * @param matchedId  要匹配的ID
	 * @return
	 */
	public boolean isMatchedId(String taskCondition, Long matchedId){
		boolean matchFlag = false;
		if(StringUtils.equals(taskCondition, String.valueOf(matchedId))){
			matchFlag = true;
		}

		return matchFlag;
	}

	/**
	 * 构造taskUserDo 对象
	 * @param taskDo
	 * @param userId
	 * @return
	 */
	private AfTaskUserDo buildTaskUserDo(AfTaskDo taskDo, Long userId){
		AfTaskUserDo taskUserDo = new AfTaskUserDo();

        int rewardType = taskDo.getRewardType();
		taskUserDo.setRewardType(rewardType);
        if(0 == rewardType){
            taskUserDo.setCoinAmount(taskDo.getCoinAmount());
        }
        else if(1 == rewardType){
            taskUserDo.setCashAmount(taskDo.getCashAmount());
        }
        else{
            taskUserDo.setCouponId(taskDo.getCouponId());
        }
		taskUserDo.setTaskId(taskDo.getRid());
        taskUserDo.setTaskName(taskDo.getTaskName());
		taskUserDo.setUserId(userId);
		taskUserDo.setGmtCreate(new Date());
		taskUserDo.setStatus(Constants.TASK_USER_REWARD_STATUS_0);

		return taskUserDo;
	}


}