package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.common.enums.ThirdPartyLinkChannel;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

public class MaidianRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");// 埋点日志
    private final Logger maidianNewLog = LoggerFactory.getLogger("FBMDNEW_BI");// 埋点日志

    private String[] extInfo;
    private HttpServletRequest request;
    private String respData;
    private boolean respSuccess;

    public MaidianRunnable(HttpServletRequest request, String respData,boolean respSuccess, String... extInfo){
        this.extInfo = extInfo;
        this.request = request;
        this.respData = respData;
        this.respSuccess = respSuccess;
    }

    @Override
    public void run() {
        doMaidianLog(this.request,this.respData,this.extInfo);
    }

    /**
     * 记录埋点相关日志日志
     *
     */
    private void doMaidianLog(HttpServletRequest request, String respData,String... extInfo) {
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
            else if(request.getHeader("userName")!= null) {
                userName = request.getHeader("userName").toString();
            }

            // 第三方链接进入
            if ("/fanbei-web/thirdPartyLink".equals(request.getRequestURI())) {
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
                                        channel = ThirdPartyLinkChannel.DEFAULT.getCode(); // 防止人为的设置过大的数值
                                    } else {
                                        channel = ThirdPartyLinkChannel.getChannel(vals[1]);
                                    }
                                }
                            }
                        }
                    }
                }
                maidianNewLog
                        .info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                                "	", "h", "	", CommonUtil.getIpAddr(request), "	", userName, "	", 0, "	",
                                request.getRequestURI(), "	", respSuccess, "	",
                                DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "md", "	",
                                request.getParameter("lsmNo"), "	", request.getParameter("linkType"), "	", channel,
                                "	", "", "	", param == null ? "{}" : param.toString(), "	",
                                respData == null ? "{}" : respData));

                maidianLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                        "	", "h", "	rmtIP=", CommonUtil.getIpAddr(request), "	userName=", userName, "	", 0, "	",
                        request.getRequestURI(), "	result=", respSuccess, "	",
                        DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "md", "	lsmNo=",
                        request.getParameter("lsmNo"), "	linkType=", request.getParameter("linkType"), "	channel=",
                        channel, "	", "", "	reqD=", param.toString(), "	resD=",
                        respData == null ? "null" : respData));
            } else {
                // 获取可变参数
                String ext1 = "";
                String ext2 = "";
                String ext3 = "";
                String ext4 = "";
                try {
                    if(extInfo!=null && extInfo.length>0) {
                        ext1 = extInfo[0];
                        ext2 = extInfo[1];
                        ext3 = extInfo[2];
                        ext4 = extInfo[3];
                    }
                } catch (Exception e) {
                    // ignore error
                }
                maidianNewLog
                        .info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                                "	", "h", "	", CommonUtil.getIpAddr(request), "	", userName, "	", 0, "	",
                                request.getRequestURI(), "	", respSuccess, "	",
                                DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "md", "	", ext1,
                                "	", ext2, "	", ext3, "	", ext4, "	", param == null ? "{}" : param.toString(),
                                "	", respData == null ? "{}" : respData));

                maidianLog.info(StringUtil.appendStrs("	", DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT),
                        "	", "h", "	rmtIP=", CommonUtil.getIpAddr(request), "	userName=", userName, "	", 0, "	",
                        request.getRequestURI(), "	result=", respSuccess, "	",
                        DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "md", "	", ext1, "	", ext2,
                        "	", ext3, "	", ext4, "	reqD=", param.toString(), "	resD=",
                        respData == null ? "null" : respData));
            }

        } catch (Exception e) {
            logger.info("maidian logger:"+request.toString());
            logger.error("maidian logger error", e);
        }
    }
}
