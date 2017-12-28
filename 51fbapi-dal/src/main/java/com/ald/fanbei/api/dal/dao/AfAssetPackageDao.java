package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAssetPackageDo;

/**
 * 资产包信息Dao
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAssetPackageDao extends BaseDao<AfAssetPackageDo, Long> {

	/**
	 * 更新实际借款信息
	 * @param afAssetPackageDo
	 * @return
	 */
	int updateRealTotalMoneyById(AfAssetPackageDo afAssetPackageDo);

	/**
	 * 获取资产方当日已获取消费分期额度
	 * @param rid
	 * @return
	 */
	
	BigDecimal getCurrDayHaveGetTotalBorrowAmount(@Param("assetSideId")Long assetSideId);
	/**
	 * 获取资产方当日已获取现金贷额度
	 * @param rid
	 * @return
	 */
	BigDecimal getCurrDayHaveGetTotalBorrowCashAmount(@Param("assetSideId")Long assetSideId);
	
}
