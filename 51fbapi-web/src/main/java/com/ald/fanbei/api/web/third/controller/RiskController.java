package com.ald.fanbei.api.web.third.controller;

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

import com.ald.fanbei.api.biz.third.util.RiskUtil;

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
}
