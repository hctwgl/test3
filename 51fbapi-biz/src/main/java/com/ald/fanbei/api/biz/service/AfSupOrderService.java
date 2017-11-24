package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfSupOrderDo;

/**
 * 新人专享Service
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-22 13:57:29 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSupOrderService extends ParentService<AfSupOrderDo, Long> {

    String processCallbackResult(String userOrderId, String status, String mes, String kminfo, String payoffPriceTotal, String signs);

}
