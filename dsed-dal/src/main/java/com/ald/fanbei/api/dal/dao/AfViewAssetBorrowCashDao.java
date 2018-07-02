package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.Date;
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

	/**
	 * 获取指定时间内的债权总金额
	 * @param gmtCreateStart
	 * @param gmtCreateEnd
	 * @return
	 */
	BigDecimal getSumAmount(@Param("gmtCreateStart")Date gmtCreateStart,@Param("gmtCreateEnd") Date gmtCreateEnd);
	
	/**
	 * 获取指定时间内的7天债权总金额
	 * @param gmtCreateStart
	 * @param gmtCreateEnd
	 * @return
	 */
	BigDecimal getSumMinAmount(@Param("gmtCreateStart")Date gmtCreateStart,@Param("gmtCreateEnd") Date gmtCreateEnd,@Param("minBorrowTime") String minBorrowTime);

	/**
	 * 获取指定时间内的14天债权总金额
	 * @param gmtCreateStart
	 * @param gmtCreateEnd
	 * @return
	 */
	BigDecimal getSumMaxAmount(@Param("gmtCreateStart")Date gmtCreateStart,@Param("gmtCreateEnd") Date gmtCreateEnd,@Param("maxBorrowTime") String maxBorrowTime);


	/**
	 * 根据条件获取一条视图明细记录
	 * @param afViewAssetBorrowCashQuery
	 * @return
	 */
	AfViewAssetBorrowCashDo getByQueryCondition(AfViewAssetBorrowCashQuery query);


	/**
	 * 
	 * @param afViewAssetBorrowCashQuery
	 * @return
	 */
	BigDecimal checkAmount(AfViewAssetBorrowCashQuery query);
}
