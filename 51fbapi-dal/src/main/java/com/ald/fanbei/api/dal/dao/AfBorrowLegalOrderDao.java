package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowLegalOrderQuery;

import org.apache.ibatis.annotations.Param;

/**
 * Dao
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalOrderDao extends BaseDao<AfBorrowLegalOrderDo, Long> {

	AfBorrowLegalOrderDo getLastBorrowLegalOrderByBorrowId(Long borrowId);

	List<AfBorrowLegalOrderDo> getUserBorrowLegalOrderList(AfBorrowLegalOrderQuery query);

	int addBorrowLegalOrder(AfBorrowLegalOrderDo borrowLegalOrder);

	AfBorrowLegalOrderDo getLastOrderByBorrowId(Long borrowId);
	
	Long tuchByBorrowId(Long borrowId);

	void updateSmartAddressScore (@Param("smartAddressScore") int smartAddressScore,@Param("borrowId") long borrowId,@Param("orderNo") String orderno);

	AfBorrowLegalOrderDo getBorrowLegalOrderByBorrowId(Long borrowId);

}
