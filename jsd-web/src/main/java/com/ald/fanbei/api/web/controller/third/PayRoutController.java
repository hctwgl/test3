package com.ald.fanbei.api.web.controller.third;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;

/**
 * @author chenjinhu 2017年2月20日 下午2:59:32 @类现描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/ups")
public class PayRoutController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static String TRADE_STATUE_SUCC = "00";
	private static String TRADE_STATUE_FAIL = "10"; // 处理失败

	@Resource
	JsdBorrowCashService jsdBorrowCashService;

	@Resource
	JsdBorrowCashRenewalService jsdBorrowCashRenewalService;

	@Resource
	JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;



	@RequestMapping(value = {"/authSignReturn"}, method = RequestMethod.POST)
	@ResponseBody
	public String authSignReturn(HttpServletRequest request, HttpServletResponse response) {
		for (String paramKey : request.getParameterMap().keySet()) {
			logger.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
		}
		return "succ";
	}


	@RequestMapping(value = { "/authSignNotify" }, method = RequestMethod.POST)
	@ResponseBody
	public String authSignNotify(HttpServletRequest request, HttpServletResponse response) {
		for (String paramKey : request.getParameterMap().keySet()) {
			logger.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
		}
		return "succ";
	}

	@RequestMapping(value = { "/authSignValidNotify" }, method = RequestMethod.POST)
	@ResponseBody
	public String authSignValidNotify(HttpServletRequest request, HttpServletResponse response) {
		for (String paramKey : request.getParameterMap().keySet()) {
			logger.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
		}
		return "succ";
	}

	@RequestMapping(value = { "/authPay" }, method = RequestMethod.POST)
	@ResponseBody
	public String authPay(HttpServletRequest request, HttpServletResponse response) {
		for (String paramKey : request.getParameterMap().keySet()) {
			logger.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
		}
		return "succ";
	}

	@RequestMapping(value = { "/authPayConfirm" }, method = RequestMethod.POST)
	@ResponseBody
	public String authPayConfirm(HttpServletRequest request, HttpServletResponse response) {
		for (String paramKey : request.getParameterMap().keySet()) {
			logger.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
		}
		return "succ";
	}

	@RequestMapping(value = { "/delegatePay" }, method = RequestMethod.POST)
	@ResponseBody
	public String delegatePay(HttpServletRequest request, HttpServletResponse response) {
		String outTradeNo = request.getParameter("orderNo");
		String merPriv = request.getParameter("merPriv");
		String tradeState = request.getParameter("tradeState");
		long result = NumberUtil.objToLongDefault(request.getParameter("reqExt"), 0);
		String upsResponse = " merPriv=" + merPriv + ",tradeState=" + tradeState + ",reqExt=" + result + ",outTradeNo=" + outTradeNo;
		logger.info("delegatePay callback, params: " + upsResponse);
		try {
			if (TRADE_STATUE_SUCC.equals(tradeState)) {// 打款成功
				jsdBorrowCashService.dealBorrowSucc(result, outTradeNo);
    		} else if (TRADE_STATUE_FAIL.equals(tradeState)) {// 打款失败
    			jsdBorrowCashService.dealBorrowFail(result, outTradeNo, "UPS打款异步反馈失败");
			}
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("delegatePay", e);
			return "ERROR";
		}
	}


	@RequestMapping(value = { "/signRelease" }, method = RequestMethod.POST)
	@ResponseBody
	public String signRelease(HttpServletRequest request, HttpServletResponse response) {
		for (String paramKey : request.getParameterMap().keySet()) {
			logger.info("paramKey=" + paramKey + ",paramValue=" + request.getParameterMap().get(paramKey));
		}
		return "succ";
	}

	@RequestMapping(value = { "/collect" }, method = RequestMethod.POST)
	@ResponseBody
	public String collect(HttpServletRequest request, HttpServletResponse response) {
		String outTradeNo = request.getParameter("orderNo");
		String merPriv = request.getParameter("merPriv");
		String tradeNo = request.getParameter("tradeNo");
		String tradeState = request.getParameter("tradeState");
		String respCode = StringUtil.null2Str(request.getParameter("respCode"));
		String respDesc = StringUtil.null2Str(request.getParameter("respDesc"));
		String tradeDesc = StringUtil.null2Str(request.getParameter("tradeDesc"));

		logger.info("collect callback, params: merPriv=" + merPriv + ",tradeState=" + tradeState + "tradeDesc:" + tradeDesc
				+ ",outTradeNo=" + outTradeNo + ",tradeNo=" + tradeNo + ",respCode=" + respCode + ",respDesc="
				+ respDesc);
		try {
			if (TRADE_STATUE_SUCC.equals(tradeState)) {// 代收成功
				if(PayOrderSource.REPAY_JSD.getCode().equals(merPriv)){
					jsdBorrowCashRepaymentService.dealRepaymentSucess(outTradeNo, tradeNo);
				}else if(PayOrderSource.RENEW_JSD.getCode().equals(merPriv)){
					jsdBorrowCashRenewalService.dealJsdRenewalSucess(outTradeNo, tradeNo);
				}
			} else if (TRADE_STATUE_FAIL.equals(tradeState)) {// 只处理代收失败的
				if(PayOrderSource.REPAY_JSD.getCode().equals(merPriv)){
					jsdBorrowCashRepaymentService.dealRepaymentFail(outTradeNo, tradeNo, true,"", respDesc);
				}else if(PayOrderSource.RENEW_JSD.getCode().equals(merPriv)){
					jsdBorrowCashRenewalService.dealJsdRenewalFail(outTradeNo, tradeNo, true, respCode, respDesc);
				}
			}
			return "SUCCESS";
		} catch (Exception e) {
			logger.error("collect", e);
			return "ERROR";
		}
	}
	
}
