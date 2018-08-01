package com.ald.fanbei.web.test.api.dsed;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.AssetSideReqBo;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.web.test.common.BaseTest;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.*;

public class DsedPdfTest extends BaseTest {

    //    String urlBase = "https://yapp.51fanbei.com";
    String urlBase = "http://localhost:8080";
    @Test
    public void testGiveBackPdfInfoApi() {
        String url = urlBase + "/third/eProtocol/giveBackPdfInfo";
        Map<String, Object> params = new HashMap<>();
        params.put("debtType", 2);
        params.put("orderNo", "dk2018072322460600027");
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
}
