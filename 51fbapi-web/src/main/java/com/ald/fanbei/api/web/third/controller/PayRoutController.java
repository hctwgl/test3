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

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.wxpay.WxSignBase;
import com.ald.fanbei.api.biz.service.wxpay.WxXMLParser;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.WxTradeState;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月20日 下午2:59:32
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/ups")
public class PayRoutController{
	
    protected static Logger   logger = LoggerFactory.getLogger(PayRoutController.class);
	
	@Resource
	private AfOrderService afOrderService;
	
	@Resource
	private AfRepaymentService afRepaymentService;

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
    	String outTradeNo = request.getParameter("outTradeNo");
    	String tradeNo = request.getParameter("tradeNo");
    	try {
    		if(afRepaymentService.dealRepaymentSucess(outTradeNo, tradeNo)>0){
        		return "succ";
        	}else{
        		return "error";
        	}
		} catch (Exception e) {
			logger.error("collect",e);
			return "error";
		}
    }
}
