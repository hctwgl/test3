package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfContractPdfDao;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfFundSideBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRescourceLogService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfContractPdfDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfUserSealDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @类描述：
 * 
 * @author suweili 2017年4月5日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/app/")
public class AppH5ProtocolController extends BaseController {

	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfUserService afUserService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfRenewalDetailDao afRenewalDetailDao;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfRescourceLogService afRescourceLogService;
	@Resource
	AfFundSideBorrowCashService afFundSideBorrowCashService;
	@Resource
	AfESdkService afESdkService;
	@Resource
	AfContractPdfDao afContractPdfDao;
	@Resource
	AfUserSealDao afUserSealDao;
	@Resource
	AfUserOutDayDao afUserOutDayDao;
	@Resource
	NumberWordFormat numberWordFormat;

	@RequestMapping(value = {"protocolFenqiService"}, method = RequestMethod.GET)
	public void protocolFenqiService(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext webContext = doWebCheckNoAjax(request, false);
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		Long borrowId = NumberUtil.objToLongDefault(request.getParameter("borrowId"), 0l);
		if (userName == null || !webContext.isLogin()) {
			throw new FanbeiException("非法用户");
		}
		Integer nper = NumberUtil.objToIntDefault(request.getParameter("nper"), 0);
		BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("amount"), new BigDecimal(0));
		BigDecimal poundage = NumberUtil.objToBigDecimalDefault(request.getParameter("poundage"), new BigDecimal(0));

		AfUserDo afUserDo = afUserService.getUserByUserName(userName);

		Long userId = afUserDo.getRid();
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (accountDo == null) {
			logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}

		model.put("idNumber", accountDo.getIdNumber());
		model.put("realName", accountDo.getRealName());
		AfResourceDo consumeDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsume.getCode());
		AfResourceDo consumeOverdueDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsumeOverdue.getCode());
		AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode());
		model.put("lender", lenderDo.getValue());// 出借人
		model.put("mobile", afUserDo.getMobile());// 联系电话
		List<NperDo> list = JSONArray.parseArray(consumeDo.getValue(), NperDo.class);
		List<NperDo> overduelist = JSONArray.parseArray(consumeOverdueDo.getValue(), NperDo.class);
		model.put("lateFeeRate", consumeOverdueDo.getValue1());
		if (StringUtils.isNotBlank(consumeOverdueDo.getValue2())) {
			String[] amounts = consumeOverdueDo.getValue2().split(",");
			model.put("lateFeeMin", new BigDecimal(amounts[0]));
			model.put("lateFeeMax", new BigDecimal(amounts[1]));
		}
		Date date = new Date();

		if (null != borrowId && 0 != borrowId) {
			GetSeal(model, afUserDo, accountDo);
			lender(model, null);
			//取当前的分期时间，而不是当前时间
			AfBorrowDo afBorrowDo= afBorrowService.getBorrowById(borrowId);
			date= afBorrowDo.getGmtCreate();
		}
		model.put("gmtStart", date);
		model.put("gmtEnd", DateUtil.addMonths(date, nper));
		model.put("amountCapital", toCapital(borrowAmount.doubleValue()));
		model.put("amountLower", borrowAmount);
