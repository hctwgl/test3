package com.ald.fanbei.api.web.common;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.biz.arbitration.MD5;
import com.ald.fanbei.api.biz.service.DsedResourceService;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.impl.DsedH5HandleFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 郭帅强 2018年1月16日 下午11:56:17 @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Logger biLog = LoggerFactory.getLogger("DSED_BI");// 新app原生接口入口日志
    protected final Logger mdLog = LoggerFactory.getLogger("DSED_MD");// 埋点日志
    protected final Logger thirdLog = LoggerFactory.getLogger("DSED_THIRD");// 第三方调用日志

    private static String PRIVATE_KEY = ConfigProperties.get(Constants.CONFKEY_XGXY_AES_PASSWORD);
    
    @Resource
    protected TokenCacheUtil tokenCacheUtil;
    @Resource
    private DsedResourceService dsedResourceService;
    @Resource
    private DsedH5HandleFactory dsedH5HandleFactory;

    protected String processRequest(HttpServletRequest request,String data,String sign) {
        String retMsg = StringUtils.EMPTY;
        DsedH5HandleResponse baseResponse = null;
        Context context = null;
        Long stmap = System.currentTimeMillis();
        try {
            context = parseRequestData(request,data);
            compareSign(request, context,sign);
            baseResponse = doProcess(context);
            retMsg = JSON.toJSONString(baseResponse);
        } catch (FanbeiException e) {
            baseResponse = buildErrorResult(e, request);
            retMsg = JSON.toJSONString(baseResponse);
        } catch (Exception e) {
            baseResponse = buildErrorResult(FanbeiExceptionCode.SYSTEM_ERROR, request);
            retMsg = JSON.toJSONString(baseResponse);
        }
        
        doBiLog(context,request, baseResponse, null, System.currentTimeMillis() - stmap, context != null? context.getUserName():"");
        return retMsg;
    }

    protected DsedH5HandleResponse buildErrorResult(FanbeiExceptionCode exceptionCode, HttpServletRequest request) {
        DsedH5HandleResponse resp;
        if (exceptionCode == null) {
            exceptionCode = FanbeiExceptionCode.SYSTEM_ERROR;
        }
        resp = new DsedH5HandleResponse(exceptionCode);
        return resp;
    }

    protected DsedH5HandleResponse buildErrorResult(FanbeiException e, HttpServletRequest request) {
        FanbeiExceptionCode exceptionCode = e.getErrorCode();
        DsedH5HandleResponse resp;
        if (exceptionCode == null) {
            exceptionCode = FanbeiExceptionCode.SYSTEM_ERROR;
        }
        if (e.getDynamicMsg() != null && e.getDynamicMsg()) {
            resp = new DsedH5HandleResponse(exceptionCode, e.getMessage());
        } else {
            resp = new DsedH5HandleResponse(exceptionCode);
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
    public abstract Context parseRequestData(HttpServletRequest request, String data);

    /**
     * 处理请求
     *
     * @param requestDataVo
     * @param context
     * @param httpServletRequest
     * @return
     */
    public abstract DsedH5HandleResponse doProcess(Context context);


    private void compareSign(HttpServletRequest request, Context context,String sign) {
        Map<String, Object> systemsMap = context.getSystemsMap();
        String md5Value = generateSign(systemsMap, PRIVATE_KEY);
        if (logger.isDebugEnabled())
            logger.info("signStrBefore=" + systemsMap + ",md5Value=" + md5Value + ",sign=" + sign);
        if (!StringUtils.equals(sign, md5Value)) {
            logger.error("signStrBefore=" + systemsMap + ",md5Value=" + md5Value + ",sign=" + sign);
            throw new FanbeiException("sign is error", FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
        }
    }

    /**
     * 通过appSecret加密参数
     *
     * @param params
     * @param appSecret
     * @return
     */
    public static String paramsEncrypt(JSONObject params, String appSecret) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        JSONObject obj = new JSONObject(true);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.getString(key);
            obj.put(key, value);
        }
        String result = obj.toString();
        result = AesUtil.encryptToBase64(result, appSecret);
        return result;
    }

    /**
     * 通过appSecret解密参数
     *
     * @param params
     * @param appSecret
     * @return
     */
    public static JSONObject paramsDecrypt(String params, String appSecret) {
        params = AesUtil.decryptFromBase64(params, appSecret);
        JSONObject result = JSONObject.parseObject(params);
        return result;
    }

    /**
     * 生成本地签名
     *
     * @param params
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static String generateSign(Map<String,Object> params, String appSecret) throws IllegalArgumentException {
        List<String> keys = new ArrayList<String>(params.keySet());
        keys.remove("signCode");
        Collections.sort(keys);
        StringBuffer result = null;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (i == 0) result = new StringBuffer();
            else result.append("&");
            result.append(key).append("=").append(params.get(key));
        }
        result.append("&appSecret=" + appSecret);
        return params == null ? null : MD5.md5(result.toString());
    }


    /**
     * 记录埋点相关日志日志
     *
     * @param request
     * @param respData
     * @param exeT
     */
    protected void doMaidianLog(HttpServletRequest request, DsedH5HandleResponse respData, String... extInfo) {
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
    protected void doBiLog(Context context,HttpServletRequest request, DsedH5HandleResponse respData, String appInfo, long exeT,
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
            Map<String, Object> map =  context.getSystemsMap();
            Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                param.put(entry.getKey(), entry.getValue());
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
    protected void doLog(String reqData, DsedH5HandleResponse respData, String httpMethod, String rmtIp, String exeT,
                         String inter, String userName, String ext1, String ext2, String ext3, String ext4, String ext5) {

    	biLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT), "	",
                "h", "	", rmtIp, "	", userName, "	", exeT, "	", inter, "	",
                respData == null ? "false" : respData.getCode() + "", "	",
                DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", ext1, "	", ext2, "	", ext3, "	",
                ext4, "	", ext5, "	", StringUtil.isBlank(reqData) ? "{}" : reqData, "	",
                respData == null ? "{}" : respData.toString()));
    }

}