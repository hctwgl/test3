package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowCashDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowCashQuery;

/**
 * 资产可用债权信息Dao
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:50:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfViewAssetBorrowCashDao {

	/**
	 * 根据用户借款id获取视图信息
	 * @param borrowCashId
	 * @return
	 */
	AfViewAssetBorrowCashDo getByBorrowCashId(@Param("borrowCashId")Long borrowCashId);
	
	/**
	 * 根据条件获取视图明细记录
	 * @param afViewAssetBorrowCashQuery
	 * @return
	 */
	List<AfViewAssetBorrowCashDo> getListByQueryCondition(AfViewAssetBorrowCashQuery afViewAssetBorrowCashQuery);

}
