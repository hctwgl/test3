package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowLegalOrderQuery;

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

	AfBorrowLegalOrderDo getLastBorrowLegalOrderById(Long id);

	int saveBorrowLegalOrder(AfBorrowLegalOrderDo afBorrowLegalOrderDo);

	List<AfBorrowLegalOrderDo> getUserBorrowLegalOrderList(AfBorrowLegalOrderQuery query);
	
	boolean isV2BorrowCash(Long borrowId);
	
	/**
	 * 检查旧版客户端是否存在新版V2借钱数据
	 * @param version
	 * @param borrowId
	 */
	void checkIllegalVersionInvoke(Integer version, Long borrowId);

	void updateSmartAddressScore (int smartAddressScore,long borrowId,String orderno);

	AfBorrowLegalOrderDo getBorrowLegalOrderByBorrowId(Long borrowId);
	
}
