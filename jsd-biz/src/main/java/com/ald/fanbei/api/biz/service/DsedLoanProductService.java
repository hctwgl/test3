package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedLoanProductDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRateDo;

/**
 * 都市e贷借款产品表Service
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:44:46
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanProductService extends ParentService<DsedLoanProductDo, Long>{

    DsedLoanRateDo getByPrdTypeAndNper(String prdType, String nper);

    Integer getMaxPeriodsByPrdType(String prdType);
}