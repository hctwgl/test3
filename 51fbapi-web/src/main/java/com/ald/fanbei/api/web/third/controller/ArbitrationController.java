package com.ald.fanbei.api.web.third.controller;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.arbitration.*;
import com.ald.fanbei.api.biz.service.RiskTrackerService;
import com.ald.fanbei.api.dal.domain.RiskTrackerDo;
import com.alibaba.fastjson.JSON;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.ArbitrationRespBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.ArbitrationService;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ArbitrationStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
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
    private static final String URL = "http://localhost:8080/ArbInterface/v1/third.htm";
    private static final String TRACK_PREFIX = "track_arb_";

    @Resource
    RiskTrackerService trackerService;

    @ResponseBody
    @RequestMapping(value = "/submit", method = RequestMethod.GET)
    public String submit(String borrowNo) {
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
            orderInfo.setLoanBillNo(borrowNo);//案件订单编号

            paramsInfo.setParam(URLEncoder.encode(JSON.toJSONString(orderInfo), "UTF-8"));
            //-----
            paramsInfo.setSignType("MD5");
            paramsInfo.setSignCode(MD5.md5(generateSign(paramsInfo)));
            String jsonParam = StringCompressUtils.compress(JSON.toJSONString(paramsInfo));
            logger.info("jsonParam=" + jsonParam);
            String trackId = TRACK_PREFIX + new Date().getTime();
            String result = HttpClientUtils.postWithString(URL, jsonParam);
            logger.info(result);

            RiskTrackerDo riskTrackerDo = new RiskTrackerDo();
            riskTrackerDo.setGmtCreate(new Date());
            riskTrackerDo.setParams(JSON.toJSONString(paramsInfo));
            riskTrackerDo.setTrackId(trackId);
            riskTrackerDo.setUrl(URL);
            riskTrackerDo.setResult(result);
            trackerService.saveRecord(riskTrackerDo);
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
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/setStatus"}, method = RequestMethod.POST)
    public String setStatus() {
        printParams();
        Map resultMap=new HashMap<String,String>();
        resultMap.put("errCode","0000");
        resultMap.put("errMsg","");
        try {
            String loanBillNo = ObjectUtils.toString(request
                    .getParameter("loanBillNo"));
            String processCode = ObjectUtils.toString(request
                    .getParameter("processCode"));
            String process = ObjectUtils.toString(request
                    .getParameter("process"));
            String statusCode = ObjectUtils.toString(request
                    .getParameter("statusCode"));
            String status = ObjectUtils.toString(request
                    .getParameter("status"));
            String message = ObjectUtils.toString(request
                    .getParameter("message"));

            if(processCode.equals("0")){
                //进行案件申请
                application(loanBillNo);
            }
            return JSON.toJSONString(resultMap);
        }catch (Exception e){
            logger.info("setStatus error :",e);
            resultMap.put("errMsg",e.getMessage());
            return JSON.toJSONString(resultMap);
        }

    }


    @ResponseBody
    @RequestMapping(value = "/application", method = RequestMethod.GET)
    public String application( String borrowNo) {
        printParams();
       // String borrowNo = "jq2018041910202400260";
        try {
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
            orderInfo.setBatchNo(borrowNo);

            paramsInfo.setParam(URLEncoder.encode(JSON.toJSONString(orderInfo), "UTF-8"));
            //-----
            paramsInfo.setSignType("MD5");
            paramsInfo.setSignCode(MD5.md5(generateSign(paramsInfo)));
            String jsonParam = StringCompressUtils.compress(JSON.toJSONString(paramsInfo));
            logger.info("jsonParam=" + jsonParam);
            String trackId = TRACK_PREFIX + new Date().getTime();

            String result = StringCompressUtils.decompress(HttpClientUtils.postWithString(URL, jsonParam));
            logger.info(result);

            RiskTrackerDo riskTrackerDo = new RiskTrackerDo();
            riskTrackerDo.setGmtCreate(new Date());
            riskTrackerDo.setParams(JSON.toJSONString(paramsInfo));
            riskTrackerDo.setTrackId(trackId);
            riskTrackerDo.setUrl(URL);
            riskTrackerDo.setResult(result);
            trackerService.saveRecord(riskTrackerDo);
            return "success";
        } catch (Exception e) {
            logger.info("application error :",e);
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
    public ArbitrationRespBo getData(HttpServletResponse response) {
	 String type = ObjectUtils.toString(request.getParameter("type"));
	 String loanBillNo = ObjectUtils.toString(request.getParameter("loanBillNo"));
	 
	 if("GETORDERINFO".equals(type)) {
	     return arbitrationService.getOrderInfo(loanBillNo);
	 } else if("GETFUNDINFO".equals(type)){
	     return arbitrationService.getFundInfo(loanBillNo);
	 } else if("SETSTATUS".equals(type)){
	     setStatus();
	 } else if("GETLITIGANTS".equals(type)){
	     String ltype = ObjectUtils.toString(request.getParameter("ltype"));
	     return arbitrationService.getLitiGants(loanBillNo,ltype);
	 } else if("GETCREDITAGREEMENT".equals(type)){
	     return arbitrationService.getCreditAgreement(loanBillNo);
	 } else if("GETCREDITINFO".equals(type)){
	     return arbitrationService.getCreditInfo(loanBillNo);
	 } else if("GETREFUNDINFO".equals(type)){
	     return arbitrationService.getRefundInfo(loanBillNo);
	 } else if("GETPAYVOUCHER".equals(type)){
	     return arbitrationService.getPayVoucher(loanBillNo);
	 } 
	    logger.info("type is Undefined,loanBillNo= "+loanBillNo);
	    ArbitrationRespBo resp = new ArbitrationRespBo();
	    List result = new ArrayList();
	    resp.setErrCode(ArbitrationStatus.FAILURE.getCode());
	    resp.setErrMsg(ArbitrationStatus.FAILURE.getName());
	    resp.setResult(result);
	    return resp;
	
    }
    
    /**
     * 测试时打印所有参数
     */
    public void printParams(){
        StringBuilder sb = new StringBuilder();
        sb.append("---arbit begin:"+request.getRequestURI());
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
