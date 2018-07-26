package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.DsedRetryTemplDo;

/**
 * 重试模板Dao
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-16 16:28:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedRetryTemplDao extends BaseDao<DsedRetryTemplDo, Long> {

	DsedRetryTemplDo getByBusIdAndEventType(@Param("busId")String busId,@Param("eventType") String eventType);

	int deleteByBusidAndEventType(@Param("busId")String busId,@Param("eventType") String eventType);

	DsedRetryTemplDo getCurPushDebt(@Param("busId")String busId,@Param("eventType") String eventType);

}
