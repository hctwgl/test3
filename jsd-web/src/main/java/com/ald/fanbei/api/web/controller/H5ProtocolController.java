package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.alibaba.fastjson.JSON;


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
    BizCacheUtil bizCacheUtil;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    NumberWordFormat numberWordFormat;

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
    
    /**
     * 数字证书服务协议
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

}
