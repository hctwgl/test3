package com.ald.fanbei.api.web.h5.api.loan;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanProductService;
import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.AfLoanPeriodStatus;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.afu.LoanStatusCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.vo.AfLoanVo;

/**
 * @Description: 白领贷借款信息
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年1月22日
 */
@Component("getLoanInfoApi")
public class GetLoanInfoApi implements H5Handle {

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
	AfLoanProductService afLoanProductService;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	RiskUtil riskUtil;
	
	@Override
	public H5HandleResponse process(Context context) {
		
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
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
			List<AfLoanPeriodsDo> afLoanPeriodList = afLoanPeriodsService.listCanRepayPeriods(loanId);
			
			AfUserAccountDo userDo = afUserAccountService.getUserAccountByUserId(context.getUserId());
			if (userDo == null) {
				throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			}
			
			AfLoanVo loanVo = new AfLoanVo();
			if(afLoanPeriodList.size()!=0){
				
				String loanPeriodsIds = "";
				BigDecimal currentPeriodAmount = BigDecimal.ZERO;	// 本月待还金额
				for (int i = 0; i < afLoanPeriodList.size(); i++) {
					// 期数id
					if(i == afLoanPeriodList.size()-1){
						loanPeriodsIds += afLoanPeriodList.get(i).getRid();
					}else{
						loanPeriodsIds += afLoanPeriodList.get(i).getRid()+",";
					}
					// 本月待还金额
					currentPeriodAmount = BigDecimalUtil.add(currentPeriodAmount,afLoanRepaymentService.calculateRestAmount(afLoanPeriodList.get(i).getRid()));
				}
				
				loanVo.setOverdueStatus(afLoanPeriodList.get(0).getOverdueStatus());	// 逾期状态
				loanVo.setLoanPeriodsIds(loanPeriodsIds);		// 当前借款期数id
				loanVo.setCurrentPeriodAmount(currentPeriodAmount);	// 本月待还金额
				
				// 当前期
				AfLoanPeriodsDo nowLoanPeriodsDo = afLoanPeriodList.get(afLoanPeriodList.size()-1);
				loanVo.setNper(nowLoanPeriodsDo.getNper());		// 当前期数
				loanVo.setGmtPlanRepay(nowLoanPeriodsDo.getGmtPlanRepay());		// 本月还款时间

				// 贷款状态
				loanVo.setStatus(loanDo.getStatus());
				if(loanDo.getStatus().equals(AfLoanStatus.TRANSFERRED.name())){
					
					if(!afLoanRepaymentService.canRepay(nowLoanPeriodsDo)){
						loanVo.setStatus("CURR_COMPLETED");		// 当月已还清，下月的还款时间还没开始
					}
					if(afLoanRepaymentService.getProcessLoanRepaymentByLoanId(loanId) != null){
						loanVo.setStatus(AfLoanPeriodStatus.REPAYING.name());		// 还款中
					}
					
				}
			}else {
				loanVo.setOverdueStatus("N");	// 逾期状态
				loanVo.setCurrentPeriodAmount(BigDecimal.ZERO);	// 本月待还金额
					loanVo.setStatus(loanDo.getStatus());		
				if(loanDo.getStatus().equals(AfLoanStatus.TRANSFERRED.name())){
					loanVo.setStatus("CURR_COMPLETED");	// 当月已还清，下月的还款时间还没开始
				}
			}
			
			AfLoanPeriodsDo loanPeriodsDo = afLoanPeriodsService.getOneByLoanId(loanId);
			loanVo.setRepayDay(DateUtil.getDay(loanPeriodsDo.getGmtPlanRepay()));	// 每月还款时间

			// 每期应还金额
			BigDecimal perPeriodAmount = BigDecimalUtil.add(loanPeriodsDo.getAmount(),loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
					loanPeriodsDo.getInterestFee(),loanPeriodsDo.getRepaidInterestFee());
			loanVo.setPerPeriodAmount(perPeriodAmount);		// 每期还款金额
			
			loanVo.setAmount(loanDo.getAmount());		// 借款金额
			loanVo.setTotalServiceFee(loanDo.getTotalServiceFee());	// 总手续费
			loanVo.setTotalInterestFee(loanDo.getTotalInterestFee());	// 总利息
			loanVo.setPeriods(loanDo.getPeriods());		// 还款期限
			
			String productName = afLoanProductService.getNameByPrdType(loanDo.getPrdType());
			loanVo.setLoanProduct(productName);	// 借钱产品
			loanVo.setCardNumber(loanDo.getCardNo());	// 银行卡号
			loanVo.setCardName(loanDo.getCardName());	// 银行卡名称
			loanVo.setGmtCreate(loanDo.getGmtCreate());		// 申请时间
			loanVo.setGmtArrival(loanDo.getGmtArrival());	// 打款时间
			loanVo.setRebateAmount(userDo.getRebateAmount());	// 账户余额
			
			
			resp.setResponseData(loanVo);
			
		} catch (Exception e) {
			logger.error("/loanInfoApi error = {}", e.getStackTrace());
			resp.setResponseData("获取借款信息失败");
		}
		
		return resp;
	}

}
