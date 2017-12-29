package com.ald.fanbei.api.biz.third.util.fenqicuishou;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.util.AlgorithmHelper;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfRepaymentDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author honghzengpei 2017/11/29 17:37
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("fenqiCuishouUtil")
public class FenqiCuishouUtil {

    private static Logger logger = LoggerFactory.getLogger(FenqiCuishouUtil.class);
    protected static final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");
    @Resource
    AfRepaymentService afRepaymentService;
    @Resource
    AfBorrowBillService afBorrowBillService;
    @Resource
    AfBorrowService afBorrowService;
    @Resource
    AfRepaymentDao afRepaymentDao;

    public static ThreadLocal<Boolean> checkChuiSou = new ThreadLocal<Boolean>();

    public static ThreadLocal<String> repaymentNo = new ThreadLocal<String>();

    public static boolean getCheck(){
        if(checkChuiSou.get() ==null) return false;
        return checkChuiSou.get();
    }

    public static void setCheck(Boolean bo){
        checkChuiSou.set(bo);
    }

    public static String getRepaymentNo(){
        if(repaymentNo.get() ==null) return "";
        return repaymentNo.get();
    }
    public static void setRepaymentNo(String no){
        repaymentNo.set(no);
    }

    /**
     *
     * @param repay_no
     * @param code
     * @param msg
     */
    public void postChuiSohiu(String repay_no, String code,String msg){
        String  url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL)+"/api/getway/callback/nperRepay";

        thirdLog.info("cuishouhuankuan postChuiSohiu postUrl:"+url);

        //String url = "http://192.168.117.103:8081/api/getway/callback/nperRepay";
        String salt = "51fabbeicuoshou";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("repay_no",repay_no);
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        String mm = JSON.toJSONString(jsonObject);
        try {
            byte[] pd = DigestUtil.digestString(mm.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign = DigestUtil.encodeHex(pd);
            HashMap<String,String> mp = new HashMap<String,String>();
            mp.put("sign",sign);
            mp.put("data",mm);
            mp.put("timeStamp",String.valueOf( new Date().getTime()));
            thirdLog.info("cuishouhuankuan  postChuiSohiu {sign:"+sign+",data:"+mm+"}");
            String e1="";
            if (url.toLowerCase().startsWith("https")) {
                logger.info("cuishouhuankuan  postChuiSohiu https");
                e1 =HttpUtil.doHttpsPost(url,mp,"utf-8");
            }
            else{
                e1 = HttpUtil.post(url, mp);
            }
            thirdLog.info("cuishouhuankuan  postChuiSohiu back"+e1);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("cuishouhuankuan  postChuiSohiu error",e);
            thirdLog.error("cuishouhuankuan  postChuiSohiu error",e);
        }
    }


