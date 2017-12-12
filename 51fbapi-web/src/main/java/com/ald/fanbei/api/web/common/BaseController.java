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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.service.AfAppUpgradeService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.ThirdPartyLinkChannel;
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
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.impl.ApiHandleFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 陈金虎 2017年1月16日 下午11:56:17
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Logger biLogger = LoggerFactory.getLogger("FANBEI_BI");//app原生接口入口日志
    protected final Logger biNewLog = LoggerFactory.getLogger("FANBEINEW_BI");//新app原生接口入口日志

    protected final Logger webbiLog = LoggerFactory.getLogger("FBWEB_BI");//h5接口入口日志
    protected final Logger webbiNewLog = LoggerFactory.getLogger("FBWEBNEW_BI");//新h5接口入口日志

    protected final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");//埋点日志
    protected final Logger maidianNewLog = LoggerFactory.getLogger("FBMDNEW_BI");//埋点日志

    protected final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");//第三方调用日志

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
    AfShopService afShopService;

    @Resource
    private AfResourceService afResourceService;

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
            // 检查参	数是否为空
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

            //判断版本更新 后台控制
            try {
                AfResourceDo afResourceDo = afResourceService.getAfResourceAppVesion();
                if (afResourceDo != null && afResourceDo.getValue().toLowerCase().equals("true")) {
                    if (contex.getAppVersion() < Integer.parseInt(afResourceDo.getValue1()) && requestDataVo.getId().endsWith("www")) {
                        if (!(request.getRequestURI().toString().toLowerCase().contains("/system/appupgrade") || request.getRequestURI().toLowerCase().contains("/system/checkversion"))) {
                            throw new FanbeiException("version is letter 391", FanbeiExceptionCode.VERSION_ERROR);
                        }
                    }
                }
            } catch (Exception e) {
                if (e instanceof FanbeiException) {
                    FanbeiException fanebei1 = (FanbeiException) e;
                    if (fanebei1.getErrorCode().getCode().equals("VERSION_ERROR")) {
                        throw e;
                    } else {
                        logger.info("update version error", e.toString());
                        logger.error("update version error", e);
                    }
                } else {
                    logger.info("update version error", e.toString());
                    logger.error("update version error", e);
                }
            }

            // 处理业务
            exceptionresponse = doProcess(requestDataVo, contex, request);
            resultStr = JSON.toJSONString(exceptionresponse);
        } catch (FanbeiException e) {
            exceptionresponse = buildErrorResult(e.getErrorCode(), request);
            resultStr = JSON.toJSONString(exceptionresponse);
            logger.error("o2oapp exception id=" + (requestDataVo == null ? reqData : requestDataVo.getId()), e);
        } catch (Exception e) {
            exceptionresponse = buildErrorResult(FanbeiExceptionCode.SYSTEM_ERROR, request);
            resultStr = JSON.toJSONString(exceptionresponse);
            logger.error("system exception id=" + (requestDataVo == null ? reqData : requestDataVo.getId()), e);
        } finally {
            try {
                Calendar calEnd = Calendar.getInstance();
                if (StringUtils.isNotBlank(reqData)) {
                    reqData = reqData.replace("\r", "").replace("\n", "").replace(" ", "");
                }
                if (biLogger.isInfoEnabled()) {
                    String url = browsingAndCertification(request, reqData);
                    String req = "";
                    String userName = "no user";
                    if (requestDataVo == null) {
                        req = reqData;
                    } else {
                        userName = (String) requestDataVo.getSystem().get(Constants.REQ_SYS_NODE_USERNAME);
                        if ("/auth/checkFaceApi".equals(requestDataVo.getMethod())) {
                            req = reqData.length() + "";
                        } else {
                            req = requestDataVo.toString();
                        }
                    }
                    String clientType = "o";
                    if (requestDataVo != null && requestDataVo.getId() != null && requestDataVo.getId().startsWith("i_")) {
                        clientType = "i";
                    } else if (requestDataVo != null && requestDataVo.getId() != null && requestDataVo.getId().startsWith("a_")) {
                        clientType = "a";
                    }
                    biNewLog.info(StringUtil.appendStrs(
                            "	", DateUtil.formatDate(calStart.getTime(), DateUtil.DATE_TIME_SHORT),
                            "	", clientType,
                            "	", CommonUtil.getIpAddr(request),
                            "	", userName,
                            "	", (calEnd.getTimeInMillis() - calStart.getTimeInMillis()),
                            "	", url,
                            "	", exceptionresponse == null ? 9999 : ((ApiHandleResponse) exceptionresponse).getResult().getCode(),
                            "	", DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN),
                            "	", "",
                            "	", "",
                            "	", "",
                            "	", "",
                            "	", "",
                            "	", req,
                            "	", requestDataVo != null && ("/system/getArea".equals(requestDataVo.getMethod())) ? resultStr.length() + "" : resultStr));

                    biLogger.info(StringUtil.appendStrs(
                            "	", DateUtil.formatDate(calStart.getTime(), DateUtil.DATE_TIME_SHORT),
                            "	", clientType,
                            "	rmtIP=", CommonUtil.getIpAddr(request),
                            "	userName=", userName,
                            "	", (calEnd.getTimeInMillis() - calStart.getTimeInMillis()),
                            "	", url,
                            "	result=", exceptionresponse == null ? 9999 : ((ApiHandleResponse) exceptionresponse).getResult().getCode(),
                            "	", DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN),
                            "	", "",
                            "	", "",
                            "	", "",
                            "	", "",
                            "	", "",
                            "	reqD=", req,
                            "	resD=", requestDataVo != null && ("/system/getArea".equals(requestDataVo.getMethod())) ? resultStr.length() + "" : resultStr));
                }
            } catch (Exception e) {
                logger.error("app bi exception", e);
            }
        }
        return resultStr;
    }

    /**
     * 逛逛和实名认证埋点
     *
     * @param reqData
     * @param request
     * @return
     */
    private String browsingAndCertification(HttpServletRequest request,
                                            String reqData) {
        // TODO Auto-generated method stub
        String url = request.getRequestURI();
        JSONObject result = JSONObject.parseObject(reqData);
        if ("/brand/getBrandUrl".equals(url)) {
            Long shopId = NumberUtil.objToLongDefault(result.get("shopId"), null);
            AfShopDo afShopDo = afShopService.getShopById(shopId);
            String type = afShopDo.getType();
            url = url + "_" + type.toLowerCase();
        } else if ("/system/maidian".equals(url)) {
            String maidianEvent = ObjectUtils.toString(result.get("maidianEvent"), null);
            url = maidianEvent;
        }
        return url;
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
    public abstract BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest);

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
        if (idName.startsWith("i") && context.getAppVersion() < 379) {
            String[] strs = idName.split("_");
            String name = idName.substring(idName.lastIndexOf("_") + 1);
            if (strs.length == 3) {
                name = "www";
            }
            AfAppUpgradeDo afAppUpgradeDo = afAppUpgradeService.getNewestIOSVersionBySpecify(context.getAppVersion(), name);
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
     *
     * @param request
     * @param needToken
     * @return
     */
    protected FanbeiH5Context doH5Check(HttpServletRequest request, boolean needToken) {
        FanbeiH5Context webContext = new FanbeiH5Context();

        RequestDataVo requestDataVo = parseRequestData(StringUtils.EMPTY, request);

        checkH5Sign(request, webContext, requestDataVo, needToken);

        return webContext;
    }

    /**
     * 验证 token
     *
     * @param userName  用户名
     * @param time      时间戳
     * @param params    所有请求参数
     * @param needToken 是否需要needToken，不依赖登录的请求不需要，依赖登录的请求需要
     */
    private void checkH5Sign(HttpServletRequest request, FanbeiH5Context h5Context, RequestDataVo requestDataVo, boolean needToken) {
        //从cookie中取openid和token
        Map<String, String> openidToken = getUserNameToken(request);
        String username = openidToken.get(Constants.H5_USER_NAME_COOKIES_KEY);
        String tokenCookie = openidToken.get(Constants.H5_USER_TOKEN_COOKIES_KEY);

        if (logger.isDebugEnabled()) {
            logger.debug(" username = " + username + " token= " + tokenCookie);
        }
        if (needToken) {//需要登录的接口必须加token
            if (tokenCookie == null) {
                throw new FanbeiException("no login", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
            }
            String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + username;
            Object token = bizCacheUtil.getObject(tokenKey);
            if (token == null || !tokenCookie.equals(token.toString())) {
                throw new FanbeiException("no login", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
            }
            if (username != null) {
                AfUserDo userInfo = afUserService.getUserByUserName(username);
                h5Context.setUserName(username);
                h5Context.setUserId(userInfo.getRid());
                h5Context.setLogin(true);
            }
        } else {//否则服务端判断是否有token,如果有说明登入过并且未过期
            if (tokenCookie != null && username != null) {
                AfUserDo userInfo = afUserService.getUserByUserName(username);
                h5Context.setUserName(username);
                h5Context.setUserId(userInfo.getRid());
                h5Context.setLogin(true);
            }
        }
        return;
    }

    private Map<String, String> getUserNameToken(HttpServletRequest request) {
        Map<String, String> openidAndToken = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        String userName = null;
        String token = null;

        if (cookies != null && cookies.length > 0) {
            for (Cookie item : cookies) {
                if (StringUtils.equals(item.getName(), Constants.H5_USER_NAME_COOKIES_KEY)) {
                    userName = item.getValue();
                    openidAndToken.put(Constants.H5_USER_NAME_COOKIES_KEY, userName);
                    continue;
                }
                if (StringUtils.equals(item.getName(), Constants.H5_USER_TOKEN_COOKIES_KEY)) {
                    token = item.getValue();
                    openidAndToken.put(Constants.H5_USER_TOKEN_COOKIES_KEY, token);
                }
            }
        }
        return openidAndToken;
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
        //如果是测试环境
        logger.info("doWebCheck appInfo = {}", appInfo);
        if (Constants.INVELOMENT_TYPE_TEST.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE)) && StringUtil.isBlank(appInfo)) {
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
        RequestDataVo requestDataVo = parseRequestData(appInfo, request);
        requestDataVo.setParams(new HashMap<String, Object>());
        FanbeiContext baseContext = this.doBaseParamCheck(requestDataVo);
        webContext.setUserName(baseContext.getUserName());
        webContext.setAppVersion(baseContext.getAppVersion());
        checkWebSign(webContext, requestDataVo, needToken);
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

                throw new FanbeiException(requestDataVo.getId() + " user don't exist", FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
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
        //针对ios的379版本的升级接口不做处理
        if ("/system/appUpgrade".equals(requestDataVo.getMethod())) {
            logger.info(StringUtil.appendStrs("id=", requestDataVo.getId(), ",appUpgrade context=", context));
        }
        if ("/system/appUpgrade".equals(requestDataVo.getMethod()) && "379".equals(systemMap.get(Constants.REQ_SYS_NODE_VERSION)) && (requestDataVo.getId() != null && requestDataVo.getId().startsWith("i_"))) {
            logger.info(StringUtil.appendStrs("id=", requestDataVo.getId(), ",appUpgrade not check sign"));
            return context;
        }
        this.checkSign(context.getAppVersion() + "", ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_NETTYPE)), context.getUserName(),
                ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_SIGN)), ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_TIME)), requestDataVo.getParams(), beforeLogin);

        return context;
    }

    private FanbeiContext doBaseParamCheck(RequestDataVo requestDataVo) {

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
     * @param appVersion app版本
     * @param userName   用户名
     * @param sign       签名
     * @param time       时间戳
     * @param params     所有请求参数
     * @param needToken  是否需要needToken，不依赖登录的请求不需要，依赖登录的请求需要
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
     * 验证签名
     *
     * @param appVersion app版本
     * @param userName   用户名
     * @param sign       签名
     * @param time       时间戳
     * @param params     所有请求参数
     * @param needToken  是否需要needToken，不依赖登录的请求不需要，依赖登录的请求需要
     */
    private void checkWebSign(FanbeiWebContext webContext, RequestDataVo requestDataVo, boolean needToken) {

        Map<String, Object> systemMap = requestDataVo.getSystem();
        String appVersion = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_VERSION));
        String netType = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_NETTYPE));
        String userName = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_USERNAME));
        String sign = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_SIGN));
        String time = ObjectUtils.toString(systemMap.get(Constants.REQ_SYS_NODE_TIME));
        TokenBo token = (TokenBo) tokenCacheUtil.getToken(userName);
        logger.info("checkWebSign systemMap = {} ,token = {}", systemMap, token);
        if (logger.isDebugEnabled()) {
            logger.debug(userName + " token= " + token);
        }
        if (Constants.SWITCH_OFF.equals(ConfigProperties.get(Constants.CONFKEY_CHECK_SIGN_SWITCH))) {
            if (needToken) {//需要登录的接口必须加token
                if (token == null) {
                    throw new FanbeiException("token is expire", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
                }
                webContext.setLogin(true);
            } else {//否则服务端判断是否有token,如果有说明登入过并且未过期则需要+token否则签名不加token
                if (token != null) {
                    webContext.setLogin(true);
                }
            }
            return;
        }
        String signStrBefore = "appVersion=" + appVersion + "&netType=" + netType + "&time=" + time + "&userName=" + userName;
//		TokenBo token = (TokenBo) tokenCacheUtil.getToken(userName);
        if (needToken) {//需要登录的接口必须加token
            if (token == null) {
                throw new FanbeiException("token is expire", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
            }
            signStrBefore = signStrBefore + token.getToken();
            webContext.setLogin(true);
        } else {//否则服务端判断是否有token,如果有说明登入过并且未过期则需要+token否则签名不加token
            if (token != null) {
                signStrBefore = signStrBefore + token.getToken();
                webContext.setLogin(true);
            }
        }
        logger.info("signStrBefore = {}", signStrBefore);
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
            //第三方链接进入
            if (request.getRequestURI().equals("/fanbei-web/thirdPartyLink")) {
                String channel = null;
                String referer = request.getHeader("Referer");
                if (StringUtils.isNotBlank(referer)) {
                    int index = referer.indexOf("?");
                    if (index != -1) {
                        String paramStrs = referer.substring(++index);
                        String[] params = paramStrs.split("&");
                        for (String urlParam : params) {
                            if (StringUtils.isNotBlank(urlParam)) {
                                String vals[] = urlParam.split("=");
                                if ("channel".equals(vals[0])) {
                                    if (vals.length == 1) {
                                        channel = ThirdPartyLinkChannel.DEFAULT.getCode(); //防止人为的设置过大的数值
                                    } else {
                                        channel = ThirdPartyLinkChannel.getChannel(vals[1]);
                                    }
                                }
                            }
                        }
                    }
                }
                maidianNewLog.info(StringUtil.appendStrs(
                        "	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                        "	", "h",
                        "	", CommonUtil.getIpAddr(request),
                        "	", userName,
                        "	", 0,
                        "	", request.getRequestURI(),
                        "	", respData == null ? false : respData.getSuccess(),
                        "	", DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN),
                        "	", "md",
                        "	", request.getParameter("lsmNo"),
                        "	", request.getParameter("linkType"),
                        "	", channel,
                        "	", "",
                        "	", param == null ? "{}" : param.toString(),
                        "	", respData == null ? "{}" : respData.toString()));

                maidianLog.info(StringUtil.appendStrs(
                        "	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                        "	", "h",
                        "	rmtIP=", CommonUtil.getIpAddr(request),
                        "	userName=", userName,
                        "	", 0,
                        "	", request.getRequestURI(),
                        "	result=", respData == null ? false : respData.getSuccess(),
                        "	", DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN),
                        "	", "md",
                        "	lsmNo=", request.getParameter("lsmNo"),
                        "	linkType=", request.getParameter("linkType"),
                        "	channel=", channel,
                        "	", "",
                        "	reqD=", param.toString(),
                        "	resD=", respData == null ? "null" : respData.toString()));
            } else {
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
                maidianNewLog.info(StringUtil.appendStrs(
                        "	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                        "	", "h",
                        "	", CommonUtil.getIpAddr(request),
                        "	", userName,
                        "	", 0,
                        "	", request.getRequestURI(),
                        "	", respData == null ? false : respData.getSuccess(),
                        "	", DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN),
                        "	", "md",
                        "	", ext1,
                        "	", ext2,
                        "	", ext3,
                        "	", ext4,
                        "	", param == null ? "{}" : param.toString(),
                        "	", respData == null ? "{}" : respData.toString()));

                maidianLog.info(StringUtil.appendStrs(
                        "	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                        "	", "h",
                        "	rmtIP=", CommonUtil.getIpAddr(request),
                        "	userName=", userName,
                        "	", 0,
                        "	", request.getRequestURI(),
                        "	result=", respData == null ? false : respData.getSuccess(),
                        "	", DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN),
                        "	", "md",
                        "	", ext1,
                        "	", ext2,
                        "	", ext3,
                        "	", ext4,
                        "	reqD=", param.toString(),
                        "	resD=", respData == null ? "null" : respData.toString()));
            }


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
    protected void doLog(HttpServletRequest request, H5CommonResponse respData, String appInfo, long exeT, String userName) {
        try {
            JSONObject param = new JSONObject();
//			String userName = "no user";
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
            if ("/app/user/getRegisterSmsCode".equals(request.getRequestURI())) {
                ext1 = ObjectUtils.toString(request.getParameter("channelCode"), "").toString();
                ext2 = ObjectUtils.toString(request.getParameter("pointCode"), "").toString();
                ext3 = respData != null ? respData.getMsg() : "";
                ext4 = ObjectUtils.toString(request.getParameter("token"), "").toString();
            }
            if ("/app/user/commitChannelRegister".equals(request.getRequestURI())) {
                ext1 = ObjectUtils.toString(request.getParameter("channelCode"), "").toString();
                ext2 = ObjectUtils.toString(request.getParameter("pointCode"), "").toString();
                ext3 = respData != null ? respData.getMsg() : "";
                ext4 = ObjectUtils.toString(request.getParameter("token"), "").toString();
            }
            this.doLog(param.toString(), respData, request.getMethod(), CommonUtil.getIpAddr(request), exeT + "", request.getRequestURI(), userName, ext1, ext2, ext3, ext4, ext5);
        } catch (Exception e) {
            logger.error("do log exception", e);
        }
    }

    /**
     * @param reqData     请求参数
     * @param respData    响应结果
     * @param httpMethod  请求方法 GET或POST
     * @param rmtIp       远程id
     * @param exeT        执行时间
     * @param inter       接口
     * @param userName用户名
     * @param ext1        扩展参数1
     * @param ext2        扩展参数2
     * @param ext3        扩展参数3
     * @param ext4        扩展参数4
     * @param ext5        扩展参数5
     */
    protected void doLog(String reqData, H5CommonResponse respData, String httpMethod, String rmtIp, String exeT, String inter, String userName, String ext1, String ext2, String ext3, String ext4, String ext5) {

        webbiNewLog.info(StringUtil.appendStrs(
                "	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                "	", "h",
                "	", rmtIp,
                "	", userName,
                "	", exeT,
                "	", inter,
                "	", respData == null ? "false" : respData.getSuccess() + "",
                "	", DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN),
                "	", ext1,
                "	", ext2,
                "	", ext3,
                "	", ext4,
                "	", ext5,
                "	", StringUtil.isBlank(reqData) ? "{}" : reqData,
                "	", respData == null ? "{}" : respData.toString()));

        webbiLog.info(StringUtil.appendStrs(
                "	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                "	", "h",
                "	rmtIP=", rmtIp,
                "	userName=", userName,
                "	", exeT,
                "	", inter,
                "	result=", respData == null ? "false" : respData.getSuccess() + "",
                "	", DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN),
                "	", ext1,
                "	", ext2,
                "	", ext3,
                "	", ext4,
                "	", ext5,
                "	reqD=", reqData,
                "	resD=", respData == null ? "null" : respData.toString()));
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
    	if(StringUtils.isBlank(url)) {
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

    /**
     * h5接口验证，验证基础参数、签名
     *
     * @param request
     * @param needToken
     * @return
     */
    protected FanbeiWebContext doWebCheckNoAjax(HttpServletRequest request, boolean needToken) {
        FanbeiWebContext webContext = new FanbeiWebContext();
        String appInfo = request.getParameter("_appInfo");
        webContext.setAppInfo(appInfo);
        if (StringUtil.isBlank(appInfo)) {
            if (needToken) {
                throw new FanbeiException("no login", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
            } else {
                return webContext;
            }
        }
        RequestDataVo requestDataVo = parseRequestData(appInfo, request);
        requestDataVo.setParams(new HashMap<String, Object>());
        FanbeiContext baseContext = this.doBaseParamCheck(requestDataVo);
        webContext.setUserName(baseContext.getUserName());
        webContext.setAppVersion(baseContext.getAppVersion());
        checkWebSign(webContext, requestDataVo, needToken);
        return webContext;
    }

    /**
     * 解析request
     *
     * @param reqData
     * @param httpServletRequest
     * @return
     */
    protected String processTradeWeiXinRequest(String reqData, HttpServletRequest request, boolean isForQQ) {
//        String resultStr = StringUtils.EMPTY;
        BaseResponse exceptionresponse = null;
        RequestDataVo requestDataVo = null;
        try {
            // 检查参数是否为空
            reqData = checkCommonParam(reqData, request, isForQQ);

            // 解析参数（包括请求头中的参数和报文体中的参数）
            requestDataVo = parseRequestData(reqData, request);

            // 验证参数
            boolean beforeLogin = apiHandleFactory.checkBeforlogin(requestDataVo.getMethod());
            if (!beforeLogin) { //需要登录的接口
                String loginKey = Constants.TRADE_LOGIN_BUSINESSID + request.getHeader("businessId");
                String token = request.getHeader("token");
                if (StringUtil.isBlank(token) || StringUtil.isBlank(loginKey)) {
                    throw new FanbeiException("business don't exist", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
                }
                Object value = bizCacheUtil.getObject(loginKey);
                if (value == null || !token.equals(value)) {
                    throw new FanbeiException("business is timeout", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT);
                }
                //更新有效时间
                bizCacheUtil.saveObject(loginKey, (String) value, Constants.SECOND_OF_HALF_HOUR);
            }

            // 处理业务
            exceptionresponse = doProcess(requestDataVo, null, request);
        } catch (FanbeiException e) {
            exceptionresponse = new ApiHandleResponse("trade_weixin_error", e.getErrorCode());
            logger.error("trade weixin exception {}", e);
        } catch (Exception e) {
            exceptionresponse = new ApiHandleResponse("trade_weixin_error", FanbeiExceptionCode.SYSTEM_ERROR);
            logger.error("system exception {}", e);
        }
        return JSON.toJSONString(exceptionresponse);
    }
}
