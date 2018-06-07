package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfSignRewardDo;
import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
import com.ald.fanbei.api.dal.domain.dto.AfSignRewardDto;
import com.ald.fanbei.api.dal.domain.query.AfSignRewardQuery;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 分类运营位配置Service
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 13:51:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSignRewardService {

    boolean isExist(Long userId);

    List<AfSignRewardDo> sumSignDays(Long userId,Date startTime);

    List<AfSignRewardDto> getRewardDetailList(AfSignRewardQuery query);

    int saveRecord(AfSignRewardDo afSignRewardDo);

    boolean checkUserSign(Long userId);

    boolean friendUserSign(Long friendUserId);

    int frienddUserSignCount(Long userId,Long friendUserId);

    boolean frienddUserSignCountToDay(Long userId,Long friendUserId);

    Map<String,String> supplementSign(AfSignRewardExtDo afSignRewardExtDo, int num,String status);

    int saveRecordBatch(List<AfSignRewardDo> list);

}
