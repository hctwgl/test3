package com.ald.fanbei.api.biz.service;

import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;

/**
 * '第三方-上树请求记录Service
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:35:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeActivityService extends ParentService<AfBoluomeActivityDo, Long>{
    int ggLightActivity(final AfOrderDo afOrder);
    Map<String, String> activityOffical(Long userId);
    int sentNewUserBoluomeCoupon(AfUserDo afUserDo);
    int sentNewUserBoluomeCouponForDineDash(AfUserDo afUserDo);
}
