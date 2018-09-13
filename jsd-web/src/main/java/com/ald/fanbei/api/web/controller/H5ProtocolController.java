package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


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
	JsdESdkService jsdESdkService;
	@Resource
	JsdUserSealService jsdUserSealService;

    /**
     * 借钱协议
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
                logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
                throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            
            JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_CASH.name());
            model.put("yfCompany", resdo.getValue1());
            model.put("bfCompany", resdo.getValue2());
            model.put("dfCompany", resdo.getValue3());
            
            
            BigDecimal amountLower, interestRate, serviceRate, overdueRate;
            if(StringUtils.isNotBlank(tradeNoXgxy)) {
            	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
            	
            	amountLower = cashDo.getAmount();
            	interestRate = cashDo.getInterestRate();
            	serviceRate = cashDo.getPoundageRate();
            	overdueRate = cashDo.getOverdueRate();
            	
            	model.put("borrowNo", cashDo.getBorrowNo());
            	model.put("gmtStart", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtEnd", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtPlanRepayment", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
            }else{
            	TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
            	trialBo.req = JSON.parseObject(preview, TrialBeforeBorrowReq.class);
            	trialBo.req.openId = openId;
            	trialBo.req.term = trialBo.req.nper;
            	trialBo.userId = userDo.getRid();
            	trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(openId);
            	jsdBorrowCashService.resolve(trialBo);
            	
            	amountLower = new BigDecimal(trialBo.resp.borrowAmount);
            	ResourceRateInfoBo rateInfo = jsdResourceService.getRateInfo(trialBo.req.nper);
            	interestRate = rateInfo.interestRate;
            	serviceRate = rateInfo.serviceRate;
            	overdueRate = rateInfo.overdueRate;
            }
            
            model.put("interestRate", interestRate.setScale(2));
            model.put("serviceRate", serviceRate.setScale(2));
            model.put("overdueRate", overdueRate.setScale(2));
            model.put("idNumber", userDo.getIdNumber());
            model.put("realName", userDo.getRealName());
            model.put("email", userDo.getEmail());//电子邮箱
            model.put("mobile", userDo.getMobile());// 联系电话
            model.put("amountCapital", NumberUtil.number2CNMontrayUnit(amountLower));
            model.put("amountLower", amountLower);
            
            logger.info("cashProtocol, params=" + JSON.toJSONString(model));
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
                logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
                throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            
            JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_ORDER.name());
            model.put("yfCompany", resdo.getValue1());
            model.put("bfCompany", resdo.getValue2());
            
            
            BigDecimal amountLower, interestRate, serviceRate, overdueRate;
            if(StringUtils.isNotBlank(tradeNoXgxy)) {
            	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
            	JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(cashDo.getRid());
            	
            	amountLower = orderCashDo.getAmount();
            	interestRate = orderCashDo.getInterestRate();
            	serviceRate = orderCashDo.getPoundageRate();
            	overdueRate = orderCashDo.getOverdueRate();
            	
            	model.put("gmtStart", DateUtil.formatDate(orderCashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtEnd", DateUtil.formatDate(orderCashDo.getGmtPlanRepay(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtPlanRepayment", DateUtil.formatDate(orderCashDo.getGmtPlanRepay(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
                model.put("gmtSign", DateUtil.formatDate(orderCashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
            }else{
            	TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
            	trialBo.req = JSON.parseObject(preview, TrialBeforeBorrowReq.class);
            	trialBo.req.openId = openId;
            	trialBo.req.term = trialBo.req.nper;
            	trialBo.userId = userDo.getRid();
            	trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(openId);
            	jsdBorrowCashService.resolve(trialBo);
            	
            	amountLower = new BigDecimal(trialBo.resp.totalDiffFee);
            	ResourceRateInfoBo rateInfo = jsdResourceService.getOrderRateInfo(trialBo.req.nper);
            	interestRate = rateInfo.interestRate;
            	serviceRate = rateInfo.serviceRate;
            	overdueRate = rateInfo.overdueRate;
            }
            
            model.put("interestRate", interestRate.setScale(2));
            model.put("serviceRate", serviceRate.setScale(2));
            model.put("overdueRateDaily", overdueRate
            			.multiply(new BigDecimal(100))
            			.divide( new BigDecimal(Constants.ONE_YEAY_DAYS)).setScale(2) );
            model.put("idNumber", userDo.getIdNumber());
            model.put("realName", userDo.getRealName());
            model.put("email", userDo.getEmail());//电子邮箱
            model.put("mobile", userDo.getMobile());// 联系电话
            model.put("amountCapital", NumberUtil.number2CNMontrayUnit(amountLower));
            model.put("amountLower", amountLower);
            
            // TODO 签章 图片url链接
            
            logger.info("orderProtocol, params=" + JSON.toJSONString(model));
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
	            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
	            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
	        }
	        
	        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_PLATFORM.name());
	        model.put("jfCompany", resdo.getValue1());
	        
	        if(StringUtils.isNotBlank(tradeNoXgxy)) {
	        	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
	        	
	        	model.put("borrowNo", cashDo.getBorrowNo());
	        	model.put("interestRate", cashDo.getInterestRate().setScale(2));
	            model.put("serviceRate", cashDo.getPoundageRate().setScale(2));
	            model.put("overdueRate", cashDo.getOverdueRate().setScale(2));
	            model.put("serviceAmount", cashDo.getPoundageAmount());
	            model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        }else{
	        	TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
	        	trialBo.req = JSON.parseObject(preview, TrialBeforeBorrowReq.class);
	        	trialBo.req.openId = openId;
	        	trialBo.req.term = trialBo.req.nper;
	        	trialBo.userId = userDo.getRid();
	        	trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(trialBo.req.nper);
	        	jsdBorrowCashService.resolve(trialBo);
	        	
	        	TrialBeforeBorrowResp resp = trialBo.resp;
	        	model.put("interestRate", resp.interestRate);
	            model.put("serviceRate", resp.serviceRate);
	            model.put("overdueRate", resp.overdueRate);
	            model.put("serviceAmount", resp.serviceAmount);
	        }
	        
	        model.put("idNumber", userDo.getIdNumber());
	        model.put("realName", userDo.getRealName());
	        model.put("email", userDo.getEmail());//电子邮箱
	        model.put("mobile", userDo.getMobile());// 联系电话
	        
	        logger.info("platformProtocol, params=" + JSON.toJSONString(model));
	    }catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    }

	private void getCompanySeal(ModelMap map, JsdUserDo afUserDo) {
		try {
			JsdUserSealDo companyUserSealDo = jsdESdkService.selectUserSealByUserId(-1l);
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("companyUserSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("公司印章不存在 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
			getUserSeal(map, afUserDo);
			companyUserSealDo = jsdUserSealService.getUserSealByUserName("浙江楚橡信息科技股份有限公司");
			if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
				map.put("thirdSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
			} else {
				logger.error("获取钱包印章失败 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
				throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
			}
		} catch (Exception e) {
			logger.error("get userSeal  error", e);
		}
	}

	private void getUserSeal(ModelMap map, JsdUserDo afUserDo) {
		JsdUserSealDo afUserSealDo = jsdUserSealService.getUserSeal(afUserDo);
		if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
			logger.error("获取个人印章失败 => {}" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
			throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
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
        
        JsdUserDo afUserDo = jsdUserService.getByOpenId(openId);
        if (afUserDo == null) {
            logger.warn("refer user not exist by openId " + openId);
            return;
        }
        
        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_DIGITAL_CERTIFICATE.name());
        
        model.put("idNumber", afUserDo.getIdNumber());
        model.put("realName", afUserDo.getRealName());
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
        
        JsdUserDo afUserDo = jsdUserService.getByOpenId(openId);
        if (afUserDo == null) {
            logger.warn("refer user not exist by openId " + openId);
            return;
        }
        
        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_AGENCY.name(), ResourceSecType.PROTOCOL_AGENCY.name());
        
        model.put("realName", afUserDo.getRealName());
        model.put("yfCompany", resdo.getValue1());
        
        logger.info("agencyProtocol, params=" + JSON.toJSONString(model));
    }
    
    /**
     * 续期协议
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
	            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
	            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
	        }
	        
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
	        	model.put("oriInterestRate", cashDo.getInterestRate().setScale(2));
	        	model.put("oriGmtStart", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("oriGmtEnd", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	            
	        	//续期信息
	        	model.put("reAmount", renewalDo.getRenewalAmount());
	        	model.put("reAmountUpper", NumberUtil.number2CNMontrayUnit(renewalDo.getRenewalAmount()));
	        	model.put("reInterestRate", renewalDo.getBaseBankRate().setScale(2));
	        	model.put("reGmtStart", DateUtil.formatDate(renewalDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("reGmtEnd", DateUtil.formatDate(renewalDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("remark", renewalDo.getRemark());
	        	model.put("reGmtPlanRepay", DateUtil.formatDate(renewalDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("reRepayCapital", renewalDo.getCapital());
	        	model.put("reRepayCapitalUpper", NumberUtil.number2CNMontrayUnit(renewalDo.getCapital()));
	        	model.put("reServiceRate", renewalDo.getPoundageRate().setScale(2));
	        	
	        	model.put("overdueRate", cashDo.getOverdueRate().setScale(2));
	        	
	            model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        }else{
	        	JSONObject obj = JSON.parseObject(preview);
	        	String borrowNoXgxy = obj.getString("borrowNo");
	        	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(borrowNoXgxy);
	        	
	        	// 原借款
	        	model.put("oriBorrowNo", cashDo.getBorrowNo());
	        	model.put("oriAmount", cashDo.getAmount());
	        	model.put("oriAmountUpper", NumberUtil.number2CNMontrayUnit(cashDo.getAmount()));
	        	model.put("oriInterestRate", cashDo.getInterestRate().setScale(2));
	        	
	        	JSONArray renewalDetail = jsdBorrowCashRenewalService.getRenewalDetail(cashDo);
	        	JSONObject info = renewalDetail.getJSONObject(0);
	        	// 续期信息
	        	model.put("reAmount", info.getString("principalAmount"));
	        	model.put("reAmountUpper", NumberUtil.number2CNMontrayUnit( new BigDecimal(info.getString("principalAmount")) ));
	        	model.put("reInterestRate", info.getString("interestRate"));
	        	model.put("remark", "续期");
	        	model.put("reRepayCapital", info.getString("capital"));
	        	model.put("reRepayCapitalUpper", NumberUtil.number2CNMontrayUnit( new BigDecimal(info.getString("capital")) ));
	        	model.put("reServiceRate", info.getString("serviceRate"));
	        	
	        	model.put("overdueRate", cashDo.getOverdueRate().setScale(2));
	        }
	        
	        model.put("idNumber", userDo.getIdNumber());
	        model.put("realName", userDo.getRealName());
	        model.put("email", userDo.getEmail());//电子邮箱
	        model.put("mobile", userDo.getMobile());// 联系电话
	        
	        logger.info("renewalProtocol, params=" + JSON.toJSONString(model));
	    }catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    }
    

}
