package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowQuery;

/**
 * 资产方消费分期债权视图Service
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017-12-14 16:59:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfViewAssetBorrowService extends ParentService<AfViewAssetBorrowDo, Long>{

	BigDecimal getSumAmount(Date gmtCreateStart, Date gmtCreateEnd);

	List<AfViewAssetBorrowDo> getListByQueryCondition(AfViewAssetBorrowQuery query);

	BigDecimal checkAmount(AfViewAssetBorrowQuery query);

	AfViewAssetBorrowDo getByQueryCondition(AfViewAssetBorrowQuery query);

}
