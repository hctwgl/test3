package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.dsed.DsedParam;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.context.ContextImpl;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.chain.impl.InterceptorChain;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.common.impl.DsedH5HandleFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;


/**
 * @author 郭帅强 2018年1月17日 下午6:15:06
 * @类描述：FanbeiH5Controller
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class DsedH5Controller extends BaseController {

    @Resource
    DsedH5HandleFactory dsedH5HandleFactory;

    @Resource
    DsedUserService dsedUserService;

    @Resource
    InterceptorChain interceptorChain;

    @RequestMapping(value = "/third/xgxy/v1/**", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String h5Request(@RequestBody DsedParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(request, param.getData(), param.getSign());
    }


    @Override
    public Context parseRequestData(HttpServletRequest request, String data) {
        try {
            return buildContext(request, data);
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    private Context buildContext(HttpServletRequest request, String data) {

        ContextImpl.Builder builder = new ContextImpl.Builder();
        String method = request.getRequestURI();
        Map<String, Object> systemsMap = new HashMap<>();

        Map<String, Object> dataMaps = Maps.newHashMap();
        if (StringUtils.isNotEmpty(data)) {
            String decryptData = AesUtil.decryptFromBase64Third(data, "aef5c8c6114b8d6a");
            JSONObject dataInfo = JSONObject.parseObject(decryptData);
            String openId = (String.valueOf(dataInfo.get("userId")));
            DsedUserDo userDo = dsedUserService.getByOpenId(openId);
            systemsMap = JSON.parseObject(decryptData);
            builder.method(method).systemsMap(systemsMap);
            if (userDo != null) {
            	builder.userName(userDo.getUserName());
                builder.userId(userDo.getRid());
                builder.idNumber(userDo.getIdNumber());
                builder.realName(userDo.getRealName());
            }
        }

        wrapRequest(request, dataMaps);
        builder.dataMap(systemsMap);

        String clientIp = CommonUtil.getIpAddr(request);
        builder.clientIp(clientIp);
        Context context = builder.build();
        return context;
    }

    private void wrapRequest(HttpServletRequest request, Map<String, Object> dataMaps) {

        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!StringUtils.equals("_appInfo", paramName)) {
                String objVal = request.getParameter(paramName);
                dataMaps.put(paramName, objVal);
            }
        }
    }


    @Override
    public DsedH5HandleResponse doProcess(Context context) {
        interceptorChain.execute(context);
        DsedH5Handle methodHandle = dsedH5HandleFactory.getHandle(context.getMethod());

        DsedH5HandleResponse handelResult;
        try {
            handelResult = methodHandle.process(context);
            int resultCode = handelResult.getCode();
            if (resultCode != 200) {
            	// TODO logger.info(context.getId() + " err,Code=" + resultCode);
            }
            return handelResult;
        } catch (FanbeiException e) {
            logger.error("biz exception, msg=" + e.getMessage() + ", code=" + e.getErrorCode());
            throw e;
        } catch (Exception e) {
            logger.error("sys exception", e);
            throw new FanbeiException("sys exception", FanbeiExceptionCode.SYSTEM_ERROR);
        }
    }


}