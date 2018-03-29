package com.ald.fanbei.api.biz.third.util.bkl;


/**
 * 白骑士数据处理
 */


import com.ald.fanbei.api.biz.iagent.utils.AOSHttpClient;
import com.ald.fanbei.api.biz.iagent.utils.AOSJson;
import com.ald.fanbei.api.biz.iagent.utils.HttpRequestVO;
import com.ald.fanbei.api.biz.iagent.utils.HttpResponseVO;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Component("bklUtils")
public class BklUtils {

    private static Logger logger = Logger.getLogger(BklUtils.class);
    @Resource
    AfUserService afUserService;

    public  void requestJob(String csv_phone_num,String csv_arn,String csv_name,String csv_sex,String csv_digit_4,String csv_birth_date,String csv_staging,String csv_amt,String csv_pay_way,String csv_product_category) {
        Map map=new HashMap();
        map.put("work_id", "work_0008");
        map.put("corp_code", "51返呗");
        map.put("access_token", "xCGQF6wNeA1z6Cmz2UMW8F1as");
        map.put("job_code", "51FB-SP01");
        map.put("order_id", "trade0008");
        String work_data = "{'csv_phone_num':'15990182307','csv_arn':'tr008','csv_name':'任春雷','csv_sex':'男','csv_digit_4':8755,'csv_birth_date':'19890610','csv_staging':9,'csv_amt':1500,'csv_pay_way':'分期付款','csv_product_category':'手机通讯' }";
        Map dtt=null;
        try {
            dtt = AOSJson.fromJson(work_data, HashMap.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> mdata = Maps.newHashMap();

        map.put("work_data", dtt);
        mdata.put("mdata", AOSJson.toJson(map));

        HttpRequestVO httpRequestVO = new HttpRequestVO("http://221.133.225.245:18080/worksheetAPI/requestJob", mdata);

        try {
            HttpResponseVO httpResponseVO = AOSHttpClient.upload(httpRequestVO);
            httpResponseVO.getOut();
            httpResponseVO.getStatus();
            System.out.println(httpResponseVO.getOut());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}