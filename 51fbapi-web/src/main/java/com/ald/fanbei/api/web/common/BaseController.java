package com.ald.fanbei.api.web.common;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.ald.fanbei.api.biz.service.AfUserService;
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
import com.ald.fanbei.api.dal.domain.AfAppUpgradeDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.impl.ApiHandleFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * 
 * @author 陈金虎 2017年1月16日 下午11:56:17
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class BaseController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Logger biLogger = LoggerFactory.getLogger("FANBEI_BI");
	protected final Logger webbiLog = LoggerFactory.getLogger("FBWEB_BI");
	protected final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");
	protected final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");

	@Resource
	protected ApiHandleFactory apiHandleFactory;
	@Resource
	protected TokenCacheUtil tokenCacheUtil;
	@Resource
	AfUserService afUserService;

	@Resource
	AfAppUpgradeService afAppUpgradeService;

	/**
	 * 解析request
	 * 
	 * @param reqData
	 * @param httpServletRequest
	 * @return
	 */
	protected String processRequest(String reqData, HttpServletRequest request, boolean isForQQ) {
		String resultStr = StringUtils.EMPTY;
		BaseResponse exceptionresponse = null;
		Calendar calStart = Calendar.getInstance();
		RequestDataVo requestDataVo = null;
		try {
			// 检查参数是否为空
			reqData = checkCommonParam(reqData, request, isForQQ);

			// if (StringUtils.isBlank(reqData)) {
			// exceptionresponse =
			// buildErrorResult(FanbeExceptionCode.REQUEST_PARAM_NOT_EXIST,
			// request);
			// return JSON.toJSONString(exceptionresponse);
			// }
			// 解析参数（包括请求头中的参数和报文体中的参数）
			requestDataVo = parseRequestData(reqData, request);

			// 验证参数、签名
			FanbeiContext contex = doCheck(requestDataVo);

			// 处理业务
			resultStr = doProcess(requestDataVo, contex, request);
		} catch (FanbeiException e) {
			exceptionresponse = buildErrorResult(e.getErrorCode(), request);
			resultStr = JSON.toJSONString(exceptionresponse);
			logger.error("o2oapp exception id=" + (requestDataVo == null ? reqData : requestDataVo.getId()), e);
		} catch (Exception e) {
			exceptionresponse = buildErrorResult(FanbeiExceptionCode.SYSTEM_ERROR, request);
			resultStr = JSON.toJSONString(exceptionresponse);
			logger.error("system exception id=" + (requestDataVo == null ? reqData : requestDataVo.getId()), e);
		} finally {
			Calendar calEnd = Calendar.getInstance();
			if (StringUtils.isNotBlank(reqData)) {
				reqData = reqData.replace("\r", "").replace("\n", "").replace(" ", "");
			}
			if (biLogger.isInfoEnabled()) {
				String req = "";
				if (requestDataVo == null) {
					req = reqData;
				} else {
					if ("/auth/checkFaceApi".equals(requestDataVo.getMethod())) {
						req = reqData.length() + "";
					} else {
						req = requestDataVo.toString();
					}
				}
				biLogger.info(StringUtil.appendStrs("reqD=", req, ";resD=",
						requestDataVo != null && ("/system/getArea".equals(requestDataVo.getMethod()))
								? resultStr.length() + "" : resultStr,
						";rmtIP=", CommonUtil.getIpAddr(request), ";exeT=", (calEnd.getTimeInMillis() - calStart.getTimeInMillis()), ";intefN=", request.getRequestURI()));
			}
		}
		return resultStr;
	}

	/**
	 * 验证参数
	 * 
	 * @param reqData
	 * @param request
	 * @return
	 */
	public abstract String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ);

	protected BaseResponse buildErrorResult(FanbeiExceptionCode exceptionCode, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse();
		resp.setId(request.getHeader(Constants.REQ_SYS_NODE_ID));
		if (exceptionCode == null) {
			exceptionCode = FanbeiExceptionCode.SYSTEM_ERROR;
		}
		resp = new ApiHandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode);
		return resp;
	}

	/**
	 * 解析请求参数
	 * 
	 * @param requestData
	 * @param request
	 * @return
	 */
	public abstract RequestDataVo parseRequestData(String requestData, HttpServletRequest request);

	/**
	 * 处理请求
	 * 
	 * @param requestDataVo
	 * @param context
	 * @param httpServletRequest
	 * @return
	 */
	public abstract String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest);

	/**
	 * 验证基础参数、签名
	 * 
	 * @param requestDataVo
	 * @return
	 */
	protected FanbeiContext doCheck(RequestDataVo requestDataVo) {
		FanbeiContext context = doSystemCheck(requestDataVo);

		if (requestDataVo.getId() != null && requestDataVo.getId().indexOf("i_") == 0 && context.getAppVersion() == 121) {
			throw new FanbeiException("系统维护中", FanbeiExceptionCode.SYSTEM_REPAIRING_ERROR);
		}
		String idName = requestDataVo.getId();
		if (idName.startsWith("i")) {
			String[] strs = idName.split("_");
			String name =idName.substring(idName.lastIndexOf("_")+1) ;
			if(strs.length==3){
				name ="www";
			}
			AfAppUpgradeDo afAppUpgradeDo = afAppUpgradeService.getNewestIOSVersionBySpecify(context.getAppVersion(),name);
			if (afAppUpgradeDo != null && StringUtils.equals(afAppUpgradeDo.getIsForce(), YesNoStatus.YES.getCode())) {
				throw new FanbeiException("system update", FanbeiExceptionCode.SYSTEM_UPDATE);
			}
		}

		String interfaceName = requestDataVo.getMethod();
		if (StringUtils.isBlank(interfaceName)) {
			throw new FanbeiException("request method is null or empty", FanbeiExceptionCode.REQUEST_PARAM_METHOD_ERROR);
		}
		return context;
	}
	
	/**
	 * h5接口验证，验证基础参数、签名
	 * @param request
	 * @param needToken
	 * @return
	 */
	protected FanbeiWebContext doWebCheck(HttpServletRequest request,boolean needToken){
		FanbeiWebContext webContext = new FanbeiWebContext();
		String appInfo = getAppInfo(request.getHeader("Referer"));
		//如果是测试环境
		if(Constants.INVELOMENT_TYPE_TEST.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE)) && StringUtil.isBlank(appInfo)){
			String testUser = getTestUser(request.getHeader("Referer"));
			if(testUser != null){
				if("no".equals(testUser)){
//					webContext.setUserName("no");
					return webContext;
				}else{
					webContext.setUserName(testUser);
					return webContext;
				}
			}
		}
		webContext.setAppInfo(appInfo);
		if(StringUtil.isBlank(appInfo)){
			if(needToken){
				throw new FanbeiException("no login",FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
			}else{
				return webContext;
			}
		}
		RequestDataVo requestDataVo = parseRequestData(appInfo, request);
		requestDataVo.setParams(new HashMap<String, Object>());
		FanbeiContext baseContext = this.doBaseParamCheck(requestDataVo);
		webContext.setUserName(baseContext.getUserName());
		webContext.setAppVersion(baseContext.getAppVersion());
		checkWebSign(requestDataVo, needToken);
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
		String borrowMethodName = "/borrow/getBorrowHomeInfo";
		// TODO 设置上下文
		if (!beforeLogin) {// 需要登录的接口
			AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
			String methodString = requestDataVo.getMethod();
			if (userInfo == null && StringUtils.equals(borrowMethodName, methodString)) {
				throw new FanbeiException(requestDataVo.getId() + "user don't exist", FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR);

			} else if (userInfo == null) {

				throw new FanbeiException(requestDataVo.getId() + "user don't exist", FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
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
		this.checkSign(context.getAppVersion()+"" , ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_NETTYPE)), context.getUserName(), 
				ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_SIGN)), ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_TIME)), requestDataVo.getParams(), beforeLogin);

		return context;
	}
	
	private FanbeiContext doBaseParamCheck(RequestDataVo requestDataVo){

		FanbeiContext context = new FanbeiContext();
		if (requestDataVo == null || requestDataVo.getSystem() == null || requestDataVo.getParams() == null || requestDataVo.getMethod() == null) {
			throw new FanbeiException("缺少系统参数", FanbeiExceptionCode.REQUEST_PARAM_SYSTEM_NOT_EXIST);
		}
		Map<String, Object> systemMap = requestDataVo.getSystem();
		String appVersion = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_VERSION));
		String netType = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_NETTYPE));
		String userName = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_USERNAME));
		String sign = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_SIGN));
		String time = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_TIME));
		if (StringUtils.isBlank(appVersion) || StringUtils.isBlank(ObjectUtils.toString(netType)) || StringUtils.isBlank(ObjectUtils.toString(sign))
				|| StringUtils.isBlank(ObjectUtils.toString(time))) {
			logger.error(requestDataVo.getId() + ",version or netType or userName or sign or time is null or empty");
			throw new FanbeiException(requestDataVo.getId() + ",version or userId or sign or time can't be null or empty ", FanbeiExceptionCode.REQUEST_PARAM_SYSTEM_NOT_EXIST);
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
	private void checkSign(String appVersion, String netType, String userName, String sign, String time, Map<String, Object> params, boolean needToken) {
		if (Constants.SWITCH_OFF.equals(ConfigProperties.get(Constants.CONFKEY_CHECK_SIGN_SWITCH))) {
			return;
		}
		List<String> paramList = new ArrayList<String>();
		if (params != null && params.size() > 0) {
			paramList.addAll(params.keySet());
			Collections.sort(paramList);
		}

		String signStrBefore = "appVersion=" + appVersion + "&netType=" + netType + "&time=" + time + "&userName=" + userName;
		if (!needToken) {
			TokenBo token = (TokenBo) tokenCacheUtil.getToken(userName);
			if (token == null) {
				throw new FanbeiException("token is expire", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
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
	private void checkWebSign(RequestDataVo requestDataVo, boolean needToken) {
		if (Constants.SWITCH_OFF.equals(ConfigProperties.get(Constants.CONFKEY_CHECK_SIGN_SWITCH))) {
			return;
		}
		
		Map<String, Object> systemMap = requestDataVo.getSystem();
		String appVersion = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_VERSION));
		String netType = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_NETTYPE));
		String userName = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_USERNAME));
		String sign = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_SIGN));
		String time = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_TIME));
		String signStrBefore = "appVersion=" + appVersion + "&netType=" + netType + "&time=" + time + "&userName=" + userName;
		TokenBo token = (TokenBo) tokenCacheUtil.getToken(userName);
		if (needToken) {//需要登录的接口必须加token
			if (token == null) {
				throw new FanbeiException("token is expire", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
			}
			signStrBefore = signStrBefore + token.getToken();
		}else{//否则服务端判断是否有token,如果有说明登入过并且未过期则需要+token否则签名不加token
			if(token != null){
				signStrBefore = signStrBefore + token.getToken();
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
	 * 记录H5日志
	 * @param request
	 * @param respData
	 * @param exeT
	 */
	protected void doMaidianLog(HttpServletRequest request){
		JSONObject param = new JSONObject();
		Enumeration<String> enu=request.getParameterNames();  
		while(enu.hasMoreElements()){  
			String paraName=(String)enu.nextElement();  
			param.put(paraName, request.getParameter(paraName));
		}
		maidianLog.info(StringUtil.appendStrs("inte=",request.getRequestURI(),";ip=",CommonUtil.getIpAddr(request),";param=",param.toString()));
	}
	
	/**
	 * 记录H5日志
	 * @param request
	 * @param appInfo
	 * @param respData
	 * @param exeT
	 */
	protected void doLog(HttpServletRequest request,String appInfo,String respData,long exeT){
		JSONObject param = new JSONObject();
		param.put("_appInfo", appInfo);
		Enumeration<String> enu=request.getParameterNames();  
		while(enu.hasMoreElements()){  
			String paraName=(String)enu.nextElement();  
			param.put(paraName, request.getParameter(paraName));
		}
		this.doLog(param.toString(), respData, request.getMethod(), CommonUtil.getIpAddr(request), exeT+"", request.getRequestURI());
	}

	/**
	 * 记录日志
	 * @param reqData 请求参数
	 * @param resD 响应结果
	 * @param httpMethod 请求方法 GET或POST
	 * @param rmtIp 远程id
	 * @param exeT 执行时间
	 * @param inter 接口
	 */
	protected void doLog(String reqData,String resD,String httpMethod,String rmtIp,String exeT,String inter){
		webbiLog.info(StringUtil.appendStrs("reqD=",reqData,";resD=",resD,";methd=",httpMethod,";rmtIp=",rmtIp,";exeT=",exeT,";inter=",inter));
	}
	
	private static String getAppInfo(String url) {
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
			if(_appInfo != null && _appInfo.size() > 0){
				result = _appInfo.get(0);
			}
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}
	
	private static String getTestUser(String url) {
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
			if(_appInfo != null && _appInfo.size() > 0){
				result = _appInfo.get(0);
			}
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}
}
