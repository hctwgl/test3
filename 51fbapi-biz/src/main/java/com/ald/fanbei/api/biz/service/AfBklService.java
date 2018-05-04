package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfOrderDo;

import java.math.BigDecimal;


/**
 * @author gsq 2018年4月20日下午4:08:25
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBklService {

    String isBklResult(AfOrderDo orderInfo);

    void submitBklInfo(AfOrderDo orderInfo, String type, BigDecimal amount);
}
