package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfContractPdfEdspaySealDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author guoshuaiqiang 2017年12月19日下午1:41:05
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/app/")
public class AppH5ProtocolWhiteLoanController extends BaseController {

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
	AfUserAccountService afUserAccountService;
	@Resource
	AfRescourceLogService afRescourceLogService;
	@Resource
	AfFundSideBorrowCashService afFundSideBorrowCashService;
	@Resource
	AfESdkService afESdkService;
	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
	@Resource
	AfBorrowLegalOrderService afBorrowLegalOrderService;
	@Resource
	AfUserSealDao afUserSealDao;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfContractPdfDao afContractPdfDao;
	@Resource
	AfContractPdfEdspaySealDao afContractPdfEdspaySealDao;

	@Resource
	AfLoanService afLoanService;

	@Resource
	AfLoanPeriodsService afLoanPeriodsService;


	@RequestMapping(value = {"protocolLegalInstalmentV2"}, method = RequestMethod.GET)
	public String protocolLegalInstalment(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext webContext = doWebCheckNoAjax(request, false);
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		Long borrowId = NumberUtil.objToLongDefault(request.getParameter("borrowId"), 0l);
		if (userName == null || !webContext.isLogin()) {
			throw new FanbeiException("非法用户");
		}
		Integer nper = NumberUtil.objToIntDefault(request.getParameter("nper"), 0);
		BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("amount"), new BigDecimal(0));//借款本金
		BigDecimal poundage = NumberUtil.objToBigDecimalDefault(request.getParameter("poundage"), new BigDecimal(0));
		AfResourceDo consumeDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsume.getCode());
		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		Long userId = afUserDo.getRid();
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (accountDo == null) {
			logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}

		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL.getCode());
		model.put("idNumber", accountDo.getIdNumber());
		model.put("realName", accountDo.getRealName());
		AfResourceDo consumeOverdueDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsumeOverdue.getCode());
		AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode());
		model.put("lender", lenderDo.getValue());// 出借人
		model.put("mobile", afUserDo.getUserName());// 联系电话
		model.put("lateFeeRate", consumeOverdueDo.getValue1());
		if (StringUtils.isNotBlank(consumeOverdueDo.getValue2())) {
			String[] amounts = consumeOverdueDo.getValue2().split(",");
			model.put("lateFeeMin", new BigDecimal(amounts[0]));
			model.put("lateFeeMax", new BigDecimal(amounts[1]));
		}
		model.put("interest", BigDecimal.valueOf(Double.parseDouble(consumeDo.getValue3())));
//		getResourceRate(model, type, afResourceDo, "instalment");
		if (null != borrowId && 0 != borrowId) {
			AfBorrowDo afBorrowDo = afBorrowService.getBorrowById(borrowId);
			GetSeal(model, afUserDo, accountDo);
			lender(model, null);
			Date date = afBorrowDo.getGmtCreate();
			getEdspayInfo(model, borrowId, (byte) 2);
			if (afBorrowDo.getVersion() == 0) {//老版分期
				return "redirect:/fanbei-web/app/protocolFenqiService?userName=" + userName +
						"&borrowId=" + borrowId + "&nper=" + nper + "&amount=" + borrowAmount +
						"&poundage=" + poundage;
			}
			BigDecimal nperAmount = afBorrowDo.getNperAmount();
			model.put("nperAmount", nperAmount);
			model.put("gmtStart", date);
			model.put("gmtEnd", DateUtil.addMonths(date, nper));
			nper = afBorrowDo.getNper();
			List repayPlan = new ArrayList();
			if (nper != null) {
				BigDecimal money = afBorrowDo.getNperAmount().subtract(afBorrowDo.getAmount().divide(BigDecimal.valueOf(afBorrowDo.getNper())));
				for (int i = 0; i < nper; i++) {
					AfBorrowDo borrowDo = new AfBorrowDo();
					borrowDo.setGmtCreate(DateUtil.addMonths(date, i));
					borrowDo.setNperAmount(money);
					borrowDo.setAmount(afBorrowDo.getAmount().divide(BigDecimal.valueOf(afBorrowDo.getNper())));
					borrowDo.setNper(i + 1);
					repayPlan.add(borrowDo);
				}
				model.put("repayPlan", repayPlan);
			}
		}

		model.put("amountCapital", toCapital(borrowAmount.doubleValue()));
		model.put("amountLower", borrowAmount);
