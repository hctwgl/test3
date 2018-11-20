package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdViewAssetDo;
import com.ald.fanbei.api.dal.query.JsdViewAssetQuery;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Dao
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 14:22:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdViewAssetDao extends BaseDao<JsdViewAssetDo, Long> {

    List<JsdViewAssetDo> getListByQueryCondition(JsdViewAssetQuery query);

    BigDecimal getSumMinAmount(@Param("gmtCreateStart")Date gmtCreateStart,@Param("gmtCreateEnd") Date gmtCreateEnd,@Param("minBorrowTime") String minBorrowTime);

    BigDecimal getSumMaxAmount(@Param("gmtCreateStart")Date gmtCreateStart, @Param("gmtCreateEnd") Date gmtCreateEnd, @Param("maxBorrowTime") String maxBorrowTime);

    JsdViewAssetDo getByQueryCondition(JsdViewAssetQuery query);

    BigDecimal checkAmount(JsdViewAssetQuery query);
}
