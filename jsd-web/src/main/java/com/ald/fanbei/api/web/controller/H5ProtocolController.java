package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.ProductType;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.itextpdf.text.DocumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author guoshuaiqiang 2018年09月11日下午1:41:05
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/jsd-web/protocol/")
public class H5ProtocolController {
    private final Logger logger = Logger.getLogger(H5ProtocolController.class);

    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
	@Resource
	JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
	@Resource
	JsdESdkService jsdESdkService;
	@Resource
	JsdUserSealService jsdUserSealService;
	@Resource
	BeheadBorrowCashRenewalService beheadBorrowCashRenewalService;
	@Resource
	JsdLegalContractPdfCreateService jsdLegalContractPdfCreateService;

    private static final BigDecimal NUM100 = new BigDecimal(100);

    /**
     * 借钱协议(赊销)
     *
     * @param request
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = {"cashProtocol"}, method = RequestMethod.GET)
    public void cashProtocol(HttpServletRequest request, ModelMap model){
    	try {
    		String openId = request.getParameter("openId");
        	String preview = request.getParameter("preview");
        	String tradeNoXgxy = request.getParameter("tradeNoXgxy");

            JsdUserDo userDo = jsdUserService.getByOpenId(openId);
            if (userDo == null) {
                logger.error("user not exist" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
                throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            this.inpourUserInfo(model, userDo);


            JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_CASH.name());
            model.put("yfCompany", resdo.getValue1());
            model.put("bfCompany", resdo.getValue2());
            model.put("dfCompany", resdo.getValue3());
			String borrowRemark ="" ,repayRemark = "";
            BigDecimal amountLower, interestRate, serviceRate;
            if(StringUtils.isNotBlank(tradeNoXgxy)) {
            	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);

            	amountLower = cashDo.getAmount();
            	interestRate = cashDo.getInterestRate();
            	serviceRate = cashDo.getPoundageRate();
				repayRemark = cashDo.getRepayRemark();
				borrowRemark = cashDo.getBorrowRemark();
            	model.put("borrowNo", cashDo.getBorrowNo());
            	model.put("gmtStart", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtEnd", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtPlanRepayment", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));

				getCompanySeal(model);
				getUserSeal(model,userDo);
            }else{
            	TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
            	trialBo.req = JSON.parseObject(preview, TrialBeforeBorrowReq.class);
            	trialBo.req.openId = openId;
            	trialBo.req.term = trialBo.req.nper;
            	trialBo.userId = userDo.getRid();
            	trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(openId,trialBo.req.term,trialBo.req.unit);
            	jsdBorrowCashService.resolve(trialBo);

            	amountLower = new BigDecimal(trialBo.resp.borrowAmount);
            	ResourceRateInfoBo rateInfo = jsdResourceService.getRateInfo(trialBo.req.nper);
            	interestRate = rateInfo.interestRate;
            	serviceRate = rateInfo.serviceRate;
            }
			model.put("borrowRemark",borrowRemark);
			model.put("repayRemark",repayRemark);
            model.put("interestRate", interestRate.multiply(NUM100).setScale(2) + "%");
            model.put("serviceRate", serviceRate.multiply(NUM100).setScale(2) + "%");
            model.put("amountCapital", NumberUtil.number2CNMontrayUnit(amountLower));
            model.put("amountLower", amountLower);

    	}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    }

    /**
     * 商品分期服务协议
     *
     * @param request
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = {"orderProtocol"}, method = RequestMethod.GET)
    public void orderProtocol(HttpServletRequest request, ModelMap model){
    	try {
    		String openId = request.getParameter("openId");
        	String preview = request.getParameter("preview");
        	String tradeNoXgxy = request.getParameter("tradeNoXgxy");

            JsdUserDo userDo = jsdUserService.getByOpenId(openId);
            if (userDo == null) {
                logger.error("user not exist" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
                throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            this.inpourUserInfo(model, userDo);

            JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_ORDER.name());
            model.put("yfCompany", resdo.getValue1());
            model.put("bfCompany", resdo.getValue2());


            BigDecimal amountLower, interestRate, serviceRate, overdueRate;
            if(StringUtils.isNotBlank(tradeNoXgxy)) {
            	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
            	JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(cashDo.getRid());
				JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getLastOrderByBorrowId(cashDo.getRid());
				if(orderCashDo == null){
                    amountLower = jsdBorrowLegalOrderDo.getPriceAmount();
					interestRate = cashDo.getInterestRate();
					serviceRate = cashDo.getPoundageRate();
					overdueRate = cashDo.getOverdueRate();
					model.put("gmtStart", "");
					model.put("gmtEnd", "");
					model.put("gmtPlanRepayment", "");
					model.put("gmtSign", "");
                }else {
                    amountLower = orderCashDo.getAmount();
					interestRate = orderCashDo.getInterestRate();
					serviceRate = orderCashDo.getPoundageRate();
					overdueRate = orderCashDo.getOverdueRate();
					model.put("gmtStart", DateUtil.formatDate(orderCashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
					model.put("gmtEnd", DateUtil.formatDate(orderCashDo.getGmtPlanRepay(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
					model.put("gmtPlanRepayment", DateUtil.formatDate(orderCashDo.getGmtPlanRepay(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
					model.put("gmtSign", DateUtil.formatDate(orderCashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                }
				getCompanySeal(model);
				getUserSeal(model,userDo);
            }else{
            	TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
            	trialBo.req = JSON.parseObject(preview, TrialBeforeBorrowReq.class);
            	trialBo.req.openId = openId;
            	trialBo.req.term = trialBo.req.nper;
            	trialBo.userId = userDo.getRid();
            	trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(openId,trialBo.req.term,trialBo.req.unit);
            	jsdBorrowCashService.resolve(trialBo);

            	amountLower = new BigDecimal(trialBo.resp.totalDiffFee);
            	ResourceRateInfoBo rateInfo = jsdResourceService.getOrderRateInfo(trialBo.req.nper);
            	interestRate = rateInfo.interestRate;
            	serviceRate = rateInfo.serviceRate;
            	overdueRate = rateInfo.overdueRate;
            }

            model.put("interestRate", interestRate.multiply(NUM100).setScale(2) + "%");
            model.put("serviceRate", serviceRate.multiply(NUM100).setScale(2) + "%");
            model.put("overdueRateDaily", overdueRate.multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%" );
            model.put("amountCapital", NumberUtil.number2CNMontrayUnit(amountLower));
            model.put("amountLower", amountLower);

            // TODO 签章 图片url链接

    	}catch (Exception e) {
    		logger.error(e.getMessage(), e);
		}
    }

    /**
     * 平台服务协议
     *
     * @param request
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = {"platformProtocol"}, method = RequestMethod.GET)
    public void platformProtocol(HttpServletRequest request, ModelMap model){
    	try {
	    	String openId = request.getParameter("openId");
	    	String preview = request.getParameter("preview");
	    	String tradeNoXgxy = request.getParameter("tradeNoXgxy");

	        JsdUserDo userDo = jsdUserService.getByOpenId(openId);
	        if (userDo == null) {
	            logger.error("user not exist" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
	            throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
	        }
	        this.inpourUserInfo(model, userDo);

	        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_PLATFORM.name());
	        model.put("jfCompany", resdo.getValue1());

	        if(StringUtils.isNotBlank(tradeNoXgxy)) {
	        	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);

	        	model.put("borrowNo", cashDo.getBorrowNo());
	        	model.put("interestRate", cashDo.getInterestRate().multiply(NUM100).setScale(2) + "%" );
	            model.put("serviceRate", cashDo.getPoundageRate().multiply(NUM100).setScale(2) + "%" );
	            model.put("overdueRateDaily", cashDo.getOverdueRate().multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%" );
	            model.put("serviceAmount", cashDo.getPoundageAmount().add(cashDo.getSumRepaidPoundage()));
	            model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				getCompanySeal(model);
				getUserSeal(model,userDo);
	        }else{
	        	TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
	        	trialBo.req = JSON.parseObject(preview, TrialBeforeBorrowReq.class);
	        	trialBo.req.openId = openId;
	        	trialBo.req.term = trialBo.req.nper;
	        	trialBo.userId = userDo.getRid();
	        	trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(openId,trialBo.req.term,trialBo.req.unit);
	        	jsdBorrowCashService.resolve(trialBo);

	        	TrialBeforeBorrowResp resp = trialBo.resp;
	        	model.put("interestRate", new BigDecimal(resp.interestRate).multiply(NUM100).setScale(2) + "%" );
	            model.put("serviceRate", new BigDecimal(resp.serviceRate).multiply(NUM100).setScale(2) + "%" );
	            model.put("overdueRateDaily", new BigDecimal(resp.overdueRate).multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%" );
	            model.put("serviceAmount", resp.serviceAmount);
	        }

	    }catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    }

	private void getCompanySeal(ModelMap map) {
		try {
			JsdUserSealDo companyUserSealDo = jsdESdkService.selectUserSealByUserId(-1l);
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("aldUserSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取阿拉丁印章失败 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
			companyUserSealDo = jsdUserSealService.getUserSealByUserName("浙江楚橡信息科技有限公司");
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("cxSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取楚橡印章失败 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
			companyUserSealDo = jsdUserSealService.getUserSealByUserName("杭州绿游网络科技有限公司");
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("lvSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取绿游印章失败 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
			companyUserSealDo = jsdUserSealService.getUserSealByUserName("金泰嘉鼎（深圳）资产管理有限公司");
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("jtSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取金泰印章失败 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
			companyUserSealDo = jsdUserSealService.getUserSealByUserName("深圳前海资生管家互联网金融服务有限公司");
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("qhSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取前海印章失败 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
			companyUserSealDo = jsdUserSealService.getUserSealByUserName("杭州朗下网络科技有限公司");
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("lxSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取郎下印章失败 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
			companyUserSealDo = jsdUserSealService.getUserSealByUserName("浙江弹个指网络科技有限公司");
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("tgzSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取弹个指印章失败 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
		} catch (Exception e) {
			logger.error("get userSeal  error", e);
		}
	}

	private void getUserSeal(ModelMap map, JsdUserDo afUserDo) {
		JsdUserSealDo afUserSealDo = jsdUserSealService.getUserSeal(afUserDo);
		if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
			logger.error("获取个人印章失败 => {}" + BizExceptionCode.PERSON_SEAL_CREATE_FAILED);
			throw new BizException(BizExceptionCode.PERSON_SEAL_CREATE_FAILED);
		}
		map.put("personUserSeal", "data:image/png;base64," + afUserSealDo.getUserSeal());
	}

	/**
     * 风险提示函
     * @param request
     * @param model
     */
    @RequestMapping(value = { "noticeProtocol" }, method = RequestMethod.GET)
    public void noticeProtocol(HttpServletRequest request, ModelMap model){
    }

