package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfBorrowCashPushDo;

/**
 * 菠萝觅订单详情Service
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-03-01 18:59:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowCashPushService extends ParentService<AfBorrowCashPushDo, Long>{

	void saveOrUpdate(AfBorrowCashPushDo borrowCashPush);

	AfBorrowCashPushDo getByBorrowCashId(Long rid);

}
