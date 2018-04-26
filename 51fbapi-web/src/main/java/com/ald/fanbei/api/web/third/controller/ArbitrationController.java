package com.ald.fanbei.api.web.third.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.arbitration.*;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.RiskTrackerDo;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.ArbitrationRespBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ArbitrationStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfArbitrationDao;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.domain.AfArbitrationDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.alibaba.fastjson.JSONObject;

/**
 * @author fanmanfu
 * @version 创建时间：2018年4月13日 下午4:50:00
 * @类描述：在线仲裁系统接口
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Controller
@RequestMapping("/third/arbitration")
public class ArbitrationController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    HttpServletRequest request;
    @Resource
    ArbitrationService arbitrationService;
    @Resource
    AfBorrowCashDao afBorrowCashDao;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfUserAccountService afUserAccountService;
    public static final String Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";
    private static final String MERCHANTCODE = "150778004447";
    private static final String URL = "http://test.arbexpress.cn/arbinter/v1/third.htm";
    private static final String TRACK_PREFIX = "track_arb_";
    //分页查询返回结果
    public static final String MAP_VALUE_COUNT = "count";
    public static final String MAP_VALUE_LIST = "result";

    //默认每页数量
    public static final int DEFAULT_PAGESIZE = 10;
    //默认页码
    public static final int DEFAULT_PAGENUM = 1;
    //100的BigDecimal类型
    public static final BigDecimal BIGDECIMAL_100 = new BigDecimal(100);
    public static final BigDecimal BIGDECIMAL_ZERO = new BigDecimal(0);

    //----------error code----------//
    //系统异常
    public static final String SYS_EXCEPTION_CODE = "9999";//系统异常code
    public static final String SYS_EXCEPTION_MSG = "系统异常";//系统异常msg
    //成功
    public static final String RET_CODE_SUCC = "0000";//成功编码
    public static final String RET_MSG_SUCC = "success";//成功信息
    @Resource
    RiskTrackerService trackerService;
    @Resource
    AfLegalContractPdfCreateServiceV2 afLegalContractPdfCreateServiceV2;
    @ResponseBody
    @RequestMapping(value = "/createPdf", method = RequestMethod.GET)
    public void createPdf(String loanBillNo) throws  Exception {
        String protocal= afLegalContractPdfCreateServiceV2.getProtocalLegalByTypeWithoutSeal((1-1), loanBillNo);
        logger.info(protocal);
    }

    @ResponseBody
    @RequestMapping(value = "/submit", method = RequestMethod.GET)
    public String submit(String loanBillNo) {
        printParams();
        SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D_H_M_S);
        // String borrowNo = "jq2018041910202400260";
        try {
            //立案提交
            ThirdParamsInfo paramsInfo = new ThirdParamsInfo();
            paramsInfo.setBusiCode("SUBMIT");
            paramsInfo.setMerchantCode(MERCHANTCODE);
            paramsInfo.setEncode("UTF-8");
            paramsInfo.setFormat("JSON");
            paramsInfo.setTime(sdf.format(new Date()));
            //-----
            ThirdOrderInfo orderInfo = new ThirdOrderInfo();
            orderInfo.setLoanBillNo(loanBillNo);//案件订单编号

            paramsInfo.setParam(URLEncoder.encode(JSON.toJSONString(orderInfo), "UTF-8"));
            //-----
            paramsInfo.setSignType("MD5");
            paramsInfo.setSignCode(MD5.md5(generateSign(paramsInfo)));
            String jsonParam = StringCompressUtils.compress(JSON.toJSONString(paramsInfo));
            logger.info("jsonParam=" + jsonParam);
            String trackId = TRACK_PREFIX + new Date().getTime();
            String result = HttpClientUtils.postWithString(URL, jsonParam);
            logger.info(result);
            AfArbitrationDo afArbitrationDo = new AfArbitrationDo();
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson.getString("errCode").equals("0000")) {
                String batchNo = resultJson.getString("batchNo");
                afArbitrationDo.setBatchNo(batchNo);
                JSONArray resultArr = resultJson.getJSONArray("result");
                if (resultArr.size() > 0) {
                    JSONObject firstData = resultArr.getJSONObject(0);
                    if (firstData.getString("errCode").equals("0000")) {
                        afArbitrationDo.setProcessCode("100");
                        afArbitrationDo.setProcess("仲裁申请");
                        afArbitrationDo.setStatusCode("0");
                        afArbitrationDo.setStatus("案件待提交");
                        afArbitrationDo.setValue2(result);
                        afArbitrationDo.setLoanBillNo(loanBillNo);
                        String protocal= afLegalContractPdfCreateServiceV2.getProtocalLegalByTypeWithoutSeal((1-1), loanBillNo);
                        arbitrationService.saveRecord(afArbitrationDo);
                    } else {
                        logger.info("submit error errorMsg " + firstData.getString("errMsg"));
                    }
                } else {
                    logger.info("submit error resultJson size zero");
                }
                ;
            }

//            errCode	错误码	varchar(4)	Y
//            errMsg	错误信息	varchar(100)	N
//            result	OBJECT		Y	JSON格式
//                errCode	错误码	varchar(4)	Y
//                errMsg	错误信息	varchar(100)	N
//                caseOrderId	案件订单id	varchar(30)	N	在线网络仲裁内部案件订单id
//                loanBillNo	案件订单编号	varchar(60)	Y	客户请求时的编号
//                batchNo	批次号	varchar(20)	Y
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * 该API接口由客户提供，互仲向客户平台发起该请求，客户回应并返回相应结果信息。通过该订单杭州互仲将订单状态推送给客户
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/setStatus"}, method = RequestMethod.POST)
    public String setStatus() {
        printParams();
        Map resultMap = new HashMap<String, String>();
        resultMap.put("errCode", "0000");
        resultMap.put("errMsg", "");
        try {
            String loanBillNo = ObjectUtils.toString(request.getParameter("loanBillNo"));
            String processCode = ObjectUtils.toString(request.getParameter("processCode"));
            String process = ObjectUtils.toString(request.getParameter("process"));
            String statusCode = ObjectUtils.toString(request.getParameter("statusCode"));
            String status = ObjectUtils.toString(request.getParameter("status"));
            String message = ObjectUtils.toString(request.getParameter("message"));

            if ("0".equals(statusCode)) {
                //进行案件申请
                application(loanBillNo);

                AfArbitrationDo afArbitrationDo = new AfArbitrationDo();
                afArbitrationDo.setGmtModified(new Date());
                afArbitrationDo.setLoanBillNo(loanBillNo);
                afArbitrationDo.setProcess(process);
                afArbitrationDo.setProcessCode(processCode);
                afArbitrationDo.setStatus(status);
                afArbitrationDo.setStatusCode(statusCode);
                afArbitrationDo.setMessage(message);
                arbitrationService.updateByloanBillNo(afArbitrationDo);
            }
            return JSON.toJSONString(resultMap);
        } catch (Exception e) {
            logger.info("setStatus error :", e);
            resultMap.put("errMsg", e.getMessage());
            return JSON.toJSONString(resultMap);
        }

    }


    @ResponseBody
    @RequestMapping(value = "/application", method = RequestMethod.GET)
    public String application(String borrowNo) {
        printParams();
        // String borrowNo = "jq2018041910202400260";
        try {
            AfArbitrationDo afArbitrationDo = arbitrationService.getByBorrowNo(borrowNo);
            //立案申请
            ThirdParamsInfo paramsInfo = new ThirdParamsInfo();
            paramsInfo.setBusiCode("APPLICATION");
            paramsInfo.setMerchantCode(MERCHANTCODE);
            paramsInfo.setEncode("UTF-8");
            paramsInfo.setFormat("JSON");
            SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D_H_M_S);
            paramsInfo.setTime(sdf.format(new Date()));
            //-----
            ThirdOrderInfo orderInfo = new ThirdOrderInfo();
            orderInfo.setBatchNo(afArbitrationDo.getBatchNo());

            paramsInfo.setParam(URLEncoder.encode(JSON.toJSONString(orderInfo), "UTF-8"));
            //-----
            paramsInfo.setSignType("MD5");
            paramsInfo.setSignCode(MD5.md5(generateSign(paramsInfo)));
            String jsonParam = StringCompressUtils.compress(JSON.toJSONString(paramsInfo));
            logger.info("jsonParam=" + jsonParam);
            String trackId = TRACK_PREFIX + new Date().getTime();
            String respResult = HttpClientUtils.postWithString(URL, jsonParam);
            logger.info("resp un zip result " + respResult);
            String result = StringCompressUtils.decompress(HttpClientUtils.postWithString(URL, jsonParam));
            logger.info(result);
            return "success";
        } catch (Exception e) {
            logger.info("application error :", e);
            return "error";
        }
    }


    /**
     * 生成本地签名
     *
     * @param thirdParamsInfo
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private static String generateSign(ThirdParamsInfo thirdParamsInfo) throws IllegalArgumentException, IllegalAccessException {

        Map<String, String> paramsMap = getParamsMap(thirdParamsInfo);
        paramsMap.remove("signCode");//去除签名本身
        StringBuffer params = null;
        for (String key : paramsMap.keySet()) {
            if (params == null) params = new StringBuffer();
            else params.append("&");
            params.append(key).append("=").append(paramsMap.get(key));
        }
        return params == null ? null : params.toString();
    }

    /**
     * 获取参数并将参数放入map中
     *
     * @param thirdParamsInfo
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private static Map<String, String> getParamsMap(ThirdParamsInfo thirdParamsInfo) throws IllegalArgumentException, IllegalAccessException {
        Map<String, String> map = new TreeMap<String, String>();
        //获取对象中的所有属性
        for (Field field : thirdParamsInfo.getClass().getDeclaredFields()) {
            field.setAccessible(true); //设置属性是可以访问的
            String fieldName = field.getName();
            Object value = field.get(thirdParamsInfo);
            if (value != null) {
                map.put(fieldName, String.valueOf(value));
            }
        }
        return map;
    }

    @ResponseBody
    @RequestMapping(value = {"/getData"}, method = RequestMethod.POST)
    public void getData(HttpServletResponse response) throws Exception {
        printParams();

        ThirdParamsInfo paramsInfo = null;
        // 默认返回成功

        Result result = new Result(RET_CODE_SUCC, RET_MSG_SUCC);
        ArbitrationRespBo defaultBo=new ArbitrationRespBo();
        defaultBo.setErrCode(RET_CODE_SUCC);
        defaultBo.setErrMsg(RET_MSG_SUCC);
        result.setResult(JSON.toJSONString(defaultBo));
        try {
            // 获取请求的参数
            String paramString = this.getParamString(request);
            logger.info("paramString=" + paramString);
            if (paramString == null) {
                throw new ArbitramentException("1001", "参数不能为空");
            }
            logger.info("paramString.length() = " + paramString.length());

            //需要通过解压来获取相应的json对象
            String deCompressString = StringCompressUtils.decompress(paramString);
            logger.info("参数信息：" + deCompressString);
            // 将参数转化成对象
            paramsInfo = JSON.parseObject(deCompressString, ThirdParamsInfo.class);
            // 参数验证
            if (this.validate(paramsInfo)) {
                // 签名
                String signCode = paramsInfo.getSignCode();
                // 通过参数本地生成签名
                String sign = MD5.md5(this.generateSign(paramsInfo));
                logger.info("参数签名：" + signCode + ", 本地签名：" + sign);
                if (!signCode.equals(sign)) {
                    throw new ArbitramentException("1002", "参数【signCode】数据错误");
                }
                String param = paramsInfo.getParam();
                String actualParamString = URLDecoder.decode(param, "UTF-8");
                if (actualParamString == null) {
                    throw new ArbitramentException("1002", "参数【param】数据错误");
                }
                JSONObject actualParamJSON = JSON.parseObject(actualParamString);
                String loanBillNo = actualParamJSON.getString("loanBillNo");
                // 商户编码
                String merchantCode = paramsInfo.getMerchantCode();
//                if (!merchantCode.equals(ArbitramentConfig.getString("merchant.code"))) {
//                    throw new ArbitramentException("1002", "参数【merchantCode】数据错误");
//                }
                // 业务编码
                String busiCode = paramsInfo.getBusiCode();
                if (BusiCodeEnum.GETCREDITAGREEMENT.getCode().equalsIgnoreCase(busiCode)) {// 2.获取借款协议
                    // 借款协议查询
                    String jsonString = JSON.toJSONString(arbitrationService.getCreditAgreement(loanBillNo));
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (BusiCodeEnum.GETCREDITINFO.getCode().equalsIgnoreCase(busiCode)) {// 3.获取借款信息
                    // 借款信息查询
                    String jsonString = JSON.toJSONString(arbitrationService.getCreditInfo(loanBillNo));
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (BusiCodeEnum.GETREFUNDINFO.getCode().equalsIgnoreCase(busiCode)) {// 4.获取还款信息
                    // 还款信息查询
                    String jsonString = JSON.toJSONString(arbitrationService.getRefundInfo(loanBillNo));
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (BusiCodeEnum.GETPAYVOUCHER.getCode().equalsIgnoreCase(busiCode)) {// 5.获取打款凭证
                    // 打款凭证查询
                    String jsonString = JSON.toJSONString(arbitrationService.getPayVoucher(loanBillNo));
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (BusiCodeEnum.GETFUNDINFO.getCode().equalsIgnoreCase(busiCode)) {// 7.获取案件订单相关金额
                    // 案件相关获取金额
                    String jsonString = JSON.toJSONString(arbitrationService.getFundInfo(loanBillNo));
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (BusiCodeEnum.GETORDERINFO.getCode().equalsIgnoreCase(busiCode)) {// 8.获取案件订单信息
                    // 获取案件订单信息
                    String jsonString = JSON.toJSONString(arbitrationService.getOrderInfo(loanBillNo));
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else if (BusiCodeEnum.SETSTATUS.getCode().equalsIgnoreCase(busiCode)) {// 9.案件订单状态通知
                    // TO_DO 需要自己处理此处业务\
                    String processCode = actualParamJSON.getString("processCode");
                    String process = actualParamJSON.getString("process");
                    String statusCode = actualParamJSON.getString("statusCode");
                    String status = actualParamJSON.getString("status");
                    String message = actualParamJSON.getString("message");

                    if ("0".equals(status)) {
                        //进行案件申请
                        application(loanBillNo);

                        AfArbitrationDo afArbitrationDo = new AfArbitrationDo();
                        afArbitrationDo.setGmtModified(new Date());
                        afArbitrationDo.setLoanBillNo(loanBillNo);
                        afArbitrationDo.setProcess(process);
                        afArbitrationDo.setProcessCode(processCode);
                        afArbitrationDo.setStatus(status);
                        afArbitrationDo.setStatusCode(statusCode);
                        afArbitrationDo.setMessage(message);
                        arbitrationService.updateByloanBillNo(afArbitrationDo);
                    }

                } else if (BusiCodeEnum.GETLITIGANTS.getCode().equalsIgnoreCase(busiCode)) {// 10.获取案件订单相关当事人信息
                    String ltype = actualParamJSON.getString("ltype");
                    String jsonString = JSON.toJSONString(arbitrationService.getLitiGants(loanBillNo, ltype));
                    result.setResult(jsonString);// 将结果添加到返回信息里面
                } else {
                    throw new ArbitramentException("1002", "参数【busiCode】数据错误");
                }
            }
        } catch (ArbitramentException e) {
            logger.error("第三方对外接口-Arbexpress异常", e);
            result = new Result(e.getErr_code(), e.getErr_msg());
        } catch (Exception e) {
            logger.error("第三方对外接口-异常", e);
            result = new Result(SYS_EXCEPTION_CODE, SYS_EXCEPTION_MSG);
        } finally {
            // 返回的结果
            //String result_msg = JSON.toJSONString(result);

            // 返回结果
            returnMsg(response, StringCompressUtils.compress(result.getResult()));
        }

    }


    /* 从请求流中获取参数json串
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String getParamString(HttpServletRequest request) throws IOException {
        final int BUFFER_SIZE = 8 * 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream sis = request.getInputStream();
        int length = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((length = sis.read(buffer)) > 0) {
            baos.write(buffer, 0, length);
        }
        String bodyData = new String(baos.toByteArray(), "UTF-8");
        return bodyData;
    }

    /**
     * 接口参数验证
     *
     * @param thirdParamsInfo
     * @return
     * @throws ArbitramentException
     */
    private boolean validate(ThirdParamsInfo thirdParamsInfo) throws ArbitramentException {
        String merchantCode = thirdParamsInfo.getMerchantCode();
        if (StringUtils.isBlank(merchantCode)) {
            throw new ArbitramentException("1001", "参数不能为空");
        }
        String format = thirdParamsInfo.getFormat();
        if (StringUtils.isBlank(format)) {
            thirdParamsInfo.setFormat("JSON");
        }
        if (!"JSON".equalsIgnoreCase(format)) {
            throw new ArbitramentException("1002", "参数【format】数据错误");
        }
        String encode = thirdParamsInfo.getEncode();
        if (StringUtils.isBlank(encode)) {
            thirdParamsInfo.setEncode("UTF-8");
        }
        if (!"UTF-8".equalsIgnoreCase(encode)) {
            throw new ArbitramentException("1002", "参数【encode】数据错误");
        }
        String busiCode = thirdParamsInfo.getBusiCode();
        if (StringUtils.isBlank(busiCode)) {
            throw new ArbitramentException("1001", "参数不能为空");
        }
        String param = thirdParamsInfo.getParam();
        if (StringUtils.isBlank(param)) {
            throw new ArbitramentException("1001", "参数不能为空");
        }
        String time = thirdParamsInfo.getTime();
        if (StringUtils.isBlank(time)) {
            throw new ArbitramentException("1001", "参数不能为空");
        }
        String signType = thirdParamsInfo.getSignType();
        if (StringUtils.isBlank(signType)) {
            thirdParamsInfo.setSignType("MD5");
        }
        if (!"MD5".equalsIgnoreCase(signType)) {
            throw new ArbitramentException("1002", "参数【signType】数据错误");
        }
        String signCode = thirdParamsInfo.getSignCode();
        if (StringUtils.isBlank(signCode)) {
            throw new ArbitramentException("1001", "参数【signCode】不能为空");
        }
        return true;
    }


    /**
     * 返回的信息
     *
     * @param response
     * @param msg
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private void returnMsg(HttpServletResponse response, String msg) throws UnsupportedEncodingException, IOException {
        response.getOutputStream().write(msg.getBytes("utf-8"));
    }

    /**
     * 测试时打印所有参数
     */
    public void printParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("---arbit begin:" + request.getRequestURI());
        sb.append("---arbit begin params:");
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String key : paramMap.keySet()) {
            String[] values = paramMap.get(key);
            for (String value : values) {
                sb.append("键:" + key + ",值:" + value);
            }
        }
        sb.append("---arbit end");
        logger.info(sb.toString());
    }
}
