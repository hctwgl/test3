package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfViewAssetLoanDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetLoanQuery;

/**
 * 白领带非实时推送债权视图信息Service
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-25 13:39:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfViewAssetLoanService extends ParentService<AfViewAssetLoanDo, Long>{

	BigDecimal getSumAmount(Date gmtCreateStart, Date gmtCreateEnd);

	List<AfViewAssetLoanDo> getListByQueryCondition(AfViewAssetLoanQuery query);

	BigDecimal checkAmount(AfViewAssetLoanQuery query);

	AfViewAssetLoanDo getByQueryCondition(AfViewAssetLoanQuery query);

}
