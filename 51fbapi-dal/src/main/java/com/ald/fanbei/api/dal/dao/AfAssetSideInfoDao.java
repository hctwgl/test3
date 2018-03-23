package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;

/**
 * 资产方信息Dao
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAssetSideInfoDao extends BaseDao<AfAssetSideInfoDo, Long> {

	/**
	 * 根据资产方标识获取资产方信息
	 * @param assetSideFlag
	 * @return
	 */
	AfAssetSideInfoDo getByAssetSideFlag(@Param("assetSideFlag")String assetSideFlag);

}
