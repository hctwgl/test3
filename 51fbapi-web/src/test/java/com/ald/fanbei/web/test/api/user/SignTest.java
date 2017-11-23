package com.ald.fanbei.web.test.api.user;

import com.ald.fanbei.api.biz.bo.TokenBo;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignTest extends BaseTest {
    /**
     * 自测根据自己的业务修改下列属性 TODO
     */
    String urlBase = "http://localhost:8080";
    String userName = "13656640521";

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
    public void  testGetRedRainRoundsApi() {
        String url = urlBase + "/user/getBorrowCashProtocol";
        Map<String,String> params = new HashMap<>();
        params.put("userName", "97");
        params.put("borrowId", "531");
        params.put("type", "7");
        params.put("protocolCashType", "1");
        params.put("borrowAmount", "500");
        testApi(url, params, userName,true);
    }
    @Test
    public void  testGetRedRainRoundsApi2() {
        String url = urlBase + "/user/getBorrowCashProtocol";
        Map<String,String> params = new HashMap<>();
        params.put("userName", "15968109556");
        params.put("borrowId", "531");
        params.put("protocolCashType", "2");
        params.put("nper", "3");
        params.put("amount", "500");
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