    /**
     * 数字证书服务协议（暂时弃用）
     * @param request
     * @param model
     */
    @RequestMapping(value = { "digitalProtocol" }, method = RequestMethod.GET)
    public void numProtocol(HttpServletRequest request, ModelMap model){
    	String openId = request.getParameter("openId");

        JsdUserDo userDo = jsdUserService.getByOpenId(openId);
        if (userDo == null) {
            logger.warn("refer user not exist by openId " + openId);
            return;
        }
        this.inpourUserInfo(model, userDo);

        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_DIGITAL_CERTIFICATE.name());

        model.put("platformCompany", resdo.getValue1());
        model.put("platformCompanyAddress", resdo.getValue2());
        model.put("platformName", resdo.getValue3());
        model.put("platformSimpleName", resdo.getValue4());
    }

    /**
     * 代买协议
     * @param request
     * @param model
     */
    @RequestMapping(value = { "agencyProtocol" }, method = RequestMethod.GET)
    public void agencyProtocol(HttpServletRequest request, ModelMap model){
    	String openId = request.getParameter("openId");

        JsdUserDo userDo = jsdUserService.getByOpenId(openId);
        if (userDo == null) {
            logger.warn("refer user not exist by openId " + openId);
            return;
        }
        this.inpourUserInfo(model, userDo);

        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_AGENCY.name(), ResourceSecType.PROTOCOL_AGENCY.name());

        model.put("yfCompany", resdo.getValue1());

    }

    /**
     * 续期协议(赊销)
     * @param request
     * @param model
     */
    @RequestMapping(value = { "renewalProtocol" }, method = RequestMethod.GET)
    public void renewalProtocol(HttpServletRequest request, ModelMap model){
    	try {
	    	String openId = request.getParameter("openId");
	    	String preview = request.getParameter("preview");
	    	String tradeNoXgxy = request.getParameter("tradeNoXgxy");

	        JsdUserDo userDo = jsdUserService.getByOpenId(openId);
	        if (userDo == null) {
	            logger.error("user not exist" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
	            throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
	        }
	        this.inpourUserInfo(model, userDo);

	        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_RENEWAL.name(), ResourceSecType.PROTOCOL_RENEWAL.name());
	        model.put("yfCompany", resdo.getValue1());
            model.put("bfCompany", resdo.getValue2());

	        if(StringUtils.isNotBlank(tradeNoXgxy)) {
	        	JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalService.getByTradeNoXgxy(tradeNoXgxy);
	        	JsdBorrowCashDo cashDo = jsdBorrowCashService.getById(renewalDo.getBorrowId());

	        	model.put("renewalNo", renewalDo.getTradeNo());
	        	// 原借款
	        	model.put("oriBorrowNo", cashDo.getBorrowNo());
	        	model.put("oriAmount", cashDo.getAmount());
	        	model.put("oriAmountUpper", NumberUtil.number2CNMontrayUnit(cashDo.getAmount()));
	        	model.put("oriInterestRate", cashDo.getInterestRate().multiply(NUM100).setScale(2) + "%");
	        	model.put("oriGmtStart", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("oriGmtEnd", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));

	        	//续期信息
	        	model.put("reAmount", renewalDo.getRenewalAmount());
	        	model.put("reAmountUpper", NumberUtil.number2CNMontrayUnit(renewalDo.getRenewalAmount()));
	        	model.put("reInterestRate", renewalDo.getBaseBankRate().multiply(NUM100).setScale(2) + "%");
				model.put("reGmtStart", DateUtil.formatDate(DateUtil.addDays(renewalDo.getGmtPlanRepayment(),renewalDo.getOverdueDay()), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("reGmtEnd", DateUtil.formatDate(DateUtil.addDays(DateUtil.addDays(renewalDo.getGmtPlanRepayment(), Math.toIntExact(renewalDo.getRenewalDay())),renewalDo.getOverdueDay()), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("remark", renewalDo.getRemark());
	        	model.put("reGmtPlanRepay", DateUtil.formatDate(DateUtil.addDays(renewalDo.getGmtPlanRepayment(),renewalDo.getOverdueDay()), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("reRepayCapital", renewalDo.getCapital());
	        	model.put("reRepayCapitalUpper", NumberUtil.number2CNMontrayUnit(renewalDo.getCapital()));
	        	model.put("reServiceRate", renewalDo.getPoundageRate().multiply(NUM100).setScale(2) + "%");

	        	model.put("overdueRateDaily", cashDo.getOverdueRate().multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%");

	            model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));

				getCompanySeal(model);
				getUserSeal(model,userDo);
	        }else{
	        	JSONObject obj = JSON.parseObject(preview);
	        	String borrowNoXgxy = obj.getString("borrowNo");
	        	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(borrowNoXgxy);

	        	// 原借款
	        	model.put("oriBorrowNo", cashDo.getBorrowNo());
	        	model.put("oriAmount", cashDo.getAmount());
	        	model.put("oriAmountUpper", NumberUtil.number2CNMontrayUnit(cashDo.getAmount()));
	        	model.put("oriInterestRate", cashDo.getInterestRate().multiply(NUM100).setScale(2) + "%");

	        	JSONArray renewalDetail = jsdBorrowCashRenewalService.getRenewalDetail(cashDo);
	        	JSONObject info = renewalDetail.getJSONObject(0);
	        	// 续期信息
	        	model.put("reAmount", info.getString("principalAmount"));
	        	model.put("reAmountUpper", NumberUtil.number2CNMontrayUnit( new BigDecimal(info.getString("principalAmount")) ));
	        	model.put("reInterestRate", new BigDecimal(info.getString("interestRate")).multiply(NUM100).setScale(2) + "%" );
	        	model.put("remark", "续期");
	        	model.put("reRepayCapital", info.getString("payCapital"));
	        	model.put("reRepayCapitalUpper", NumberUtil.number2CNMontrayUnit( new BigDecimal(info.getString("payCapital")) ));
	        	model.put("reServiceRate", new BigDecimal(info.getString("serviceRate")).multiply(NUM100).setScale(2) + "%" );

	        	model.put("overdueRateDaily", cashDo.getOverdueRate().multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%");
	        }

	    }catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    }


	/**
	 * 借钱协议(plus)
	 *
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = {"cashPlusProtocol"}, method = RequestMethod.GET)
	public void cashPlusProtocol(HttpServletRequest request, ModelMap model){
		try {
			String openId = request.getParameter("openId");
			String preview = request.getParameter("preview");
			String tradeNoXgxy = request.getParameter("tradeNoXgxy");
			logger.info("tradeNoXgxy = " + tradeNoXgxy + " ,preview = " + preview);
			JsdUserDo userDo = jsdUserService.getByOpenId(openId);
			if (userDo == null) {
				logger.error("user not exist" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
				throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			}
			this.inpourUserInfo(model, userDo);


			JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PLUS_PROTOCOL_BORROW.name(), ResourceSecType.PLUS_PROTOCOL_BORROW_CASH.name());
			model.put("bfCompany", resdo.getValue2());
			model.put("yfCompany", resdo.getValue3());

			BigDecimal amountLower, interestRate, serviceRate;
			String borrowRemark ="" ,repayRemark = "";
			if(StringUtils.isNotBlank(tradeNoXgxy)) {
				JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);

				amountLower = cashDo.getAmount();
				interestRate = cashDo.getInterestRate();
				serviceRate = cashDo.getPoundageRate();
				borrowRemark = cashDo.getBorrowRemark();
				repayRemark = cashDo.getRepayRemark();
				model.put("borrowNo", cashDo.getBorrowNo());
				model.put("gmtStart", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("gmtEnd", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("gmtPlanRepayment", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("bfurl", resdo.getValue1());
				model.put("yfurl", resdo.getValue4());
				logger.info("cashDo = " + JSON.toJSONString(cashDo) + "model = " + JSON.toJSONString(model));
				getCompanySeal(model);
				getUserSeal(model,userDo);
				if(StringUtils.equals(resdo.getValue5(), ProductType.DFD.name())){
					model.put("bfCompanyPic",model.get("lxSeal"));
					model.put("yfCompanyPic",model.get("tgzSeal"));
				}
				if(StringUtils.equals(resdo.getValue5(), ProductType.JGD.name())){
					model.put("bfCompanyPic",model.get("lvSeal"));
					model.put("yfCompanyPic",model.get("jtSeal"));
				}
			}else{
				TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
				trialBo.req = JSON.parseObject(preview, TrialBeforeBorrowReq.class);
				trialBo.req.openId = openId;
				trialBo.req.term = trialBo.req.nper;
				trialBo.userId = userDo.getRid();
				trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(openId,trialBo.req.term,trialBo.req.unit);
				jsdBorrowCashService.resolve(trialBo);

				amountLower = trialBo.req.amount;
				ResourceRateInfoBo rateInfo = jsdResourceService.getRateInfo(trialBo.req.nper);
				interestRate = rateInfo.interestRate;
				serviceRate = rateInfo.serviceRate;
			}

			model.put("interestRate", interestRate.multiply(NUM100).setScale(2) + "%");
			model.put("serviceRate", serviceRate.multiply(NUM100).setScale(2) + "%");
			model.put("amountCapital", NumberUtil.number2CNMontrayUnit(amountLower));
			model.put("amountLower", amountLower);
			model.put("borrowRemark",borrowRemark);
			model.put("repayRemark",repayRemark);

		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}


	/**
	 * 续期协议(plus)
	 * @param request
	 * @param model
	 */
	@RequestMapping(value = { "renewalPlusProtocol" }, method = RequestMethod.GET)
	public void renewalPlusProtocol(HttpServletRequest request, ModelMap model){
		try {
			String openId = request.getParameter("openId");
			String preview = request.getParameter("preview");
			String tradeNoXgxy = request.getParameter("tradeNoXgxy");
			logger.info("tradeNoXgxy = " + tradeNoXgxy + " ,preview = " + preview);
			JsdUserDo userDo = jsdUserService.getByOpenId(openId);
			if (userDo == null) {
				logger.error("user not exist" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
				throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			}
			this.inpourUserInfo(model, userDo);

			JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PLUS_PROTOCOL_RENEWAL.name(), ResourceSecType.PLUS_PROTOCOL_RENEWAL.name());
			model.put("yfCompany", resdo.getValue1());
			model.put("bfCompany", resdo.getValue2());

			if(StringUtils.isNotBlank(tradeNoXgxy)) {
				JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalService.getByTradeNoXgxy(tradeNoXgxy);
				JsdBorrowCashDo cashDo = jsdBorrowCashService.getById(renewalDo.getBorrowId());

				model.put("renewalNo", renewalDo.getTradeNo());
				// 原借款
				model.put("oriBorrowNo", cashDo.getBorrowNo());
				model.put("oriAmount", cashDo.getAmount());
				model.put("oriAmountUpper", NumberUtil.number2CNMontrayUnit(cashDo.getAmount()));
				model.put("oriInterestRate", cashDo.getInterestRate().multiply(NUM100).setScale(2) + "%");
				model.put("oriGmtStart", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("oriGmtEnd", DateUtil.formatDate(DateUtil.addDays(cashDo.getGmtCreate(),Integer.valueOf(cashDo.getType())-1), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("borrowRemark", cashDo.getBorrowRemark());

				//续期信息
				model.put("reAmount", renewalDo.getRenewalAmount());
				model.put("reAmountUpper", NumberUtil.number2CNMontrayUnit(renewalDo.getRenewalAmount()));
				model.put("reInterestRate", renewalDo.getBaseBankRate().multiply(NUM100).setScale(2) + "%");
				model.put("reGmtStart", DateUtil.formatDate(DateUtil.addDays(renewalDo.getGmtPlanRepayment(),renewalDo.getOverdueDay()), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("reGmtEnd", DateUtil.formatDate(DateUtil.addDays(DateUtil.addDays(renewalDo.getGmtPlanRepayment(), Math.toIntExact(renewalDo.getRenewalDay())),renewalDo.getOverdueDay()), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("remark", renewalDo.getRemark());
				model.put("reGmtPlanRepay", DateUtil.formatDate(DateUtil.addDays(DateUtil.addDays(renewalDo.getGmtPlanRepayment(),Math.toIntExact(renewalDo.getRenewalDay())),renewalDo.getOverdueDay()), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("reRepayCapital", renewalDo.getCapital());
				model.put("reRepayCapitalUpper", NumberUtil.number2CNMontrayUnit(renewalDo.getCapital()));
				model.put("reServiceRate", renewalDo.getPoundageRate().multiply(NUM100).setScale(2) + "%");
				logger.info(" renewalPlusProtocol reServiceRate = "+renewalDo.getPoundageRate().multiply(NUM100).setScale(2));

				model.put("overdueRateDaily", cashDo.getOverdueRate().multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%");

				model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
				model.put("bfurl", resdo.getValue3());
				model.put("yfurl", resdo.getValue4());
				logger.info("cashDo = " + JSON.toJSONString(cashDo) +" ,renewalDo = " + JSON.toJSONString(renewalDo) + " ,model = " + JSON.toJSONString(model));

				getCompanySeal(model);
				getUserSeal(model,userDo);
				if(StringUtils.equals(resdo.getValue5(), ProductType.DFD.name())){
					model.put("bfCompanyPic",model.get("lxSeal"));
					model.put("yfCompanyPic",model.get("tgzSeal"));
				}
				if(StringUtils.equals(resdo.getValue5(), ProductType.JGD.name())){
					model.put("bfCompanyPic",model.get("lvSeal"));
					model.put("yfCompanyPic",model.get("qhSeal"));
				}
			}else{
				JSONObject obj = JSON.parseObject(preview);
				String borrowNoXgxy = obj.getString("borrowNo");
				JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(borrowNoXgxy);

				// 原借款
				model.put("oriBorrowNo", cashDo.getBorrowNo());
				model.put("oriAmount", cashDo.getAmount());
				model.put("oriAmountUpper", NumberUtil.number2CNMontrayUnit(cashDo.getAmount()));
				model.put("oriInterestRate", cashDo.getInterestRate().multiply(NUM100).setScale(2) + "%");
				model.put("borrowRemark", cashDo.getBorrowRemark());
				JSONArray renewalDetail = beheadBorrowCashRenewalService.getBeheadRenewalDetail(cashDo);
				JSONObject info = renewalDetail.getJSONObject(0);
				logger.info("info = " + info);
				// 续期信息
				model.put("reAmount", info.getString("principalAmount"));
				model.put("reAmountUpper", NumberUtil.number2CNMontrayUnit( new BigDecimal(info.getString("principalAmount")) ));
				model.put("reInterestRate", new BigDecimal(info.getString("interestRate")).multiply(NUM100).setScale(2) + "%" );
				model.put("remark", "续期");
				model.put("reRepayCapital", info.getString("payCapital"));
				model.put("reRepayCapitalUpper", NumberUtil.number2CNMontrayUnit( new BigDecimal(info.getString("payCapital")) ));
				model.put("reServiceRate", new BigDecimal(info.getString("serviceRate")).multiply(NUM100).setScale(2) + "%" );

				model.put("overdueRateDaily", cashDo.getOverdueRate().multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%");
			}

		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@ResponseBody
	@RequestMapping(value = {"protocolSellPdf"}, method = RequestMethod.POST)
	public void protocolSellPdf( String data) throws IOException, DocumentException {
		jsdLegalContractPdfCreateService.platformServiceSellProtocol(data);
	}

	@ResponseBody
	@RequestMapping(value = {"protocolBeheadPdf"}, method = RequestMethod.POST)
	public void protocolBeheadPdf( String data) throws IOException, DocumentException {
		jsdLegalContractPdfCreateService.platformServiceBeheadProtocol(data);
	}

	@ResponseBody
	@RequestMapping(value = {"decrypt"}, method = RequestMethod.POST)
	public String decrypt(String data,String password) throws IOException, DocumentException {
		return AesUtil.decryptFromBase64(data,password);
	}


    private void inpourUserInfo(ModelMap model, JsdUserDo userDo) {
    	model.put("idNumber", this.privacyIdNumber(userDo.getIdNumber()));
        model.put("realName", this.privacyRealName(userDo.getRealName()));
        model.put("email", userDo.getEmail());							//电子邮箱
        model.put("mobile", this.privacyMobile(userDo.getMobile()));	// 联系电话
    }
    private String privacyMobile(String mobile) {
    	String first = mobile.substring(0, 3);
        String second = mobile.substring(7);
        return first + "****"+second;
    }

    private String privacyRealName(String userName) {
        if (userName.length() == 2 || userName.length() == 3) {
            return "*" + userName.substring(1);
        } else if (userName.length() > 3){
        	return "**" + userName.substring(2);
        }
        return userName;
    }

    private String privacyIdNumber(String idNumber) {
        String firstCardId = idNumber.substring(0, 3);
        String secondCardId = idNumber.substring(14);
        return firstCardId + "****"+secondCardId;
    }

}
