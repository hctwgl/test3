package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAssetPackageDetailDo;

/**
 * 资产包与债权记录关系Dao
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAssetPackageDetailDao extends BaseDao<AfAssetPackageDetailDo, Long> {

	AfAssetPackageDetailDo getByBorrowNo(@Param("borrowNo")String borrowNo);
	
	int updateByPackageId(AfAssetPackageDetailDo afAssetPackageDetailDo);
	
	int updateByBorrowNo(AfAssetPackageDetailDo afAssetPackageDetailDo);

	int updateReDisTri(@Param("borrowNo")String borrowNo);

	int invalidPackageDetail(@Param("borrowNo")String borrowNo);

	List<AfAssetPackageDetailDo> getPackageDetailByBorrowNo(@Param("borrowNo")String borrowNo);
}
