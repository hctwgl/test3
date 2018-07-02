package com.ald.fanbei.web.test.api.user;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.bo.assetside.AssetSideReqBo;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.web.test.common.BaseTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.oss.OSSClient;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignTest extends BaseTest {
    /**
     * 自测根据自己的业务修改下列属性 TODO
     */
    String urlBase = "http://localhost:8080";
    String userName = "17612158083";

    @Resource
    private OSSClient ossClient;
    /**
     * 自动注入登陆令牌
     */
    @Before
    public void init(){
        super.init(userName);
    }

    @Test
    public void  testGiveBackPdfInfoApi() {
        String url = urlBase + "/third/eProtocol/giveBackPdfInfo";
        Map<String,Object> params = new HashMap<>();
        params.put("debtType", 0);
        params.put("orderNo", "jq20180227224855423167");
        params.put("borrowerName", "伍德才");
        params.put("companyName", " ");
        params.put("companyTaxNo"," ");
        params.put("protocolUrl", "https://aliyunsso.edspay.com/protocol/loanProtocol/1000002121078946.pdf");
        String mapStr="[{\"investorCardId\":\"370632197106242112\",\"investorName\":\"毕建利\\r\\n\",\"amount\":17.19,\"investorPhone\":\"15666318216\"},{\"investorCardId\":\"440582199301292399\",\"investorName\":\"李晓鹏\",\"amount\":400.0,\"investorPhone\":\"15917904651\"},{\"investorCardId\":\"360104197010180485\",\"investorName\":\"王瑶\",\"amount\":522.0,\"investorPhone\":\"13870806326\"},{\"investorCardId\":\"120103196210302621\",\"investorName\":\"洪力生\",\"amount\":486.87,\"investorPhone\":\"13920015811\"},{\"investorCardId\":\"330103198102131616\",\"investorName\":\"王继\",\"amount\":174.13,\"investorPhone\":\"15988869096\"},{\"investorCardId\":\"370702197205141023\",\"investorName\":\"陈盛夏\",\"amount\":264.5,\"investorPhone\":\"13573603300\"},{\"investorCardId\":\"133031197211210322\",\"investorName\":\"焦金娟\",\"amount\":335.31,\"investorPhone\":\"13641277712\"}]";
        List<Map<String, Object>> matchUserList = JSON.parseObject(mapStr,ArrayList.class);
        params.put("investorList", matchUserList);
        String protocolSignUrl = "https://yapp.51fanbei.com/third/eProtocol/giveBackPdfInfo";
        System.out.println("加密之前的参数："+params);
        String encodsStr = encode(params);
//        httpPost(url, JSONObject.toJSONString(encodsStr), null);
        String respResult= HttpUtil.doHttpPostJsonParam(protocolSignUrl, encodsStr);
//        String result = HttpClientUtil.doPostJson(protocolSignUrl, encodsStr);
//        testApi(url, encodsStr, userName,true);
    }

    @Test
    public void  testGiveBackSealInfoApi() {
        String url = urlBase + "/third/eProtocol/giveBackSealInfo";
        Map<String,Object> params = new HashMap<>();
        String mapStr="[{\"realName\":\"习伟成\",\"edspayUserCardId\":\"33112119920330641x\",\"userType\":\"3\",\"mobile\":\"18911110017\"}]";
        List<Map<String, Object>> matchUserList = JSON.parseObject(mapStr,ArrayList.class);
        params.put("investorList", matchUserList);
//        String protocolSignUrl = "https://yapp.51fanbei.com/third/eProtocol/giveBackPdfInfo";
        System.out.println("加密之前的参数："+params);
        String encodsStr = encode(matchUserList);
//        httpPost(url, JSONObject.toJSONString(encodsStr), null);
        String respResult= HttpUtil.doHttpPostJsonParam(url, encodsStr);
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

//        String encryptData = AesUtil.encryptToBase64(JSON.toJSONString(data),"R8SIRR7HGMOMO2A4");
        String encryptData = AesUtil.encryptToBase64(JSON.toJSONString(data),"2KA4WGA857FFCC65");
        transport.setData(encryptData);
        return JSON.toJSONString(transport);
    }

    @Test
    public void  testGetContractProtocolPdf() {
        String url = urlBase + "/third/collection/getContractProtocolPdf";
        Map params = new HashMap<>();
        params.put("borrowNo", "jq2018030716224300273");
        params.put("type", "1");
        String data = JsonUtil.toJSONString(params);
        String timestamp = DateUtil.formatDate(new Date());
        String sign = DigestUtil.MD5(data);
        Map<String,String> paramsT = new  HashMap<>();
        paramsT.put("data",data);
        paramsT.put("sign",sign);
        paramsT.put("timestamp",timestamp);
//        HttpUtil.doHttpsPostIgnoreCertUrlencoded(url,getUrlParamsByMap(paramsT) );
        HttpUtil.doHttpPost(url,getUrlParamsByMap(paramsT));
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

    @Test
    public void  testGetRedRainRoundsApi2() {
        String url = urlBase + "/borrowCash/getConfirmBorrowInfo";
        Map<String,String> params = new HashMap<>();
        params.put("userName", "15968109556");
        params.put("borrowId", "33399634");
        params.put("protocolCashType", "4");
        params.put("userId", "13989456652");
        params.put("poundage", "10");
        params.put("amount", "500");
        params.put("type", "10");
        testApi(url, params, userName,true);
    }

    @Test
    public void  testGetRedRainRoundsApi3() {
        String url = urlBase + "/user/getBorrowCashProtocol";
        Map<String,String> params = new HashMap<>();
        params.put("userName", "15968109556");
        params.put("borrowId", "531");
        params.put("protocolCashType", "3");
        params.put("renewalDay", "10");
        params.put("renewalAmount", "500");
        params.put("renewalId", "0");
        testApi(url, params, userName,true);
    }

    //	@Test
    public void  testApplyHitH5() {
        String url = urlBase + "/fanbei-web/redRain/applyHit";
        Map<String,String> params = new HashMap<>();
        testH5(url, params, userName, false);
    }

    //	@Test
    public void  fetchRounds() {
        String url = urlBase + "/fanbei-web/redRain/fetchRounds";
        Map<String,String> params = new HashMap<>();
        testH5(url, params, userName, false);
    }

    @Test
    public void performanceTest() {
        ExecutorService executer = Executors.newFixedThreadPool(500);

        try {
            String s = FileUtils.readFileToString(new File(this.getClass().getClassLoader().getResource("userNames.json").toURI()));
            final JSONArray arr = JSON.parseArray(s);
            int size = arr.size(); // 5万请求时会挂掉，降低测试基准改为1万
            final String url = urlBase + "/fanbei-web/redRain/applyHit";

            for(int i = 0; i<10000; i++) {
                final String userName = arr.getJSONObject(i).getString("user_name");
                final TokenBo tokenBo = init(userName);

                executer.execute(new Runnable() {
                    public void run() {
                        Map<String,String> params = new HashMap<>();
                        testH5(url, params, userName, false, tokenBo);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

}
