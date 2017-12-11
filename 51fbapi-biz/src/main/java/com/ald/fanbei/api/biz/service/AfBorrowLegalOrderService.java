package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;

/**
 * Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalOrderService extends ParentService<AfBorrowLegalOrderDo, Long>{

	/**
	 * 上一笔订单记录
	 */
	AfBorrowLegalOrderDo getLastBorrowLegalOrderByBorrowId(Long borrowId);

}
