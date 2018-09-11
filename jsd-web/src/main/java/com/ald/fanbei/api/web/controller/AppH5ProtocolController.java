package com.ald.fanbei.api.web.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author guoshuaiqiang 2017年12月19日下午1:41:05
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/jsd-web/h5/")
public class AppH5ProtocolController extends H5ProtocolController {


    private static final Logger logger = Logger.getLogger(AppH5ProtocolController.class);
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdResourceService afResourceService;
    @Resource
    JsdBorrowCashService afBorrowCashService;
    @Resource
    JsdBorrowLegalOrderCashService afBorrowLegalOrderCashService;
    @Resource
    JsdBorrowLegalOrderService afBorrowLegalOrderService;
    @Resource
    NumberWordFormat numberWordFormat;

    @RequestMapping(value = { "numProtocol" }, method = RequestMethod.GET)
    public void numProtocol(HttpServletRequest request, ModelMap model)throws IOException{
        String userName = ObjectUtils.toString(request.getParameter("openId"), "").toString();
        JsdUserDo afUserDo = jsdUserService.getByOpenId(userName);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        model.put("idNumber", afUserDo.getIdNumber());
        model.put("realName", afUserDo.getRealName());
    }

    @RequestMapping(value = {"protocolLegalInstalment"}, method = RequestMethod.GET)
    public void protocolLegalInstalment(HttpServletRequest request, ModelMap model) throws IOException {
        String openId = ObjectUtils.toString(request.getParameter("openId"), "").toString();
        String type = ObjectUtils.toString(request.getParameter("type"), "").toString();
        Long borrowId = NumberUtil.objToLongDefault(request.getParameter("borrowId"), 0);
        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("amount"), new BigDecimal(0));//借款本金
        JsdUserDo afUserDo = jsdUserService.getByOpenId(openId);
        JsdResourceDo afResourceDo = afResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RATE_INFO.getCode());
        model.put("idNumber", afUserDo.getIdNumber());
        model.put("realName", afUserDo.getRealName());
        model.put("mobile", afUserDo.getMobile());// 联系电话
        Date date = new Date();
        if (null != borrowId && 0 != borrowId) {
//            GetSeal(model, afUserDo, accountDo);
//            lender(model, null);
            JsdBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getLastOrderCashByBorrowId(borrowId);
            JsdBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderService.getById(afBorrowLegalOrderCashDo.getBorrowLegalOrderId());
            type = afBorrowLegalOrderCashDo.getType();
            getResourceRate(model, type, afResourceDo, "instalment");
            if (afBorrowLegalOrderCashDo != null) {
                model.put("instalmentGmtCreate", afBorrowLegalOrderCashDo.getGmtCreate());
                model.put("instalmentRepayDay", afBorrowLegalOrderCashDo.getGmtPlanRepay());
                model.put("gmtStart", afBorrowLegalOrderDo.getGmtCreate());
                model.put("gmtEnd", afBorrowLegalOrderCashDo.getGmtPlanRepay());
                model.put("poundageRate", afBorrowLegalOrderCashDo.getPoundageRate());//手续费率
                model.put("yearRate", afBorrowLegalOrderCashDo.getInterestRate());//利率
                model.put("amountCapital", toCapital(afBorrowLegalOrderCashDo.getAmount().doubleValue()));
                model.put("amountLower", afBorrowLegalOrderCashDo.getAmount());
            }
        } else {
            model.put("amountCapital", toCapital(borrowAmount.doubleValue()));
            model.put("amountLower", borrowAmount);
            model.put("gmtStart", date);
            model.put("gmtEnd", DateUtil.addDays(date, numberWordFormat.borrowTime(type) - 1));
            getResourceRate(model, type, afResourceDo, "instalment");
        }
    }


    /**
     * 平台服务协议
     * @param request
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = { "platformServiceProtocol" }, method = RequestMethod.GET)
    public void platformServiceProtocol(HttpServletRequest request, ModelMap model) throws IOException {
        String openId = ObjectUtils.toString(request.getParameter("openId"), "");
        String type = ObjectUtils.toString(request.getParameter("type"), "").toString();
        BigDecimal poundage = NumberUtil.objToBigDecimalDefault(request.getParameter("poundage"), new BigDecimal(0));
        String loanNo = ObjectUtils.toString(request.getParameter("loanNo"), "");
        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("borrowAmount"), new BigDecimal(0));

        JsdUserDo afUserDo = jsdUserService.getByOpenId(openId);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        JsdResourceDo afResourceDo = afResourceService.getByTypeAngSecType(ResourceType.BORROW_RATE.getCode(), ResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
        getResourceRate(model, type,afResourceDo,"borrow");
        model.put("email", afUserDo.getEmail());//电子邮箱
        model.put("mobile", afUserDo.getUserName());// 联系电话
        model.put("realName",afUserDo.getRealName());
//		Integer days = NumberUtil.objToIntDefault(type, 0);
//		BigDecimal serviceAmount = borrowAmount.multiply(new BigDecimal(days)).multiply(new BigDecimal(model.get("SERVICE_RATE").toString())).divide(BigDecimal.valueOf(360)).setScale(2,BigDecimal.ROUND_HALF_UP);
        model.put("poundage",poundage);//手续费
        if (model.get("overdueRate") != null){
            String overdueRate =  model.get("overdueRate").toString();
            model.put("overdueRate",BigDecimal.valueOf(Double.parseDouble(overdueRate)).divide(BigDecimal.valueOf(360)));
        }
        if(null != loanNo){
            JsdBorrowCashDo afBorrowCashDo = afBorrowCashService.getByTradeNoXgxy(loanNo);
            if(null != afBorrowCashDo){
                model.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
            }
            Calendar c = Calendar.getInstance();
            c.setTime(afBorrowCashDo.getGmtCreate());
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DATE);
            int year = c.get(Calendar.YEAR);
            String time = year + "年" + month + "月" + day + "日";
            model.put("time", time);// 签署日期
        }

    }
    /**
     * 借钱协议
     *
     * @param request
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = {"loanProtocol"}, method = RequestMethod.GET)
    public void loanProtocol(HttpServletRequest request, ModelMap model) throws IOException {
        String openId = ObjectUtils.toString(request.getParameter("openId"), "");
        String type = ObjectUtils.toString(request.getParameter("type"), "").toString();

        Long borrowId = NumberUtil.objToLongDefault(request.getParameter("borrowId"), 0l);
        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("borrowAmount"), new BigDecimal(0));

        JsdUserDo afUserDo = jsdUserService.getByOpenId(openId);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();
        JsdResourceDo afResourceDo = afResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RATE_INFO.getCode());
        getResourceRate(model, type, afResourceDo, "borrow");
        model.put("idNumber", afUserDo.getIdNumber());
        model.put("realName", afUserDo.getRealName());
        model.put("email", afUserDo.getEmail());//电子邮箱
        model.put("mobile", afUserDo.getMobile());// 联系电话
        JsdBorrowCashDo afBorrowCashDo = null;

        model.put("amountCapital", toCapital(borrowAmount.doubleValue()));
        model.put("amountLower", borrowAmount);

        if (borrowId > 0) {
            afBorrowCashDo = afBorrowCashService.getById(borrowId);
            if (afBorrowCashDo != null) {
                model.put("gmtStart", afBorrowCashDo.getGmtCreate());
                model.put("gmtEnd", afBorrowCashDo.getGmtPlanRepayment());
                JsdBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(borrowId);
                if (afBorrowLegalOrderCashDo != null) {
                    model.put("useType", afBorrowLegalOrderCashDo.getBorrowRemark());
					/*model.put("poundageRate",afBorrowLegalOrderCashDo.getPoundageRate());//手续费率
					model.put("yearRate",afBorrowLegalOrderCashDo.getInterestRate());//利率
					model.put("overdueRate","36");*/
                } else {
                    getResourceRate(model, type, afResourceDo, "borrow");
                }
                model.put("gmtCreate", afBorrowCashDo.getGmtCreate());// 出借时间
                model.put("borrowNo", afBorrowCashDo.getBorrowNo());
                if (StringUtils.equals(afBorrowCashDo.getStatus(), "TRANSFERRED") || StringUtils.equals(afBorrowCashDo.getStatus(), "FINISHED")) {
                    model.put("gmtArrival", afBorrowCashDo.getGmtArrival());
//					Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                    Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
                    Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                    Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                    model.put("repaymentDay", repaymentDay);
                    model.put("gmtPlanRepayment", afBorrowCashDo.getGmtPlanRepayment());
                    //查看有无和资金方关联，有的话，替换里面的借出人信息
//                    GetSeal(model, afUserDo, accountDo);
//                    lender(model, fundSideInfo);
                }
            } else {
                getResourceRate(model, type, afResourceDo, "borrow");
            }
        } else {
            getResourceRate(model, type, afResourceDo, "borrow");
        }

        logger.info(JSON.toJSONString(model));
    }

    private void getResourceRate(ModelMap model, String type, JsdResourceDo afResourceDo, String borrowType) {
        if (afResourceDo != null && afResourceDo.getValue2() != null) {
            JSONArray array = new JSONArray();
            String oneDay = afResourceDo.getTypeDesc().split(",")[0];
            String twoDay = afResourceDo.getTypeDesc().split(",")[1];
            if ("instalment".equals(borrowType)) {
                JSONObject jsonObject = JSONObject.parseObject(afResourceDo.getValue3());
                JSONObject rateDetail = JSONObject.toJSONO()jsonObject.get(type) ;
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String consumeTag = jsonObject.get("consumeTag").toString();
                    if ("INTEREST_RATE".equals(consumeTag)) {//借款利率

                    }
                    if ("SERVICE_RATE".equals(consumeTag)) {//手续费利率
                        if ("SEVEN".equals(type)) {
                            model.put("poundageRate", jsonObject.get("consumeFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            model.put("poundageRate", jsonObject.get("consumeSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                model.put("poundageRate", jsonObject.get("consumeFirstType"));
                            } else if (twoDay.equals(type)) {
                                model.put("poundageRate", jsonObject.get("consumeSecondType"));
                            }
                        }
                    }
                    if ("OVERDUE_RATE".equals(consumeTag)) {//逾期利率
                        if ("SEVEN".equals(type)) {
                            model.put("overdueRate", jsonObject.get("consumeFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            model.put("overdueRate", jsonObject.get("consumeSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                model.put("overdueRate", jsonObject.get("consumeFirstType"));
                            } else if (twoDay.equals(type)) {
                                model.put("overdueRate", jsonObject.get("consumeSecondType"));
                            }
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
                            model.put("yearRate", jsonObject.get("borrowFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            model.put("yearRate", jsonObject.get("borrowSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                model.put("yearRate", jsonObject.get("borrowFirstType"));
                            } else if (twoDay.equals(type)) {
                                model.put("yearRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                    if ("SERVICE_RATE".equals(borrowTag)) {//手续费利率
                        if ("SEVEN".equals(type)) {
                            model.put("poundageRate", jsonObject.get("borrowFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            model.put("poundageRate", jsonObject.get("borrowSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                model.put("poundageRate", jsonObject.get("borrowFirstType"));
                            } else if (twoDay.equals(type)) {
                                model.put("poundageRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                    if ("OVERDUE_RATE".equals(borrowTag)) {//逾期利率
                        if ("SEVEN".equals(type)) {
                            model.put("overdueRate", jsonObject.get("borrowFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            model.put("overdueRate", jsonObject.get("borrowSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                model.put("overdueRate", jsonObject.get("borrowFirstType"));
                            } else if (twoDay.equals(type)) {
                                model.put("overdueRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                }
            }

        }
    }

    private void lender(ModelMap model) {
		/*if (fundSideInfo != null && StringUtil.isNotBlank(fundSideInfo.getName())) {
			model.put("lender", fundSideInfo.getName());// 出借人
		} else {
			JsdResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
			model.put("lender", lenderDo.getValue());// 出借人
		}*/
/*        JsdResourceDo lenderDo = afResourceService.getByTypeAngSecType(ResourceType.borrowRate.getCode(), JsdResourceSecType.borrowCashLenderForCash.getCode());
        model.put("lender", lenderDo.getValue());// 出借人
        JsdUserSealDo companyUserSealDo = afUserSealDao.selectByUserName(model.get("lender").toString());
        if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
            model.put("secondSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
        }*/
    }

    private void GetSeal(ModelMap model, JsdUserDo afUserDo) {
       /* try {
            JsdUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-1l);
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                model.put("CompanyUserSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
            }
            JsdUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
            if (null != afUserSealDo && null != afUserSealDo.getUserSeal()) {
                model.put("personUserSeal", "data:image/png;base64," + afUserSealDo.getUserSeal());
            }
        } catch (Exception e) {
            logger.error("UserSeal create error", e);
        }*/
    }

   /* @RequestMapping(value = {"protocolLegalRenewal"}, method = RequestMethod.GET)
    public void protocolLegalRenewal(HttpServletRequest request, ModelMap model) throws IOException {
//		FanbeiWebContext webContext = doWebCheckNoAjax(request, false);
        String openId = ObjectUtils.toString(request.getParameter("openId"), "");
        String type = ObjectUtils.toString(request.getParameter("type"), "").toString();
//		if(userName == null || !webContext.isLogin() ) {
//			throw new FanbeiException("非法用户");
//		}
        Long borrowId = NumberUtil.objToLongDefault(request.getParameter("borrowId"), 0l);
        Long renewalId = NumberUtil.objToLongDefault(request.getParameter("renewalId"), 0l);
        int renewalDay = NumberUtil.objToIntDefault(request.getParameter("renewalDay"), 0);
        BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("renewalAmount"), BigDecimal.ZERO);

        JsdUserDo afUserDo = jsdUserService.getByOpenId(openId);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();

        JsdResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), JsdResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
        getResourceRate(model, type, afResourceDo, "borrow");
        model.put("realName", afUserDo.getRealName());//借款人
        model.put("idNumber", afUserDo.getIdNumber());//身份证号
        model.put("mobile", afUserDo.getMobile());// 联系电话
        model.put("email", afUserDo.getEmail());//电子邮箱
        List<JsdResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
        JsdBorrowCashDo afBorrowCashDo = null;
        if (borrowId > 0) {
            afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
            if (afBorrowCashDo != null) {
                model.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
                if (StringUtils.equals(afBorrowCashDo.getStatus(), JsdBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), JsdBorrowCashStatus.finsh.getCode())) {
//					Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                    Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
                    Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                    Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                    model.put("gmtBorrowBegin", arrivalStart);//到账时间，借款起息日
                    model.put("gmtBorrowEnd", repaymentDay);//借款结束日
                    model.put("amountCapital", toCapital(afBorrowCashDo.getAmount().doubleValue()));
                    model.put("amountLower", afBorrowCashDo.getAmount());
                    //查看有无和资金方关联，有的话，替换里面的借出人信息
                    JsdFundSideInfoDo fundSideInfo = afFundSideBorrowCashService.getLenderInfoByBorrowCashId(borrowId);
                    if (renewalId > 0) {
                        lender(model, fundSideInfo);
                        GetSeal(model, afUserDo, accountDo);
                    }
					*//*if(fundSideInfo!=null && StringUtil.isNotBlank(fundSideInfo.getName())){
						model.put("lender", fundSideInfo.getName());// 出借人
					}else{
						AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
						model.put("lender", lenderDo.getValue());// 出借人
					}*//*
                }
            }

            if (renewalId > 0) {
                JsdRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByRenewalId(renewalId);
                Date gmtCreate = afRenewalDetailDo.getGmtCreate();
                Date gmtPlanRepayment = afRenewalDetailDo.getGmtPlanRepayment();
                if (afRenewalDetailDo != null) {
					*//*AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getLastOrderCashByBorrowId(afRenewalDetailDo.getBorrowId());
					if (afBorrowLegalOrderCashDo != null){
						model.put("useType",afBorrowLegalOrderCashDo.getBorrowRemark());
						model.put("poundageRate",afBorrowLegalOrderCashDo.getPoundageRate());//手续费率
						model.put("yearRate",afBorrowLegalOrderCashDo.getInterestRate());//利率
						model.put("overdueRate","36");
					}else {
						getResourceRate(model, type,afResourceDo,"borrow");
					}*//*
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
    }*/

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

}
