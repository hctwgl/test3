package com.ald.fanbei.api.web.controller;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.wxpay.WxSignBase;
import com.ald.fanbei.api.biz.service.wxpay.WxXMLParser;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.WxOrderSource;
import com.ald.fanbei.api.common.enums.WxTradeState;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类描述：第三方回调接口
 *@author hexin 2017年2月28日 上午11:16:34
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 */
@Controller
@RequestMapping("/third")
public class ThirdController extends BaseController {

	@Resource
	private AfOrderService afOrderService;
	
	@Resource
	private AfRepaymentService afRepaymentService;
	
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		return null;
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
    	logger.info("wxpayNotify");
        PrintWriter out = null;
        StringBuffer xmlStr = new StringBuffer();
        try {
            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                xmlStr.append(line);
            }   
            logger.info("wxpayNotify result："+xmlStr.toString());
            
            //验证通知是否是微信的通知
    		Properties properties = WxXMLParser.parseXML(xmlStr.toString());
			String resultStr = WxSignBase.checkWxSign(properties, AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_WX_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
			String checkSign = WxSignBase.byteToHex(WxSignBase.MD5Digest(resultStr.getBytes("utf-8")));
			logger.info(properties.getProperty("sign") + "----" + checkSign.toUpperCase());
			
			if(!StringUtil.equals(properties.getProperty("sign"), checkSign.toUpperCase())){
				return "succ";
			}
    		
    		String resultCode = properties.getProperty("result_code");
    		if(StringUtil.equals(resultCode, WxTradeState.SUCCESS.getCode())){
	    		String outTradeNo = properties.getProperty("out_trade_no");
	    		String transactionId = properties.getProperty("transaction_id");
	    		String attach = properties.getProperty("attach");
	    		if(WxOrderSource.ORDER.getCode().equals(attach)){
		    		afOrderService.dealMobileChargeOrder(outTradeNo, transactionId);
	    		}else if(WxOrderSource.REPAYMENT.getCode().equals(attach)){
	    			
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
}