//		model.put("poundage", consumeDo.getValue1());
		model.put("poundage", poundage);

		/*model.put("gmtStart", date);
		if ("SEVEN".equals(type)){
			model.put("gmtEnd", DateUtil.addDays(date, 6));
		}else if ("FOURTEEN".equals(type)){
			model.put("gmtEnd", DateUtil.addDays(date, 13));
		}

		int repayDay = 20;
		AfUserOutDayDo afUserOutDayDo =  afUserOutDayDao.getUserOutDayByUserId(userId);
		if(afUserOutDayDo !=null) {
			repayDay = afUserOutDayDo.getPayDay();
		}
		model.put("repayDay", repayDay);*/

		logger.info(JSON.toJSONString(model));
		return "/fanbei-web/app/protocolLegalInstalmentV2";
	}

	/**
	 * 借钱协议(白领贷)
	 *
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = {"whiteLoanProtocol"}, method = RequestMethod.GET)
	public void whiteLoanProtocol(HttpServletRequest request, ModelMap model) throws IOException {
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		BigDecimal amount = NumberUtil.objToBigDecimalDefault(request.getParameter("amount"), new BigDecimal(0));
		Integer nper = NumberUtil.objToIntDefault(request.getParameter("nper"), 0);
		long loanId = NumberUtil.objToLongDefault(request.getParameter("loanId"), 0);
		String loanRemark = ObjectUtils.toString(request.getParameter("loanRemark"), "").toString();
		String repayRemark = ObjectUtils.toString(request.getParameter("repayRemark"), "").toString();
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
		model.put("mobile", afUserDo.getUserName());// 联系电话
		model.put("amountCapital", toCapital(amount.doubleValue()));//大写本金金额
		model.put("amount", amount);//借钱本金
		if(loanId>0){//借了钱的借钱协议
			getModelLoanId(model, nper, loanId, afUserDo, accountDo,repayRemark,loanRemark);
		}else{//借钱前的借钱协议
			getModelNoLoanId(model, amount, nper, loanRemark, repayRemark, userId);
		}
		logger.info(JSON.toJSONString(model));
	}

	private void getModelLoanId(ModelMap model, Integer nper, long loanId, AfUserDo afUserDo, AfUserAccountDo accountDo,String repayRemark,String loanRemark) {
		AfLoanDo afLoanDo = afLoanService.selectById(loanId);
		Calendar c = Calendar.getInstance();
		c.setTime(afLoanDo.getGmtCreate());
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DATE);
		int year = c.get(Calendar.YEAR);
		String time = year + "年" + month + "月" + day + "日";
		model.put("time", time);// 签署日期
		model.put("gmtStart",time);
		model.put("loanNo", afLoanDo.getLoanNo());//原始借款协议编号
		List<AfLoanPeriodsDo> afLoanPeriodsDoList = afLoanPeriodsService.listByLoanId(loanId);
		if(null != afLoanPeriodsDoList && afLoanPeriodsDoList.size()>0){
            List<Object> array = new ArrayList<Object>();
            for(int i=1;i<afLoanDo.getPeriods();i++){
                Map<String,Object> map = new HashMap<String,Object>();
                AfLoanPeriodsDo afLoanPeriodsDo = afLoanPeriodsDoList.get(i);
                if(i == nper){
                    c.setTime(afLoanPeriodsDo.getGmtPlanRepay());
                    int periodsMonth = c.get(Calendar.MONTH)+1;
                    int periodsDay = c.get(Calendar.DATE);
                    int periodsYear = c.get(Calendar.YEAR);
                    String periodsTime = periodsYear + "年" + periodsMonth + "月" + periodsDay + "日";
                    model.put("gmtEnd",periodsTime);
                    model.put("days",periodsDay);
                }
                map.put("gmtPlanRepay",afLoanPeriodsDo.getGmtPlanRepay());
                map.put("loanAmount",afLoanPeriodsDo.getAmount());
                map.put("fee",afLoanPeriodsDo.getInterestFee().add(afLoanPeriodsDo.getServiceFee()));
                array.add(map);
            }
			model.put("nperArray",array);
        }
		model.put("repayRemark",repayRemark);//还款方式
		model.put("loanRemark",loanRemark);//借钱用途
		model.put("totalPeriods",afLoanDo);//总借钱信息
		GetSeal(model, afUserDo, accountDo);
		getEdspayInfo(model, loanId, (byte) 2);
	}

	private void getModelNoLoanId(ModelMap model, BigDecimal amount, Integer nper, String loanRemark, String repayRemark, Long userId) {
		List<Object> resultList = afLoanPeriodsService.resolvePeriods(amount,userId,nper,"","BLD_LOAN");
		if(null != resultList && resultList.size()>0){
            //借钱总汇
			AfLoanDo afLoanDo = (AfLoanDo)resultList.get(0);
            Calendar c = Calendar.getInstance();
            c.setTime(afLoanDo.getGmtCreate());
            int loanMonth = c.get(Calendar.MONTH)+1;
            int loanDay = c.get(Calendar.DATE);
            int loanYear = c.get(Calendar.YEAR);
            String gmtStart = loanYear + "年" + loanMonth + "月" + loanDay + "日";
            model.put("gmtStart",gmtStart);
            model.put("loanNo", afLoanDo.getLoanNo());//原始借款协议编号
            //借钱分期
            List<Object> array = new ArrayList<Object>();
            for(int i=1;i<=nper;i++){
                Map<String,Object> map = new HashMap<String,Object>();
                AfLoanPeriodsDo afLoanPeriodsDo = (AfLoanPeriodsDo)resultList.get(i);
                if(i == nper){
                    c.setTime(afLoanPeriodsDo.getGmtPlanRepay());
                    int month = c.get(Calendar.MONTH)+1;
                    int day = c.get(Calendar.DATE);
                    int year = c.get(Calendar.YEAR);
                    String time = year + "年" + month + "月" + day + "日";
                    model.put("gmtEnd",time);
					model.put("days",day);
                }
                map.put("gmtPlanRepay",afLoanPeriodsDo.getGmtPlanRepay());
                map.put("loanAmount",afLoanPeriodsDo.getAmount());
                map.put("fee",afLoanPeriodsDo.getInterestFee().add(afLoanPeriodsDo.getServiceFee()));
                array.add(map);
            }
            model.put("nperArray",array);
            model.put("repayRemark",repayRemark);//还款方式
            model.put("loanRemark",loanRemark);//借钱用途
            model.put("totalPeriods",afLoanDo);//总借钱信息
        }
	}

	private void getEdspayInfo(ModelMap model, Long borrowId, byte type) {
		AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
		afContractPdfDo.setTypeId(borrowId);
		afContractPdfDo.setType(type);
		afContractPdfDo = afContractPdfDao.selectByTypeId(afContractPdfDo);
		if (afContractPdfDo != null && afContractPdfDo.getUserSealId() != null) {
			AfUserSealDo afUserSealDo = afUserSealDao.selectById(afContractPdfDo.getUserSealId());
			List<AfContractPdfEdspaySealDto> edspaySealDoList = afContractPdfEdspaySealDao.getByPDFId(afContractPdfDo.getId());
			for (AfContractPdfEdspaySealDto eds:edspaySealDoList) {
				String name = eds.getUserName().substring(0,1);
				if (eds.getUserName().length() == 2){
					eds.setUserName(name+"*");
				}else if (eds.getUserName().length() == 3){
					eds.setUserName(name+"**");
				}
				String cardId = eds.getEdspayUserCardId().substring(0,10);
				eds.setEdspayUserCardId(cardId+"*********");
			}
			model.put("edspayUserCardId", afUserSealDo.getEdspayUserCardId());
			model.put("edspayUserName", afUserSealDo.getUserName());
			model.put("secondSeal", afUserSealDo.getUserSeal());
			model.put("edspaySealDoList", edspaySealDoList);
		}
	}

	private void getResourceRate(ModelMap model, String type, AfResourceDo afResourceDo, String borrowType) {
		if (afResourceDo != null && afResourceDo.getValue2() != null) {
			JSONArray array = new JSONArray();
			if ("instalment".equals(borrowType)) {
				array = JSONObject.parseArray(afResourceDo.getValue3());
				for (int i = 0; i < array.size(); i++) {
					JSONObject jsonObject = array.getJSONObject(i);
					String consumeTag = jsonObject.get("consumeTag").toString();
					if ("INTEREST_RATE".equals(consumeTag)) {//借款利率
						if ("SEVEN".equals(type)) {
							model.put("yearRate", jsonObject.get("consumeSevenDay"));
						} else if ("FOURTEEN".equals(type)) {
							model.put("yearRate", jsonObject.get("consumeFourteenDay"));
						}
					}
					if ("SERVICE_RATE".equals(consumeTag)) {//手续费利率
						if ("SEVEN".equals(type)) {
							model.put("poundageRate", jsonObject.get("consumeSevenDay"));
						} else if ("FOURTEEN".equals(type)) {
							model.put("poundageRate", jsonObject.get("consumeFourteenDay"));
						}
					}
					if ("OVERDUE_RATE".equals(consumeTag)) {//逾期利率
						if ("SEVEN".equals(type)) {
							model.put("overdueRate", jsonObject.get("consumeSevenDay"));
						} else if ("FOURTEEN".equals(type)) {
							model.put("overdueRate", jsonObject.get("consumeFourteenDay"));
						}
					}
				}
			} else if ("borrow".equals(borrowType)) {
				array = JSONObject.parseArray(afResourceDo.getValue2());
				for (int i = 0; i < array.size(); i++) {
					JSONObject jsonObject = array.getJSONObject(i);
					String borrowTag = jsonObject.get("borrowTag").toString();
					if ("INTEREST_RATE".equals(borrowTag)) {//借款利率
						if ("SEVEN".equals(type)) {
							model.put("yearRate", jsonObject.get("borrowSevenDay"));
						} else if ("FOURTEEN".equals(type)) {
							model.put("yearRate", jsonObject.get("borrowFourteenDay"));
						}
					}
					if ("SERVICE_RATE".equals(borrowTag)) {//手续费利率
						if ("SEVEN".equals(type)) {
							model.put("poundageRate", jsonObject.get("borrowSevenDay"));
						} else if ("FOURTEEN".equals(type)) {
							model.put("poundageRate", jsonObject.get("borrowFourteenDay"));
						}
					}
					if ("OVERDUE_RATE".equals(borrowTag)) {//逾期利率
						if ("SEVEN".equals(type)) {
							model.put("overdueRate", jsonObject.get("borrowSevenDay"));
						} else if ("FOURTEEN".equals(type)) {
							model.put("overdueRate", jsonObject.get("borrowFourteenDay"));
						}
					}
				}
			}

		}
	}

	private void lender(ModelMap model, AfFundSideInfoDo fundSideInfo) {
		/*if (fundSideInfo != null && StringUtil.isNotBlank(fundSideInfo.getName())) {
			model.put("lender", fundSideInfo.getName());// 出借人
		} else {
			AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
			model.put("lender", lenderDo.getValue());// 出借人
		}*/
		AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
		model.put("lender", lenderDo.getValue());// 出借人
		/*AfUserSealDo companyUserSealDo = afUserSealDao.selectByUserName(model.get("lender").toString());
		if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
			model.put("secondSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
		}*/
	}

	/*private void GetSeal(ModelMap model, AfUserDo afUserDo, AfUserAccountDo accountDo) {
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
	}*/

	private void GetSeal(ModelMap map, AfUserDo afUserDo, AfUserAccountDo accountDo) {
		try {
			AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-1l);
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("companyUserSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("公司印章不存在 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
			AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
			if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
				logger.error("获取个人印章失败 => {}" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
				throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
			}
			map.put("personUserSeal", "data:image/png;base64," + afUserSealDo.getUserSeal());
			companyUserSealDo = afUserSealDao.selectByUserName("浙江楚橡信息科技股份有限公司");
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("thirdSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取钱包印章失败 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
		} catch (Exception e) {
			logger.error("UserSeal create error", e);
		}
	}

	@RequestMapping(value = {"protocolLegalRenewalV2"}, method = RequestMethod.GET)
	public void protocolLegalRenewal(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext webContext = doWebCheckNoAjax(request, false);
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		String type = ObjectUtils.toString(request.getParameter("type"), "").toString();
		if (userName == null || !webContext.isLogin()) {
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

		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL.getCode());
		getResourceRate(model, type, afResourceDo, "borrow");
		model.put("realName", accountDo.getRealName());//借款人
		model.put("idNumber", accountDo.getIdNumber());//身份证号
		model.put("mobile", afUserDo.getUserName());// 联系电话
		model.put("email", afUserDo.getEmail());//电子邮箱
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		AfBorrowCashDo afBorrowCashDo = null;
		if (borrowId > 0) {
			afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
			if (afBorrowCashDo != null) {
				model.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
				if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
					Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
					Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
					Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
					model.put("gmtBorrowBegin", arrivalStart);//到账时间，借款起息日
					model.put("gmtBorrowEnd", repaymentDay);//借款结束日
					model.put("amountCapital", toCapital(afBorrowCashDo.getAmount().doubleValue()));
					model.put("amountLower", afBorrowCashDo.getAmount());
					//查看有无和资金方关联，有的话，替换里面的借出人信息
					AfFundSideInfoDo fundSideInfo = afFundSideBorrowCashService.getLenderInfoByBorrowCashId(borrowId);
					if (renewalId > 0) {
						lender(model, fundSideInfo);
						model.put("lender", "金泰嘉鼎（深圳）资产管理有限公司");
						GetSeal(model, afUserDo, accountDo);
						AfUserSealDo companyUserSealDo = afUserSealDao.selectByUserName(model.get("lender").toString());
						if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
							model.put("secondSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
						}
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
				if (afRenewalDetailDo != null) {
					/*AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getLastOrderCashByBorrowId(afRenewalDetailDo.getBorrowId());
					if (afBorrowLegalOrderCashDo != null){
						model.put("useType",afBorrowLegalOrderCashDo.getBorrowRemark());
						model.put("poundageRate",afBorrowLegalOrderCashDo.getPoundageRate());//手续费率
						model.put("yearRate",afBorrowLegalOrderCashDo.getInterestRate());//利率
						model.put("overdueRate","36");
					}else {
						getResourceRate(model, type,afResourceDo,"borrow");
					}*/
				}
				// 如果预计还款时间在申请日期之后，则在原预计还款时间的基础上加上续期天数，否则在申请日期的基础上加上续期天数，作为新的续期截止时间
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
				model.put("renewalGmtCreate", afRenewalDetailDo.getGmtCreate());//续借时间
				model.put("renewalAmountCapital", toCapital(afRenewalDetailDo.getRenewalAmount().doubleValue()));//续借金额大写
				model.put("repayAmountLower", afRenewalDetailDo.getCapital());//续借金额小写
				model.put("repayAmountCapital", toCapital(afRenewalDetailDo.getCapital().doubleValue()));//续借金额大写
//				Date gmtRenewalBegin = afRenewalDetailDo.getGmtCreate();
//				Date gmtRenewalEnd = DateUtil.addDays(gmtRenewalBegin, afRenewalDetailDo.getRenewalDay());
			} else {
				getResourceRate(model, type, afResourceDo, "borrow");
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
//				AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
//				BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 借钱手续费率（日）
				String yearRate = afResourceDo.getValue();
				if (yearRate != null && !"".equals(yearRate)) {
					BigDecimal capital = afBorrowCashDo.getAmount().divide(BigDecimal.valueOf(100)).multiply(new BigDecimal(yearRate)).setScale(2, RoundingMode.HALF_UP);
					model.put("repayAmountLower", capital);//续借金额小写
					model.put("repayAmountCapital", toCapital(capital.doubleValue()));//续借金额大写
				}
			}
		}

		logger.info(JSON.toJSONString(model));
	}

	/**
	 * 代买协议
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = { "protocolAgentBuyService" }, method = RequestMethod.GET)
	public void protocolAgentBuyService(HttpServletRequest request, ModelMap model) throws IOException {
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
		if(!StringUtils.isEmpty(accountDo.getRealName())){
			model.put("realName", accountDo.getRealName());//借款人
		}else{
			logger.error("realName not exist" + FanbeiExceptionCode.USER_REALNAME_NOT_EXIST_ERROR);
			throw new FanbeiException(FanbeiExceptionCode.USER_REALNAME_NOT_EXIST_ERROR);
		}
	}

	/**
	 * 平台服务协议(白领贷)
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = { "whiteLoanPlatformServiceProtocol" }, method = RequestMethod.GET)
	public void whiteLoanPlatformServiceProtocol(HttpServletRequest request, ModelMap model) throws IOException {
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		BigDecimal totalServiceFee = NumberUtil.objToBigDecimalDefault(request.getParameter("totalServiceFee"), new BigDecimal(0));
		Long loanId = NumberUtil.objToLongDefault(request.getParameter("loanId"), 0l);
		BigDecimal overdueRate = NumberUtil.objToBigDecimalDefault(request.getParameter("overdueRate"), new BigDecimal(0));
		BigDecimal serviceRate = NumberUtil.objToBigDecimalDefault(request.getParameter("serviceRate"), new BigDecimal(0));
		BigDecimal interestRate = NumberUtil.objToBigDecimalDefault(request.getParameter("interestRate"), new BigDecimal(0));
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

		model.put("email", afUserDo.getEmail());//电子邮箱
		model.put("mobile", afUserDo.getUserName());// 联系电话
		model.put("realName",accountDo.getRealName());
		model.put("totalServiceFee",totalServiceFee);//手续费
		model.put("overdueRate",overdueRate);//逾期费率
		model.put("serviceRate",serviceRate);//手续费率
		model.put("interestRate",interestRate);//借钱利率
		if(loanId > 0){
			AfLoanDo afLoanDo = afLoanService.getById(loanId);
			if(null != afLoanDo){
				model.put("loanNo", afLoanDo.getLoanNo());//原始借款协议编号
			}
			Calendar c = Calendar.getInstance();
			c.setTime(afLoanDo.getGmtCreate());
			int month = c.get(Calendar.MONTH)+1;
			int day = c.get(Calendar.DATE);
			int year = c.get(Calendar.YEAR);
			String time = year + "年" + month + "月" + day + "日";
			model.put("time", time);// 签署日期
			GetSeal(model, afUserDo, accountDo);
		}

	}


	public static String ToBig(int num) {
		String str[] = {"壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾"};
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
