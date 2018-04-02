package com.ald.fanbei.api.biz.third.util.bkl;


/**
 * 白骑士数据处理
 */


import com.ald.fanbei.api.biz.iagent.utils.AOSHttpClient;
import com.ald.fanbei.api.biz.iagent.utils.AOSJson;
import com.ald.fanbei.api.biz.iagent.utils.HttpRequestVO;
import com.ald.fanbei.api.biz.iagent.utils.HttpResponseVO;
import com.ald.fanbei.api.biz.service.AfIagentResultService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.dal.dao.AfIagentResultDao;
import com.ald.fanbei.api.dal.domain.AfBklDo;
import com.ald.fanbei.api.dal.domain.AfIagentResultDo;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component("bklUtils")
public class BklUtils {

    private static Logger logger = Logger.getLogger(BklUtils.class);

    @Resource
    AfIagentResultService iagentResultService;

    @Resource
    SmsUtil smsUtil;
    public  void submitJob(AfBklDo bklDo) {
        Map map=new HashMap();
        map.put("work_id", bklDo.getCsvArn()+ new Date().getTime());
        map.put("corp_code", "51返呗");
        map.put("access_token", "xCGQF6wNeA1z6Cmz2UMW8F1as");
        map.put("job_code", "51FB-SP01");
        map.put("order_id", bklDo.getCsvArn() + new Date().getTime());
        String work_data = "{'csv_phone_num':'"+bklDo.getCsvPhoneNum()+"','csv_arn':'"+bklDo.getCsvArn()+"','csv_name':'"+bklDo.getCsvName()+"','csv_sex':'"+bklDo.getCsvSex()+
                "','csv_digit_4':"+bklDo.getCsvDigit4()+",'csv_birth_date':'"+bklDo.getCsvBirthDate()+"','csv_staging':"+bklDo.getCsvStaging()+
                ",'csv_amt':"+bklDo.getCsvAmt()+",'csv_pay_way':'"+bklDo.getCsvPayWay()+"','csv_product_category':'"+bklDo.getCsvProductCategory()+"' }";
        Map dtt=null;
        try {
            dtt = AOSJson.fromJson(work_data, HashMap.class);
            logger.info("bklUtils submitJob success json dtt = "+dtt);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("bklUtils submitJob json error = >{}",e);
        }

        Map<String, String> mdata = Maps.newHashMap();

        map.put("work_data", dtt);
        mdata.put("mdata", AOSJson.toJson(map));

        HttpRequestVO httpRequestVO = new HttpRequestVO("http://221.133.225.245:18080/worksheetAPI/requestJob", mdata);

        try {
            HttpResponseVO httpResponseVO = AOSHttpClient.upload(httpRequestVO);
            httpResponseVO.getOut();
            httpResponseVO.getStatus();
            logger.info("bklUtils submitJob httpResponseVO success out =" + httpResponseVO.getOut());
            JSONObject object = JSONObject.parseObject(httpResponseVO.getOut());
            JSONObject object1 = (JSONObject) object.get("success");
            logger.info("bklUtils submitJob bklDo info =" +bklDo.getOrderId()+",csvArn=" +bklDo.getCsvArn()+",user ="+bklDo.getUserId() +",receipt_id ="+Long.parseLong(String.valueOf(object1.get("receipt_id"))));
            if (object1 != null){
                AfIagentResultDo iagentResultDo = new AfIagentResultDo();
                iagentResultDo.setWorkId(Long.parseLong(String.valueOf(object1.get("receipt_id"))));
                iagentResultDo.setOrderId(bklDo.getOrderId());
                iagentResultDo.setOrderNo(bklDo.getCsvArn());
                iagentResultDo.setOrderType("0");
                iagentResultDo.setUserId(bklDo.getUserId());
                iagentResultDo.setGmtCreate(new Date());
                iagentResultDo.setGmtModified(new Date());
                iagentResultService.saveRecord(iagentResultDo);
            }else {
                logger.error("bklUtils submitJob httpResponseVO error out ="+httpResponseVO.getOut());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("bklUtils submitJob httpResponseVO error =>{}",e);
        }
    }


}