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

    /**
     * 获取已拆红包数量
     *
     * @author wangli
     * @date 2018/5/7 9:54
     */
    int getOpenedNum(Long redPacketTotalId);

    /**
     * 拆红包
     *
     * @author wangli
     * @date 2018/5/7 10:04
     */
     AfRedPacketSelfOpenDo open(Long userId, String modifier, String sourceType);

     /**
      * 绑定手机号并拆红包
      *
      * @author wangli
      * @date 2018/5/10 11:53
      */
    AfRedPacketSelfOpenDo bindPhoneAndOpen(Long userId, String modifier, String wxCode, String sourceType);

}
