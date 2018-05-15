package com.ald.fanbei.api.biz.service;


import java.util.List;

import com.ald.fanbei.api.dal.domain.AfRetryTemplDo;

/**
 * 重试模板Service
 * 
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2018-02-26 14:49:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRetryTemplService extends ParentService<AfRetryTemplDo, Long>{

	int deleteByBusidAndEventType(String borrowNo, String eventType);
	
	AfRetryTemplDo getByBusIdAndEventType(String busId,String eventType);

	List<AfRetryTemplDo> getByBusId(String busId);

	AfRetryTemplDo getCurPushDebt(String busId, String eventType);

}
