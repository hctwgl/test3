package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfRedPacketSelfOpenDo;
import com.ald.fanbei.api.dal.domain.dto.AfRedPacketSelfOpenDto;

import java.util.List;

/**
 * Service
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRedPacketSelfOpenService extends ParentService<AfRedPacketSelfOpenDo, Long>{

    /**
     * 查找拆红包记录列表
     *
     * @author wangli
     * @date 2018/5/3 20:58
     */
    List<AfRedPacketSelfOpenDto> findOpenRecordList(Long redPacketTotalId);
}
