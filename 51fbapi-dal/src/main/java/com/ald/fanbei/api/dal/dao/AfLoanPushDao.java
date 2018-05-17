package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfLoanPushDo;

/**
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-10 16:51:38
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanPushDao extends BaseDao<AfLoanPushDo, Long> {

	AfLoanPushDo getByLoanId(Long loanId);

    

}
