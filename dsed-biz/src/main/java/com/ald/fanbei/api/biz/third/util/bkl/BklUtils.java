package com.ald.fanbei.api.biz.third.util.bkl;


/**
 * 白骑士数据处理
 */


import com.ald.fanbei.api.biz.iagent.utils.AOSHttpClient;
import com.ald.fanbei.api.biz.iagent.utils.AOSJson;
import com.ald.fanbei.api.biz.iagent.utils.HttpRequestVO;
import com.ald.fanbei.api.biz.iagent.utils.HttpResponseVO;
import com.ald.fanbei.api.biz.service.AfIagentResultService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.YFSmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.dao.AfIagentResultDao;
import com.ald.fanbei.api.dal.domain.AfBklDo;
import com.ald.fanbei.api.dal.domain.AfIagentResultDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component("bklUtils")
public class BklUtils {

    private static Logger logger = Logger.getLogger(BklUtils.class);

    @Resource
    AfIagentResultService iagentResultService;
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfResourceService afResourceService;

    @Resource
    SmsUtil smsUtil;
    public  void submitJob(AfBklDo bklDo) {
        if (!checkTodayOrders(bklDo)){
            return;
        }
        Map map=new HashMap();
        map.put("work_id", bklDo.getCsvArn()+ new Date().getTime());
        map.put("corp_code", "爱上街");
        map.put("access_token", ConfigProperties.get(Constants.CONFKEY_BKL_ACCESS_TOKEN));
        map.put("job_code", "51FB-SP01");
        map.put("order_id", bklDo.getCsvArn() + new Date().getTime());
        String work_data = "{'csv_phone_num':'"+bklDo.getCsvPhoneNum()+"','csv_arn':'"+bklDo.getCsvArn()+"','csv_name':'"+bklDo.getCsvName()+"','csv_sex':'"+bklDo.getCsvSex()+
                "','csv_digit_4':"+bklDo.getCsvDigit4()+",'csv_birth_date':'"+bklDo.getCsvBirthDate()+"','csv_staging':"+bklDo.getCsvStaging()+
                ",'csv_amt':"+bklDo.getCsvAmt()+",'csv_pay_way':'"+bklDo.getCsvPayWay()+"','csv_product_category':'"+bklDo.getCsvProductCategory()+"' }";
        Map dtt=null;
        logger.info("bklUtils submitJob work_data = "+work_data);
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

        HttpRequestVO httpRequestVO = new HttpRequestVO(ConfigProperties.get(Constants.CONFKEY_BKL_URL), mdata);

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
                afOrderService.updateIagentStatusByOrderId(bklDo.getOrderId(),"C");
                String content = "尊敬的用户，为了保障您的财产安全，加速订单申请，我们稍后将会对您下单金额100元以上的订单进行智能电核，审核电话号码前三位为951，请您保持电话畅通并耐心完成互动！";
                smsUtil.sendSmsToDhst(bklDo.getCsvPhoneNum(),content);
            }else {
                logger.error("bklUtils submitJob httpResponseVO error out ="+httpResponseVO.getOut());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("bklUtils submitJob httpResponseVO error =>{}",e);
        }
    }
    /**
     * 判断是否发起电核
     * @return 是否发起电核true是false否
     * 返回值
     * <code>
     *  {
     *      "code": 1000,           //1000正常,其他异常
     *      "msg": "",              //错误信息
     *      "data": ""     //返回数据
     *  }
     * </code>
     * @throws Exception 异常
     * @date: 2018/4/12 14:32
     * @author: xieqiang
     */
private boolean checkTodayOrders(AfBklDo bklDo){
    try {
        BigDecimal amount = new BigDecimal(100);
        AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.ORDER_MOBILE_VERIFY_SET.getCode(), AfResourceSecType.ORDER_MOBILE_VERIFY_SET.getCode());
        if (afResourceDo != null){
            amount = new BigDecimal(afResourceDo.getValue());
        }
        AfIagentResultDo afIagentResultDo = iagentResultService.getIagentByUserIdToday(bklDo.getUserId());
        AfOrderDo afOrderDo = afOrderService.selectTodayIagentStatus(bklDo.getUserId(),amount);
        if (afOrderDo ==null){
            return true;
        }else{
            if (afIagentResultDo == null){
                return true;
            }
            String iagentstatus = afOrderDo.getIagentStatus();
            bklDo.setIagentState("ADF".contains(iagentstatus)?"A":"BEG".contains(iagentstatus)?"B":iagentstatus);
            //afOrderService.updateIagentStatusByOrderId(bklDo.getOrderId(),iagentstatus);
            if ("BEG".contains(iagentstatus)){
                AfOrderDo afOrderClose = afOrderService.getOrderById(bklDo.getOrderId());
                Map<String,String> qmap = new HashMap<>();
                qmap.put("orderNo",afOrderClose.getOrderNo());
                //HttpUtil.doHttpPost("https://admin.51fanbei.com/orderClose/closeOrderAndBorrow",JSONObject.toJSONString(qmap));
                final String  orderNo = afOrderClose.getOrderNo();
                final String json = JSONObject.toJSONString(qmap);
                logger.info("checkTodayOrders closeOrderAndBorrow checkTodayOrders info ="+orderNo);
                YFSmsUtil.pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        HttpUtil.doHttpPost(ConfigProperties.get(Constants.CONFKEY_ADMIN_URL)+"/orderClose/closeOrderAndBorrow?orderNo="+orderNo,json);
                    }
                });

            }
            return false;
            /*String checkState = afIagentResultDo.getCheckState();
            if ( checkState!=null && !"".equals(checkState)){

                if (afOrderDo != null){

                }else{
                    return true;
                }
            }*/
        }
    }catch (Exception e){
        logger.info("checkTodayOrders error orderno="+bklDo.getCsvArn(),e);
        return false;
    }

}

}