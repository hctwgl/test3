package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowCashDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowCashQuery;

/**
 * 资产可用债权信息Service
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:50:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfViewAssetBorrowCashService {
	
	/**
	 * 根据条件获取视图明细记录
	 * @param afViewAssetBorrowCashQuery
	 * @return
	 */
	List<AfViewAssetBorrowCashDo> getListByQueryCondition(AfViewAssetBorrowCashQuery afViewAssetBorrowCashQuery);

	BigDecimal getSumAmount(Date gmtCreateStart, Date gmtCreateEnd);

	BigDecimal getSumMinAmount(Date gmtCreateStart, Date gmtCreateEnd,String minBorrowTime);

	BigDecimal getSumMaxAmount(Date gmtCreateStart, Date gmtCreateEnd,String maxBorrowTime);

	AfViewAssetBorrowCashDo getByQueryCondition(AfViewAssetBorrowCashQuery query);

	BigDecimal checkAmount(AfViewAssetBorrowCashQuery query);
}
