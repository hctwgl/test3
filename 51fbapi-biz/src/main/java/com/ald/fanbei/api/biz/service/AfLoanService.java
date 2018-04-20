package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.bo.loan.LoanHomeInfoBo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.dto.AfLoanDto;

/**
 * 贷款业务Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanService extends ParentService<AfLoanDo, Long>{
	
	/**
	 * 获取贷款首页信息
	 * @param userId
	 * @return
	 */
	List<LoanHomeInfoBo> getHomeInfo(Long userId);
	
	/**
	 * 执行借贷
	 * @param bo
	 */
	void doLoan(ApplyLoanBo bo);
	
	/**
	 * 借贷成功回调
	 * @param loanId
	 * @param loanNo
	 * @param tradeNoOut
	 */
	void dealLoanSucc(Long loanId, String tradeNoOut);
	
	/**
	 * 借贷失败回调
	 * @param loanId
	 * @param loanNo
	 * @param tradeNoOut
	 */
	void dealLoanFail(Long loanId, String tradeNoOut, String msgOut);
	
	/**
	 * 获取用户白领贷分层日利率,缓存1小时
	 * @deprecated 暂时写死：年化利率 0.36
	 * @param userId
	 * @param prdType 对应product表中的prdType字段值
	 * @return
	 */
	BigDecimal getUserLayDailyRate(Long userId, String prdType);

	AfLoanDo selectById(Long loanId);

	AfLoanDo getByLoanNo(String loanNo);

	/**
	 * 根据用户id和产品类型获取最新一条
	 *
	 * @author wangli
	 * @date 2018/4/14 12:13
	 */
	AfLoanDo getLastByUserIdAndPrdType(Long userId, String prdType);

}
