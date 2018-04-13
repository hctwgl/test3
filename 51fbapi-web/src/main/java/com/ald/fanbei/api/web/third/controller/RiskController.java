package com.ald.fanbei.api.web.third.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.RiskOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.common.util.StringUtil;

/**
 * @类现描述：
 * @author hexin 2017年3月24日 下午16:59:39
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/risk")
public class RiskController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static String TRADE_STATUE_SUCC = "0000";
	@Resource
	RiskUtil riskUtil;

	@RequestMapping(value = { "/verify" }, method = RequestMethod.POST)
	@ResponseBody
	@Deprecated
	public String verify(HttpServletRequest request, HttpServletResponse response) {
		String code = ObjectUtils.toString(request.getParameter("code"));
		String data = ObjectUtils.toString(request.getParameter("data"));
		String msg = ObjectUtils.toString(request.getParameter("msg"));
		String signInfo = ObjectUtils.toString(request.getParameter("signInfo"));

		logger.info("asyVerify begin,code=" + code + ",data=" + data + ",msg=" + msg + ",signInfo=" + signInfo);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				riskUtil.asyVerify(code, data, msg, signInfo);
				return "SUCCESS";
			} catch (Exception e) {
				return "FAIL";
			}
		} else {
			return "FAIL";
		}
	}
	
	//payOrder的异步
	@RequestMapping(value = { "/payOrder" }, method = RequestMethod.POST)
	@ResponseBody
	public String payOrder(HttpServletRequest request, HttpServletResponse response) {
		String code = ObjectUtils.toString(request.getParameter("code"));
		String data = ObjectUtils.toString(request.getParameter("data"));
		String msg = ObjectUtils.toString(request.getParameter("msg"));
		String signInfo = ObjectUtils.toString(request.getParameter("signInfo"));

		logger.info("asyPayOrder begin,code=" + code + ",data=" + data + ",msg=" + msg + ",signInfo=" + signInfo);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				 riskUtil.asyPayOrder(code, data, msg, signInfo);
			
				return "SUCCESS";
			} catch (Exception e) {
				return "FAIL";
			}
		} else {
			return "FAIL";
		}
	}
	
	

	@RequestMapping(value = { "/operator" }, method = RequestMethod.POST)
	@ResponseBody
	public String operator(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal operator begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.operatorNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	
	@RequestMapping(value = { "/registerStrongRisk" }, method = RequestMethod.POST)
	@ResponseBody
	public String registerStrongRisk(HttpServletRequest request, HttpServletResponse response) {
		String code = ObjectUtils.toString(request.getParameter("code"));
		String data = ObjectUtils.toString(request.getParameter("data"));
		String msg = ObjectUtils.toString(request.getParameter("msg"));
		String signInfo = ObjectUtils.toString(request.getParameter("signInfo"));

		logger.info("asyRegisterStrongRisk begin,code=" + code + ",data=" + data + ",msg=" + msg + ",signInfo=" + signInfo);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				riskUtil.asyRegisterStrongRisk(code, data, msg, signInfo);
				return "SUCCESS";
			} catch (Exception e) {
				return "FAIL";
			}
		} else {
			return "FAIL";
		}
	}

	/**
	 * 强风控回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/registerStrongRiskV1" }, method = RequestMethod.POST)
	@ResponseBody
	public String registerStrongRiskV1(HttpServletRequest request, HttpServletResponse response) {
		String code = ObjectUtils.toString(request.getParameter("code"));
		String data = ObjectUtils.toString(request.getParameter("data"));
		String msg = ObjectUtils.toString(request.getParameter("msg"));
		String signInfo = ObjectUtils.toString(request.getParameter("signInfo"));

		logger.info("asyRegisterStrongRiskV1 begin,code=" + code + ",data=" + data + ",msg=" + msg + ",signInfo=" + signInfo);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				riskUtil.asyRegisterStrongRiskV1(code, data, msg, signInfo);
				return "SUCCESS";
			} catch (Exception e) {
				return "FAIL";
			}
		} else {
			return "FAIL";
		}
	}
	
	
	
	/**
	 * 开通白领贷回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/dredgeWhiteCollarLoan" }, method = RequestMethod.POST)
	@ResponseBody
	public String dredgeWhiteCollarLoan(HttpServletRequest request, HttpServletResponse response) {
		String code = ObjectUtils.toString(request.getParameter("code"));
		String data = ObjectUtils.toString(request.getParameter("data"));
		String msg = ObjectUtils.toString(request.getParameter("msg"));
		String signInfo = ObjectUtils.toString(request.getParameter("signInfo"));

		logger.info("dredgeWhiteCollarLoan begin,code=" + code + ",data=" + data + ",msg=" + msg + ",signInfo=" + signInfo);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				riskUtil.asyDredgeWhiteCollarLoan(code, data, msg, signInfo);
				return "SUCCESS";
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return "FAIL";
			}
		} else {
			return "FAIL";
		}
	}
	
	
	
	/**
	 * 公积金异步回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/fund" }, method = RequestMethod.POST)
	@ResponseBody
	public String fund(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal fund begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				riskUtil.fundNotify(code, data, msg, signInfo);
				return "SUCCESS";
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return "FAIL";
			}
		} else {
			return "ERROR";
		}
	}
	
	/**
	 * 51公积金风控异步回调（魔蝎换成51）
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/newFund" }, method = RequestMethod.POST)
	@ResponseBody
	public String newFund(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal newFund begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				riskUtil.newFundNotify(code, data, msg, signInfo);
				return "SUCCESS";
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return "FAIL";
			}
		} else {
			return "ERROR";
		}
	}
	
	/**
	 * 社保异步回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/jinpo" }, method = RequestMethod.POST)
	@ResponseBody
	public String socialSecurity(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal socialSecurity begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				riskUtil.socialSecurityNotify(code, data, msg, signInfo);
				return "SUCCESS";
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return "FAIL";
			}
		} else {
			return "ERROR";
		}
	}
	/**
	 * 信用卡异步回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/creditCard" }, method = RequestMethod.POST)
	@ResponseBody
	public String creditCard(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal creditCard begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			try {
				riskUtil.creditCardNotify(code, data, msg, signInfo);
				return "SUCCESS";
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return "FAIL";
			}
		} else {
			return "ERROR";
		}
	}

	/**
	 * 支付宝异步回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/alipay" }, method = RequestMethod.POST)
	@ResponseBody
	public String alipay(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal alipay begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.alipayNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	
	/**
	 * 学信网认证异步回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/chsi" }, method = RequestMethod.POST)
	@ResponseBody
	public String chsi(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal chsi begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.chsiNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	/**
	 *网银认证异步回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/onlinebank" }, method = RequestMethod.POST)
	@ResponseBody
	public String wangyin(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal wangyin begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.onlinebankNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	
	/**
	 * 征信
	 */
	@RequestMapping(value = { "/zhengxin/applyReport" }, method = RequestMethod.POST)
	@ResponseBody
	public String applyReport(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal chsi begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.applyReportNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	
	/**
	 * 征信
	 */
	@RequestMapping(value = { "/zhengxin/createTask" }, method = RequestMethod.POST)
	@ResponseBody
	public String createTask(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal chsi begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.createTaskNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	
	/**
	 * 征信
	 */
	@RequestMapping(value = { "/zhengxin/taskFinish" }, method = RequestMethod.POST)
	@ResponseBody
	public String taskFinish(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal chsi begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.taskFinishNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	
	
	/**
	 * 补充认证统一回调
	 */
	@RequestMapping(value = { "/supplementAuth" }, method = RequestMethod.POST)
	@ResponseBody
	public String supplementAuth(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		String scene = request.getParameter("scene");
		logger.info("deal supplementAuth begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.supplementAuthNotify(code, data, msg, signInfo,scene);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	/**
	 * 公信宝认证(电商)风控异步回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/gxb/ecommerce"}, method = RequestMethod.POST)
	@ResponseBody
	public String authGxb(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		logger.info("deal authGxb begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.authGxbNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	
	/**
	 * 风控直接修改用户认证信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/directModifyUserAuthInfo"}, method = RequestMethod.POST)
	@ResponseBody
	public String directModifyUserAuthInfo(HttpServletRequest request, HttpServletResponse response) {
		String code = request.getParameter("code");
		String data = request.getParameter("data");
		String msg = request.getParameter("msg");
		String signInfo = request.getParameter("signInfo");
		
		logger.info("directModifyUserAuthInfo begin,code=" + code + ",data=" + data);
		if (TRADE_STATUE_SUCC.equals(code)) {
			riskUtil.authGxbNotify(code, data, msg, signInfo);
			return "SUCCESS";
		} else {
			return "ERROR";
		}
	}
	
	private void checkSign(Map<String, Object> reqParams) {
	}
}
