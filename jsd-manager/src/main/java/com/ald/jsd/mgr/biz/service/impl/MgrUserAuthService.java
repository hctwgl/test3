package com.ald.jsd.mgr.biz.service.impl;

import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.UserAuthDto;
import com.ald.fanbei.api.dal.query.UserAuthQuery;

/**
 * Service
 *
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrUserAuthService {

    List<UserAuthDto> getListUserAuth(UserAuthQuery query);

    int getPassPersonNumByStatusAndDays(String status, Integer days);

    int getPassPersonNumByStatusBetweenStartAndEnd(String status, Date startDate,Date endDate);

    int getPassPersonNumByStatusEqualDays(String status, Integer days);

    JsdUserAuthDo getByUserId(Long userId);
}