//		model.put("poundage", consumeDo.getValue1());
		model.put("poundage", poundage);


		for (NperDo nperDo : list) {
			if (nperDo.getNper() == nper) {
				model.put("yearRate", nperDo.getRate());
			}
		}
		for (NperDo nperDo : overduelist) {
			if (nperDo.getNper() == nper) {
				model.put("overdueRate", nperDo.getRate() != null?nperDo.getRate():"");
			}
		}
		int repayDay = 20;
		AfUserOutDayDo afUserOutDayDo =  afUserOutDayDao.getUserOutDayByUserId(userId);
		if(afUserOutDayDo !=null) {
			repayDay = afUserOutDayDo.getPayDay();
		}
		model.put("repayDay", repayDay);
		List<NperDo> rateList = JSONArray.parseArray(consumeDo.getValue3(), NperDo.class);
		if(nper == 5){
			nper = 6;
		}
		if(nper == 11){
			nper = 12;
		}
		for (NperDo nperDo : rateList) {
			if (nperDo.getNper() == nper) {
				model.put("interest", borrowAmount.multiply(nperDo.getRate()).multiply(new BigDecimal(nper)).divide(new BigDecimal(12),2,BigDecimal.ROUND_UP));
			}
		}
		logger.info(JSON.toJSONString(model));
	}
	@RequestMapping(value = { "numProtocol" }, method = RequestMethod.GET)
	public void numProtocol(HttpServletRequest request, ModelMap model)throws IOException{
		FanbeiWebContext webContext = doWebCheckNoAjax(request, false);
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		if(userName == null || !webContext.isLogin() ) {
			throw new FanbeiException("非法用户");
		}
		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		if (afUserDo == null) {
			logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		Long userId = afUserDo.getRid();
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (accountDo == null) {
			logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		model.put("idNumber", accountDo.getIdNumber());
		model.put("realName", accountDo.getRealName());
	}


	@RequestMapping(value = { "protocolCashLoan" }, method = RequestMethod.GET)
	public void protocolCashLoan(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext webContext = doWebCheckNoAjax(request, false);
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		if(userName == null || !webContext.isLogin() ) {
			throw new FanbeiException("非法用户");
		}
		Long borrowId = NumberUtil.objToLongDefault(request.getParameter("borrowId"), 0l);
		BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("borrowAmount"), new BigDecimal(0));

		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		if (afUserDo == null) {
			logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		Long userId = afUserDo.getRid();
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (accountDo == null) {
			logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}

		model.put("idNumber", accountDo.getIdNumber());
		model.put("realName", accountDo.getRealName());
		model.put("email", afUserDo.getEmail());//电子邮箱	 
		model.put("phone", userName);//联系方式
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(list, borrowId);
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal poundage = new BigDecimal(rate.get("poundage").toString());
		BigDecimal overduePoundage = new BigDecimal(rate.get("overduePoundage").toString());

        BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);

        AfBorrowCashDo afBorrowCashDo = null;
        if (borrowId > 0) {
            afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
            poundage = afBorrowCashDo.getPoundageRate();
        }
        BigDecimal overdue = bankService.add(poundage).add(overduePoundage);

        Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
        if (poundageRateCash != null) {
            poundage = new BigDecimal(poundageRateCash.toString());
        }
        //fmf_得到逾期费率
        AfResourceDo afResourceDo1 = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_POUNDAGE);
        AfResourceDo afResourceDo2 = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_OVERDUE_POUNDAGE);
        double borrowCashPoundage = Double.parseDouble(afResourceDo1.getValue().toString());
        double borrowCashOvreduePoundage = Double.parseDouble(afResourceDo2.getValue().toString());
        double sumOverdue = borrowCashPoundage + borrowCashOvreduePoundage;
        model.put("overduePoundageRate", sumOverdue);//逾期手续费率


		model.put("dayRate", bankService);//日利率
		model.put("overdueRate", overdue);//逾期费率（日）
		model.put("poundageRate", poundage);//手续费率

		model.put("amountCapital", toCapital(borrowAmount.doubleValue()));
		model.put("amountLower", borrowAmount);
		if (borrowId > 0) {
            afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
			model.put("gmtCreate", afBorrowCashDo.getGmtCreate());// 出借人
			AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
			afContractPdfDo.setType((byte) 3);
			afContractPdfDo.setTypeId(borrowId);
			AfContractPdfDo afContractPdfDo1 = afContractPdfDao.selectByTypeId(afContractPdfDo);
			if (null != afContractPdfDo1 && null != afContractPdfDo1.getContractPdfUrl()) {
				model.put("pdfUrl", afContractPdfDo1.getContractPdfUrl());
			}
			if (afBorrowCashDo != null) {
				model.put("borrowNo", afBorrowCashDo.getBorrowNo());
				if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
					model.put("gmtArrival", afBorrowCashDo.getGmtArrival());
//					Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
					Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
					Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
					Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
					model.put("repaymentDay", repaymentDay);
					model.put("lenderIdNumber", rate.get("lenderIdNumber"));
					model.put("lenderIdAmount", afBorrowCashDo.getAmount());
					model.put("gmtPlanRepayment", afBorrowCashDo.getGmtPlanRepayment());

					//查看有无和资金方关联，有的话，替换里面的借出人信息
					AfFundSideInfoDo fundSideInfo = afFundSideBorrowCashService.getLenderInfoByBorrowCashId(borrowId);
					GetSeal(model, afUserDo, accountDo);
					lender(model, fundSideInfo);
				}
			}
		}

		logger.info(JSON.toJSONString(model));
	}

	private void lender(ModelMap model, AfFundSideInfoDo fundSideInfo) {
		if (fundSideInfo != null && StringUtil.isNotBlank(fundSideInfo.getName())) {
			model.put("lender", fundSideInfo.getName());// 出借人
		} else {
			AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
			model.put("lender", lenderDo.getValue());// 出借人
		}
		AfUserSealDo companyUserSealDo = afUserSealDao.selectByUserName(model.get("lender").toString());
		if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
			model.put("secondSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
		}
	}

	private void GetSeal(ModelMap model, AfUserDo afUserDo, AfUserAccountDo accountDo) {
		try {
			AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-1l);
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()){
				model.put("CompanyUserSeal","data:image/png;base64," + companyUserSealDo.getUserSeal());
			}
			AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
			if (null != afUserSealDo && null != afUserSealDo.getUserSeal()){
				model.put("personUserSeal","data:image/png;base64,"+afUserSealDo.getUserSeal());
			}
		}catch (Exception e){
			logger.error("UserSeal create error",e);
		}
	}

	@RequestMapping(value = { "protocolRenewal" }, method = RequestMethod.GET)
	public void protocolRenewal(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext webContext = doWebCheckNoAjax(request, false);
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		if(userName == null || !webContext.isLogin() ) {
			throw new FanbeiException("非法用户");
		}
		Long borrowId = NumberUtil.objToLongDefault(request.getParameter("borrowId"), 0l);
		Long renewalId = NumberUtil.objToLongDefault(request.getParameter("renewalId"), 0l);
		int renewalDay = NumberUtil.objToIntDefault(request.getParameter("renewalDay"), 0);
		BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("renewalAmount"), BigDecimal.ZERO);
		
		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		if (afUserDo == null) {
			logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		Long userId = afUserDo.getRid();
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (accountDo == null) {
			logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}

		model.put("realName", accountDo.getRealName());//借款人
		model.put("idNumber", accountDo.getIdNumber());//身份证号
		model.put("phone", userName);//联系方式
		model.put("email", afUserDo.getEmail());//电子邮箱	 
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(list, borrowId);
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);
		model.put("dayRate", bankService);
		BigDecimal poundage = new BigDecimal(rate.get("poundage").toString());
		Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
		if (poundageRateCash != null) {
			poundage = new BigDecimal(poundageRateCash.toString());
		}
		AfBorrowCashDo afBorrowCashDo = null;
		if (borrowId > 0) {
			afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
			poundage = afBorrowCashDo.getPoundageRate();
		}

		//fmf_得到逾期费率
		AfResourceDo afResourceDo1 = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,Constants.BORROW_CASH_POUNDAGE);
		AfResourceDo afResourceDo2 = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,Constants.BORROW_CASH_OVERDUE_POUNDAGE);
		double borrowCashPoundage = Double.parseDouble(afResourceDo1.getValue().toString());
		double borrowCashOvreduePoundage = Double.parseDouble(afResourceDo2.getValue().toString());
		double sumOverdue=borrowCashPoundage+borrowCashOvreduePoundage;
		model.put("overduePoundageRate", sumOverdue);//逾期手续费率
		model.put("poundageRate", poundage);//手续费率
		
		if (borrowId > 0) {
			 afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
			if (afBorrowCashDo != null) {
				model.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
				if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
//					Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
					Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
					Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
					Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
					model.put("gmtBorrowBegin", arrivalStart);//到账时间，借款起息日
					model.put("gmtBorrowEnd", repaymentDay);//借款结束日	 
					model.put("amountCapital", toCapital(afBorrowCashDo.getAmount().doubleValue()));
					model.put("amountLower", afBorrowCashDo.getAmount());
					//查看有无和资金方关联，有的话，替换里面的借出人信息
					AfFundSideInfoDo fundSideInfo = afFundSideBorrowCashService.getLenderInfoByBorrowCashId(borrowId);
                    if (renewalId > 0){
                        lender(model, fundSideInfo);
                        GetSeal(model, afUserDo, accountDo);
                    }
					/*if(fundSideInfo!=null && StringUtil.isNotBlank(fundSideInfo.getName())){
						model.put("lender", fundSideInfo.getName());// 出借人
					}else{
						AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
						model.put("lender", lenderDo.getValue());// 出借人
					}*/
				}
			}
			
			if (renewalId > 0) {
				AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByRenewalId(renewalId);
				Date gmtCreate = afRenewalDetailDo.getGmtCreate();
				Date gmtPlanRepayment = afRenewalDetailDo.getGmtPlanRepayment();
				// 如果预计还款时间在申请日期之后，则在原预计还款时间的基础上加上续期天数，否则在申请日期的基础上加上续期天数，作为新的续期截止时间
				AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
				afContractPdfDo.setType((byte)3);
				afContractPdfDo.setTypeId(renewalId);
				AfContractPdfDo afContractPdfDo1 = afContractPdfDao.selectByTypeId(afContractPdfDo);
				if (null != afContractPdfDo1 && null != afContractPdfDo1.getContractPdfUrl()){
					model.put("pdfUrl",afContractPdfDo1.getContractPdfUrl());
				}
				if (gmtPlanRepayment.after(gmtCreate)) {
					Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, afRenewalDetailDo.getRenewalDay()));
					afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
					model.put("gmtRenewalBegin", gmtPlanRepayment);
					model.put("gmtRenewalEnd", repaymentDay);
					model.put("repaymentDay", repaymentDay);
				} else {
					Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtCreate, afRenewalDetailDo.getRenewalDay()));
					afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
					model.put("gmtRenewalBegin", gmtCreate);
					model.put("gmtRenewalEnd", repaymentDay);
					model.put("repaymentDay", repaymentDay);
				}
				model.put("renewalAmountLower", afRenewalDetailDo.getRenewalAmount());//续借金额小写
				model.put("renewalAmountCapital", toCapital(afRenewalDetailDo.getRenewalAmount().doubleValue()));//续借金额大写	
				model.put("repayAmountLower", afRenewalDetailDo.getCapital());//续借金额小写
				model.put("repayAmountCapital", toCapital(afRenewalDetailDo.getCapital().doubleValue()));//续借金额大写	
