package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfSignRewardWithdrawDo;
import com.ald.fanbei.api.dal.domain.dto.AfSignRewardWithdrawDto;
import com.ald.fanbei.api.dal.domain.query.AfSignRewardWithdrawQuery;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分类运营位配置Service
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 14:01:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSignRewardWithdrawService {

    List<AfSignRewardWithdrawDto> getWithdrawList(AfSignRewardWithdrawQuery query);

    int saveRecord(AfSignRewardWithdrawDo afSignRewardWithdrawDo);

    /**
     * 获取今天提交提现申请的总金额
     * @return
     */
    BigDecimal getTodayWithdrawAmount();

}
