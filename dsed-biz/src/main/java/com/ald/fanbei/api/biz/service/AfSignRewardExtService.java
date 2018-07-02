package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 分类运营位配置Service
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 14:01:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSignRewardExtService {

    AfSignRewardExtDo selectByUserId(Long userId);

    int extractMoney(Long userId, BigDecimal amount);

    int updateSignRewardExt(AfSignRewardExtDo afSignRewardExtDo);

    int updateSignRemind(AfSignRewardExtDo afSignRewardExtDo);

    int increaseMoney(AfSignRewardExtDo afSignRewardExtDo);

    int saveRecord(AfSignRewardExtDo afSignRewardExtDo);

    Map<String,Object> getHomeInfo(Long userId,String status );

    List<AfSignRewardExtDo> selectByUserIds(List<Long> userIds);

    int saveRecordBatch(List<AfSignRewardExtDo> list );

    int increaseMoneyBtach(List<AfSignRewardExtDo> list );




}
