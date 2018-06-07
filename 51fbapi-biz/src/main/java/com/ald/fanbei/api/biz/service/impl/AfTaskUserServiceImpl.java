package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfTaskSecType;
import com.ald.fanbei.api.common.enums.AfTaskType;
import com.ald.fanbei.api.common.enums.AfUserCouponStatus;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.AfTaskUserDao;

import java.math.BigDecimal;
import java.util.*;


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
	AfCouponService afCouponService;

    @Resource
    private AfTaskService afTaskService;

    @Resource
	BizCacheUtil bizCacheUtil;

    @Resource
    private AfGoodsService afGoodsService;

    @Resource
    private AfOrderService afOrderService;

    @Resource
    private AfUserCouponService afUserCouponService;

    @Resource
    private AfUserAccountService afUserAccountService;


	@Override
	public List<AfTaskUserDo> isNotDailyFinishTaskList(Long userId){
		return afTaskUserDao.isNotDailyFinishTaskList(userId);
	}

	@Override
	public List<AfTaskUserDo> isDailyFinishTaskList(Long userId){
		return afTaskUserDao.isDailyFinishTaskList(userId);
	}



	@Override
	public int insertTaskUserDo(AfTaskUserDo afTaskUserDo){
		return afTaskUserDao.insertTaskUserDo(afTaskUserDo);
	}

	@Override
	public List<AfTaskUserDo> batchInsertTaskUserDo(List<AfTaskUserDo> taskUserDoList){
    	afTaskUserDao.batchInsertTaskUserDo(taskUserDoList);
    	return taskUserDoList;
	}

	@Override
	public List<AfTaskUserDo> browerAndShoppingHandler(Long userId, Long goodsId, String afTaskType){
		logger.info("browerAndShoppingHandler start..");
		List<AfTaskUserDo> toAddTaskUserList = Lists.newArrayList();
		try {
			// 获取用户能参加的活动
			List<Integer> userLevelList = afTaskService.getUserLevelByUserId(userId);
			logger.info("browerAndShoppingHandler userLevelList:" + JSON.toJSONString(userLevelList));
			List<AfTaskDo> taskList = afTaskService.getTaskListByTaskTypeAndUserLevel(afTaskType, userLevelList, null);
			logger.info("browerAndShoppingHandler taskList0:" + JSON.toJSONString(taskList));

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
					Iterator<AfTaskDo> iter = taskList.iterator();
					while(iter.hasNext()){
						if(taskUserCompleteList.contains(iter.next())){
							iter.remove();
						}
					}
				}

				AfTaskUserDo taskUserDo;
				if(!taskList.isEmpty()){
					for (AfTaskDo taskDo : taskList) {
						if (StringUtils.equals(AfTaskSecType.CATEGORY.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), categoryId)) {
								taskUserDo = buildTaskUserDo(taskDo, userId);
								toAddTaskUserList.add(taskUserDo);
							}
						} else if (StringUtils.equals(AfTaskSecType.BRAND.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), brandId)) {
								taskUserDo = buildTaskUserDo(taskDo, userId);
								toAddTaskUserList.add(taskUserDo);
							}

						} else if (StringUtils.equals(AfTaskSecType.COMMODITY.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), goodsId)) {
								taskUserDo = buildTaskUserDo(taskDo, userId);
								toAddTaskUserList.add(taskUserDo);
							}
						} else if (StringUtils.equals(AfTaskType.SHOPPING.getCode(), taskDo.getTaskType()) && StringUtils.equals(AfTaskSecType.QUANTITY.getCode(), taskDo.getTaskSecType())) {
							// 用户购物数量
							logger.info("browerAndShoppingHandler quantity:" + JSON.toJSONString(taskDo));
							int orderCount = afOrderService.getSignFinishOrderCount(userId,taskDo.getTaskBeginTime());
							logger.info("browerAndShoppingHandler getTaskCondition:" + taskDo.getTaskCondition());
							logger.info("browerAndShoppingHandler orderCount:" + orderCount);
							if (orderCount == Integer.parseInt(taskDo.getTaskCondition())) {
								taskUserDo = buildTaskUserDo(taskDo, userId);
								toAddTaskUserList.add(taskUserDo);
							}
						}
					}

					if (!toAddTaskUserList.isEmpty()) {
						logger.info("browerAndShoppingHandler completeTask:" + JSON.toJSONString(toAddTaskUserList));
						for(AfTaskUserDo afTaskUserDo : toAddTaskUserList){
							insertTaskUserDo(afTaskUserDo);
						}
						return toAddTaskUserList;
					}
				}
			}
		} catch (Exception e) {
			logger.error("browerAndShoppingHandler failed, ", e);
		}

		logger.info("browerAndShoppingHandler end..");
		return toAddTaskUserList;
	}

	@Override
	public List<AfTaskUserDo> taskHandler(Long userId, String afTaskType, String taskCondition) {
		logger.info("taskHandler start..");
		 List<AfTaskUserDo> toAddTaskUserList = Lists.newArrayList();
		try {
			// 获取用户能参加的活动
			List<Integer> userLevelList = afTaskService.getUserLevelByUserId(userId);
			List<AfTaskDo> taskList = afTaskService.getTaskListByTaskTypeAndUserLevel(afTaskType, userLevelList, taskCondition);
			if (null != taskList && !taskList.isEmpty()) {
				List<AfTaskDo> notDailyTaskUserList = afTaskService.getNotDailyTaskListByUserId(userId, afTaskType);
				List<AfTaskDo> dailyTaskUserList = null;

				// 实名认证、强风控提交、消息推送没有每日更新任务
				if (!StringUtils.equals(AfTaskType.VERIFIED.getCode(), afTaskType) || !StringUtils.equals(AfTaskType.STRONG_RISK.getCode(), afTaskType) || !StringUtils.equals(AfTaskType.PUSH.getCode(), afTaskType)) {
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
					Iterator<AfTaskDo> iter = taskList.iterator();
					while(iter.hasNext()){
						if(taskUserCompleteList.contains(iter.next())){
							iter.remove();
						}
					}
				}

				if (!taskList.isEmpty()) {
					AfTaskUserDo taskUserDo;
					for (AfTaskDo taskDo : taskList) {
						taskUserDo = buildTaskUserDo(taskDo, userId);
						toAddTaskUserList.add(taskUserDo);
					}
					for(AfTaskUserDo afTaskUserDo : toAddTaskUserList){
						logger.info("taskHandler completeTaskCount:" + toAddTaskUserList.size());
						insertTaskUserDo(afTaskUserDo);
					}
				}
			}
		} catch (Exception e) {
			logger.error("taskHandler failed, ", e);
		}
		logger.info("taskHandler end..");
		return toAddTaskUserList;
	}

	@Override
	public Long getAvailableCoinAmount(Long userId){
    	return afTaskUserDao.getAvailableCoinAmount(userId);
	}

	@Override
	public Long getYestadayChangedCoinAmount(Long userId) {
		return afTaskUserDao.getYestadayChangedCoinAmount(userId);
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
		afTaskUserDo.setTaskId(-5l);
		afTaskUserDo.setUserId(userId);
		afTaskUserDo.setRewardType(1);
		afTaskUserDo.setCashAmount(cashAmount);
		afTaskUserDo.setGmtCreate(new Date());
		afTaskUserDo.setRewardTime(new Date());

		return afTaskUserDao.insertTaskUserDo(afTaskUserDo);
	}

	@Override
	public AfTaskUserDo getTodayTaskUserDoByTaskName(String taskName, Long userId, Integer rewardType) {
		return afTaskUserDao.getTodayTaskUserDoByTaskName(taskName, userId, rewardType);
	}

	@Override
	public Integer isSameRewardType(List<AfTaskUserDo> taskUserDoList) {
		if(null != taskUserDoList && !taskUserDoList.isEmpty()){
			int rewardType = taskUserDoList.get(0).getRewardType();
			for(AfTaskUserDo taskUserDo: taskUserDoList){
				if(rewardType == taskUserDo.getRewardType().intValue()){
					continue;
				}
				else{
					return -2;
				}
			}

			return rewardType;
		}

		return -1;
	}

	@Override
	public List<AfTaskUserDo> getTaskUserListByIds(List<Long> taskUserIdList) {
		return afTaskUserDao.getTaskUserListByIds(taskUserIdList);
	}

	@Override
	public String getRewardAmount(List<AfTaskUserDo> taskUserDoList, int rewardType) {
		Long coinAmount = 0l;
		BigDecimal cashAmount = BigDecimal.ZERO;
		List<Long> couponIdList = new ArrayList<>();
		String message = "";
		for (AfTaskUserDo taskUserDo : taskUserDoList) {
			if (0 == rewardType) {
				coinAmount += taskUserDo.getCoinAmount();
			} else if (1 == rewardType) {
				cashAmount = cashAmount.add(taskUserDo.getCashAmount());
			} else if (2 == rewardType) {
				couponIdList.add(taskUserDo.getCouponId());
			}
		}

		if(0 == rewardType){
			cashAmount = cashAmount.add(new BigDecimal(coinAmount));
			message = cashAmount.toString()+"金币已入账，继续逛逛能得到更多哦";
		}
		if(2 == rewardType){
			BigDecimal couponAmount = getCouponAmountByIds(couponIdList);
			cashAmount = cashAmount.add(couponAmount == null ? new BigDecimal(0): couponAmount);
			message = cashAmount.toString()+"元优惠券已入账，继续逛逛能得到更多哦";
		}
		if(1 == rewardType){
			message = cashAmount.toString()+"元现金已入账，继续逛逛能得到更多哦";
		}

		return message;
	}

	@Override
	public void saveCouponRewardAndUpdateAccount(List<AfTaskUserDo> taskUserDoList){
		// 余额
		BigDecimal cashAmount = BigDecimal.ZERO;

		// 优惠券
		AfCouponDo couponDo;
		AfUserCouponDo userCouponDo;
		for(AfTaskUserDo taskUserDo: taskUserDoList){
			if(Constants.REWARD_TYPE_CASH == taskUserDo.getRewardType().intValue()){
				cashAmount = cashAmount.add(taskUserDo.getCashAmount());
			}
			else if(Constants.REWARD_TYPE_COUPON == taskUserDo.getRewardType().intValue()){
				couponDo = afCouponService.getCouponById(taskUserDo.getCouponId());
				if(null != couponDo){
					userCouponDo = new AfUserCouponDo();
					userCouponDo.setCouponId(taskUserDo.getCouponId());
					userCouponDo.setUserId(taskUserDo.getUserId());
					userCouponDo.setGmtCreate(new Date());
					userCouponDo.setGmtStart(couponDo.getGmtStart());
					userCouponDo.setGmtEnd(couponDo.getGmtEnd());
					userCouponDo.setStatus(AfUserCouponStatus.NOUSE.getCode());
					userCouponDo.setSourceType(CouponSenceRuleType.TASK_REWARD.getCode());
					afUserCouponService.addUserCoupon(userCouponDo);

					couponDo.setRid(taskUserDo.getCouponId());
					couponDo.setQuotaAlready(1);
					afCouponService.updateCouponquotaAlreadyById(couponDo);
				}
			}
		}

		// 更新余额
		Long userId = taskUserDoList.get(0).getUserId();
		AfUserAccountDo userAccountDo = new AfUserAccountDo();
		userAccountDo.setUserId(userId);
		userAccountDo.setRebateAmount(cashAmount);
		afUserAccountService.updateRebateAmount(userAccountDo);
	}

	@Override
	public BigDecimal getCouponAmountByIds(List<Long> couponIdList) {
		return afTaskUserDao.getCouponAmountByIds(couponIdList);
	}

	@Override
	public int batchUpdateTaskUserStatus(List<Long> taskUserIdList) {
		return afTaskUserDao.batchUpdateTaskUserStatus(taskUserIdList);
	}

	@Override
	public int updateDailyByTaskNameAndUserId(AfTaskUserDo afTaskUserDo){
		return afTaskUserDao.updateDailyByTaskNameAndUserId(afTaskUserDo);
	}

	@Override
	public int updateNotDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo){
		return afTaskUserDao.updateNotDailyByTaskIdAndUserId(afTaskUserDo);
	}

	@Override
	public BigDecimal getAccumulatedIncome(Long userId) {
		return afTaskUserDao.getAccumulatedIncome(userId);
	}

	@Override
	public AfTaskUserDo getTaskUserByTaskIdAndUserId(Long taskId,Long userId){
		return afTaskUserDao.getTaskUserByTaskIdAndUserId(taskId,userId);
	}

	@Override
	public AfTaskUserDo getTodayTaskUserByTaskIdAndUserId(Long taskId,Long userId){
		return afTaskUserDao.getTodayTaskUserByTaskIdAndUserId(taskId,userId);
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

	//获取此用户每日完成的任务(未领奖和已领奖)
	public List<AfTaskUserDo> getIsDailyTaskListByUserId(Long userId){
		return afTaskUserDao.getIsDailyTaskListByUserId(userId);
	}
	//获取此用户非每日完成的任务(未领奖和已领奖)
	public List<AfTaskUserDo> getIsNotDailyTaskListByUserId(Long userId){
		return afTaskUserDao.getIsNotDailyTaskListByUserId(userId);
	}


}