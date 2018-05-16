package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfTaskService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfTaskSecType;
import com.ald.fanbei.api.common.enums.AfTaskType;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.dal.domain.dto.AfTaskDto;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTaskUserDao;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.biz.service.AfTaskUserService;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


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
	public List<AfTaskUserDo> getNotDailyTaskListByUserId(Long userId, String afTaskType){
		return afTaskUserDao.getNotDailyTaskListByUserId(userId, afTaskType);
	}

	@Override
	public List<AfTaskUserDo> getDailyTaskListByUserId(Long userId, String afTaskType){
		return afTaskUserDao.getDailyTaskListByUserId(userId, afTaskType);
	}

	@Override
	public int batchInsertTaskUserDo(List<AfTaskUserDo> taskUserDoList){
		return afTaskUserDao.batchInsertTaskUserDo(taskUserDoList);
	}

	@Override
	public void shoppingTaskHandler(AfOrderDo orderInfo, String afTaskType){
		logger.info("afTaskUser start..");
		try {
			// 获取用户能参加的活动
			List<Integer> userLevelList = afTaskService.getUserLevelByUserId(orderInfo.getUserId());
			List<AfTaskDto> taskList = afTaskService.getTaskListByTaskTypeAndUserLevel(afTaskType, userLevelList, null);

			if(null != taskList && !taskList.isEmpty()) {
				// 获取商品ID、商品品牌ID、商品分类ID
				Long goodsId = orderInfo.getGoodsId();
				AfGoodsDo goodsDo = afGoodsService.getGoodsById(goodsId);
				Long brandId = goodsDo.getBrandId();
				Long categoryId = goodsDo.getCategoryId();

				// 非每日更新,用户已完成任务
				List<AfTaskUserDo> notDailyTaskUserList = getNotDailyTaskListByUserId(orderInfo.getUserId(), afTaskType);

				// 每日更新任务，用户今日完成任务
				List<AfTaskUserDo> dailyTaskUserList = getDailyTaskListByUserId(orderInfo.getUserId(), afTaskType);

				// 用户完成的任务
				List<AfTaskUserDo> taskUserCompleteList = Lists.newArrayList();
				if (null != notDailyTaskUserList && !notDailyTaskUserList.isEmpty()) {
					taskUserCompleteList.addAll(notDailyTaskUserList);
				}

				if (null != dailyTaskUserList && !dailyTaskUserList.isEmpty()) {
					taskUserCompleteList.addAll(dailyTaskUserList);
				}

				if (!taskUserCompleteList.isEmpty()) {
					AfTaskDto taskDto;
					Iterator<AfTaskDto> iter = taskList.iterator();
					while(iter.hasNext()){
						taskDto = iter.next();
						for(AfTaskUserDo taskUserDo: taskUserCompleteList){
							if(taskDto.getRid().equals(taskUserDo.getTaskId())){
								iter.remove();
							}
						}
					}
				}

				List<AfTaskUserDo> taskUserDoList = Lists.newArrayList();
				AfTaskUserDo taskUserDo;
				if(!taskList.isEmpty()){
					for (AfTaskDo taskDo : taskList) {
						if (StringUtils.equals(AfTaskSecType.CATEGORY.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), goodsId)) {
								taskUserDo = buildTaskUserDo(taskDo, orderInfo.getUserId());
								taskUserDoList.add(taskUserDo);
							}
						} else if (StringUtils.equals(AfTaskSecType.BRAND.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), brandId)) {
								taskUserDo = buildTaskUserDo(taskDo, orderInfo.getUserId());
								taskUserDoList.add(taskUserDo);
							}

						} else if (StringUtils.equals(AfTaskSecType.COMMODITY.getCode(), taskDo.getTaskSecType())) {
							if (isMatchedId(taskDo.getTaskCondition(), categoryId)) {
								taskUserDo = buildTaskUserDo(taskDo, orderInfo.getUserId());
								taskUserDoList.add(taskUserDo);
							}
						}
					}

					if (!taskUserDoList.isEmpty()) {
						batchInsertTaskUserDo(taskUserDoList);
					}
				}
			}
			logger.info("afTaskUser end..");
		} catch (Exception e) {
			logger.error("afTaskUser failed, ", e);
		}
	}

	@Override
	public void taskHandler(Long userId, String afTaskType, String taskCondition) {
		logger.info("afTaskUser start..");
		try {
			// 获取用户能参加的活动
			List<Integer> userLevelList = afTaskService.getUserLevelByUserId(userId);
			List<AfTaskDto> taskList = afTaskService.getTaskListByTaskTypeAndUserLevel(afTaskType, userLevelList, taskCondition);
			if (null != taskList && !taskList.isEmpty()) {
				List<AfTaskUserDo> notDailyTaskUserList = getNotDailyTaskListByUserId(userId, afTaskType);
				List<AfTaskUserDo> dailyTaskUserList = null;

				// 实名认证、强风控通过没有每日更新任务
				if (StringUtils.equals(AfTaskType.SHARE.getCode(), afTaskType) || StringUtils.equals(AfTaskType.LOAN_MARKET_ACCESS.getCode(), afTaskType)) {
					dailyTaskUserList = getDailyTaskListByUserId(userId, afTaskType);
				}

				List<AfTaskUserDo> taskUserCompleteList = Lists.newArrayList();
				if (null != notDailyTaskUserList && !notDailyTaskUserList.isEmpty()) {
					taskUserCompleteList.addAll(notDailyTaskUserList);
				}

				if (null != dailyTaskUserList && !dailyTaskUserList.isEmpty()) {
					taskUserCompleteList.addAll(dailyTaskUserList);
				}

				if (!taskUserCompleteList.isEmpty()) {
					AfTaskDto taskDto;
					Iterator<AfTaskDto> iter = taskList.iterator();
					while (iter.hasNext()) {
						taskDto = iter.next();
						for (AfTaskUserDo taskUserDo : taskUserCompleteList) {
							if (taskDto.getRid().equals(taskUserDo.getTaskId())) {
								iter.remove();
							}
						}
					}
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
					}
				}
			}
		} catch (Exception e) {
			logger.error("afTaskUser failed, ", e);
		}
		logger.info("afTaskUser end..");
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
		taskUserDo.setUserId(userId);
		taskUserDo.setGmtCreate(new Date());

		return taskUserDo;
	}


}