package com.ald.fanbei.api.dal.dao;


import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfRetryTemplDo;

import java.util.List;

/**
 * 重试模板Dao
 * 
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2018-02-26 14:49:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRetryTemplDao extends BaseDao<AfRetryTemplDo, Long> {

	int deleteByBusidAndEventType(@Param("borrowNo")String borrowNo, @Param("eventType")String eventType);

	AfRetryTemplDo getByBusIdAndEventType(@Param("busId")String busId,@Param("eventType") String eventType);

	List<AfRetryTemplDo> getByBusId(@Param("busId")String busId);

	AfRetryTemplDo getCurPushDebt(@Param("busId")String busId, @Param("eventType")String eventType);

}