//				Date gmtRenewalBegin = afRenewalDetailDo.getGmtCreate();
//				Date gmtRenewalEnd = DateUtil.addDays(gmtRenewalBegin, afRenewalDetailDo.getRenewalDay());
			} else {
				Date gmtPlanRepayment = afBorrowCashDo.getGmtPlanRepayment();
				Date now = new Date(System.currentTimeMillis());
				// 如果预计还款时间在今天之后，则在原预计还款时间的基础上加上续期天数，否则在今天的基础上加上续期天数，作为新的续期截止时间
				if (gmtPlanRepayment.after(now)) {
					Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, renewalDay));
					afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
					model.put("gmtRenewalBegin", gmtPlanRepayment);
					model.put("gmtRenewalEnd", repaymentDay);
					model.put("repaymentDay", repaymentDay);
				} else {
					Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(now, renewalDay));
					afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
					model.put("gmtRenewalBegin", now);
					model.put("gmtRenewalEnd", repaymentDay);
					model.put("repaymentDay", repaymentDay);
				}
				model.put("renewalAmountLower", renewalAmount);//续借金额小写
				model.put("renewalAmountCapital", toCapital(renewalAmount.doubleValue()));//续借金额大写	
				AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
				BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 借钱手续费率（日）
				BigDecimal capital = afBorrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);
				model.put("repayAmountLower", capital);//续借金额小写
				model.put("repayAmountCapital", toCapital(capital.doubleValue()));//续借金额大写	
			}
		}
		
		logger.info(JSON.toJSONString(model));
	}

	public static String ToBig(int num) {
		String str[] = { "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾" };
		return str[num - 1];
	}

    public static String toCapital(double x) {
        DecimalFormat format = new DecimalFormat("#.00");
        String str = format.format(x);
        System.out.println(str);
        String s[] = str.split("\\.");
        String temp = "";
        int ling = 0;
        int shu = 0;
        int pos = 0;
        for (int j = 0; j < s[0].length(); ++j) {
            int num = s[0].charAt(j) - '0';
            if (num == 0) {
                ling++;
                if (ling == s[0].length()) {
                    temp = "零";
                } else if (s[0].length() - j - 1 == 4) {
                    if (shu == 1 && (s[0].length() - pos - 1) >= 5 && (s[0].length() - pos - 1) <= 7) {
                        temp += "万";
                    }
                } else if (s[0].length() - j - 1 == 8) {
                    if (shu == 1 && (s[0].length() - pos - 1) >= 9 && (s[0].length() - pos - 1) <= 11) {
                        temp += "亿";
                    }
                }
            } else {
                shu++;
                int flag = 0;
                if (shu == 1) {
                    ling = 0;
                    pos = j;
                }
                if (shu == 2) {
                    flag = 1;
                    if (ling > 0) {
                        temp += "零";
                    }
                    shu = 1;
                    pos = j;
                    ling = 0;
                }
                if (s[0].length() - j - 1 == 11) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 10) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 9) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else if (s[0].length() - j - 1 == 8) {
                    temp += ToBig(num) + "亿";
                } else if (s[0].length() - j - 1 == 7) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 6) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 5) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else if (s[0].length() - j - 1 == 4) {
                    temp += ToBig(num) + "万";
                } else if (s[0].length() - j - 1 == 3) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 2) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 1) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else {
                    temp += ToBig(num);
                }
            }
            // System.out.println(temp);
        }
        temp += "元";
        for (int j = 0; j < s[1].length(); ++j) {
            int num = s[1].charAt(j) - '0';
            if (j == 0) {
                if (num != 0)
                    temp += ToBig(num) + "角";
                else if (num == 0 && 1 < s[1].length() && s[1].charAt(1) != '0') {
                    temp += "零";
                }
            } else if (j == 1) {
                if (num != 0)
                    temp += ToBig(num) + "分";
            }
        }
        System.out.println(temp);
        return temp;
    }

    public Map<String, Object> getObjectWithResourceDolist(List<AfResourceDo> list, Long borrowId) {
        Map<String, Object> data = new HashMap<String, Object>();
        AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);

        for (AfResourceDo afResourceDo : list) {
            if (StringUtils.equals(afResourceDo.getType(), AfResourceType.borrowRate.getCode())) {
                if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashRange.getCode())) {

                    data.put("maxAmount", afResourceDo.getValue());
                    data.put("minAmount", afResourceDo.getValue1());

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashBaseBankDouble.getCode())) {
                    data.put("bankDouble", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashBaseBankDouble.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("bankDouble", borrow.getValue());
                        }
                    }

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashPoundage.getCode())) {
                    data.put("poundage", afResourceDo.getValue());

                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashPoundage.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("poundage", borrow.getValue());
                        }
                    }

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashOverduePoundage.getCode())) {
                    data.put("overduePoundage", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashOverduePoundage.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("overduePoundage", borrow.getValue());
                        }
                    }
                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BaseBankRate.getCode())) {
                    data.put("bankRate", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BaseBankRate.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("bankRate", borrow.getValue());
                        }
                    }
                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.borrowCashLender.getCode())) {
                    data.put("lender", afResourceDo.getValue());
                    data.put("lenderIdNumber", afResourceDo.getValue1());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("lender", borrow.getValue());
                            data.put("lenderIdNumber", borrow.getValue1());

                        }
                    }
                }
            } else {
                if (StringUtils.equals(afResourceDo.getType(), AfResourceSecType.BorrowCashDay.getCode())) {
                    data.put("borrowCashDay", afResourceDo.getValue());

                }
            }
        }

        // rate.put("overduePoundage", data.get("overduePoundage"));
        // rate.put("bankService", bankService);
        // rate.put("poundage", data.get("poundage"));
        // rate.put("maxAmount", data.get("maxAmount"));
        // rate.put("minAmount", data.get("minAmount"));
        // rate.put("borrowCashDay", data.get("borrowCashDay"));

        return data;

    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);
			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}



}
