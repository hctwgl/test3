package com.ald.fanbei.api.biz.third.util;


import com.ald.fanbei.api.biz.bo.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("XgxyUtil")
public class XgxyUtil {


    Logger logger = LoggerFactory.getLogger(XgxyUtil.class);


    private static String url = null;


    private static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";



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
            XgxyPayReqBo  payReqBo=new XgxyPayReqBo();
            payReqBo.setAppId(payBo.getTrade());
            payReqBo.setBorrowNo(payBo.getBorrowNo());
            payReqBo.setStatus(payBo.getStatus());
            if(StringUtils.equals(payBo.getStatus(),"N")){
                payReqBo.setReason(payBo.getReason());
            }else {
                payReqBo.setGmtArrival(payBo.getGmtArrival());
            }
            payReqBo.setSign(SignUtil.sign(createLinkString(payReqBo), PRIVATE_KEY));
            String reqResult = HttpUtil.post(getXgxyUrl(), payReqBo);
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
     * @param repayBo
     * @return
     */
    public boolean  rePayNoticeRequest(XgxyRepayBo repayBo){
        try {
            XgxyRepayReqBo repayReqBo=new XgxyRepayReqBo();
            repayReqBo.setAppId(repayBo.getTradeNo());
            repayReqBo.setBorrowNo(repayBo.getBorrowNo());
            repayReqBo.setStatus(repayBo.getStatus());
            repayReqBo.setSign(DsedSignUtil.paramsEncrypt(JSONObject.parseObject(createLinkString(repayReqBo)), PRIVATE_KEY));
            repayReqBo.setSign(SignUtil.sign(createLinkString(repayReqBo), PRIVATE_KEY));
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
     * 逾期通知请求
     * @param overdueBo
     * @return
     */
    public boolean  overDueNoticeRequest(XgxyOverdueBo overdueBo){
        try {
            XgxyOverdueReqBo overdueReqBo=new XgxyOverdueReqBo();
            overdueReqBo.setAppId(overdueBo.getTradeNo());
            overdueReqBo.setBorrowNo(overdueBo.getBorrowNo());
            overdueReqBo.setOverdueDays(String.valueOf(overdueBo.getOverdueDays()));
            overdueReqBo.setCurPeriod(String.valueOf(overdueBo.getCurPeriod()));
            Map<String,Object> data=new HashMap<>();
            data.put("test","test");
            overdueReqBo.setData(data);
            overdueReqBo.setSign(SignUtil.sign(createLinkString(overdueReqBo), PRIVATE_KEY));
            String reqResult = HttpUtil.post(getXgxyUrl(), overdueReqBo);
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
            JSONObject jsStr = JSONObject.parseObject(createLinkString(repayReqBo));
            repayReqBo.setSign(DsedSignUtil.paramsEncrypt(jsStr, PRIVATE_KEY));
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
