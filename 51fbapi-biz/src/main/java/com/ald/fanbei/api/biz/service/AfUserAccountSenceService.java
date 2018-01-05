package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;

/**
 * 额度拆分多场景分期额度记录Service
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:57:51 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountSenceService extends ParentService<AfUserAccountSenceDo, Long> {

    int updateUserSceneAuAmount(String scene, Long userId, BigDecimal auAmount);

    AfUserAccountSenceDo getByUserIdAndScene(String scene, Long userId);
}
