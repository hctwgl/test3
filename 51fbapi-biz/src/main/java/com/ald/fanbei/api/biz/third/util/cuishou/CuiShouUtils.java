package com.ald.fanbei.api.biz.third.util.cuishou;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.fenqicuishou.FenqiCuishouUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.RepaymentStatus;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import sun.awt.windows.ThemeReader;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component("cuiShouUtils")
public class CuiShouUtils {
    private final String salt ="51fabbeicuoshou";
    protected static final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");

    @Resource
    GeneratorClusterNo generatorClusterNo;

    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    AfRepaymentDao afRepaymentDao;

    @Resource
    AfRepaymentService afRepaymentService;

    @Resource
    AfBorrowCashService afBorrowCashService;

    @Resource
    AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;

    @Resource
    AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

    @Resource
    AfBorrowLegalRepaymentService afBorrowLegalRepaymentService;

    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;

    @Resource
    AfBorrowLegalRepaymentV2Service afBorrowLegalRepaymentV2Service;
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;

    /**
     * 线下还款
     * @param sign
     * @param data
     * @return
     */
    public String offlineRepaymentMoney(String sign,final String data) {
        try {
            byte[] pd = DigestUtil.digestString(data.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if (!sign1.equals(sign)) return new CuiShouBackMoney(201, "验签错误").toString();


            final JSONObject jsonObject = JSONObject.parseObject(data);
            String c_type = jsonObject.getString("type");//类型

            if (CuiShouType.BORROW.getCode().equals(c_type)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CuiShouUtils.setIsXianXiaHuangKuang(true);
                        CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
                        cuiShouBackMoney.setCode(500);

                        cuiShouBackMoney = borrowBackMoney(jsonObject);
                        if(cuiShouBackMoney.getCode().intValue() ==200){
                            JSONObject jsonObject1 = (JSONObject) cuiShouBackMoney.getData();
                            jsonObject1.put("ref_id",CuiShouUtils.getAfRepaymentDo().getRid());
                        }
                        sycnSuccessAndError(cuiShouBackMoney,0); //同步返回数据
                    }
                }).start();
                return new CuiShouBackMoney(200, "成功").toString();//同步反回接收成功

            } else if (CuiShouType.BORROW_CASH.getCode().equals(c_type)) {
                CuiShouUtils.setIsXianXiaHuangKuang(true);
                CuiShouBackMoney c = borrowCashMoney(jsonObject);
                JSONObject j = (JSONObject) c.getData();
                if (CuiShouUtils.getAfRepaymentBorrowCashDo() != null) {
                    j.put("ref_id", CuiShouUtils.getAfRepaymentBorrowCashDo().getRid());
                    c.setData(j);
                }
                return JSON.toJSONString(c);
            } else if (CuiShouType.WITH_BORROW.getCode().equals(c_type)) {


            }
            return JSONObject.toJSONString(new  CuiShouBackMoney(200, "成功")) ;//同步反回接收成功
        } catch (Exception e) {
            thirdLog.error("offlineRepaymentMoney error", e);
            CuiShouBackMoney cuiShouBackMoney = new  CuiShouBackMoney(500, "内部错误");
            JSONObject jsonObject = new JSONObject();
            try {
                final JSONObject _jsonObject = JSONObject.parseObject(data);
                jsonObject.put("id", _jsonObject.getLongValue("id"));
                cuiShouBackMoney.setData(jsonObject);
            }
            catch (Exception e1){
                e1.printStackTrace();
            }
            return JSON.toJSONString(cuiShouBackMoney);
        }
    }


    /**
     * 消费分期还款
     * @param jsonObject
     * @return
     */
    private CuiShouBackMoney borrowBackMoney(JSONObject jsonObject){

        CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
        JSONObject _returnObject = new JSONObject();
        _returnObject.put("id",jsonObject.getLongValue("id"));
        _returnObject.put("type",CuiShouType.BORROW.getCode());
        cuiShouBackMoney.setData(_returnObject);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            AfRepaymentDo afRepaymentDo = new AfRepaymentDo();
            afRepaymentDo.setStatus("A");
            afRepaymentDo.setRepayNo(generatorClusterNo.getRepaymentNo(new Date()));
            afRepaymentDo.setPayTradeNo(jsonObject.get("trade_no").toString());
            afRepaymentDo.setUserId(Long.parseLong(jsonObject.get("consumer_no").toString()));
            try {
                //2017-12-27 18:06:58.0
                afRepaymentDo.setGmtCreate(sdf.parse(jsonObject.get("repay_time").toString()));
            } catch (Exception e) {
                thirdLog.info("cuiShouUtils  getRepayMentDo updateTime =0");
            }


            afRepaymentDo.setGmtModified(afRepaymentDo.getGmtCreate());
            afRepaymentDo.setTradeNo(jsonObject.get("trade_no").toString());
            BigDecimal amount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(jsonObject.get("repay_amount").toString()), BigDecimal.ZERO);
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                thirdLog.info("cuishouhuankuan  getRepayMentDo error amount =0");
                cuiShouBackMoney.setCode(203);
                return cuiShouBackMoney;
            }
            afRepaymentDo.setActualAmount(amount);

            JSONArray billListArray = (JSONArray) jsonObject.get("bill_id");

            String billIds = "";
            if (billListArray == null || billListArray.size() == 0) {
                thirdLog.info("cuiShouUtils  getRepayMentDo error billids size=0");
                cuiShouBackMoney.setCode(204);
                return cuiShouBackMoney;
            }
            for (int i = 0; i < billListArray.size(); i++) {
                if (billIds.length() > 0) {
                    billIds += ",";
                }
                billIds += billListArray.get(i).toString();
            }
            List<Long> billIdList = CollectionConverterUtil.convertToListFromArray(billIds.split(","), new Converter<String, Long>() {
                @Override
                public Long convert(String source) {
                    return Long.parseLong(source);
                }
            });

            List<AfBorrowBillDo> borrowBillList = afBorrowBillService.getBorrowBillByIds(billIdList);
            BigDecimal repayAmount = BigDecimal.ZERO;
            if (borrowBillList.size() != billIdList.size()) {
                thirdLog.info("cuiShouUtils  getRepayMentDo error billids =" + billIds);
                cuiShouBackMoney.setCode(205);
                return cuiShouBackMoney;
            }
            for (AfBorrowBillDo afBorrowBillDo : borrowBillList) {
                repayAmount = repayAmount.add(afBorrowBillDo.getBillAmount());
            }


            afRepaymentDo.setBillIds(billIds);
            afRepaymentDo.setRepaymentAmount(repayAmount);

            afRepaymentDo.setUserCouponId(0l);

            afRepaymentDo.setCardName(jsonObject.get("card_name").toString());
            afRepaymentDo.setCardNumber("");

            afRepaymentDo.setName("分期催收还款");
            afRepaymentDo.setRebateAmount(BigDecimal.ZERO);
            afRepaymentDo.setJfbAmount(BigDecimal.ZERO);
            afRepaymentDo.setCouponAmount(BigDecimal.ZERO);

            final String key = "choushou_redis_check_" + afRepaymentDo.getPayTradeNo();
            long count = redisTemplate.opsForValue().increment(key, 1);

            if (count != 1) {
                cuiShouBackMoney.setCode(206);
                return cuiShouBackMoney;
            }
            redisTemplate.expire(key, 30, TimeUnit.SECONDS);

            List<AfRepaymentDo> repaymentlist = afRepaymentDao.getRepaymentListByPayTradeNo(afRepaymentDo.getPayTradeNo());
            if (repaymentlist == null || repaymentlist.size() == 0) {
                afRepaymentDao.addRepayment(afRepaymentDo);
                jsonObject.put("ref_id",afRepaymentDo.getRid());
            } else {
                for (AfRepaymentDo __repayment : repaymentlist) {
                    if (!__repayment.getBillIds().trim().equals(billIds)) {
                        thirdLog.info("cuiShouUtils  getRepayMentDo error payTradeNo =" + afRepaymentDo.getPayTradeNo());
                        cuiShouBackMoney.setCode(205);
                        return cuiShouBackMoney;
                    } else {
                        if (__repayment.getStatus().equals(RepaymentStatus.YES.getCode())) {
                            thirdLog.info("cuiShouUtils  getRepayMentDo  success");
                            setAfRepaymentDo(__repayment);
                            cuiShouBackMoney.setCode(200);
                            jsonObject.put("ref_id",__repayment.getRid());
                            return cuiShouBackMoney;
                        }
                    }
                }
            }

            long i = afRepaymentService.dealRepaymentSucess(afRepaymentDo.getPayTradeNo(), afRepaymentDo.getTradeNo(), false);
            if (i > 0) {
                redisTemplate.delete(key);
                thirdLog.info("cuiShouUtils  getRepayMentDo  success");
                setAfRepaymentDo(afRepaymentDo);
                cuiShouBackMoney.setCode(200);
                return cuiShouBackMoney;

            } else {
                redisTemplate.delete(key);
                thirdLog.info("cuishouhuankuan  getRepayMentDo  error500");
                cuiShouBackMoney.setCode(500);
                return cuiShouBackMoney;

            }
        }
        catch (Exception e){
            thirdLog.error("cuishouhuankuan  getRepayMentDo  error500",e);
            cuiShouBackMoney.setCode(500);
            return cuiShouBackMoney;
        }
    }


    /**
     * 现金贷还款
     * @param jsonObject
     * @return
     */
    private CuiShouBackMoney borrowCashMoney(JSONObject obj) {


        CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
        JSONObject _returnObject = new JSONObject();
        _returnObject.put("id", obj.getLongValue("id"));
        _returnObject.put("type", CuiShouType.BORROW_CASH.getCode());
        cuiShouBackMoney.setData(_returnObject);

        try {
            String repayNo = obj.getString("repay_no");
            String borrowNo = obj.getString("borrow_no");
            String repayType = obj.getString("repay_type");
            String repayTime = obj.getString("repay_time");
            String repayAmount = obj.getString("repay_amount");
            String restAmount = obj.getString("rest_amount");
            String repayCardNum = obj.getString("repay_cardNum");
            String operator = obj.getString("operator");
            String tradeNo = obj.getString("trade_no"); // 三方交易流水号
            String isBalance = obj.getString("is_balance");
            String isAdmin = obj.getString("is_admin");

            if (StringUtil.isAllNotEmpty(repayNo, borrowNo, repayType, repayTime, repayAmount, restAmount, tradeNo, isBalance)) {
                AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
                if (afBorrowCashDo == null) {
                    cuiShouBackMoney.setCode(301);
                    thirdLog.error("offlineRepaymentNotify error borrowNo ="+ borrowNo);
                    return cuiShouBackMoney;
                }

                String respCode = FanbeiThirdRespCode.SUCCESS.getCode();
                Long borrowId = afBorrowCashDo.getRid();

                //合规线下还款V1
                if (afBorrowLegalOrderCashDao.tuchByBorrowId(borrowId) != null) {
                    AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(afBorrowCashDo.getRid());
                    afBorrowLegalOrderCashService.checkOfflineRepayment(afBorrowCashDo, orderCashDo, repayAmount, tradeNo);
                    afBorrowLegalRepaymentService.offlineRepay(orderCashDo, borrowNo, repayType, repayTime, repayAmount, restAmount, tradeNo, isBalance, repayCardNum, operator, isAdmin);
                }//合规线下还款V2
                else if (afBorrowLegalOrderService.isV2BorrowCash(borrowId)) {
                    afBorrowLegalRepaymentV2Service.offlineRepay(afBorrowCashDo, borrowNo, repayType, repayTime, repayAmount, restAmount, tradeNo, isBalance, repayCardNum, operator, isAdmin);
                }
                //旧版线下还款
                else {
                    AfRepaymentBorrowCashDo existItem = afRepaymentBorrowCashService.getRepaymentBorrowCashByTradeNo(borrowId, tradeNo);
                    if (existItem != null) {
                        thirdLog.info("offlineRepaymentNotify exist trade_no");
                        cuiShouBackMoney.setCode(302);
                        return cuiShouBackMoney;
                    }
                    afBorrowLegalOrderCashService.checkOfflineRepayment(afBorrowCashDo, null, repayAmount, tradeNo);
                    respCode = afRepaymentBorrowCashService.dealOfflineRepaymentSucess(repayNo, borrowNo,
                            repayType, repayTime,
                            NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO),
                            NumberUtil.objToBigDecimalDivideOnehundredDefault(restAmount, BigDecimal.ZERO), tradeNo,
                            isBalance);
                }

                cuiShouBackMoney.setCode(200);

                return cuiShouBackMoney;
            } else {
                //notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST);
                thirdLog.info("offlineRepaymentNotify REQUEST_PARAM_NOT_EXIST");
                cuiShouBackMoney.setCode(303);
                return cuiShouBackMoney;
            }
        } catch (Exception e) {
            thirdLog.error("offlineRepaymentNotify error", e);
            cuiShouBackMoney.setCode(500);
            return cuiShouBackMoney;
        }
    }

    /**
     * 还款成功或失败同步回去
     * @param cuiShouBackMoney
     * @param type 1  主动还款，0 线下还款
     */
    public void sycnSuccessAndError(CuiShouBackMoney cuiShouBackMoney,Integer type){
//        String  url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL)+"/api/getway/callback/nperRepay";
        String url = ConfigProperties.get("fbapi.cuishou.sycnurl");
        //String url ="http://192.168.117.50:8003/report/repayment";
        try {
            String mm = JSON.toJSONString(cuiShouBackMoney);
            byte[] pd = DigestUtil.digestString(mm.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign = DigestUtil.encodeHex(pd);
            HashMap<String, String> mp = new HashMap<String, String>();
            mp.put("sign", sign);
            mp.put("data", mm);
            mp.put("type",String.valueOf(type));
            //mp.put("timeStamp", String.valueOf(new Date().getTime()));
            thirdLog.info("cuishouhuankuan  postChuiSohiu {sign:" + sign + ",data:" + mm + "}");
            String e1 = "";
            if (url.toLowerCase().startsWith("https")) {
                thirdLog.info("cuishouhuankuan  postChuiSohiu https");
                e1 = HttpUtil.doHttpsPost(url, mp, "utf-8");
            } else {
                e1 = HttpUtil.post(url, mp);
            }
            thirdLog.info("cuishouhuankuan  postChuiSohiu back" + e1);
        }
        catch (Exception e){
            thirdLog.error("cuishouhuankuan  postChuiSohiu error",e);
        }

    }


    /**
     * 现金贷
     * @param afRepaymentBorrowCashDo
     */
    public void syncCuiShou(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo){
        setAfRepaymentBorrowCashDo(afRepaymentBorrowCashDo);
        CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
        cuiShouBackMoney.setCode(200);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ref_id",afRepaymentBorrowCashDo.getRid());
        jsonObject.put("type",CuiShouType.BORROW_CASH.getCode());
        cuiShouBackMoney.setData(jsonObject);
        sycnSuccessAndError(cuiShouBackMoney,1);
    }

    /**
     * 分期还款同步
     * @param afRepaymentDo
     */
    public void syncCuiShou(AfRepaymentDo afRepaymentDo){
        if(CuiShouUtils.getIsXianXiaHuangKuang() !=null && CuiShouUtils.getIsXianXiaHuangKuang()){
            return;
        }
        setAfRepaymentDo(afRepaymentDo);
        CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
        cuiShouBackMoney.setCode(200);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ref_id",afRepaymentDo.getRid());
        jsonObject.put("type",CuiShouType.BORROW.getCode());
        cuiShouBackMoney.setData(jsonObject);
        sycnSuccessAndError(cuiShouBackMoney,1);
    }

    /**
     * 续期成功通知催收
     */
    public void syncXuqi(AfBorrowCashDo afBorrowCashDo){
        try {
            CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
            cuiShouBackMoney.setCode(200);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ref_id", afBorrowCashDo.getRid());
            jsonObject.put("type", CuiShouType.BORROW_RENEWAL.getCode());
            cuiShouBackMoney.setData(jsonObject);
            sycnSuccessAndError(cuiShouBackMoney, 1);
        }
        catch (Exception e){
            thirdLog.error("cuishou sync error",e);
        }
    }

    @Resource
    AfBorrowCashDao afBorrowCashDao;
    @Resource
    AfBorrowBillDao afBorrowBillDao;
    @Resource
    AfBorrowDao afBorrowDao;
    /**
     * 强行平帐
     * @param data
     * @param sign
     * @return
     */
    public String finishBorrow(String data,String sign){
        try {
            byte[] pd = DigestUtil.digestString(data.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if (!sign1.equals(sign)) return JSON.toJSONString(new  CuiShouBackMoney(201, "验签错误"));

            JSONObject jsonObject = JSONObject.parseObject(data);
            String type = jsonObject.getString("type");
            if(type.equals("BORROW")){
                CuiShouBackMoney cuiShouBackMoney =new CuiShouBackMoney();
                Long billId = jsonObject.getLong("ref_id");
                AfBorrowBillDo afBorrowBillDo = afBorrowBillDao.getBorrowBillById(billId);;
                if(afBorrowBillDo ==null){
                    cuiShouBackMoney.setCode(304);
                    return JSONObject.toJSONString(cuiShouBackMoney);  //更新失败
                }
                List<Long> bilIds = new ArrayList<Long>();
                Integer i = afBorrowBillDao.updateBorrowBillStatusByBillIdsAndStatus(bilIds, BorrowBillStatus.YES.getCode());

                if(i ==0){
                    cuiShouBackMoney.setCode(304);
                    return JSONObject.toJSONString(cuiShouBackMoney);  //更新失败
                }
                Long borrowId = afBorrowBillDo.getBorrowId();
                List<AfBorrowBillDo> list = afBorrowBillDao.getAllBorrowBillByBorrowId(borrowId);
                if(!checkHasNoPayBill(list)){
                    afBorrowDao.updateBorrowStatus(borrowId, BorrowStatus.FINISH.getCode());
                }
                List<AfBorrowBillDo> afBorrowBillDoList = afBorrowBillDao.getBorrowBillListByStatus(afBorrowBillDo.getUserId(),afBorrowBillDo.getBillYear(),afBorrowBillDo.getBillMonth());
                if(afBorrowBillDoList == null || afBorrowBillDoList.size() ==0){
                    afBorrowBillDao.updateTotalBillStatus(afBorrowBillDo.getBillYear(),afBorrowBillDo.getBillMonth(),afBorrowBillDo.getUserId(),BorrowBillStatus.YES.getCode());
                }
                cuiShouBackMoney.setCode(200);
                return JSONObject.toJSONString(cuiShouBackMoney);
            }
            else if(type.equals("BORROW_CASH")){
                Long borrowId = jsonObject.getLong("ref_id");
                AfBorrowCashDo afBorrowCashDo = new AfBorrowCashDo();
                afBorrowCashDo.setRid(borrowId);
                afBorrowCashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
                Integer i = afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
                CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();

                if(i>0){
                    cuiShouBackMoney.setCode(200);
                    return JSONObject.toJSONString(cuiShouBackMoney);
                }
                cuiShouBackMoney.setCode(304);

                return JSONObject.toJSONString(cuiShouBackMoney);

            }

            thirdLog.info("cuishou finishBorrow type error" + type +",data:"+data);
            return JSON.toJSONString(new  CuiShouBackMoney(207, "类型错误"));
        }
        catch (Exception e){
            thirdLog.error("cuishou finishBorrow error",e);
            return JSON.toJSONString(new  CuiShouBackMoney(500, "内部错误"));
        }
    }

    private Boolean checkHasNoPayBill(List<AfBorrowBillDo> list){
        if(list ==null){
            return false;
        }
        for (AfBorrowBillDo afBorrowBillDo:list){
            if(afBorrowBillDo.getStatus().equals(BorrowBillStatus.NO) || afBorrowBillDo.getStatus().equals(BorrowBillStatus.DEALING)){
                return  true;
            }
        }
        return false;
    }



    private  static ThreadLocal <AfRepaymentBorrowCashDo> afRepaymentBorrowCashDoThreadLocal = new ThreadLocal<AfRepaymentBorrowCashDo>();

    public static void  setAfRepaymentBorrowCashDo(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo){
        afRepaymentBorrowCashDoThreadLocal.set(afRepaymentBorrowCashDo);
    }

    public static AfRepaymentBorrowCashDo getAfRepaymentBorrowCashDo(){
        return afRepaymentBorrowCashDoThreadLocal.get();
    }

    private static ThreadLocal<AfRepaymentDo> afRepaymentDoThreadLocal = new ThreadLocal<AfRepaymentDo>();

    private static void setAfRepaymentDo(AfRepaymentDo afRepaymentDo){
        afRepaymentDoThreadLocal.set(afRepaymentDo);
    }

    private static AfRepaymentDo getAfRepaymentDo(){
        return afRepaymentDoThreadLocal.get();
    }

    private static ThreadLocal<Boolean> isXianXiaHuangKuang = new ThreadLocal<Boolean>();

    public static void setIsXianXiaHuangKuang(Boolean a){
        isXianXiaHuangKuang.set(a);
    }

    public static Boolean getIsXianXiaHuangKuang(){
        return isXianXiaHuangKuang.get();
    }
}
