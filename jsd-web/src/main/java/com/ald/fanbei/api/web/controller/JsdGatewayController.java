package com.ald.fanbei.api.web.controller;

import java.io.IOException;
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

import com.ald.fanbei.api.biz.bo.jsd.JsdParam;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
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
 * @类描述：FanbeiH5Controller
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class JsdGatewayController extends BaseController {
	
	private static String PRIVATE_KEY = ConfigProperties.get(Constants.CONFKEY_XGXY_AES_PASSWORD);
	
    @Resource
    JsdH5HandleFactory jsdH5HandleFactory;

    @Resource
    JsdUserService jsdUserService;

    @Resource
    InterceptorChain interceptorChain;

    @RequestMapping(value = {"/third/xgxy/v1/**","/third/eca/v1/**"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String h5Request(@RequestBody JsdParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    	ContextBuilder contextBuilder = new ContextBuilder();
        String method = request.getRequestURI();
        Map<String, Object> dataMap = new HashMap<>();

        if (StringUtils.isNotEmpty(data)) {
            String decryptData = AesUtil.decryptFromBase64Third(data, PRIVATE_KEY);
            JSONObject dataInfo = JSONObject.parseObject(decryptData);
            String openId = (String.valueOf(dataInfo.get("openId")));
            JsdUserDo userDo = jsdUserService.getByOpenId(openId);
            dataMap = JSON.parseObject(decryptData);
            contextBuilder.method(method).systemsMap(dataMap);
            if (userDo != null) {
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
        logger.info("BaseController buildContext data = "+data+",context=",JSON.toJSONString(context));
        return context;
    }

    @Override
    public JsdH5HandleResponse doProcess(Context context) {
        interceptorChain.execute(context);
        
        JsdH5Handle methodHandle = jsdH5HandleFactory.getHandle(context.getMethod());

        JsdH5HandleResponse handelResult;
        try {
            handelResult = methodHandle.process(context);
            int resultCode = handelResult.getCode();
            if (resultCode != 200) {
//            	logger.error(context.getId() + " err,Code=" + resultCode);
            }
            return handelResult;
        } catch (FanbeiException e) {
            logger.error("biz exception, msg=" + e.getMessage() + ", code=" + e.getErrorCode(), e);
            throw e;
        } catch (Exception e) {
            logger.error("sys exception", e);
            throw new FanbeiException("sys exception", FanbeiExceptionCode.SYSTEM_ERROR);
        }
    }


}