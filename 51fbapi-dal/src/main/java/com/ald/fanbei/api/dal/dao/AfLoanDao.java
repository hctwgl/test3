package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.dto.AfLoanDto;

/**
 * 贷款业务Dao
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanDao extends BaseDao<AfLoanDo, Long> {

	AfLoanDo getLastByUserIdAndPrdType(@Param("userId") Long userId, @Param("prdType") String prdType);
	
	AfLoanDo getByLoanNo(String loanNo);
	
	/**
	 * 获取已经完成的贷款记录
	 * @param userId
	 * @param start
	 * @return
	 */
	List<AfLoanDo> listDoneLoansByUserId(@Param("userId") Long userId, @Param("start") Integer start);
	
	/**
	 * 获取处理中的贷款
	 * @param userId
	 * @return
	 */
	List<AfLoanDo> listDealingLoansByUserId(@Param("userId") Long userId);

	AfLoanDo selectById(@Param("loanId")Long loanId);

	AfLoanDto getBorrowInfoById(AfLoanDo loanDo);

	AfLoanDo getLoanById(Long borrowCashId);

}
