package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdAssetPackageDo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * Dao
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 11:22:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdAssetPackageDao extends BaseDao<JsdAssetPackageDo, Long> {

    /**
     * 更新实际借款信息
     * @param afAssetPackageDo
     * @return
     */
    int updateRealTotalMoneyById(JsdAssetPackageDo afAssetPackageDo);

    /**
     * 获取资产方当日已获取消费分期额度
     * @param
     * @return
     */

    BigDecimal getCurrDayHaveGetTotalBorrowAmount(@Param("assetSideId")Long assetSideId);
    /**
     * 获取资产方当日已获取现金贷额度
     * @param
     * @return
     */
    BigDecimal getCurrDayHaveGetTotalBorrowCashAmount(@Param("assetSideId")Long assetSideId);

}
