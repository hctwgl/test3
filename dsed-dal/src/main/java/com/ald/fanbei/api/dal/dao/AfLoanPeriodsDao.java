package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
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
public interface AfLoanPeriodsDao extends BaseDao<AfLoanPeriodsDo, Long> {

	AfLoanPeriodsDo getLastActivePeriodByLoanId(Long loanId);

	List<AfLoanPeriodsDo> listByLoanId(Long loanId);

	List<AfLoanPeriodsDo> getNoRepayListByLoanId(Long loanId);

	AfLoanPeriodsDo getPeriodByLoanIdAndNper(@Param("loanId")Long loanId, @Param("nper")Integer nper);
	
	List<AfLoanPeriodsDo> listCanRepayPeriods(Long loanId);
	
	List<AfLoanPeriodsDo> listUnChargeRepayPeriods(Long loanId);

	AfLoanPeriodsDo getOneByLoanId(Long loanId);
	
	List<AfLoanPeriodsDo> getAllLoanPeriodsByLoanId(Long loanId);

}
