package com.ald.fanbei.api.biz.baiqishi;


/**
 * 白骑士数据处理
 */


import com.ald.fanbei.api.biz.service.AfUserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;


@Service("baiQiShiUtils")
public class BaiQiShiUtils {

    private static Logger logger = Logger.getLogger(BaiQiShiUtils.class);
    @Resource
    AfUserService afUserService;

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