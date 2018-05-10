package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.OpenRedPacketHomeBo;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     * 获取红包主页信息（站内）
     *
     * @author wangli
     * @date 2018/5/9 16:47
     */
    OpenRedPacketHomeBo getHomeInfoInSite(FanbeiWebContext context);

    /**
     * 获取红包主页信息（站外）
     *
     * @author wangli
     * @date 2018/5/9 17:26
     */
    OpenRedPacketHomeBo getHomeInfoOutSite(String wxCode, Long shareId);

    /**
     * 获取用户正在拆的红包
     *
     * @author wangli
     * @date 2018/5/3 20:00
     */
    AfRedPacketTotalDo getTheOpening(Long userId, Integer overdueIntervalHour);

    /**
     * 获取用户正在拆的红包，
     * 没有的话就创建一个
     *
     * @author wangli
     * @date 2018/5/10 10:04
     */
    AfRedPacketTotalDo getTheOpeningMust(Long userId, String modifier, Integer overdueIntervalHour);

    /**
     * 查找用户提现记录
     *
     * @author wangli
     * @date 2018/5/3 20:23
     */
    List<AfRedPacketTotalDo> findWithdrawList(Long userId, Integer queryNum);

    /**
     * 查找用户提现记录（拆红包首页展示）
     *
     * @author wangli
     * @date 2018/5/9 17:19
     */
    List<Map<String, String>> findWithdrawListOfHome(Long userId, Integer queryNum);

    /**
     * 查找拆红包记录（拆红包首页展示）
     *
     * @author wangli
     * @date 2018/5/9 17:21
     */
    List<Map<String, String>> findOpenListOfHome(Long id, Integer queryNum);

    /**
     * 红包提现
     *
     * @author wangli
     * @date 2018/5/6 16:07
     */
    void withdraw(Long id, String modifier);

    /**
     * 检查是否能拆红包
     *
     * @author wangli
     * @date 2018/5/10 10:12
     */
    void checkIsCanOpen(String sourceType, AfRedPacketTotalDo theOpening, Integer shareTime);

    /**
     * 判断用户拆完红包后，是否能再拆一个红包
     *
     * @author wangli
     * @date 2018/5/10 10:30
     */
    boolean isCanGainOne(Long id, Integer shareTime);

    /**
     * 更新红包金额
     *
     * @author wangli
     * @date 2018/5/10 10:14
     */
    void updateAmount(AfRedPacketTotalDo theOpening, BigDecimal openAmount, String modifier);

    /**
     * 计算红包提现剩余金额
     *
     * @author wangli
     * @date 2018/5/10 10:21
     */
    BigDecimal calcWithdrawRestAmount(Long id, BigDecimal thresholdAmount);
}
