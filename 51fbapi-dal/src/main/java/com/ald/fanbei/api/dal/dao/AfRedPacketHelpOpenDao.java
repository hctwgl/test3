package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfRedPacketHelpOpenDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帮拆红包Dao
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRedPacketHelpOpenDao extends BaseDao<AfRedPacketHelpOpenDo, Long> {

    /**
     * 查找拆红包记录
     *
     * @author wangli
     * @date 2018/5/4 10:03
     */
    List<AfRedPacketHelpOpenDo> findOpenRecordList(@Param("redPacketTotalId") Long redPacketTotalId,
                                                   @Param("queryNum") Integer queryNum);

    /**
     * 获取已拆红包数量
     *
     * @author wangli
     * @date 2018/5/7 11:20
     */
    int getOpenedNum(@Param("redPacketTotalId") Long redPacketTotalId);
}
