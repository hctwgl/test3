package com.ald.fanbei.api.biz.third.util.baiqishi;


/**
 * 白骑士数据处理
 */


import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.enums.TongdunEventEnmu;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Component("baiQiShiUtils")
public class BaiQiShiUtils {

    private static Logger logger = Logger.getLogger(BaiQiShiUtils.class);
    @Resource
    AfUserService afUserService;


    /**
     * 验证登录
     * @param blackBox
     * @param ip
     * @param mobile
     * @param name
     * @param idNumber
     * @param cardNumber
     * @param openId
     */
    public void getLoginResult(String requsetId,String blackBox, String ip, String mobile, String name, String idNumber, String cardNumber, String openId) {
        String platform;
        if ("h5".equals(requsetId)){
            platform = "h5";
        }else {
            platform = requsetId.startsWith("i") ? "ios" : "android";
        }
        Map<String, Object> params = getCommonParam(blackBox, ip, mobile, name, idNumber, cardNumber, openId,"login",platform);
        JSONObject apiResp = null;
        String apiUrl = "https://api.baiqishi.com/services/decision";
        try {
            String respStr = BaiQiShiInvoker.invoke(params, apiUrl);
            apiResp = JSONObject.parseObject(respStr);
        } catch (Exception e) {
            logger.error("baiQiShiUtils getLoginResult", e);
            return;
        }
        logger.info("baiQiShiUtils getLoginResult info = "+apiResp);
        /*String loginSwitch = resourceValueWhithType(AfResourceType.loginTongdunSwitch.getCode());

        if (StringUtil.isBlank(loginSwitch) || "0".equals(loginSwitch)) {// 验证开关关闭
            return;
        }

        if (apiResp != null && apiResp.get("final_decision") != null
                && resourceValueWhithType(AfResourceType.tongdunAccecptLevel.getCode()).indexOf(apiResp.get("final_decision") + "") > -1) {
            logger.info("手机号码为：" + accountMobile + "的用户在验证登录的时候被拦截" + "....同盾返回的code是...." + apiResp.get("final_decision"));
            throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_LOGIN_ERROR);
        }*/
    }

    private Map<String, Object> getCommonParam(String blackBox, String ip, String mobile, String name, String idNumber, String cardNumber, String openId,String eventType,String platform) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("name", name);
        params.put("certNo", idNumber);//用户身份证号
        params.put("bankCardNo", cardNumber);//	银行卡卡号
        params.put("zmOpenId", openId);//用户在商户端芝麻信用授权ID
        params.put("appId", "test");
        params.put("ip", ip);
        params.put("tokenKey", blackBox);
        params.put("eventType", eventType);
        params.put("platform", platform);
        params.put("verifyKey", "e93f7e5ec79548f3b0932cdc086a5ac3");
        params.put("partnerId", "fanbei");
        return params;
    }

    /**
     * 注册
     * @param blackBox
     * @param ip
     * @param mobile
     * @param name
     * @param idNumber
     * @param cardNumber
     * @param openId
     */
    public void getRegistResult(String requsetId,String blackBox, String ip, String mobile, String name, String idNumber, String cardNumber, String openId) {
        String platform;
        if ("h5".equals(requsetId)){
            platform = "h5";
        }else {
            platform = requsetId.startsWith("i") ? "ios" : "android";
        }
        Map<String, Object> params = getCommonParam(blackBox, ip, mobile, name, idNumber, cardNumber, openId,"register",platform);
        String apiUrl = "https://api.baiqishi.com/services/decision";
        JSONObject apiResp = null;
        try {
            String respStr = BaiQiShiInvoker.invoke(params, apiUrl);
            apiResp = JSONObject.parseObject(respStr);
        } catch (Exception e) {
            logger.error("baiQiShiUtils getRegistResult", e);
            return;
        }
        logger.info("baiQiShiUtils getRegistResult info = "+apiResp);
        /*if (StringUtil.isBlank(registSwitch) || "0".equals(registSwitch)) {// 验证开关关闭
            return;
        }
        if (apiResp != null && apiResp.get(" ") != null
                && resourceValueWhithType(AfResourceType.tongdunAccecptLevel.getCode()).indexOf(apiResp.get("final_decision") + "") > -1) {
            logger.info("手机号码为：" + accountMobile + "的用户在app端注册的时候被拦截....同盾返回的code是...." + apiResp.get("final_decision"));
            throw new FanbeiException(FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR);
        }*/
    }

    /**
     * 注册
     *
     * @param tokenKey 指纹
     * @return
     */
    public int register(String tokenKey) {
        //   UserService userService = (UserService) BeanUtil.getBean("userService");
//        User user = userService.find(consumerNo);
//        HashMap <String, Object> paramMap = new HashMap();
//        paramMap.put("mobile", user.getPhone());
//        paramMap.put("name", user.getRealName());
//        paramMap.put("certNo", user.getIdNo());
//        paramMap.put("bankCardNo", user.getIdNo());
//        paramMap.put("zmOpenId", user.getOpenId());
//        paramMap.put("appId", "test");
//        paramMap.put("tokenKey", model.getTokenKey());
//        String apiUrl = "https://api.baiqishi.com/services/decision";
//        paramMap.put("eventType", "loan ");
//        paramMap.put("verifyKey", "e93f7e5ec79548f3b0932cdc086a5ac3");
//        paramMap.put("partnerId", "fanbei");
//        String result = null;
//        try {
//            result = BaiQiShiInvoker.invoke(paramMap, apiUrl);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        paramMap.put("result", result);
//        mapper.saveRecord(paramMap);
        return 0;
    }

}