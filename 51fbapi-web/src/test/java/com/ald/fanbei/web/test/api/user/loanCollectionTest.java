package com.ald.fanbei.web.test.api.user;

import com.ald.fanbei.api.biz.bo.assetside.AssetSideReqBo;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.web.test.common.BaseTest;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;

public class loanCollectionTest extends BaseTest {
    /**
     * 自测根据自己的业务修改下列属性 TODO
     */
//    String urlBase = "https://yapp.51fanbei.com";
    String urlBase = "http://localhost:8080";
    String userName = "13656640521";

    @Resource
    private OSSClient ossClient;

    /**
     * 自动注入登陆令牌
     */
    @Before
    public void init() {
        super.init(userName);
    }

    @Test
    public void testGiveBackPdfInfoApi() {
        String url = urlBase + "/third/eProtocol/giveBackPdfInfo";
        Map<String, Object> params = new HashMap<>();
        params.put("debtType", 0);
        params.put("orderNo", "jq2018032016000500484");
        params.put("borrowerName", "伍德才");
        params.put("companyName", " ");
        params.put("companyTaxNo", " ");
        params.put("protocolUrl", "https://aliyunsso.edspay.com/protocol/loanProtocol/1000002121078946.pdf");
        String mapStr = "[{\"investorCardId\":\"370632197106242112\",\"investorName\":\"毕建利\\r\\n\",\"amount\":17.19,\"investorPhone\":\"15666318216\"},{\"investorCardId\":\"440582199301292399\",\"investorName\":\"李晓鹏\",\"amount\":400.0,\"investorPhone\":\"15917904651\"},{\"investorCardId\":\"360104197010180485\",\"investorName\":\"王瑶\",\"amount\":522.0,\"investorPhone\":\"13870806326\"},{\"investorCardId\":\"120103196210302621\",\"investorName\":\"洪力生\",\"amount\":486.87,\"investorPhone\":\"13920015811\"},{\"investorCardId\":\"330103198102131616\",\"investorName\":\"王继\",\"amount\":174.13,\"investorPhone\":\"15988869096\"},{\"investorCardId\":\"370702197205141023\",\"investorName\":\"陈盛夏\",\"amount\":264.5,\"investorPhone\":\"13573603300\"},{\"investorCardId\":\"133031197211210322\",\"investorName\":\"焦金娟\",\"amount\":335.31,\"investorPhone\":\"13641277712\"}]";
        List<Map<String, Object>> matchUserList = JSON.parseObject(mapStr, ArrayList.class);
        params.put("investorList", matchUserList);
        String protocolSignUrl = "https://yapp.51fanbei.com/third/eProtocol/giveBackPdfInfo";
        System.out.println("加密之前的参数：" + params);
        String encodsStr = encode(params);
//        httpPost(url, JSONObject.toJSONString(encodsStr), null);
        String respResult = HttpUtil.doHttpPostJsonParam(url, encodsStr);
//        String result = HttpClientUtil.doPostJson(protocolSignUrl, encodsStr);
//        testApi(url, encodsStr, userName,true);
    }

    public String encode(Object data) {
        AssetSideReqBo transport = new AssetSideReqBo();
        long time = new Date().getTime();
        String sigin = DigestUtil.MD5(JSON.toJSONString(data));
        transport.setSign(sigin);
        transport.setAppId("edspay");
        transport.setSendTime(time);

        String encryptData = AesUtil.encryptToBase64(JSON.toJSONString(data), "2KA4WGA857FFCC65");
//        String encryptData = AesUtil.encryptToBase64(JSON.toJSONString(data),"2KA4WGA857FFCC65");
        transport.setData(encryptData);
        return JSON.toJSONString(transport);
    }

    @Test
    public void testGetContractProtocolPdf() {
        String url = urlBase + "/third/collection/getLoanPeriodsInfo";
        Map params = new HashMap<>();
        String data = JsonUtil.toJSONString(282);
        String timestamp = DateUtil.formatDate(new Date());
        String sign = DigestUtil.MD5(data);
        Map<String, String> paramsT = new HashMap<>();
        paramsT.put("data", "282");
        paramsT.put("sign", sign);
        paramsT.put("timestamp", timestamp);
//        HttpUtil.doHttpsPostIgnoreCertUrlencoded(url,getUrlParamsByMap(paramsT) );
        HttpUtil.doHttpPost(url, getUrlParamsByMap(paramsT));
    }

    @Test
    public void testOfflineLoanRepayment() {
        String url = urlBase + "/third/collection/offlineLoanRepayment";
        Map params = new HashMap<>();
        Map map = new HashMap();
        List list = new ArrayList();
        map.put("id","525");
        map.put("repayAmount",1700.34);
        map.put("reductionAmount",56);
        list.add(map);
        map = new HashMap();
        map.put("id","526");
        map.put("repayAmount",1666.29);
        map.put("reductionAmount",0);
        list.add(map);
        map = new HashMap();
        map.put("id","527");
        map.put("repayAmount",1710.72);
        map.put("reductionAmount",0);
        list.add(map);
        System.out.println(JSON.toJSONString(list));
        params.put("repay_no","4545645645614524");
        params.put("loan_no","dk2018032714252900946");
        params.put("repay_type","");
        params.put("repay_amount","");
        params.put("rest_amount","");
        params.put("repay_cardNum","");
        params.put("trade_no","415241245454545");
        params.put("repayment_id","");
        params.put("is_all_repay","false");
        params.put("periods_list",list);
        String data = JsonUtil.toJSONString(params);
        String timestamp = DateUtil.formatDate(new Date());
        String sign = DigestUtil.MD5(data);
        Map<String, String> paramsT = new HashMap<>();
        paramsT.put("data", data);
        paramsT.put("sign", sign);
        paramsT.put("timestamp", timestamp);
//        HttpUtil.doHttpsPostIgnoreCertUrlencoded(url,getUrlParamsByMap(paramsT) );
        HttpUtil.doHttpPost(url, getUrlParamsByMap(paramsT));
    }

    public String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

}
