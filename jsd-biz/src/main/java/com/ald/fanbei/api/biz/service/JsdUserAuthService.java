package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.jsd.mgr.dal.domain.dto.UserAuthDto;
import com.ald.fanbei.api.dal.query.UserAuthQuery;

import java.util.List;

/**
 * Service
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdUserAuthService extends ParentService<JsdUserAuthDo, Long>{

    List<UserAuthDto> getListUserAuth(UserAuthQuery query);

    JsdUserAuthDo getByUserId(Long userId);
}
