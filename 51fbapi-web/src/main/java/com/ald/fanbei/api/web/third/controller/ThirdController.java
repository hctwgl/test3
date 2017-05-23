package com.ald.fanbei.api.web.third.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.service.boluome.ThirdCore;
import com.ald.fanbei.api.biz.service.boluome.ThirdNotify;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.AppResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 提供给第三方调用接口 @类描述：
 * 
 * @author xiaotianjian 2017年3月24日下午1:54:46
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/thirdApi")
public class ThirdController extends AbstractThird{

    @Resource
    AfOrderService afOrderService;
    @Resource
    AfShopService afShopService;
    @Resource
    BoluomeUtil boluomeUtil;

    @RequestMapping(value = { "/orderRefund" }, method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String orderRefund(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        thirdLog.info("orderRefund begin requestData = {}", requestData);
        JSONObject requestParams = JSON.parseObject(requestData);

        Map<String, String> params = buildParam(requestParams);

        AppResponse result = new AppResponse(FanbeiExceptionCode.SUCCESS);
        try {
            result = checkSignAndParam(params);
            Map<String, Object> resultData = new HashMap<String, Object>();
            String orderId = params.get(ThirdCore.ORDER_ID);
            String plantform = params.get(ThirdCore.PLANT_FORM);
            String refundNo = params.get(ThirdCore.REFUND_NO);
            String refundSource = params.get(ThirdCore.REFUND_SOURCE);
            BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(params.get(ThirdCore.AMOUNT), null);
            AfOrderDo orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(plantform, orderId);
            if (orderInfo == null) {
                throw new FanbeiException(FanbeiExceptionCode.BOLUOME_ORDER_NOT_EXIST);
            }

            afOrderService.dealBrandOrderRefund(orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getBankId(), orderInfo.getOrderNo(),orderInfo.getThirdOrderNo() ,refundAmount, orderInfo.getActualAmount(),
                    orderInfo.getPayType(), orderInfo.getPayTradeNo(), refundNo, refundSource);

            result.setData(resultData);
        } catch (FanbeiException e) {
            result = new AppResponse(e.getErrorCode());
        } catch (Exception e) {
            result = new AppResponse(FanbeiExceptionCode.SYSTEM_ERROR);
        }
        thirdLog.info("result is {}", JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);
    }

    private AppResponse checkSignAndParam(Map<String, String> params) {
        AppResponse result = new AppResponse(FanbeiExceptionCode.SUCCESS);
        if (StringUtils.isEmpty(params.get(ThirdCore.ORDER_ID)) || StringUtils.isEmpty(params.get(ThirdCore.PLANT_FORM)) || StringUtils.isEmpty(params.get(ThirdCore.AMOUNT))
                || StringUtils.isEmpty(params.get(ThirdCore.TIME_STAMP)) || StringUtils.isEmpty(params.get(ThirdCore.USER_ID))) {
            throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
        }
        boolean sign = ThirdNotify.verify(params);
        if (!sign) {
            throw new FanbeiException(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR);
        }
        return result;
    }

    private Map<String, String> buildParam(JSONObject requestParams) {
        Map<String, String> params = new HashMap<String, String>();
        String orderId = requestParams.getString(ThirdCore.ORDER_ID);
        String userId = requestParams.getString(ThirdCore.USER_ID);
        String timestamp = requestParams.getString(ThirdCore.TIME_STAMP);
        String plantform = requestParams.getString(ThirdCore.PLANT_FORM);
        String amount = requestParams.getString(ThirdCore.AMOUNT);
        String sign = requestParams.getString(ThirdCore.SIGN);
        String appKey = requestParams.getString(ThirdCore.APP_KEY);
        String refundNo = requestParams.getString(ThirdCore.REFUND_NO);
        String refundSource = requestParams.getString(ThirdCore.REFUND_SOURCE);

        params.put(ThirdCore.ORDER_ID, orderId);
        params.put(ThirdCore.USER_ID, userId);
        params.put(ThirdCore.TIME_STAMP, timestamp);
        params.put(ThirdCore.PLANT_FORM, plantform);
        params.put(ThirdCore.SIGN, sign);
        params.put(ThirdCore.APP_KEY, appKey);
        params.put(ThirdCore.AMOUNT, amount);
        params.put(ThirdCore.REFUND_NO, refundNo);
        params.put(ThirdCore.REFUND_SOURCE, refundSource);
        return params;
    }

    /**
     * @方法描述：异步处理手机充值状态（真实处理状态） @author huyang 2017年3月31日下午5:08:53
     * @author huyang 2017年4月1日上午9:37:43
     * @param partnerId
     *            商户编号
     * @param signType
     *            签名方式
     * @param sign
     *            签名
     * @param orderNo
     *            商户订单号
     * @param streamId
     *            流水号
     * @param orderTime
     *            订单时间
     * @param orderType
     *            订单类型 01：话费充值
     * @param accountNo
     *            充值账号
     * @param facePrice
     *            面额
     * @param payMoney
     *            支付金额
     * @param profit
     *            佣金金额
     * @param status
     *            订单状态
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
     */
    @RequestMapping(value = { "/notifyPhoneRecharge" }, method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String notifyPhoneRecharge(@RequestParam("partner_id") String partnerId, @RequestParam("sign_type") String signType, @RequestParam("sign") String sign,
            @RequestParam("order_no") String orderNo, @RequestParam("stream_id") String streamId, @RequestParam("order_time") String orderTime,
            @RequestParam("order_type") String orderType, @RequestParam("account_no") String accountNo, @RequestParam("face_price") String facePrice,
            @RequestParam("pay_money") String payMoney, @RequestParam("profit") String profit, @RequestParam("status") String status, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String url =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getServletPath();
        if (request.getQueryString() != null) {
            url += "?" + request.getQueryString();
        }
        thirdLog.info(url);
        try {
            // 验签
            JSONObject json = new JSONObject();
            json.put("account_no", accountNo);
            json.put("face_price", facePrice);
            json.put("order_no", orderNo);
            json.put("order_time", orderTime);
            json.put("order_type", orderType);
            json.put("partner_id", partnerId);
            json.put("pay_money", payMoney);
            json.put("profit", profit);
            json.put("sign_type", signType);
            json.put("status", status);
            json.put("stream_id", streamId);
            String resign = KaixinUtil.sign(json);
            if (sign.equals(resign)) {
                afOrderService.notifyMobileChargeOrder(orderNo, status);
            } else {
                throw new Exception("verify signature error ! orderNo：【" + orderNo + "】");
            }
        } catch (Exception e) {
            thirdLog.error("notifyPhoneRecharge error！", e);
            return "FAIL";
        }
        return "SUCCESS";
    }
}
