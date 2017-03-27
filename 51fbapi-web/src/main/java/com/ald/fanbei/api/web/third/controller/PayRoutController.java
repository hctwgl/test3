package com.ald.fanbei.api.web.third.controller;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.wxpay.WxSignBase;
import com.ald.fanbei.api.biz.service.wxpay.WxXMLParser;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.WxTradeState;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfCashRecordDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfCashRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月20日 下午2:59:32
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/ups")
public class PayRoutController{
	
    protected final Logger   logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private AfOrderService afOrderService;
	
	@Resource
	private AfRepaymentService afRepaymentService;
	
	@Resource
	private AfBorrowService afBorrowService;
	
	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource

	private AfBorrowCashService afBorrowCashService;

	@Resource
	private AfCashRecordDao afCashRecordDao;
	
	private static String TRADE_STATUE_SUCC = "00";

    @RequestMapping(value = {"/authSignReturn"}, method = RequestMethod.POST)
    @ResponseBody
	public String authSignReturn(HttpServletRequest request, HttpServletResponse response){
    	for(String paramKey:request.getParameterMap().keySet()){
    		System.out.println("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
    	}
    	return "succ";
    }

    @RequestMapping(value = {"/authSignNotify"}, method = RequestMethod.POST)
    @ResponseBody
	public String authSignNotify(HttpServletRequest request, HttpServletResponse response){
    	for(String paramKey:request.getParameterMap().keySet()){
    		System.out.println("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
    	}
    	return "succ";
    }
    
    @RequestMapping(value = {"/authSignValidNotify"}, method = RequestMethod.POST)
    @ResponseBody
	public String authSignValidNotify(HttpServletRequest request, HttpServletResponse response){
    	for(String paramKey:request.getParameterMap().keySet()){
    		System.out.println("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
    	}
    	return "succ";
    }
    
    @RequestMapping(value = {"/authPay"}, method = RequestMethod.POST)
    @ResponseBody
	public String authPay(HttpServletRequest request, HttpServletResponse response){
    	for(String paramKey:request.getParameterMap().keySet()){
    		System.out.println("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
    	}
    	return "succ";
    }
    
    @RequestMapping(value = {"/authPayConfirm"}, method = RequestMethod.POST)
    @ResponseBody
	public String authPayConfirm(HttpServletRequest request, HttpServletResponse response){
    	for(String paramKey:request.getParameterMap().keySet()){
    		System.out.println("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
    	}
    	return "succ";
    }
    
    @RequestMapping(value = {"/delegatePay"}, method = RequestMethod.POST)
    @ResponseBody
	public String delegatePay(HttpServletRequest request, HttpServletResponse response){
    	String outTradeNo = request.getParameter("orderNo");
    	String merPriv = request.getParameter("merPriv");
    	String tradeState = request.getParameter("tradeState");
    	long result = NumberUtil.objToLongDefault(request.getParameter("reqExt"), 0);
    	logger.info("delegatePay begin merPriv="+merPriv+",tradeState="+tradeState+",reqExt="+result,",outTradeNo="+outTradeNo);
    	try {
    		if(TRADE_STATUE_SUCC.equals(tradeState)){//代付成功
    			if(UserAccountLogType.CASH.getCode().equals(merPriv)){//现金借款
    				//生成账单
    				AfBorrowDo borrow = afBorrowService.getBorrowById(result);
    				afBorrowService.cashBillTransfer(borrow, afUserAccountService.getUserAccountByUserId(borrow.getUserId()));
        		}else if(UserAccountLogType.CONSUME.getCode().equals(merPriv)){//分期借款
        			//生成账单
        			AfBorrowDo borrow = afBorrowService.getBorrowById(result);
    				afBorrowService.consumeBillTransfer(afBorrowService.getBorrowById(result), afUserAccountService.getUserAccountByUserId(borrow.getUserId()));
        		}else if(UserAccountLogType.REBATE_CASH.getCode().equals(merPriv)){//提现
        			AfCashRecordDo record = new AfCashRecordDo();
        			record.setRid(result);
        			record.setStatus("TRANSED");
        			afCashRecordDao.updateCashRecord(record);
        		}
        		else if(UserAccountLogType.BorrowCash.getCode().equals(merPriv)){//提现
        			Long rid = NumberUtil.objToLong(result);
        			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
        			afBorrowCashDo.setStatus("TRANSED");
        			afBorrowCashService.updateBorrowCash(afBorrowCashDo);
        		}
    			return "SUCCESS";
			}else{//代付失败
				if(UserAccountLogType.CASH.getCode().equals(merPriv)){//现金借款
					//借款关闭
					afBorrowService.updateBorrowStatus(result, BorrowStatus.CLOSE.getCode());
					//账户还原
    				AfBorrowDo borrow = afBorrowService.getBorrowById(result);
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUcAmount(borrow.getAmount());
					account.setUsedAmount(borrow.getAmount());
					account.setUserId(borrow.getUserId());
        			afUserAccountService.updateUserAccount(account);
				}else if(UserAccountLogType.CONSUME.getCode().equals(merPriv)){//分期借款
					//借款关闭
					afBorrowService.updateBorrowStatus(result, BorrowStatus.CLOSE.getCode());
					//账户还原
    				AfBorrowDo borrow = afBorrowService.getBorrowById(result);
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUsedAmount(borrow.getAmount());
					account.setUserId(borrow.getUserId());
        			afUserAccountService.updateUserAccount(account);
				}else if(UserAccountLogType.REBATE_CASH.getCode().equals(merPriv)){//提现
        			AfCashRecordDo record = afCashRecordDao.getCashRecordById(result);
        			record.setStatus("REFUSE");
        			afCashRecordDao.updateCashRecord(record);
        			//
        			AfUserAccountDo updateAccountDo = new AfUserAccountDo();
        			updateAccountDo.setRebateAmount(record.getAmount());
        			updateAccountDo.setUserId(record.getUserId());
        			afUserAccountService.updateUserAccount(updateAccountDo);
        		}else if(UserAccountLogType.BorrowCash.getCode().equals(merPriv)){
        			//借钱
        			Long rid = NumberUtil.objToLong(result);
        			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
        			afBorrowCashDo.setStatus("TRANSEDFAIL");
        			afBorrowCashService.updateBorrowCash(afBorrowCashDo);
        		}
			}
    		return "ERROR";
		} catch (Exception e) {
			logger.error("delegatePay",e);
			return "ERROR";
		}
    }
    
    @RequestMapping(value = {"/signRelease"}, method = RequestMethod.POST)
    @ResponseBody
	public String signRelease(HttpServletRequest request, HttpServletResponse response){
    	for(String paramKey:request.getParameterMap().keySet()){
    		System.out.println("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
    	}
    	return "succ";
    }
    
    @RequestMapping(value = {"/batchDelegatePay"}, method = RequestMethod.POST)
    @ResponseBody
	public String batchDelegatePay(HttpServletRequest request, HttpServletResponse response){
    	/*List<JSONObject> list = new ArrayList<JSONObject>();
    	JSONObject obj = new JSONObject();
    	obj.put("tradeNo", "dele200000000111");
    	obj.put("amount", "0.01");
    	obj.put("certNo", "330330330330");
    	obj.put("bankName", "工商银行");
    	obj.put("realName", "张三");
    	obj.put("cardNo", "6222222222222");
    	list.add(obj);
    	UpsUtil.batchDelegatePay(new BigDecimal(0.01), "1377777777", "1", "代收", list.toString(), "02");*/
    	return "succ";
    }
    
    /**
     * app中微信支付回调接口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/wxpayNotify"}, method = RequestMethod.POST)
    @ResponseBody
	public String wxpayNotify(HttpServletRequest request, HttpServletResponse response){
        PrintWriter out = null;
        StringBuffer xmlStr = new StringBuffer();
        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                xmlStr.append(line);
            }   
            logger.info("wxpayNotify="+xmlStr.toString());
            //验证通知是否是微信的通知
    		Properties properties = WxXMLParser.parseXML(xmlStr.toString());
			String resultStr = WxSignBase.checkWxSign(properties, AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
			String checkSign = WxSignBase.byteToHex(WxSignBase.MD5Digest(resultStr.getBytes("utf-8")));
			
			if(!StringUtil.equals(properties.getProperty("sign"), checkSign.toUpperCase())){
				return "succ";
			}
    		
    		String resultCode = properties.getProperty("result_code");
    		if(StringUtil.equals(resultCode, WxTradeState.SUCCESS.getCode())){
	    		String outTradeNo = properties.getProperty("out_trade_no");
	    		String transactionId = properties.getProperty("transaction_id");
	    		String attach = properties.getProperty("attach");
	    		if(PayOrderSource.ORDER.getCode().equals(attach)){
		    		afOrderService.dealMobileChargeOrder(outTradeNo, transactionId);
	    		}else if(PayOrderSource.REPAYMENT.getCode().equals(attach)){
	    			afRepaymentService.dealRepaymentSucess(outTradeNo, transactionId);
	    		} else if (PayOrderSource.BRAND_ORDER.getCode().equals(attach)) {
	    			afOrderService.dealBrandOrder(outTradeNo, transactionId);
	    		}
    		}
            
        } catch (Exception e) {
        	logger.error("wxpayNotify",e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } finally {
            if (out != null) {
                out.close();
            }
        }
    	return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}
    
    @RequestMapping(value = {"/collect"}, method = RequestMethod.POST)
    @ResponseBody
	public String collect(HttpServletRequest request, HttpServletResponse response){
    	String outTradeNo = request.getParameter("orderNo");
    	String merPriv = request.getParameter("merPriv");
    	String tradeNo = request.getParameter("tradeNo");
    	String tradeState = request.getParameter("tradeState");
    	logger.info("collect begin merPriv="+merPriv+",tradeState="+tradeState+",outTradeNo="+outTradeNo+",tradeNo="+tradeNo);
    	try {
    		if(TRADE_STATUE_SUCC.equals(tradeState)){//代收成功
    			if(OrderType.MOBILE.getCode().equals(merPriv)){//手机充值订单处理
    				afOrderService.dealMobileChargeOrder(outTradeNo, tradeNo);
        		}else if(UserAccountLogType.REPAYMENT.getCode().equals(merPriv)){//还款成功处理
        			afRepaymentService.dealRepaymentSucess(outTradeNo, tradeNo);
        		} else if (OrderType.BOLUOME.getCode().equals(merPriv)) {
        			afOrderService.dealBrandOrder(outTradeNo, tradeNo);
        		}
    			return "SUCCESS";
			}else{//代收失败
				
			}
    		return "ERROR";
		} catch (Exception e) {
			logger.error("collect",e);
			return "ERROR";
		}
    }
}
