package com.ald.fanbei.api.web.third.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.YFSmsUtil;
import com.ald.fanbei.api.biz.util.OssUploadResult;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.domain.AfIagentResultDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoRespBo;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.service.boluome.ThirdCore;
import com.ald.fanbei.api.biz.service.boluome.ThirdNotify;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.third.util.KaixinUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.AppResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 提供给第三方调用接口 @类描述：
 * 
 * @author xiaotianjian 2017年3月24日下午1:54:46
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/thirdApi")
public class ThirdController extends AbstractThird{

    private static Map<String,String> RESULT_CODE = new HashMap<String, String>() {
        {
            put("未接通", "1");
            put("非本人", "2");
            put("未完成", "3");
            put("拒绝", "4");
            put("通过", "5");
        }
    };
    @Resource
    AfShopService afShopService;
    @Resource
    BoluomeUtil boluomeUtil;
    @Resource
    BoluomeService boluomeService;
    @Resource
    OssFileUploadService ossFileUploadService;
    @Resource
    AfIagentResultService afIagentResultService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfOrderService afOrderService;
    @Resource
    SmsUtil smsUtil;
    @Resource
    AfUserService afUserService;
    @Resource
    HttpServletRequest request;

    @RequestMapping(value = { "/iagent/notify.json" }, method = RequestMethod.POST)
    @ResponseBody
    public String iagentReport( @RequestParam(value = "audio",required = false) MultipartFile audio,@RequestParam("work_id") final String   work_id, @RequestParam("job_id")final String   job_id, @RequestParam("work_result") final String work_result, @RequestParam("token")String token) throws Exception {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        FileOutputStream fos = null;
        InputStream in = null;
        String fileUrl = null;
        StringBuilder sb = new StringBuilder();
        sb.append("---work_id:"+work_id);
        sb.append("---job_id:"+job_id);
        sb.append("---work_result:"+work_result);
        sb.append("---token:"+token);

        sb.append("---iagentReport begin:");
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String key : paramMap.keySet()) {
            String[] values = paramMap.get(key);
            for (String value : values) {
                sb.append("键:" + key + ",值:" + value);
            }
        }
        sb.append("---iagentReport end");
         String audioUrl = null;
        if (audio != null){
            sb.append("---audio name："+audio.getOriginalFilename());
            OssUploadResult ossUploadResult= ossFileUploadService.uploadFileToOss(audio);
            sb.append("---ossUploadResult url："+   ossUploadResult.getUrl());
            audioUrl = ossUploadResult.getUrl();
        }else {
            sb.append("---audio name：null"  );
            sb.append("---ossUploadResult url：null" );
        }
        logger.info(sb.toString());
         final String audiourl = audioUrl;
        YFSmsUtil.pool.execute(new Runnable() {
            @Override
            public void run() {
                processIagentResult(audiourl,job_id,work_result);
            }
        });
        //processIagentResult("oooooo",job_id,work_result);
        JSONObject jsonObject=new JSONObject();
        JSONObject innerJsonObject=new JSONObject();
        innerJsonObject.put("receipt_id",System.currentTimeMillis());
        innerJsonObject.put("received_time",simpleDateFormat.format(new Date()));
        jsonObject.put("success",innerJsonObject);
        return JSON.toJSONString(jsonObject);
    }

    /**
     * 处理返回结果
     * @param audioUrl
     * @param job_id
     * @param work_result
     */
    private void processIagentResult(String audioUrl,String job_id,String work_result){
        logger.info("智能电核处理返回结果");
        JSONObject result = JSONObject.parseObject(work_result);
        String result_code= result.getString("result_code");
        AfIagentResultDo afIagentResultDo = new AfIagentResultDo();
        afIagentResultDo.setAudioUrl(audioUrl);
        afIagentResultDo.setWorkId(Long.parseLong(job_id));
        afIagentResultDo.setWorkResult(work_result);
        afIagentResultDo.setCheckState(RESULT_CODE.get(result_code)==null?"6":RESULT_CODE.get(result_code));
        AfIagentResultDo aresultDo  = afIagentResultService.getIagentByWorkId(Long.parseLong(job_id));
        //处理过不再处理
        if (aresultDo != null && aresultDo.getCheckState() != null){
            return;
        }
        afIagentResultService.updateResultByWorkId(afIagentResultDo);
        dealOrder(result,Long.parseLong(job_id));
    }

    /**
     * 处理订单
     * @param result
     */
    private void dealOrder(JSONObject result,long job_id){

        String result_code= result.getString("result_code");
        String wr = RESULT_CODE.get(result_code)==null?"6":RESULT_CODE.get(result_code);
        logger.info("thirdApi dealOrder result ="+result+",job_id="+job_id);
        if ("45".contains(wr)){
            Map<String,Integer> answers = new HashMap<>();
            answers.put("baseR",0);
            answers.put("baseW",0);
            answers.put("unbaseR",0);
            answers.put("unbaseW",0);
            //审核结果统计
            checkAnswers(answers,result);
            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(Constants.ORDER_MOBILE_VERIFY_SET,Constants.ORDER_MOBILE_VERIFY_SET);
            int max = Integer.parseInt(afResourceDo.getValue5());
            int min = Integer.parseInt(afResourceDo.getValue4());
            String checkstate = "";
            String iagentstate="";
            String checkResult="";
            if (answers.get("baseW")>0){
                checkstate = "close";
                iagentstate="E";
                checkResult="1";
            }else{
                if (answers.get("unbaseW")<=min){
                    checkstate = "success";
                    iagentstate="D";
                    checkResult="0";
                }else{
                    if (answers.get("unbaseW")>max){
                        checkstate = "close";
                        iagentstate="E";
                        checkResult="1";
                    }else{
                        checkstate = "review";
                        iagentstate="C";
                        checkResult="2";
                    }
                }
            }
            AfIagentResultDo resultDo = new AfIagentResultDo();
            resultDo.setWorkId(job_id);
            resultDo.setCheckResult(checkResult);
            afIagentResultService.updateResultByWorkId(resultDo);
            AfOrderDo afOrderDo = null;
            AfIagentResultDo afIagentResultDo = afIagentResultService.getIagentByWorkId(job_id);
            if (afIagentResultDo != null){
                String ordertype = afIagentResultDo.getOrderType();
                //订单类型0自营订单1白领带
                if ("0".equals(ordertype)){
                    afOrderDo = afOrderService.getOrderById(afIagentResultDo.getOrderId());
                }else{

                }

            }

            if (afOrderDo !=null){
                String iagentStatus = afOrderDo.getIagentStatus();
                if ("C".equals(iagentStatus)){
                    afOrderService.updateIagentStatusByOrderId(afOrderDo.getRid(),iagentstate);
                }
                AfUserDo userDo = afUserService.getUserById(afOrderDo.getUserId());
                logger.info("thirdApi closeOrderAndBorrow dealOrder info ="+JSONObject.toJSONString(afOrderDo)+",checkstate="+checkstate);
                if ("close".equals(checkstate)&&"PAID".equals(afOrderDo.getStatus())&&"C".equals(afOrderDo.getIagentStatus())){
                    logger.info("thirdApi closeOrderAndBorrow dealOrder checkstate ="+checkstate);
                    Map<String,String> qmap = new HashMap<>();
                    qmap.put("orderNo",afOrderDo.getOrderNo());
                    //HttpUtil.doHttpPost("https://admin.51fanbei.com/orderClose/closeOrderAndBorrow",JSONObject.toJSONString(qmap));
                    String content = "尊敬的用户，非常遗憾您未通过本次电核，请务必确认本次借款业务由您本人申请、本人使用并按时还款，珍惜您的个人信用。请24小时之后再次下单，祝您生活愉快！";
                    YFSmsUtil.send(userDo.getMobile(),content,YFSmsUtil.NOTITION);
//                    smsUtil.sendSmsToDhst(userDo.getMobile(),content);
                    HttpUtil.doHttpPost(ConfigProperties.get(Constants.CONFKEY_ADMIN_URL)+"/orderClose/closeOrderAndBorrow?orderNo="+afOrderDo.getOrderNo(),JSONObject.toJSONString(qmap));
                }
                List<AfOrderDo> orderList = afOrderService.selectTodayIagentStatusCOrders(afOrderDo.getUserId(),afOrderDo.getGmtCreate());
                if (orderList!= null){
                    for (AfOrderDo temp:orderList){
                        afOrderService.updateIagentStatusByOrderId(temp.getRid(),iagentstate);
                        if ("PAID".equals(temp.getStatus())&&"E".equals(iagentstate)){
                            HttpUtil.doHttpPost(ConfigProperties.get(Constants.CONFKEY_ADMIN_URL)+"/orderClose/closeOrderAndBorrow?orderNo="+temp.getOrderNo(),JSONObject.toJSONString(new HashMap<>()));

                        }
                    }
                }
            }
        }else{
            logger.info("thirdApi dealOrder result end ="+result+",job_id="+job_id);
            AfIagentResultDo resultDo = new AfIagentResultDo();
            resultDo.setWorkId(job_id);
            resultDo.setCheckResult("2");
            afIagentResultService.updateResultByWorkId(resultDo);
        }


    }

    /**
     * 电核结果统计
     * @param answers
     * @param result
     */
    private void checkAnswers(Map<String,Integer> answers ,JSONObject result){
        List<AfResourceDo> questions = afResourceService.getConfigByTypes(Constants.ORDER_MOBILE_VERIFY_QUESTION_SET);
        Map<String,String> qmap = new HashMap<>();
        for (AfResourceDo temp:questions){
            qmap.put(temp.getName(),temp.getValue());
        }
        JSONArray detail = result.getJSONArray("result_details");
        if (detail != null){
            for (Object temp:detail){
                JSONObject tempj = (JSONObject)temp;
                String Q_TXT = tempj.getString("Q_TXT");
                String Q_ANS = tempj.getString("Q_ANS");
                String baseFlag = qmap.get(Q_TXT);
                if ("Y".equals(baseFlag)){
                    if ("正确".equals(Q_ANS)){
                        answers.put("baseR",answers.get("baseR").intValue()+1);
                    }else{
                        answers.put("baseW",answers.get("baseW").intValue()+1);
                    }
                }else{
                    if ("正确".equals(Q_ANS)){
                        answers.put("unbaseR",answers.get("unbaseR").intValue()+1);
                    }else{
                        answers.put("unbaseW",answers.get("unbaseW").intValue()+1);
                    }
                }
            }
        }
    }
    @RequestMapping(value = { "/orderRefund" }, method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String orderRefund(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        thirdLog.info("orderRefund begin requestData = {}", requestData);
        JSONObject requestParams = JSON.parseObject(requestData);

        Map<String, String> params = buildParam(requestParams);

        AppResponse result = new AppResponse(FanbeiExceptionCode.SUCCESS);
        try {
            result = checkSignAndParam(params, "orderRefund");
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
        	logger.error("orderRefund failed : {}", e);
            result = new AppResponse(e.getErrorCode());
        } catch (Exception e) {
        	logger.error("orderRefund failed : {}", e);
            result = new AppResponse(FanbeiExceptionCode.SYSTEM_ERROR);
        }
        thirdLog.info("result is {}", JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);
    }
    
    @RequestMapping(value = { "/getDidiRiskInfo" }, method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getDidiRiskInfo(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        thirdLog.info("getDidiRiskInfo begin requestData = {}", requestData);
        JSONObject requestParams = JSON.parseObject(requestData);

        Map<String, String> params = buildDidiRiskParam(requestParams);

        AppResponse result = new AppResponse(FanbeiExceptionCode.SUCCESS);
        try {
            result = checkSignAndParam(params,"getDidiRiskInfo");
            String orderId = params.get(ThirdCore.ORDER_ID);
            String type = params.get(ThirdCore.TYPE);
            Long userId = NumberUtil.objToLongDefault(params.get(ThirdCore.USER_ID), null);
            BoluomeGetDidiRiskInfoRespBo respInfo = boluomeService.getRiskInfo(orderId, type, userId);
            result.setData(respInfo);
        } catch (FanbeiException e) {
        	logger.error("getDidiRiskInfo failed : {}", e);
            result = new AppResponse(e.getErrorCode());
        } catch (Exception e) {
        	logger.error("getDidiRiskInfo failed : {}", e);
            result = new AppResponse(FanbeiExceptionCode.SYSTEM_ERROR);
        }
        thirdLog.info("result is {}", JSONObject.toJSONString(result));
        return JSONObject.toJSONString(result);
    }
    
    private AppResponse checkSignAndParam(Map<String, String> params, String method) {
        AppResponse result = new AppResponse(FanbeiExceptionCode.SUCCESS);
    	if ("orderRefund".equals(method) && (StringUtils.isEmpty(params.get(ThirdCore.ORDER_ID)) || StringUtils.isEmpty(params.get(ThirdCore.PLANT_FORM)) || StringUtils.isEmpty(params.get(ThirdCore.AMOUNT))
    			|| StringUtils.isEmpty(params.get(ThirdCore.TIME_STAMP)) || StringUtils.isEmpty(params.get(ThirdCore.USER_ID)))) {
    		throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
    	}
    	if ("getDidiRiskInfo".equals(method) && (StringUtils.isEmpty(params.get(ThirdCore.TIME_STAMP))
    		    || StringUtils.isEmpty(params.get(ThirdCore.SIGN))
    		    || StringUtils.isEmpty(params.get(ThirdCore.USER_ID))
    			)) {
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
    
    private Map<String, String> buildDidiRiskParam(JSONObject requestParams) {
        Map<String, String> params = new HashMap<String, String>();
        String appKey = ConfigProperties.get(Constants.CONFKEY_THIRD_BOLUOME_APPKEY);
        String orderId = requestParams.getString(ThirdCore.ORDER_ID);
        String timestamp = requestParams.getString(ThirdCore.TIME_STAMP);
        String sign = requestParams.getString(ThirdCore.SIGN);
        String type = requestParams.getString(ThirdCore.TYPE);
        String userId = requestParams.getString(ThirdCore.USER_ID);

        params.put(ThirdCore.APP_KEY, appKey);
        params.put(ThirdCore.ORDER_ID, orderId);
        params.put(ThirdCore.TIME_STAMP, timestamp);
        params.put(ThirdCore.SIGN, sign);
        params.put(ThirdCore.TYPE, type);
        params.put(ThirdCore.USER_ID, userId);
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
