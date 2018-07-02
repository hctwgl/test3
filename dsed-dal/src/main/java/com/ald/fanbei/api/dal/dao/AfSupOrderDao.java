package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfSupOrderDo;
import com.ald.fanbei.api.dal.domain.dto.GameOrderInfoDto;

/**
 * 新人专享Dao
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-22 13:57:29 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSupOrderDao extends BaseDao<AfSupOrderDo, Long> {

    AfSupOrderDo getByOrderNo(String orderNo);

    Integer updateMsgByOrder(@Param("orderNo") String orderNo, @Param("msg") String msg);

    GameOrderInfoDto getOrderInfoByOrderNo(String orderNo);
}
