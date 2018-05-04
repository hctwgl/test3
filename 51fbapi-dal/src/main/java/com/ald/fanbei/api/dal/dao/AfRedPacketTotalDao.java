package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 拆红包活动，用户总红包Dao
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRedPacketTotalDao extends BaseDao<AfRedPacketTotalDo, Long> {

    /**
     * 获取实时总提现人数
     *
     * @author wangli
     * @date 2018/5/3 17:49
     */
    int getWithdrawTotalNum();

    /**
     * 获取用户正在拆的红包
     *
     * @author wangli
     * @date 2018/5/3 20:05
     */
    AfRedPacketTotalDo getTheOpening(@Param("userId") Long userId,
                                     @Param("overdueIntervalHour") Integer overdueIntervalHour);

    /**
     * 查找用户提现记录
     *
     * @author wangli
     * @date 2018/5/3 20:24
     */
    List<AfRedPacketTotalDo> findWithdrawList(@Param("userId") Long userId, @Param("queryNum") Integer queryNum);
}
