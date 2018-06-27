package com.ald.fanbei.api.biz.third.util;


import com.ald.fanbei.api.biz.bo.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("XgxyUtil")
public class XgxyUtil {


    Logger logger = LoggerFactory.getLogger(XgxyUtil.class);


    private static String url = "192.168.107.227:2003/open/third/edspay/v1/giveBackRepayResult";


    private static String PRIVATE_KEY = "aef5c8c6114b8d6a";



    private static String getXgxyUrl(){
        if(url==null){
            url = ConfigProperties.get(Constants.CONFKEY_XGXY_URL);
            return url;
        }
        return url;
    }
    /**
     * 打款通知请求
     * @param
     * @return
     */
    public boolean  payNoticeRequest(XgxyPayBo payBo){

        try {
            Map<String,String> params=new HashMap<>();
            Map<String,String>  pay=new HashMap<>();
            pay.put("borrowNo",payBo.getBorrowNo());
            pay.put("tradeNo",payBo.getTrade());
            pay.put("status",payBo.getStatus());
            params.put("data",JSON.toJSONString(pay));
            params.put("sign",DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)), PRIVATE_KEY));
            if(StringUtils.equals(payBo.getStatus(),"N")){
                pay.put("reason",payBo.getReason());
            }else {
                pay.put("gmtArrival", String.valueOf(payBo.getGmtArrival()));
            }
            pay.put("sign",DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)), PRIVATE_KEY));
            String reqResult = HttpUtil.post(getXgxyUrl(), params);
            if(StringUtil.isBlank(reqResult)){
                return false;
            }
            XgxyPayReqBo payRespResult = JSONObject.parseObject(reqResult,XgxyPayReqBo.class);
            if("01".equals(payRespResult.getCode())){
                return true;
            }
        }catch (Exception e){
            logger.info("rePayNoticeRequest request fail",e);
        }

        return false;

    }
    /**
     * 还款通知请求(补偿机制)
     * @param
     * @return
     */
    public boolean  rePayNoticeRequest(Map<String,String> params){
        try {
            params.put("appId","");
            params.put("sign",DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)), PRIVATE_KEY));
            String reqResult = HttpUtil.post(getXgxyUrl(), params);
            if(StringUtil.isBlank(reqResult)){
                return false;
            }
            XgxyPayReqBo rePayRespResult = JSONObject.parseObject(reqResult,XgxyPayReqBo.class);
            if("01".equals(rePayRespResult.getCode())){
                return true;
            }
        }catch (Exception e){
            logger.info("rePayNoticeRequest request fail",e);
        }

        return false;

    }



    /**
     * 逾期通知请求
     * @param overdueBo
     * @return
     */
    public boolean  overDueNoticeRequest(XgxyOverdueBo overdueBo){
        try {
            Map<String,String> params=new HashMap<>();
            params.put("appId",overdueBo.getTradeNo());
            Map<String,String> overdue=new HashMap<>();
            overdue.put("borrowNo",overdueBo.getBorrowNo());
            overdue.put("overdueDays", String.valueOf(overdueBo.getOverdueDays()));
            overdue.put("curPeriod",overdueBo.getCurPeriod() );
            params.put("data", JSON.toJSONString(overdue));
            params.put("sign",DsedSignUtil.paramsEncrypt(JSONObject.parseObject(JSON.toJSONString(params)), PRIVATE_KEY));
            String reqResult = HttpUtil.post(getXgxyUrl(), params);
            if(StringUtil.isBlank(reqResult)){
                return false;
            }
            XgxyOverdueReqBo overdueReqBo1 = JSONObject.parseObject(reqResult,XgxyOverdueReqBo.class);
            if("01".equals(overdueReqBo1.getCode())){
                return true;
            }
        }catch (Exception e){
            logger.info("overDueNoticeRequest request fail",e);
        }
        return false;

    }


    /**
     * 还款通知请求
     * @param data
     * @return
     */
    public boolean  dsedRePayNoticeRequest(HashMap<String, Object> data ){
        try {
            XgxyRepayReqBo repayReqBo=new XgxyRepayReqBo();
            JSONObject jsonObj = new JSONObject(data);
            repayReqBo.setSign(DsedSignUtil.paramsEncrypt(jsonObj, PRIVATE_KEY));
            String reqResult = HttpUtil.post(getXgxyUrl(), repayReqBo);
            if(StringUtil.isBlank(reqResult)){
                return false;
            }
            XgxyPayReqBo rePayRespResult = JSONObject.parseObject(reqResult,XgxyPayReqBo.class);
            if("01".equals(rePayRespResult.getCode())){
                return true;
            }
        }catch (Exception e){
            logger.info("rePayNoticeRequest request fail",e);
        }

        return false;

    }


    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String pres = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if(params.get(key)!=null) {
                String value = (String) params.get(key);
                pres = pres + value;
            }
        }

        return pres;
    }















}
