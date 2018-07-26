package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.DsedLoanPushDo;

/**
 * 都市e贷的实时债权推送拓展表Dao
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-16 16:54:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanPushDao extends BaseDao<DsedLoanPushDo, Long> {

	DsedLoanPushDo getByLoanId(Long loanId);

}
