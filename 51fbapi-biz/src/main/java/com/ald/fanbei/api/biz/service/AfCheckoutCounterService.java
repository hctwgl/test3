package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.RiskCreditBo;
import com.ald.fanbei.api.dal.domain.AfCheckoutCounterDo;

/**
 * 收银台相关配置表Service
 *
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-10-16 09:46:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCheckoutCounterService extends ParentService<AfCheckoutCounterDo, Long> {
    AfCheckoutCounterDo getByType(String type, String secType);
    RiskCreditBo getRiskCreditSummary(Long userId);
    boolean getRiskCreditStatus(RiskCreditBo riskCredit);
}
