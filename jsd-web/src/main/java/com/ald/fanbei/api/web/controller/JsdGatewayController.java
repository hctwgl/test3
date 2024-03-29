package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.jsd.JsdParam;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsdSignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.chain.InterceptorChain;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.ContextImpl.ContextBuilder;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleFactory;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * @author 郭帅强 2018年1月17日 下午6:15:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class JsdGatewayController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Logger biLog = LoggerFactory.getLogger("JSD_BI");// 新app原生接口入口日志
    protected final Logger mdLog = LoggerFactory.getLogger("JSD_MD");// 埋点日志
    protected final Logger thirdLog = LoggerFactory.getLogger("JSD_THIRD");// 第三方调用日志
    private static String PRIVATE_KEY = ConfigProperties.get(Constants.CONFKEY_XGXY_AES_PASSWORD);
	
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdH5HandleFactory jsdH5HandleFactory;
    
    @Resource
    InterceptorChain interceptorChain;

    /**
     * 忽略处理openId的逻辑，适用于同步用户信息，注册，登陆的接口
     * @param param
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/third/eca/v1/syncUserInfo","/third/eca/v1/getAddressList"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String handleIgnoreOpenId(@RequestBody JsdParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        
        JsdH5HandleResponse baseResponse;
        Context context = null;
        Long stmap = System.currentTimeMillis();
        try {
        	ContextBuilder contextBuilder = new ContextBuilder();
            JSONObject dataMap = new JSONObject();
            String data = param.getData();
            
            if (StringUtils.isNotEmpty(data)) dataMap = JSON.parseObject(AesUtil.decryptFromBase64Third(data, PRIVATE_KEY));
            
            contextBuilder.method(request.getRequestURI());
            contextBuilder.dataMap(dataMap);
            contextBuilder.clientIp(CommonUtil.getIpAddr(request));
            context = contextBuilder.build();
            logger.debug("buildContext success, ori data = "+data+", final context=" + JSON.toJSONString(context));
        	
            this.checkSign(context, param.getSign());
            baseResponse = this.doProcess(context);
        } catch (BizException e) {
            baseResponse = buildErrorResult(e, request);
        } catch (Exception e) {
            baseResponse = buildErrorResult(BizExceptionCode.SYSTEM_ERROR, request);
        }
        
        doBiLog(context, request, baseResponse, System.currentTimeMillis() - stmap);
        return JSON.toJSONString(baseResponse);
    }
    
    /**
     * 此路径匹配的所有接口要求必须携带openId
     */
    @RequestMapping(value = {"/third/xgxy/v1/**","/third/eca/v1/**"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String handle(@RequestBody JsdParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        
        return this.processRequest(request, param.getData(), param.getSign());
    }

    private String processRequest(HttpServletRequest request, String data, String sign) {
        JsdH5HandleResponse baseResponse;
        Context context = null;
        Long stmap = System.currentTimeMillis();
        try {
            context = this.buildContext(request, data);
            this.checkSign(context, sign);
            baseResponse = this.doProcess(context);
        } catch (BizException e) {
            baseResponse = buildErrorResult(e, request);
        } catch (Exception e) {
            baseResponse = buildErrorResult(BizExceptionCode.SYSTEM_ERROR, request);
        }
        
        doBiLog(context, request, baseResponse, System.currentTimeMillis() - stmap);
        return JSON.toJSONString(baseResponse);
    }
    private Context buildContext(HttpServletRequest request, String data) {
    	ContextBuilder contextBuilder = new ContextBuilder();
        String method = request.getRequestURI();
        JSONObject dataMap = new JSONObject();

        if (StringUtils.isNotEmpty(data)) {
            String decryptData = AesUtil.decryptFromBase64Third(data, PRIVATE_KEY);
            dataMap = JSON.parseObject(decryptData);
            Object openId = dataMap.get("openId");
            if(openId == null) {
            	throw new BizException("OpenId can not be null!");
            }
            
            JsdUserDo userDo = jsdUserService.getByOpenId(openId.toString());
            
            contextBuilder.method(method);
            if (userDo == null) {
            	throw new BizException("Can not find refer user by openId " + openId);
            } else {
            	contextBuilder.userName(userDo.getMobile());
            	contextBuilder.userId(userDo.getRid());
            	contextBuilder.idNumber(userDo.getIdNumber());
            	contextBuilder.realName(userDo.getRealName());
            	contextBuilder.openId(userDo.getOpenId());
            }
        }

        contextBuilder.dataMap(dataMap);
        contextBuilder.clientIp(CommonUtil.getIpAddr(request));
        Context context = contextBuilder.build();
        logger.debug("buildContext success, ori data = "+data+", final context=" + JSON.toJSONString(context));
        return context;
    }

    public JsdH5HandleResponse doProcess(Context context) {
        try {
        	interceptorChain.execute(context);
        	JsdH5Handle methodHandle = jsdH5HandleFactory.getHandle(context.getMethod());
            return methodHandle.process(context);
        } catch (BizException e) {
            logger.error("biz exception, msg=" + e.getMessage() + ", code=" + e.getErrorCode(), e);
            throw e;
        } catch (Exception e) {
            logger.error("sys exception", e);
            throw new BizException("sys exception", BizExceptionCode.SYSTEM_ERROR);
        }
    }

    
    protected JsdH5HandleResponse buildErrorResult(BizExceptionCode exceptionCode, HttpServletRequest request) {
        JsdH5HandleResponse resp;
        if (exceptionCode == null) {
            exceptionCode = BizExceptionCode.SYSTEM_ERROR;
        }
        resp = new JsdH5HandleResponse(exceptionCode);
        return resp;
    }
    protected JsdH5HandleResponse buildErrorResult(BizException e, HttpServletRequest request) {
        BizExceptionCode exceptionCode = e.getErrorCode();
        JsdH5HandleResponse resp;
        if (exceptionCode == null) {
            exceptionCode = BizExceptionCode.SYSTEM_ERROR;
        }
        if (e.getDynamicMsg() != null && e.getDynamicMsg()) {
            resp = new JsdH5HandleResponse(exceptionCode, e.getMessage());
        } else {
            resp = new JsdH5HandleResponse(exceptionCode);
        }

        return resp;
    }

    private void checkSign(Context context, String sign) {
    	JSONObject dataMap = context.getDataMap();
        String md5Value = JsdSignUtil.generateSign(dataMap, PRIVATE_KEY);
        if (logger.isDebugEnabled())
            logger.info("signStrBefore=" + dataMap + ",md5Value=" + md5Value + ",sign=" + sign);
        /* if (!StringUtils.equals(sign, md5Value)) { TODO 取消注释
            logger.error("signStrBefore=" + systemsMap + ",md5Value=" + md5Value + ",sign=" + sign);
            throw new FanbeiException("sign is error", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
        }*/
    }

    /**
     * 记录埋点相关日志
     *
     * @param request
     * @param respData
     * @param exeT
     */
    protected void doMaidianLog(HttpServletRequest request, JsdH5HandleResponse respData, String... extInfo) {
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
            mdLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                    "	", "h", "	", CommonUtil.getIpAddr(request), "	", userName, "	", 0, "	",
                    request.getRequestURI(), "	", respData == null ? false : respData.getCode(), "	",
                    DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "md", "	", ext1, "	", ext2,
                    "	", ext3, "	", ext4, "	", param == null ? "{}" : param.toString(), "	",
                    respData == null ? "{}" : respData.toString()));

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
    protected void doBiLog(Context context, HttpServletRequest request, JsdH5HandleResponse respData, long exeT) {
        try {
            String ext1 = "";
            String ext2 = "";
            String ext3 = "";
            String ext4 = "";
            String ext5 = "";

            this.doLog(context != null ? JSON.toJSONString(context.getDataMap()):"", respData, request.getMethod(), CommonUtil.getIpAddr(request), String.valueOf(exeT),
                    request.getRequestURI(), context != null ? context.getUserName():"", ext1, ext2, ext3, ext4, ext5);
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
    protected void doLog(String reqData, JsdH5HandleResponse respData, String httpMethod, String rmtIp, String exeT,
                         String inter, String userName, String ext1, String ext2, String ext3, String ext4, String ext5) {

    	biLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT), "	",
                "h", "	", rmtIp, "	", userName, "	", exeT, "	", inter, "	",
                respData == null ? "false" : respData.getCode() + "", "	",
                DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", ext1, "	", ext2, "	", ext3, "	",
                ext4, "	", ext5, "	", StringUtil.isBlank(reqData) ? "{}" : reqData, "	",
                respData == null ? "{}" : respData.toString()));
    }


}