package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;

import java.util.List;

/**
 * 拆红包活动，用户总红包Service
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRedPacketTotalService extends ParentService<AfRedPacketTotalDo, Long>{

    /**
     * 获取实时总提现人数
     *
     * @author wangli
     * @date 2018/5/3 17:46
     */
    int getWithdrawTotalNum();

    /**
     * 获取用户正在拆的红包
     *
     * @author wangli
     * @date 2018/5/3 20:00
     */
    AfRedPacketTotalDo getTheOpening(Long userId, Integer overdueIntervalHour);

    /**
     * 查找用户提现记录
     *
     * @author wangli
     * @date 2018/5/3 20:23
     */
    List<AfRedPacketTotalDo> findWithdrawList(Long userId, Integer queryNum);
}