    /**
     * 还款摧给催收
     * @param repaymentId
     */
    public void postReapymentMoney(Long repaymentId) {
        try {
            if (FenqiCuishouUtil.getCheck() == true) {
                return;
            }

            String url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL) + "/api/getway/notify/nperRepay";

            thirdLog.info("cuishouhuankuan postReapymentMoney postUrl:"+url);

            //String url = "http://192.168.117.103:8081/api/getway/notify/nperRepay";
            //byte[] salt = DigestUtil.decodeHex("51fabbeicuoshou");
            String salt = "51fabbeicuoshou";
            AfRepaymentDo afRepaymentDo = afRepaymentService.getRepaymentById(repaymentId);
            if (afRepaymentDo == null) {
                return;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("repay_no", afRepaymentDo.getRepayNo());
            jsonObject.put("consumer_no", afRepaymentDo.getUserId());
            jsonObject.put("repay_type", getPayType(afRepaymentDo.getCardName()));
            jsonObject.put("repay_time", simpleDateFormat.format(afRepaymentDo.getGmtCreate()));
            jsonObject.put("repay_amount", afRepaymentDo.getRepaymentAmount());
            jsonObject.put("bill_id", afRepaymentDo.getBillIds());
            jsonObject.put("trade_no", afRepaymentDo.getTradeNo());
            String is_balance = "N";
            BigDecimal dd = BigDecimalUtil.add(afRepaymentDo.getActualAmount(), afRepaymentDo.getCouponAmount(), afRepaymentDo.getJfbAmount(), afRepaymentDo.getRebateAmount());
            if (afRepaymentDo.getRepaymentAmount().compareTo(dd) == 0) {
                is_balance = "Y";
            }

            jsonObject.put("is_balance", is_balance);
            String mm = JSON.toJSONString(jsonObject);

            byte[] pd = DigestUtil.digestString(mm.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign = DigestUtil.encodeHex(pd);
            HashMap<String, String> mp = new HashMap();
            mp.put("sign", sign);
            mp.put("data", mm);
            mp.put("timeStamp", String.valueOf(new Date().getTime()));
            thirdLog.info("cuishouhuankuan  postReapymentMoney {sign:"+sign+",data:"+mm+"}");
            String e1="";
            if (url.toLowerCase().startsWith("https")) {
                logger.info("cuishouhuankuan  postReapymentMoney https");
                e1= HttpUtil.doHttpsPost(url,mp,"utf-8");
            }
            else{
                e1 = HttpUtil.post(url, mp);
            }
            thirdLog.info("cuishouhuankuan  postReapymentMoney back"+e1);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("cuishouhuankuan  postReapymentMoney error",e);
            thirdLog.error("cuishouhuankuan  postReapymentMoney error", e);
        }
    }


    /**
     * 催收还款
     * @param sign
     * @param timeStamp
     * @param data
     */
    public boolean getRepayMentDo(String sign, final String timeStamp, final String data){
        try {
            FenqiCuishouUtil.setCheck(true);

            thirdLog.info("cuishouhuankuan  getRepayMentDo {sign:"+sign+",data:"+data+"}");
            String salt = "51fabbeicuoshou";
            byte[] pd = DigestUtil.digestString(data.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if(!sign1.equals(sign))return false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        JSONObject jsonObject = JSONObject.parseObject(data);
                        AfRepaymentDo afRepaymentDo = new AfRepaymentDo();
                        afRepaymentDo.setStatus("A");
                        afRepaymentDo.setRepayNo(jsonObject.get("repay_no").toString());
                        FenqiCuishouUtil.setRepaymentNo(afRepaymentDo.getRepayNo());
                        afRepaymentDo.setUserId(Long.parseLong(jsonObject.get("consumer_no").toString()));
                        try {
                            //2017-12-27 18:06:58.0
                            afRepaymentDo.setGmtCreate(sdf.parse(jsonObject.get("repay_time").toString()));
                        } catch (Exception e) {
                            thirdLog.info("cuishouhuankuan  getRepayMentDo updateTime =0");
                        }
                        afRepaymentDo.setGmtModified(afRepaymentDo.getGmtCreate());

                        BigDecimal amount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(jsonObject.get("repay_amount").toString()), BigDecimal.ZERO);
                        if (amount.compareTo(BigDecimal.ZERO) == 0) {
                            thirdLog.info("cuishouhuankuan  getRepayMentDo error amount =0");
                            postChuiSohiu(afRepaymentDo.getRepayNo(), "500", "还款失败");
                            return;
                        }
                        afRepaymentDo.setActualAmount(amount);
                        JSONArray billListArray = (JSONArray) jsonObject.get("bill_id");

                        String billIds = "";
                        if (billListArray == null || billListArray.size() == 0) {
                            thirdLog.info("cuishouhuankuan  getRepayMentDo error billids size=0");
                            postChuiSohiu(afRepaymentDo.getRepayNo(), "500", "还款失败");
                            return;
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
                            thirdLog.info("cuishouhuankuan  getRepayMentDo error billids =" + billIds);
                            postChuiSohiu(afRepaymentDo.getRepayNo(), "500", "还款失败");
                            return;
                        }
                        for (AfBorrowBillDo afBorrowBillDo : borrowBillList) {
                            repayAmount = repayAmount.add(afBorrowBillDo.getBillAmount());
                        }

                        afRepaymentDo.setBillIds(billIds);
                        afRepaymentDo.setRepaymentAmount(repayAmount);
                        afRepaymentDo.setTradeNo(jsonObject.get("trade_no").toString());
                        afRepaymentDo.setUserCouponId(0l);

                        afRepaymentDo.setCardName(getCardName(jsonObject.get("repay_type").toString()));
                        afRepaymentDo.setPayTradeNo(jsonObject.get("trade_no").toString());
                        afRepaymentDo.setCardNumber("");

                        afRepaymentDo.setName("分期催收还款");
                        afRepaymentDo.setRebateAmount(BigDecimal.ZERO);
                        afRepaymentDo.setJfbAmount(BigDecimal.ZERO);
                        afRepaymentDo.setCouponAmount(BigDecimal.ZERO);
                        afRepaymentDao.addRepayment(afRepaymentDo);
                        long i = afRepaymentService.dealRepaymentSucess(afRepaymentDo.getPayTradeNo(), afRepaymentDo.getTradeNo(), false);
                        if (i > 0) {
                            postChuiSohiu(afRepaymentDo.getRepayNo(), "200", "还款成功");
                        } else {
                            postChuiSohiu(afRepaymentDo.getRepayNo(), "500", "还款失败");
                        }
                    }catch (Exception e){
                        thirdLog.error("cuishouhuankuan  getRepayMentDo error expection =",e);
                        postChuiSohiu(FenqiCuishouUtil.getRepaymentNo(), "500", "还款失败");
                    }

                    //String bill
                    //afBorrowBillService.getBorrowBillByIds()
                    //afRepaymentDo.setRepaymentAmount();
                }
            }).start();
            return  true;

        }
        catch (Exception e){
            e.printStackTrace();
            logger.error("cuishouhuankuan  getRepayMentDo error",e);
            return false;
        }
    }


    private String getPayType(String cardName){
        //WECHAT/ALIPAY/BANK/APP
        if(cardName.equals("支付宝")){
            return "ALIPAY";
        }
        if(cardName.equals("微信")){
            return "WECHAT";
        }
        if(cardName.equals("账户余额")){
            return "APP";
        }
        return "BANK";
    }

    private String getCardName(String type){
        if(type.equals("ALIPAY")){
            return "支付宝";
        }
        if(type.equals("WECHAT")){
            return "微信";
        }
        if(type.equals("APP")){
            return "账户余额";
        }
        return "银行";
    }
}
