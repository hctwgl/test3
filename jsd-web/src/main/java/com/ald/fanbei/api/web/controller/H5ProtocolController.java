package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
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

    private static final BigDecimal NUM100 = new BigDecimal(100);
    
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
            this.inpourUserInfo(model, userDo);
            
            
            JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_CASH.name());
            model.put("yfCompany", resdo.getValue1());
            model.put("bfCompany", resdo.getValue2());
            model.put("dfCompany", resdo.getValue3());
            
            
            BigDecimal amountLower, interestRate, serviceRate;
            if(StringUtils.isNotBlank(tradeNoXgxy)) {
            	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
            	
            	amountLower = cashDo.getAmount();
            	interestRate = cashDo.getInterestRate();
            	serviceRate = cashDo.getPoundageRate();
            	
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
            }
            
            model.put("interestRate", interestRate.multiply(NUM100).setScale(2) + "%");
            model.put("serviceRate", serviceRate.multiply(NUM100).setScale(2) + "%");
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
            this.inpourUserInfo(model, userDo);
            
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
            
            model.put("interestRate", interestRate.multiply(NUM100).setScale(2) + "%");
            model.put("serviceRate", serviceRate.multiply(NUM100).setScale(2) + "%");
            model.put("overdueRateDaily", overdueRate.multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%" );
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
	        this.inpourUserInfo(model, userDo);
	        
	        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_PLATFORM.name());
	        model.put("jfCompany", resdo.getValue1());
	        
	        if(StringUtils.isNotBlank(tradeNoXgxy)) {
	        	JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
	        	
	        	model.put("borrowNo", cashDo.getBorrowNo());
	        	model.put("interestRate", cashDo.getInterestRate().multiply(NUM100).setScale(2) + "%" );
	            model.put("serviceRate", cashDo.getPoundageRate().multiply(NUM100).setScale(2) + "%" );
	            model.put("overdueRateDaily", cashDo.getOverdueRate().multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%" );
	            model.put("serviceAmount", cashDo.getPoundageAmount());
	            model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        }else{
	        	TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
	        	trialBo.req = JSON.parseObject(preview, TrialBeforeBorrowReq.class);
	        	trialBo.req.openId = openId;
	        	trialBo.req.term = trialBo.req.nper;
	        	trialBo.userId = userDo.getRid();
	        	trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(openId);
	        	jsdBorrowCashService.resolve(trialBo);
	        	
	        	TrialBeforeBorrowResp resp = trialBo.resp;
	        	model.put("interestRate", new BigDecimal(resp.interestRate).multiply(NUM100).setScale(2) + "%" );
	            model.put("serviceRate", new BigDecimal(resp.serviceRate).multiply(NUM100).setScale(2) + "%" );
	            model.put("overdueRateDaily", new BigDecimal(resp.overdueRate).multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%" );
	            model.put("serviceAmount", resp.serviceAmount);
	        }
	        
	        logger.info("platformProtocol, params=" + JSON.toJSONString(model));
	    }catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
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
	        	model.put("reGmtStart", DateUtil.formatDate(renewalDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("reGmtEnd", DateUtil.formatDate(renewalDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("remark", renewalDo.getRemark());
	        	model.put("reGmtPlanRepay", DateUtil.formatDate(renewalDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
	        	model.put("reRepayCapital", renewalDo.getCapital());
	        	model.put("reRepayCapitalUpper", NumberUtil.number2CNMontrayUnit(renewalDo.getCapital()));
	        	model.put("reServiceRate", renewalDo.getPoundageRate().multiply(NUM100).setScale(2) + "%");
	        	
	        	model.put("overdueRateDaily", cashDo.getOverdueRate().multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%");
	        	
	            model.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
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
	        	model.put("reRepayCapital", info.getString("capital"));
	        	model.put("reRepayCapitalUpper", NumberUtil.number2CNMontrayUnit( new BigDecimal(info.getString("capital")) ));
	        	model.put("reServiceRate", new BigDecimal(info.getString("serviceRate")).multiply(NUM100).setScale(2) + "%" );
	        	
	        	model.put("overdueRateDaily", cashDo.getOverdueRate().multiply(NUM100).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 12, RoundingMode.HALF_UP).setScale(2) + "%");
	        }
	        
	        logger.info("renewalProtocol, params=" + JSON.toJSONString(model));
	    }catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
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
