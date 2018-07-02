package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfRedPacketSelfOpenDo;
import com.ald.fanbei.api.dal.domain.dto.AfRedPacketSelfOpenDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户自拆红包Dao
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRedPacketSelfOpenDao extends BaseDao<AfRedPacketSelfOpenDo, Long> {

    /**
     * 查找拆红包列表
     *
     * @author wangli
     * @date 2018/5/4 10:47
     */
    List<AfRedPacketSelfOpenDto> findOpenRecordList(@Param("redPacketTotalId") Long redPacketTotalId);

    /**
     * 获取已拆红包数量
     *
     * @author wangli
     * @date 2018/5/7 9:54
     */
    int getOpenedNum(@Param("redPacketTotalId") Long redPacketTotalId);
}
