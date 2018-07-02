package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfRedPacketHelpOpenDo;
import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;

import java.util.List;

/**
 * 帮拆红包Service
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRedPacketHelpOpenService extends ParentService<AfRedPacketHelpOpenDo, Long>{

    /**
     * 查找拆红包记录
     *
     * @author wangli
     * @date 2018/5/4 9:55
     */
    List<AfRedPacketHelpOpenDo> findOpenRecordList(Long redPacketTotalId, Integer queryNum);

    /**
     * 获取帮拆红包记录（根据openId和userId）
     *
     * @author wangli
     * @date 2018/5/4 15:16
     */
    AfRedPacketHelpOpenDo getHelpOpenRecord(String openId, Long userId);

    /**
     * 判断是否可以帮拆红包
     *
     * @author wangli
     * @date 2018/5/10 17:38
     */
    boolean isCanOpen(AfRedPacketTotalDo shareRedPacket, String openId);

    /**
     * 帮拆红包
     *
     * @author wangli
     * @date 2018/5/10 10:40
     */
    AfRedPacketHelpOpenDo open(String wxCode, Long shareId);
}
