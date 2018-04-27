package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 贷款业务Dao
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanRepaymentDao extends BaseDao<AfLoanRepaymentDo, Long> {

	AfLoanRepaymentDo getProcessLoanRepaymentByLoanId(Long loanId);

	AfLoanRepaymentDo getRepayByTradeNo(String tradeNo);

	List<AfLoanRepaymentDo> listDtoByLoanId(Long loanId);

	AfLoanRepaymentDo getLoanRepaymentByTradeNo(String outTradeNo);

	/**
	 * 获取还款处理中的数量
	 *
	 * @author wangli
	 * @date 2018/4/14 12:24
	 */
    int getProcessLoanRepaymentNumByLoanId(@Param("loanId") Long loanId);
}
