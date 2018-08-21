package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedRetryTemplDo;

/**
 * 重试模板Service
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-16 16:28:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedRetryTemplService extends ParentService<DsedRetryTemplDo, Long>{

	DsedRetryTemplDo getByBusIdAndEventType(String busId, String eventType);

	int deleteByBusidAndEventType(String busId, String eventType);

	DsedRetryTemplDo getCurPushDebt(String busId, String eventType);

}