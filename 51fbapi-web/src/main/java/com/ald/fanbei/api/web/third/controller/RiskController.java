package com.ald.fanbei.api.web.third.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.risk.ReqFromRiskBo;
import com.ald.fanbei.api.biz.bo.risk.ReqFromSecondaryRiskBo;
import com.ald.fanbei.api.biz.bo.risk.ReqFromStrongRiskBo;
import com.ald.fanbei.api.biz.bo.risk.RespSecAuthInfoToRiskBo;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.SignUtil;
import com.alibaba.fastjson.JSON;

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

	private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";
	
	private static final String TRADE_STATUE_SUCC = "0000";
	private static final String REQUEST_KEY_SIGN_INFO = "signInfo";
	private static final String REQUEST_KEY_DATA = "data";
	
	private static final String RESPONSE_CODE_SUCC = "SUCCESS";
	private static final String RESPONSE_CODE_FAIL = "FAIL";
	
	@Resource
	RiskUtil riskUtil;
	
	@Resource
	AfUserAuthService afUserAuthService;

	@Resource
	AfUserService userService;

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
	

	/**
	 * 运营商认证回调
	 * @param request
	 * @param response
	 * @return
	 */
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
	
	
	/* ---------------------------------
	 * start 此区域内接口为风控主动调用    |
	 * --------------------------------- */
	/**
	 * 风控主动查询 用户补充认证信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/auth/querySecAuthInfo"}, method = RequestMethod.POST)
	@ResponseBody
	public String querySecAuthInfo(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, String> params = this.requestToMap(request);
			logger.info("querySecAuthInfo begin, request params=" + params);
			this.checkSign(params);
			
			ReqFromRiskBo req = JSON.parseObject(JSON.toJSONString(params), ReqFromRiskBo.class);
			RespSecAuthInfoToRiskBo resp = afUserAuthService.getSecondaryAuthInfo(req);
			
			return JSON.toJSONString(resp);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return RESPONSE_CODE_FAIL + (e instanceof FanbeiException ? "," +e.getMessage(): "");
		}
	}

	/**
	 * 风控主动查询 用户补充认证信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/auth/getConfirmResultByUserId"}, method = RequestMethod.POST)
	@ResponseBody
	public String getConfirmResultByUserId(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, String> params = this.requestToMap(request);
			logger.info("getConfirmResultByUserId begin, request params=" + params);
			this.checkSign(params);
			ReqFromRiskBo req = JSON.parseObject(JSON.toJSONString(params), ReqFromRiskBo.class);

			AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(req.consumerNo);
			String riskStatus = userAuthDo.getRiskStatus();
			if (riskStatus.equals("Y")){
				Long id = userService.getUserByBorrowCashStatus(req.consumerNo);
				if (id == null){
					return "SUCCESS";
				}else {
					return "FALSE";
				}
			}else {
				return "FALSE";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return RESPONSE_CODE_FAIL + (e instanceof FanbeiException ? "," +e.getMessage(): "");
		}
	}
	
	/**
	 * 强风控回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/auth/syncStrongRiskByForcePush" }, method = RequestMethod.POST)
	@ResponseBody
	public String syncStrongRiskByForcePush(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, String> params = this.requestToMap(request);
			logger.info("syncStrongRiskByForcePush begin,request params=" + params);
			this.checkSign(params);
			
			ReqFromStrongRiskBo req = JSON.parseObject(params.get(REQUEST_KEY_DATA), ReqFromStrongRiskBo.class);
			afUserAuthService.dealFromStrongRiskForcePush(req);
			
			return RESPONSE_CODE_SUCC;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return RESPONSE_CODE_FAIL + (e instanceof FanbeiException ? "," +e.getMessage(): "");
		}
	}
	
	/**
	 * 补充认证回调
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/auth/syncSecAuthByForcePush" }, method = RequestMethod.POST)
	@ResponseBody
	public String syncSecAuthByForcePush(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, String> params = this.requestToMap(request);
			logger.info("syncSecAuthByForcePush begin,request params=" + params);
			this.checkSign(params);
			
			ReqFromSecondaryRiskBo req = JSON.parseObject(params.get(REQUEST_KEY_DATA), ReqFromSecondaryRiskBo.class);
			afUserAuthService.dealFromSecondaryRiskForcePush(req);
			
			return RESPONSE_CODE_SUCC;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			
			return RESPONSE_CODE_FAIL + (e instanceof FanbeiException ? "," +e.getMessage(): "");
		}
	}
	/* -------------------------------
	 * end 此区域内接口为风控主动调用    |
	 * ------------------------------- */
	
	/**
	 * 风控验签
	 * @param params
	 */
	private void checkSign(Map<String,String> params) {
		String reqSign = params.remove(REQUEST_KEY_SIGN_INFO);
		String tmpSign = SignUtil.sign(createLinkString(params), PRIVATE_KEY);
		
		if(!reqSign.equals(tmpSign)) {
			throw new FanbeiException("Sign Error!");
		}
	}
	
	/**
	 * 请求自动转换map
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String,String> requestToMap(HttpServletRequest request){
		// 参数Map  
        Map properties = request.getParameterMap();  
        // 返回值Map  
        Map returnMap = new HashMap();  
        Iterator entries = properties.entrySet().iterator();  
        Map.Entry entry;  
        String name = "";  
        String value = "";  
        while (entries.hasNext()) {  
            entry = (Map.Entry) entries.next();  
            name = (String) entry.getKey();  
            Object valueObj = entry.getValue();  
            if(null == valueObj){  
                value = "";  
            }else if(valueObj instanceof String[]){  
                String[] values = (String[])valueObj;  
                for(int i=0;i<values.length;i++){  
                    value = values[i] + ",";  
                }  
                value = value.substring(0, value.length()-1);  
            }else{  
                value = valueObj.toString();  
            }  
            returnMap.put(name, value);  
        }  
        return returnMap;  
	}
	
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	private String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			prestr = prestr + value;
		}

		return prestr;
	}
	
}
