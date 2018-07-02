package com.ald.fanbei.api.web.h5.api.loan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.AfLoanPeriodStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.ald.fanbei.api.web.vo.AfLoanPeriodsVo;

/**
 * @Description: 白领贷还款计划
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月22日
 */
@NeedLogin
@Component("loanRepayPlanApi")
public class LoanRepayPlanApi implements H5Handle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfLoanService afLoanService;
	@Resource
	AfLoanPeriodsService afLoanPeriodsService;
	@Resource
	AfLoanRepaymentService afLoanRepaymentService;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	RiskUtil riskUtil;
	
	@Override
	public H5HandleResponse process(Context context) {
		
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		List<AfLoanPeriodsVo> data = new ArrayList<AfLoanPeriodsVo>();
		try {
			
			Long loanId = Long.parseLong(context.getData("loanId").toString());
			if(loanId == null || loanId <= 0){
				throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
			}
			
			// 借款信息
			AfLoanDo loanDo = afLoanService.getById(loanId);
			if(loanDo == null){
				throw new FanbeiException(FanbeiExceptionCode.BORROW_DETAIL_NOT_EXIST_ERROR);
			}
			
			// 当前待还分期信息
			List<AfLoanPeriodsDo> loanPeriods = afLoanPeriodsService.listByLoanId(loanId);
			if(loanPeriods.size()==0){
				throw new FanbeiException(FanbeiExceptionCode.LOAN_PERIOD_NOT_EXIST_ERROR);
			}
			
			BigDecimal allRestAmount = BigDecimal.ZERO;
			BigDecimal allActualAmount = BigDecimal.ZERO;
			
			for (AfLoanPeriodsDo loanPeriodsDo : loanPeriods) {
				// 每期需还金额(本金+手续费+利息+逾期费)
				BigDecimal perPeriodAmount = BigDecimalUtil.add(loanPeriodsDo.getAmount(),loanPeriodsDo.getServiceFee(),loanPeriodsDo.getInterestFee(),loanPeriodsDo.getOverdueAmount(),
															loanPeriodsDo.getRepaidServiceFee(),loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getRepaidOverdueAmount());
				// 每期待还金额(本金+手续费+利息+逾期费-已还金额)
				BigDecimal restAmount = afLoanRepaymentService.calculateRestAmount(loanPeriodsDo.getRid());
				
				AfLoanPeriodsVo loanPeriodsVo = new AfLoanPeriodsVo();
				loanPeriodsVo.setLoanPeriodsId(loanPeriodsDo.getRid());	// 借款期数id
				loanPeriodsVo.setNper(loanPeriodsDo.getNper());		// 期数
				loanPeriodsVo.setPerAmount(perPeriodAmount);		// 每期还款金额
				loanPeriodsVo.setRestAmount(restAmount);		// 每期待还金额
				loanPeriodsVo.setServiceFee(loanPeriodsDo.getServiceFee().add(loanPeriodsDo.getRepaidServiceFee()));		// 手续费
				loanPeriodsVo.setInterestFee(loanPeriodsDo.getInterestFee().add(loanPeriodsDo.getRepaidInterestFee()));	// 利息
				loanPeriodsVo.setOverdueAmount(loanPeriodsDo.getOverdueAmount().add(loanPeriodsDo.getRepaidOverdueAmount()));	// 逾期费
				loanPeriodsVo.setGmtPlanRepay(loanPeriodsDo.getGmtPlanRepay());		// 最后还款时间
				
				// 状态(已还款：Y；已逾期：O；还款中：D；未还款：N)
				String status = loanPeriodsDo.getStatus();
				if(status.equals(AfLoanPeriodStatus.AWAIT_REPAY.name()) || status.equals(AfLoanPeriodStatus.PART_REPAY.name())){	// 未还款
					if(afLoanRepaymentService.canRepay(loanPeriodsDo)){
						loanPeriodsVo.setStatus("H");	// 已出账
					}else{
						loanPeriodsVo.setStatus("N");	// 未出账
					}
					if(loanPeriodsDo.getOverdueStatus().equals("Y")){	// 未还款已逾期
						loanPeriodsVo.setStatus("O");
					}
					
					allRestAmount = allRestAmount.add(restAmount);
					if(!afLoanRepaymentService.canRepay(loanPeriodsDo)) {
						allActualAmount = allActualAmount.add(loanPeriodsDo.getAmount()); //未出账的期 只用还本金
					}else {
						allActualAmount = allActualAmount.add(restAmount);
					}
				}else if(status.equals(AfLoanPeriodStatus.REPAYING.name())){	// 还款中
					loanPeriodsVo.setStatus("D");
				}else if(status.equals(AfLoanPeriodStatus.FINISHED.name())){	// 已结清
					loanPeriodsVo.setStatus("Y");
//					loanPeriodsVo.setInterestFee(BigDecimal.ZERO);
//					loanPeriodsVo.setServiceFee(BigDecimal.ZERO);
//					loanPeriodsVo.setRestAmount(BigDecimal.ZERO);
				}
				
				data.add(loanPeriodsVo);
			}
			resp.addResponseData("loanPeriods", data);
			
			resp.addResponseData("allRestAmount", allRestAmount);
			resp.addResponseData("allActualAmount", allActualAmount);
			resp.addResponseData("allDerateAmount", allRestAmount.subtract(allActualAmount));
			resp.addResponseData("rebateAmount", afUserAccountService.getUserAccountByUserId(context.getUserId()).getRebateAmount());
			
		} catch (Exception e) {
			logger.error("/loanRepayPlanApi error = {}", e.getStackTrace());
			resp.setResponseData("获取信息失败");
		}
		
		return resp;
	}

}
