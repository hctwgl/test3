package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdAssetPackageDetailDo;
import org.apache.ibatis.annotations.Param;

/**
 * Dao
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 11:21:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdAssetPackageDetailDao extends BaseDao<JsdAssetPackageDetailDo, Long> {

    int updateReDisTri(@Param("borrowNo")String borrowNo);

    JsdAssetPackageDetailDo getByBorrowNo(@Param("borrowNo")String borrowNo);

    int invalidPackageDetail(@Param("borrowNo")String borrowNo);
}
