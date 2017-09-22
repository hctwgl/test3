package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUnionLoginLogDo;

/**
 * '联合登录用户登录日志表Service
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-19 15:33:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUnionLoginLogService extends ParentService<AfUnionLoginLogDo, Long>{

    void addLog(String channel, String phone, String paramsJsonStr);
}
