package com.ald.fanbei.api.web.common;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.impl.H5HandleFactory;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
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
	protected TokenCacheUtil tokenCacheUtil;
	@Resource
	AfUserService afUserService;

	@Resource
	AfAppUpgradeService afAppUpgradeService;
	@Resource
	private BizCacheUtil bizCacheUtil;

	@Resource
	private AfResourceService afResourceService;

	@Resource
	private H5HandleFactory h5HandleFactory;

	protected String processRequest(HttpServletRequest request) {
		String retMsg = StringUtils.EMPTY;
		BaseResponse baseResponse = null;
		Context context = null;
		try {
			checkAppInfo(request);
			// 解析参数（包括请求头中的参数和报文体中的参数）
			context = parseRequestData(request);
			checkLogin(context);
			// 校验请求数据
			doCheck(context);
			baseResponse = doProcess(context);
			retMsg = JSON.toJSONString(baseResponse);
		} catch (FanbeiException e) {
			baseResponse = buildErrorResult(e, request);
			retMsg = JSON.toJSONString(baseResponse);
		} catch (Exception e) {
			baseResponse = buildErrorResult(FanbeiExceptionCode.SYSTEM_ERROR, request);
			retMsg = JSON.toJSONString(baseResponse);
		}
		doMaidianLog(request,H5CommonResponse.getNewInstance(true, "succ"));
		logger.info("req method=>"+context.getMethod()+",userId=>"+context.getUserId()+",response msg=>" + retMsg);
		return retMsg;
	}

	private void checkLogin(Context context) {
		H5Handle methodHandle = h5HandleFactory.getHandle(context.getMethod());
        
        // 接口是否需要登录
        boolean needLogin = isNeedLogin(methodHandle.getClass());
        context.setData("_needLogin", needLogin);
        
        if(needLogin && context.getUserId() == null) {
        	throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
        }
		
	}
	
	
	private boolean isNeedLogin(Class<? extends H5Handle> clazz) {
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		for(Annotation annotation : annotations) {
			if(annotation instanceof NeedLogin) {
				return true;
			}
		}
		return false;
	}

	private void checkAppInfo(HttpServletRequest request) {
		String appInfo = request.getParameter("_appInfo");
		if(StringUtils.isEmpty(appInfo)) {
			String referer = request.getHeader("Referer");
			if(StringUtils.isNotEmpty(referer)) {
				if(StringUtils.contains(referer, "_appInfo")) 
					return;
			}
		}
		
		// App内部H5接口，客户端必须上送_appInfo字段
		if (StringUtils.isBlank(appInfo)) {
			throw new FanbeiException("param is null", FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
	}
	
	
	
	protected BaseResponse buildErrorResult(FanbeiExceptionCode exceptionCode, HttpServletRequest request) {
		H5HandleResponse resp = new H5HandleResponse();
		resp.setId(request.getHeader(Constants.REQ_SYS_NODE_ID));
		if (exceptionCode == null) {
			exceptionCode = FanbeiExceptionCode.SYSTEM_ERROR;
		}
		resp = new H5HandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode);
		return resp;
	}

	protected BaseResponse buildErrorResult(FanbeiException e, HttpServletRequest request) {
		FanbeiExceptionCode exceptionCode = e.getErrorCode();
		H5HandleResponse resp = new H5HandleResponse();
		resp.setId(request.getHeader(Constants.REQ_SYS_NODE_ID));
		if (exceptionCode == null) {
			exceptionCode = FanbeiExceptionCode.SYSTEM_ERROR;
		}
		if (e.getDynamicMsg() != null && e.getDynamicMsg()) {
			resp = new H5HandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode, e.getMessage());
		} else if (!StringUtil.isEmpty(e.getResourceType())) {
			AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(e.getResourceType());
			String msgTemplate = afResourceDo.getValue();
			for (String paramsKey : e.paramsMap.keySet()) {
				msgTemplate = msgTemplate.replace(paramsKey, e.paramsMap.get(paramsKey));
			}
			resp = new H5HandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode, msgTemplate);
		} else {
			resp = new H5HandleResponse(request.getHeader(Constants.REQ_SYS_NODE_ID), exceptionCode);
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
	public abstract Context parseRequestData(HttpServletRequest request);

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
	protected void doCheck(Context context) {

		String interfaceName = context.getMethod();
		if (StringUtils.isBlank(interfaceName)) {
			throw new FanbeiException("request method is null or empty",
					FanbeiExceptionCode.REQUEST_PARAM_METHOD_ERROR);
		}
		doBaseParamCheck(context);
		checkWebSign(context);
	}

	private void checkWebSign(Context context) {
		String userName = context.getUserName();
		Integer appVersion = context.getAppVersion();
		String sign = (String) context.getSystemsMap().get("sign");
		String netType = (String) context.getSystemsMap().get("netType");
		String time = (String) context.getSystemsMap().get("time");
		String signStrBefore = "appVersion=" + appVersion + "&netType=" + netType + "&time=" + time + "&userName="
				+ userName;

		TokenBo token = (TokenBo) tokenCacheUtil.getToken(userName);
		boolean needLogin = (boolean)context.getData("_needLogin");
		
		if (needLogin) {// 需要登录的接口必须加token
			if (token == null) {
				throw new FanbeiException("token is expire", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
			}
			signStrBefore = signStrBefore + token.getToken();
		} else {// 否则服务端判断是否有token,如果有说明登入过并且未过期则需要+token否则签名不加token
			if (token != null) {
				signStrBefore = signStrBefore + token.getToken();
			}
		}
		logger.info("signStrBefore = {}", signStrBefore);
		//this.compareSign(signStrBefore, sign);

	}

	private void compareSign(String signStrBefore, String sign) {
		String sha256Value = DigestUtil.getDigestStr(signStrBefore);
		if (logger.isDebugEnabled())
			logger.info("signStrBefore=" + signStrBefore + ",sha256Value=" + sha256Value + ",sign=" + sign);
		if (!StringUtils.equals(sign, sha256Value)) {
			throw new FanbeiException("sign is error", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
		}
	}

	private void doBaseParamCheck(Context context) {
		Map<String, Object> systemMap = context.getSystemsMap();
		String appVersion = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_VERSION));
		String netType = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_NETTYPE));
		String sign = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_SIGN));
		String time = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_TIME));
		if (StringUtils.isBlank(appVersion) || StringUtils.isBlank(netType) || StringUtils.isBlank(sign)
				|| StringUtils.isBlank(time)) {
			logger.error(context.getId() + ",version or netType or userName or sign or time is null or empty");
			throw new FanbeiException(context.getId() + ",version or userId or sign or time can't be null or empty ",
					FanbeiExceptionCode.REQUEST_PARAM_SYSTEM_NOT_EXIST);
		}
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
					DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "md", "	", ext1, "	", ext2,
					"	", ext3, "	", ext4, "	", param == null ? "{}" : param.toString(), "	",
					respData == null ? "{}" : respData.toString()));

			maidianLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT), "	",
					"h", "	rmtIP=", CommonUtil.getIpAddr(request), "	userName=", userName, "	", 0, "	",
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

}
