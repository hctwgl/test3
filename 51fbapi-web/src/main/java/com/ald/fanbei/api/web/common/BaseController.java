package com.ald.fanbei.api.web.common;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.impl.ApiHandleFactory;
import com.alibaba.fastjson.JSON;

/**
 * 
 *@类描述：
 *@author 陈金虎 2017年1月16日 下午11:56:17
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Logger biLogger = LoggerFactory.getLogger("FANBEI_BI");
    protected final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");
    
    @Resource
    protected ApiHandleFactory apiHandleFactory;
    @Resource
    TokenCacheUtil tokenCacheUtil;
    @Resource
    AfUserService afUserService;
    
   
    
    /**
     * 解析request
     * 
     * @param reqData
     * @param httpServletRequest
     * @return
     */
    protected  String processRequest(String reqData, HttpServletRequest request, boolean isForQQ) {
        String resultStr = StringUtils.EMPTY;
        BaseResponse exceptionresponse = null;
        Calendar calStart = Calendar.getInstance();
        RequestDataVo requestDataVo = null;
        try {
        	//检查参数是否为空
        	reqData = checkCommonParam(reqData, request, isForQQ);
//            if (StringUtils.isBlank(reqData)) {
//                exceptionresponse = buildErrorResult(FanbeExceptionCode.REQUEST_PARAM_NOT_EXIST, request);
//                return JSON.toJSONString(exceptionresponse);
//            }
            //解析参数（包括请求头中的参数和报文体中的参数）
            requestDataVo = parseRequestData(reqData,request);
            //验证参数、签名
            FanbeiContext contex = doCheck(requestDataVo);
            //处理业务
            resultStr = doProcess(requestDataVo,contex, request);
        } catch (FanbeiException e) {
            exceptionresponse = buildErrorResult(e.getErrorCode(), request);
            resultStr = JSON.toJSONString(exceptionresponse);
            logger.error("o2oapp exception id=" + (requestDataVo == null?reqData:requestDataVo.getId()), e);
        } catch (Exception e) {
            exceptionresponse = buildErrorResult(FanbeiExceptionCode.SYSTEM_ERROR, request);
            resultStr = JSON.toJSONString(exceptionresponse);
            logger.error("system exception id=" + (requestDataVo == null?reqData:requestDataVo.getId()), e);
        } finally {
            Calendar calEnd = Calendar.getInstance();
            if(StringUtils.isNotBlank(reqData)){
                reqData = reqData.replace("\r", "").replace("\n", "").replace(" ", "");
            }
            if (biLogger.isInfoEnabled()){
                biLogger.info(StringUtil.appendStrs("reqD=" , requestDataVo==null?reqData:requestDataVo , 
                		";resD=" , requestDataVo!=null&&"/system/getArea".equals(requestDataVo.getMethod())?resultStr.length()+"":resultStr
                        , ";rmtIP=" , CommonUtil.getIpAddr(request)
                        , ";exeT=" , (calEnd.getTimeInMillis() - calStart.getTimeInMillis())
                        , ";intefN=" , request.getRequestURI()));
            }
        }
        return resultStr;
    }
    
    /**
     * 验证参数
     * 
     *@param reqData
     *@param request
     *@return
     */
    public abstract String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ);
    
    protected  BaseResponse buildErrorResult(FanbeiExceptionCode exceptionCode, HttpServletRequest request){
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
     *@param requestData
     *@param request
     *@return
     */
    public abstract RequestDataVo parseRequestData(String requestData, HttpServletRequest request) ;
    
    /**
     * 处理请求
     * 
     *@param requestDataVo
     *@param context
     *@param httpServletRequest
     *@return
     */
    public abstract String doProcess(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest httpServletRequest); 
    
    /**
     * 验证基础参数、签名
     * 
     *@param requestDataVo
     *@return
     */
    protected FanbeiContext doCheck(RequestDataVo requestDataVo) {
    	FanbeiContext context = doSystemCheck(requestDataVo);
        
        if(requestDataVo.getId()!= null && requestDataVo.getId().indexOf("i_") == 0 && context.getAppVersion() == 121){
        	throw new FanbeiException("系统维护中",FanbeiExceptionCode.SYSTEM_REPAIRING_ERROR);
        }
        
        String interfaceName = requestDataVo.getMethod();
        if (StringUtils.isBlank(interfaceName)) {
            throw new FanbeiException("request method is null or empty",
                                        FanbeiExceptionCode.REQUEST_PARAM_METHOD_ERROR);
        }
        return context;
    }
    
    /**
     * 验证系统参数，验证签名
     * 
     *@param requestDataVo
     *@return
     */
    private FanbeiContext doSystemCheck(RequestDataVo requestDataVo){
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
        if (StringUtils.isBlank(appVersion)
        		|| StringUtils.isBlank(ObjectUtils.toString(netType))
                || StringUtils.isBlank(ObjectUtils.toString(sign)) 
                || StringUtils.isBlank(ObjectUtils.toString(time))
                ) {
                logger.error(requestDataVo.getId() + ",version or netType or userName or sign or time is null or empty");
                throw new FanbeiException(requestDataVo.getId() + ",version or userId or sign or time can't be null or empty ", FanbeiExceptionCode.REQUEST_PARAM_SYSTEM_NOT_EXIST);
        }
        int version = NumberUtil.objToIntDefault(systemMap.get(Constants.REQ_SYS_NODE_VERSION), 0);
        context.setUserName(userName);
        context.setAppVersion(version);
        //是否登录之前接口，若登录之前的接口不需要验证用户，否则需要验证用户
        boolean beforeLogin = true;
        beforeLogin = apiHandleFactory.checkBeforlogin(requestDataVo.getMethod());
        String borrowMethodName ="/borrow/getBorrowHomeInfo";
        //TODO 设置上下文
        if(!beforeLogin){//需要登录的接口
	        AfUserDo userInfo = afUserService.getUserByUserName(userName);
	        String methodString = requestDataVo.getMethod();
	        System.out.println(methodString);
	        if(userInfo ==null&&StringUtils.equals(borrowMethodName, methodString)){
	        	throw new FanbeiException(requestDataVo.getId() + "user don't exist", FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR);

	        }else  if (userInfo == null) {
	        	
	        	throw new FanbeiException(requestDataVo.getId() + "user don't exist", FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
	        }
	        context.setUserId(userInfo.getRid());
	        context.setNick(userInfo.getNick());
	        context.setMobile(userInfo.getMobile());
        }else if(beforeLogin && CommonUtil.isMobile(userName)){//不需要登录但是已经登录过
        	AfUserDo userInfo = afUserService.getUserByUserName(userName);
	        if (userInfo != null) {
		        context.setUserId(userInfo.getRid());
		        context.setNick(userInfo.getNick());
		        context.setMobile(userInfo.getMobile());
	        }
        }
        
        //验证签名
        this.checkSign(appVersion,netType, userName, sign, time,requestDataVo.getParams(), beforeLogin);
        
        return context;
    }
    
    /**
     * 验证签名
     *@param appVersion app版本
     *@param userName 用户名
     *@param sign 签名
     *@param time 时间戳
     *@param params 所有请求参数
     *@param needToken 是否需要needToken，不依赖登录的请求不需要，依赖登录的请求需要
     */
    private void checkSign(String appVersion,String netType,String userName,String sign,String time,Map<String, Object> params,boolean needToken){
        if(Constants.SWITCH_OFF.equals(ConfigProperties.get(Constants.CONFKEY_CHECK_SIGN_SWITCH))){
            return ;
        }
    	List<String> paramList = new ArrayList<String>();
    	if(params != null && params.size() > 0){
    		paramList.addAll(params.keySet());
    		Collections.sort(paramList);
    	}
    	
    	String signStrBefore = "appVersion=" + appVersion + "&netType=" + netType + "&time=" + time + "&userName=" + userName ;
    	if(!needToken){
	        TokenBo token = (TokenBo)tokenCacheUtil.getToken(userName);
	        if(token == null){
	            throw new FanbeiException("token is expire",FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
	        }
	        
	        //refresh token
	        Date lastAccess = DateUtil.convertMillisToDate(Long.parseLong(token.getLastAccess()),new Date());//最后访问时间
	        Date lasstAccessTemp = DateUtil.addMins(lastAccess, Constants.MINITS_OF_2HOURS);
	        if(DateUtil.afterDay(new Date(),lasstAccessTemp)){
	        	token.setLastAccess(System.currentTimeMillis()+"");
	        	tokenCacheUtil.saveToken(userName, token);
	        }
	        
	        signStrBefore = signStrBefore + token.getToken();
    	}
    	if(paramList.size() > 0){
    		for(String item:paramList){
    			signStrBefore = signStrBefore + "&" + item + "=" + params.get(item);
    		}
    	}
    	
        this.compareSign(signStrBefore, sign);
        
    }
    
    /**
     * 比较签名值
     *@param signStrBefore
     *@param sign
     */
    private void compareSign(String signStrBefore,String sign){
        String sha256Value = DigestUtil.getDigestStr(signStrBefore);
        if(logger.isDebugEnabled())
            logger.debug("signStrBefore=" + signStrBefore + ",sha256Value=" + sha256Value+",sign="+sign);
        if(!StringUtils.equals(sign, sha256Value)){
            throw new FanbeiException("sign is error",FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
        }
    }
    
    protected void writeRespData(PrintWriter writer, String resultStr) {
        try {
            writer.write(resultStr);
            writer.flush();
        } catch (Exception e) {
            logger.error("o2oapp ioexception ", e);
        } finally {
            if (writer != null) writer.close();
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
}
