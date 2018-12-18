package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdViewAssetDo;
import com.ald.fanbei.api.dal.query.JsdViewAssetQuery;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Service
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 14:22:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdViewAssetService extends ParentService<JsdViewAssetDo, Long>{
    List<JsdViewAssetDo> getListByQueryCondition(JsdViewAssetQuery query);

    BigDecimal getSumMinAmount(Date gmtCreateStart, Date gmtCreateEnd,String minBorrowTime);

    BigDecimal getSumMaxAmount(Date gmtCreateStart, Date gmtCreateEnd,String maxBorrowTime);

    JsdViewAssetDo getByQueryCondition(JsdViewAssetQuery query);

    BigDecimal checkAmount(JsdViewAssetQuery query);
}
