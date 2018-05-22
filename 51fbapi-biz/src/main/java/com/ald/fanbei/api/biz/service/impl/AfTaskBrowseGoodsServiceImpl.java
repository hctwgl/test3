package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTaskBrowseGoodsDaysService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfTaskBrowseGoodsDaysDo;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.AfTaskBrowseGoodsDao;
import com.ald.fanbei.api.dal.domain.AfTaskBrowseGoodsDo;
import com.ald.fanbei.api.biz.service.AfTaskBrowseGoodsService;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;


/**
 * 每日浏览商品数量的任务ServiceImpl
 * 
 * @author luoxiao
 * @version 1.0.0 初始化
 * @date 2018-05-16 20:02:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTaskBrowseGoodsService")
public class AfTaskBrowseGoodsServiceImpl implements AfTaskBrowseGoodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTaskBrowseGoodsServiceImpl.class);
   
    @Resource
    private AfTaskBrowseGoodsDao afTaskBrowseGoodsDao;

    @Resource
    private AfResourceService afResourceService;

    @Resource
    AfTaskUserService afTaskUserService;

    @Resource
    AfTaskBrowseGoodsDaysService afTaskBrowseGoodsDaysService;

    @Resource
    TransactionTemplate transactionTemplate;

    @Override
    public AfTaskBrowseGoodsDo isExisted(Long userId, Long goodsId) {
        return afTaskBrowseGoodsDao.isExisted(userId, goodsId);
    }

    @Override
    public int countBrowseGoodsToday(Long userId) {
        return afTaskBrowseGoodsDao.countBrowseGoodsToday(userId);
    }

    @Override
    public int addBrowseGoodsTaskUser(Long userId, Long goodsId) {
        AfTaskBrowseGoodsDo afTaskBrowseGoodsDo = new AfTaskBrowseGoodsDo();
        afTaskBrowseGoodsDo.setUserId(userId);
        afTaskBrowseGoodsDo.setGmtCreate(new Date());
        afTaskBrowseGoodsDo.setGoodsId(goodsId);
        return afTaskBrowseGoodsDao.addBrowseGoodsTaskUser(afTaskBrowseGoodsDo);
    }

    @Override
    public Long addBrowseGoodsTaskUserRecord(final Long userId, final Long goodsId){
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                try{
                    // 当前商品没有浏览过
                    if(null == isExisted(userId, goodsId)){
                        AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(Constants.BROWSE_TASK);
                        String value = resourceDo.getValue();
                        Integer setCount = Integer.parseInt(value);

                        int countToday = countBrowseGoodsToday(userId);
                        // 增加浏览记录
                        if(setCount > countToday){
                            addBrowseGoodsTaskUser(userId, goodsId);
                            countToday = countToday + 1;
                        }

                        // 今日任务完成
                        if(setCount == countToday){
                            String value1 = resourceDo.getValue1();
                            String[] coinAmountArray = value1.split(",");
                            AfTaskBrowseGoodsDaysDo taskBrowseGoodsDaysDo;

                            // 获取奖励的金币数量
                            int continueDays = 0;
                            Long coinAmount = Long.parseLong(coinAmountArray[continueDays]);
                            taskBrowseGoodsDaysDo = afTaskBrowseGoodsDaysService.isCompletedTaskYestaday(userId);
                            if(null != taskBrowseGoodsDaysDo){
                                continueDays = taskBrowseGoodsDaysDo.getContinueDays();
                                String value2 = resourceDo.getValue2();
                                Integer setCoutinueDays = Integer.parseInt(value2);
                                if(setCoutinueDays < continueDays){
                                    coinAmount = Long.parseLong(coinAmountArray[continueDays - 1]);
                                }
                                else{
                                    coinAmount = Long.parseLong(coinAmountArray[continueDays]);
                                }
                            }

                            // 判断用户是否已经参加过任务活动
                            if(null == afTaskBrowseGoodsDaysService.isUserAttend(userId)){
                                taskBrowseGoodsDaysDo = new AfTaskBrowseGoodsDaysDo();
                                taskBrowseGoodsDaysDo.setContinueDays(continueDays + 1);
                                taskBrowseGoodsDaysDo.setGmtCreate(new Date());
                                taskBrowseGoodsDaysDo.setLastCompleteTaskDate(new Date());
                                taskBrowseGoodsDaysDo.setUserId(userId);
                                afTaskBrowseGoodsDaysService.addTaskBrowseGoodsDays(taskBrowseGoodsDaysDo);
                            }
                            else{
                                afTaskBrowseGoodsDaysService.updateTaskBrowseGoodsDays(userId, continueDays + 1);
                            }

                            AfTaskUserDo taskUserDo = new AfTaskUserDo();
                            taskUserDo.setGmtCreate(new Date());
                            taskUserDo.setRewardType(0);
                            taskUserDo.setCoinAmount(coinAmount);
                            taskUserDo.setUserId(userId);
                            taskUserDo.setUserId(-999l);
                            taskUserDo.setTaskName(Constants.BROWSE_TASK_NAME);
                            taskUserDo.setStatus(Constants.TASK_USER_REWARD_STATUS_0);
                            afTaskUserService.insertTaskUserDo(taskUserDo);

                            return coinAmount;
                        }
                    }
                }catch(Exception e){
                    logger.error("addBrowseGoodsTaskUserRecord error, ", e);
                    status.setRollbackOnly();
                }

                return null;
            }
        });
    }
}