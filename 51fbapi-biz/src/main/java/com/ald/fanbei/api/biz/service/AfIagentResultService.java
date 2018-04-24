package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfIagentResultDo;

/**
 * 智能电核表Service
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-03-27 16:57:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfIagentResultService extends ParentService<AfIagentResultDo, Long>{
    public void updateResultByWorkId(AfIagentResultDo afIagentResultDo);
    public AfIagentResultDo getIagentByWorkId(long workId);
    public AfIagentResultDo getIagentByUserIdToday(long userId);
}
