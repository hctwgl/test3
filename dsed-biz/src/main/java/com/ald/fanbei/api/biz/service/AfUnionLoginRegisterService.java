package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUnionLoginRegisterDo;

/**
 * '联合登录用户登录日志表Service
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-19 15:36:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUnionLoginRegisterService extends ParentService<AfUnionLoginRegisterDo, Long>{
    String register(String channel,String phone,String paramsJsonStr) throws Exception;
}
