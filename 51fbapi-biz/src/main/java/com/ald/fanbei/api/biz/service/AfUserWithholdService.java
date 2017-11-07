package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;

/**
 * 用户代扣信息Service
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-11-07 10:52:03
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserWithholdService extends ParentService<AfUserWithholdDo, Long>{

    AfUserWithholdDo getByUserId(Long userId);
}
