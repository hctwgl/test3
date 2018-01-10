package com.ald.fanbei.api.web.common;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.service.AfAppUpgradeService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfAppUpgradeDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.impl.ApiHandleFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 江荣波 2017年1月16日 下午11:56:17 @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class H5BaseController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final Logger biNewLog = LoggerFactory.getLogger("FANBEINEW_BI");// 新app原生接口入口日志

	protected final Logger webbiLog = LoggerFactory.getLogger("FBWEB_BI");// h5接口入口日志
	protected final Logger webbiNewLog = LoggerFactory.getLogger("FBWEBNEW_BI");// 新h5接口入口日志

	protected final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");// 埋点日志
	protected final Logger maidianNewLog = LoggerFactory.getLogger("FBMDNEW_BI");// 埋点日志

	protected final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");// 第三方调用日志

	@Resource
	protected ApiHandleFactory apiHandleFactory;
	@Resource
	protected TokenCacheUtil tokenCacheUtil;
	@Resource
	AfUserService afUserService;

	@Resource
	AfAppUpgradeService afAppUpgradeService;
	@Resource
	private BizCacheUtil bizCacheUtil;

	@Resource
	private AfResourceService afResourceService;

	
	protected String processRequest(HttpServletRequest request) {
		String retMsg = StringUtils.EMPTY;
		BaseResponse exceptionresponse = null;
		try {
			checkAppInfo(request);
			// 解析参数（包括请求头中的参数和报文体中的参数）
			Context context = parseRequestData(request);
			//FanbeiContext contex = doCheck(requestDataVo);
			exceptionresponse = doProcess(context);
			retMsg = JSON.toJSONString(exceptionresponse);
		} catch (FanbeiException e) {
			exceptionresponse = buildErrorResult(e, request);
			retMsg = JSON.toJSONString(exceptionresponse);
		} catch (Exception e) {
			exceptionresponse = buildErrorResult(FanbeiExceptionCode.SYSTEM_ERROR, request);
			retMsg = JSON.toJSONString(exceptionresponse);
		}
		return retMsg;
	}


	private void checkAppInfo(HttpServletRequest request) {
		String appInfo = request.getParameter("_appInfo");
		if (StringUtils.isBlank(appInfo)) {
			throw new FanbeiException("param is null",FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
	}



	protected BaseResponse buildErrorResult(FanbeiExceptionCode exceptionCode, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse();
		resp.setId(request.getHeader(Constants.REQ_SYS_NODE_ID));
		if (exceptionCode == null) {
			exceptionCode = FanbeiExceptionCode.SYSTEM_ERROR;
		}
		resp = new ApiHandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode);
		return resp;
	}

	protected BaseResponse buildErrorResult(FanbeiException e, HttpServletRequest request) {
		FanbeiExceptionCode exceptionCode = e.getErrorCode();
		ApiHandleResponse resp = new ApiHandleResponse();
		resp.setId(request.getHeader(Constants.REQ_SYS_NODE_ID));
		if (exceptionCode == null) {
			exceptionCode = FanbeiExceptionCode.SYSTEM_ERROR;
		}
		if (e.getDynamicMsg() != null && e.getDynamicMsg()) {
			resp = new ApiHandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode, e.getMessage());
		} else if (!StringUtil.isEmpty(e.getResourceType())) {
			AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(e.getResourceType());
			String msgTemplate = afResourceDo.getValue();
			for (String paramsKey : e.paramsMap.keySet()) {
				msgTemplate = msgTemplate.replace(paramsKey, e.paramsMap.get(paramsKey));
			}
			resp = new ApiHandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode, msgTemplate);
		} else {
			resp = new ApiHandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode);
		}

		return resp;
	}

	/**
	 * 解析请求参数
	 *
	 * @param requestData
	 * @param request
	 * @return
	 */
	public abstract Context parseRequestData( HttpServletRequest request);

	/**
	 * 处理请求
	 *
	 * @param requestDataVo
	 * @param context
	 * @param httpServletRequest
	 * @return
	 */
	public abstract BaseResponse doProcess(Context context);

	/**
	 * 验证基础参数、签名
	 *
	 * @param requestDataVo
	 * @return
	 */
	protected FanbeiContext doCheck(RequestDataVo requestDataVo) {
		FanbeiContext context = doSystemCheck(requestDataVo);

		if (requestDataVo.getId() != null && requestDataVo.getId().indexOf("i_") == 0
				&& context.getAppVersion() == 121) {
			throw new FanbeiException("系统维护中", FanbeiExceptionCode.SYSTEM_REPAIRING_ERROR);
		}
		String idName = requestDataVo.getId();
		if (idName.startsWith("i") && context.getAppVersion() < 379) {
			String[] strs = idName.split("_");
			String name = idName.substring(idName.lastIndexOf("_") + 1);
			if (strs.length == 3) {
				name = "www";
			}
			AfAppUpgradeDo afAppUpgradeDo = afAppUpgradeService.getNewestIOSVersionBySpecify(context.getAppVersion(),
					name);
			if (afAppUpgradeDo != null && StringUtils.equals(afAppUpgradeDo.getIsForce(), YesNoStatus.YES.getCode())) {
				throw new FanbeiException("system update", FanbeiExceptionCode.SYSTEM_UPDATE);
			}
		}

		String interfaceName = requestDataVo.getMethod();
		if (StringUtils.isBlank(interfaceName)) {
			throw new FanbeiException("request method is null or empty",
					FanbeiExceptionCode.REQUEST_PARAM_METHOD_ERROR);
		}
		return context;
	}

	
	/**
	 * h5接口验证，验证基础参数、签名
	 *
	 * @param request
	 * @param needToken
	 * @return
	 */
	protected FanbeiWebContext doWebCheck(HttpServletRequest request, boolean needToken) {
		FanbeiWebContext webContext = new FanbeiWebContext();
		String appInfo = getAppInfo(request.getHeader("Referer"));
		// 如果是测试环境
		logger.info(String.format("doWebCheck appInfo = {%s}", appInfo));
		if (Constants.INVELOMENT_TYPE_TEST.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE))
				&& StringUtil.isBlank(appInfo)) {
			String testUser = getTestUser(request.getHeader("Referer"));
			if (testUser != null && !"".equals(testUser)) {
				if ("no".equals(testUser)) {
					return webContext;
				} else {
					webContext.setUserName(testUser);
					webContext.setLogin(true);
					return webContext;
				}
			}
		}
		webContext.setAppInfo(appInfo);
		if (StringUtil.isBlank(appInfo)) {
			if (needToken) {
				throw new FanbeiException("no login", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
			} else {
				return webContext;
			}
		}
		Context context = parseRequestData(request);
		/*
		requestDataVo.setParams(new HashMap<String, Object>());
		FanbeiContext baseContext = this.doBaseParamCheck(requestDataVo);
		webContext.setUserName(baseContext.getUserName());
		webContext.setAppVersion(baseContext.getAppVersion());
		checkWebSign(webContext, requestDataVo, needToken);
		*/
		return webContext;
	}

	/**
	 * 验证系统参数，验证签名
	 *
	 * @param requestDataVo
	 * @return
	 */
	private FanbeiContext doSystemCheck(RequestDataVo requestDataVo) {
		FanbeiContext context = this.doBaseParamCheck(requestDataVo);
		// 是否登录之前接口，若登录之前的接口不需要验证用户，否则需要验证用户
		boolean beforeLogin = true;
		beforeLogin = apiHandleFactory.checkBeforlogin(requestDataVo.getMethod());
		
		// TODO 设置上下文
		if (!beforeLogin) {// 需要登录的接口
			AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
			
			if (userInfo == null) {
				throw new FanbeiException(requestDataVo.getId() + " user don't exist",
						FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
			}
			context.setUserId(userInfo.getRid());
			context.setNick(userInfo.getNick());
			context.setMobile(userInfo.getMobile());
		} else if (beforeLogin && CommonUtil.isMobile(context.getUserName())) {// 不需要登录但是已经登录过
			AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
			if (userInfo != null) {
				context.setUserId(userInfo.getRid());
				context.setNick(userInfo.getNick());
				context.setMobile(userInfo.getMobile());
			}
		}

		// 验证签名
		Map<String, Object> systemMap = requestDataVo.getSystem();
		this.checkSign(context.getAppVersion() + "",
				ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_NETTYPE)), context.getUserName(),
				ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_SIGN)),
				ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_TIME)), requestDataVo.getParams(),
				beforeLogin);

		return context;
	}

	private FanbeiContext doBaseParamCheck(RequestDataVo requestDataVo) {

		FanbeiContext context = new FanbeiContext();
		if (requestDataVo == null || requestDataVo.getSystem() == null || requestDataVo.getParams() == null
				|| requestDataVo.getMethod() == null) {
			throw new FanbeiException("缺少系统参数", FanbeiExceptionCode.REQUEST_PARAM_SYSTEM_NOT_EXIST);
		}
		Map<String, Object> systemMap = requestDataVo.getSystem();
		String appVersion = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_VERSION));
		String netType = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_NETTYPE));
		String userName = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_USERNAME));
		String sign = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_SIGN));
		String time = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_TIME));
		if (StringUtils.isBlank(appVersion) || StringUtils.isBlank(ObjectUtils.toString(netType))
				|| StringUtils.isBlank(ObjectUtils.toString(sign)) || StringUtils.isBlank(ObjectUtils.toString(time))) {
			logger.error(requestDataVo.getId() + ",version or netType or userName or sign or time is null or empty");
			throw new FanbeiException(
					requestDataVo.getId() + ",version or userId or sign or time can't be null or empty ",
					FanbeiExceptionCode.REQUEST_PARAM_SYSTEM_NOT_EXIST);
		}
		int version = NumberUtil.objToIntDefault(systemMap.get(Constants.REQ_SYS_NODE_VERSION), 0);
		context.setUserName(userName);
		context.setAppVersion(version);
		return context;
	}

	/**
	 * 验证签名
	 *
	 * @param appVersion
	 *            app版本
	 * @param userName
	 *            用户名
	 * @param sign
	 *            签名
	 * @param time
	 *            时间戳
	 * @param params
	 *            所有请求参数
	 * @param needToken
	 *            是否需要needToken，不依赖登录的请求不需要，依赖登录的请求需要
	 */
	private void checkSign(String appVersion, String netType, String userName, String sign, String time,
			Map<String, Object> params, boolean needToken) {
		if (Constants.SWITCH_OFF.equals(ConfigProperties.get(Constants.CONFKEY_CHECK_SIGN_SWITCH))) {
			return;
		}
		List<String> paramList = new ArrayList<String>();
		if (params != null && params.size() > 0) {
			paramList.addAll(params.keySet());
			Collections.sort(paramList);
		}

		String signStrBefore = "appVersion=" + appVersion + "&netType=" + netType + "&time=" + time + "&userName="
				+ userName;
		if (!needToken) {
			TokenBo token = (TokenBo) tokenCacheUtil.getToken(userName);
			if (token == null) {
				throw new FanbeiException("token is expire", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
			}

			// refresh token
			Date lastAccess = DateUtil.convertMillisToDate(Long.parseLong(token.getLastAccess()), new Date());// 最后访问时间
			Date lasstAccessTemp = DateUtil.addMins(lastAccess, Constants.MINITS_OF_2HOURS);
			if (DateUtil.afterDay(new Date(), lasstAccessTemp)) {
				token.setLastAccess(System.currentTimeMillis() + "");
				tokenCacheUtil.saveToken(userName, token);
			}

			signStrBefore = signStrBefore + token.getToken();
		}
		if (paramList.size() > 0) {
			for (String item : paramList) {
				signStrBefore = signStrBefore + "&" + item + "=" + params.get(item);
			}
		}

		this.compareSign(signStrBefore, sign);

	}

	

	/**
	 * 比较签名值
	 *
	 * @param signStrBefore
	 * @param sign
	 */
	private void compareSign(String signStrBefore, String sign) {
		String sha256Value = DigestUtil.getDigestStr(signStrBefore);
		if (logger.isDebugEnabled())
			logger.debug("signStrBefore=" + signStrBefore + ",sha256Value=" + sha256Value + ",sign=" + sign);
		if (!StringUtils.equals(sign, sha256Value)) {
			throw new FanbeiException("sign is error", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
		}
	}

	protected void writeRespData(PrintWriter writer, String resultStr) {
		try {
			writer.write(resultStr);
			writer.flush();
		} catch (Exception e) {
			logger.error("o2oapp ioexception ", e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	protected void writeResponse(HttpServletResponse response, String writeStr) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(writeStr);
		} catch (Exception e) {
			logger.error("response write failed ,content is {}", writeStr);
		} finally {
			if (null != writer) {
				writer.close();
			}
		}

	}

	protected String base64Encoded(String baseString) {
		if (StringUtils.isNotEmpty(baseString)) {
			return new String(Base64.decode(baseString));
		}
		return StringUtils.EMPTY;
	}

	/**
	 * 记录埋点相关日志日志
	 *
	 * @param request
	 * @param respData
	 * @param exeT
	 */
	protected void doMaidianLog(HttpServletRequest request, H5CommonResponse respData, String... extInfo) {
		try {
			JSONObject param = new JSONObject();
			Enumeration<String> enu = request.getParameterNames();
			while (enu.hasMoreElements()) {
				String paraName = (String) enu.nextElement();
				param.put(paraName, request.getParameter(paraName));
			}
			String userName = "no user";
			if (param.getString("_appInfo") != null) {
				userName = (String) JSONObject.parseObject(param.getString("_appInfo")).get("userName");
			}
			
			// 获取可变参数
			String ext1 = "";
			String ext2 = "";
			String ext3 = "";
			String ext4 = "";
			try {
				ext1 = extInfo[0];
				ext2 = extInfo[1];
				ext3 = extInfo[2];
				ext4 = extInfo[3];
			} catch (Exception e) {
				// ignore error
			}
			maidianNewLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
							"	", "h", "	", CommonUtil.getIpAddr(request), "	", userName, "	", 0, "	",
							request.getRequestURI(), "	", respData == null ? false : respData.getSuccess(), "	",
							DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "md", "	", ext1,
							"	", ext2, "	", ext3, "	", ext4, "	", param == null ? "{}" : param.toString(),
							"	", respData == null ? "{}" : respData.toString()));

			maidianLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
					"	", "h", "	rmtIP=", CommonUtil.getIpAddr(request), "	userName=", userName, "	", 0, "	",
					request.getRequestURI(), "	result=", respData == null ? false : respData.getSuccess(), "	",
					DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "md", "	", ext1, "	", ext2,
					"	", ext3, "	", ext4, "	reqD=", param.toString(), "	resD=",
					respData == null ? "null" : respData.toString()));
			
		} catch (Exception e) {
			logger.error("maidian logger error", e);
		}
	}

	/**
	 * 记录H5日志
	 *
	 * @param request
	 * @param respData
	 * @param appInfo
	 * @param exeT
	 */
	protected void doLog(HttpServletRequest request, H5CommonResponse respData, String appInfo, long exeT,
			String userName) {
		try {
			JSONObject param = new JSONObject();
			// String userName = "no user";
			if (StringUtil.isBlank(userName)) {
				userName = "no user";
			}
			JSONObject temp = null;
			if (StringUtil.isNotBlank(appInfo)) {
				temp = JSONObject.parseObject(appInfo);
				userName = JSONObject.parseObject(appInfo).getString("userName");
			}
			param.put("_appInfo", temp);
			Enumeration<String> enu = request.getParameterNames();
			while (enu.hasMoreElements()) {
				String paraName = (String) enu.nextElement();
				param.put(paraName, request.getParameter(paraName));
			}

			String ext1 = "";
			String ext2 = "";
			String ext3 = "";
			String ext4 = "";
			String ext5 = "";
			
			this.doLog(param.toString(), respData, request.getMethod(), CommonUtil.getIpAddr(request), exeT + "",
					request.getRequestURI(), userName, ext1, ext2, ext3, ext4, ext5);
		} catch (Exception e) {
			logger.error("do log exception", e);
		}
	}

	/**
	 * @param reqData
	 *            请求参数
	 * @param respData
	 *            响应结果
	 * @param httpMethod
	 *            请求方法 GET或POST
	 * @param rmtIp
	 *            远程id
	 * @param exeT
	 *            执行时间
	 * @param inter
	 *            接口
	 * @param userName用户名
	 * @param ext1
	 *            扩展参数1
	 * @param ext2
	 *            扩展参数2
	 * @param ext3
	 *            扩展参数3
	 * @param ext4
	 *            扩展参数4
	 * @param ext5
	 *            扩展参数5
	 */
	protected void doLog(String reqData, H5CommonResponse respData, String httpMethod, String rmtIp, String exeT,
			String inter, String userName, String ext1, String ext2, String ext3, String ext4, String ext5) {

		webbiNewLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT), "	",
				"h", "	", rmtIp, "	", userName, "	", exeT, "	", inter, "	",
				respData == null ? "false" : respData.getSuccess() + "", "	",
				DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", ext1, "	", ext2, "	", ext3, "	",
				ext4, "	", ext5, "	", StringUtil.isBlank(reqData) ? "{}" : reqData, "	",
				respData == null ? "{}" : respData.toString()));

		webbiLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT), "	", "h",
				"	rmtIP=", rmtIp, "	userName=", userName, "	", exeT, "	", inter, "	result=",
				respData == null ? "false" : respData.getSuccess() + "", "	",
				DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", ext1, "	", ext2, "	", ext3, "	",
				ext4, "	", ext5, "	reqD=", reqData, "	resD=", respData == null ? "null" : respData.toString()));
	}

	private static String getAppInfo(String url) {
		if (StringUtil.isBlank(url)) {
			return null;
		}
		String result = "";
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}

					List<String> values = params.get(key);
					if (values == null) {
						values = new ArrayList<String>();
						params.put(key, values);
					}
					values.add(value);
				}
			}
			List<String> _appInfo = params.get("_appInfo");
			if (_appInfo != null && _appInfo.size() > 0) {
				result = _appInfo.get(0);
			}
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}

	public static String getTestUser(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		String result = "";
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}

					List<String> values = params.get(key);
					if (values == null) {
						values = new ArrayList<String>();
						params.put(key, values);
					}
					values.add(value);
				}
			}
			List<String> _appInfo = params.get("testUser");
			if (_appInfo != null && _appInfo.size() > 0) {
				result = _appInfo.get(0);
			}
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}

	
}
