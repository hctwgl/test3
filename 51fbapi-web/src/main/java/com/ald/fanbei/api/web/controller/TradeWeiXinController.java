package com.ald.fanbei.api.web.controller;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author shencheng 2017/7/28 下午1:46
 * @类描述: TradeWeiXinController
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/trade")
public class TradeWeiXinController extends BaseController {

    @RequestMapping(value = {"/login","/logout","/businessInfo","/twoBarCode","/getPayList", "/getWithdrawList", "/getRefundList", "/withdraw", "/refund"},
            method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ResponseBody
    public String tradeRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processTradeWeiXinRequest(body, request, false);
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        if (StringUtils.isBlank(reqData)) {
            throw new FanbeiException("param is null", FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
        }
        return reqData;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();
            String method = request.getRequestURI();
            reqVo.setMethod(method);

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setParams((jsonObj == null || jsonObj.isEmpty()) ? new HashMap<String, Object>() : jsonObj);

            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        ApiHandle methodHandel = apiHandleFactory.getApiHandle(requestDataVo.getMethod());
        ApiHandleResponse handelResult;
        try {
            handelResult = methodHandel.process(requestDataVo, context, httpServletRequest);
            int resultCode = handelResult.getResult().getCode();
            if (resultCode != 1000) {
                logger.info("request error,Code=" + resultCode);
            }
            return JSON.toJSONString(handelResult);
        } catch (FanbeiException e) {
            logger.error("trade weixin exception", e);
            throw e;
        } catch (Exception e) {
            logger.error("sys exception", e);
            throw new FanbeiException("sys exception", FanbeiExceptionCode.SYSTEM_ERROR);
        }
    }
}
