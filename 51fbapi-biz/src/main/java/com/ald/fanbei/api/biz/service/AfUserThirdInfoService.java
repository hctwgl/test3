package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserThirdInfoDo;
import com.alibaba.fastjson.JSONObject;

/**
 * 用户第三方信息Service
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-04 09:20:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserThirdInfoService extends ParentService<AfUserThirdInfoDo, Long>{

    /**
     * 获取用户微信信息
     *
     * @author wangli
     * @date 2018/5/4 9:24
     */
    JSONObject getUserWxInfo(Long userId);
}
