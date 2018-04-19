package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfRecordMaxDo;

/**
 * 债权推送查询上限记录表Service
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-11 18:10:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRecordMaxService extends ParentService<AfRecordMaxDo, Long>{

	AfRecordMaxDo getByBusIdAndEventype(String orderNo, String eventype);

	int deleteById(Long rid);

}
