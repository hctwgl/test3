package com.ald.fanbei.api.biz.third.util.fenqicuishou;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author honghzengpei 2017/11/29 17:37
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("fenqiCuishouUtil")
public class FenqiCuishouUtil {
    @Resource
    AfRepaymentService afRepaymentService;
    @Resource
    AfBorrowBillService afBorrowBillService;
    @Resource
    AfBorrowService afBorrowService;

    public static ThreadLocal<Boolean> checkChuiSou = new ThreadLocal<Boolean>();

    public static boolean getCheck(){
        if(checkChuiSou.get() ==null) return false;
        return checkChuiSou.get();
    }

    public static void setCheck(Boolean bo){
        checkChuiSou.set(bo);
    }

    /**
     *
     * @param repay_no
     * @param code
     * @param msg
     */
    public void postChuiSohiu(String repay_no, String code,String msg){
        //String  url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL)+"/api/getway/callback/nperRepay";
        String url = "http://192.168.117.103:8081/api/getway/callback/nperRepay";
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
            HttpUtil.post(url, mp);
        }catch (Exception e){
            e.printStackTrace();

        }
    }


    /**
     * 还款摧给催收
     * @param repaymentId
     */
    public void postReapymentMoney(Long repaymentId){
       // String  url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL)+"/api/getway/notify/nperRepay";
        String url = "http://192.168.117.103:8081/api/getway/notify/nperRepay";
        //byte[] salt = DigestUtil.decodeHex("51fabbeicuoshou");
        String salt = "51fabbeicuoshou";
        AfRepaymentDo afRepaymentDo = afRepaymentService.getRepaymentById(repaymentId);
        if(afRepaymentDo == null){
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("repay_no",afRepaymentDo.getRepayNo());
        jsonObject.put("consumer_no",afRepaymentDo.getUserId());
        jsonObject.put("repay_type","BANK");
        jsonObject.put("repay_time",simpleDateFormat.format(afRepaymentDo.getGmtCreate()));
        jsonObject.put("repay_amount",afRepaymentDo.getRepaymentAmount());
        jsonObject.put("bill_id",afRepaymentDo.getBillIds());
        jsonObject.put("trade_no",afRepaymentDo.getTradeNo());
        jsonObject.put("is_balance","N");//todo hzp
        String mm = JSON.toJSONString(jsonObject);
        try {
            byte[] pd = DigestUtil.digestString(mm.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign = DigestUtil.encodeHex(pd);
            HashMap<String,String> mp = new HashMap();
            mp.put("sign",sign);
            mp.put("data",mm);
            mp.put("timeStamp",String.valueOf( new Date().getTime()));
            String e=  HttpUtil.post(url, mp);
            String f ="";
        }catch (Exception e){
            e.printStackTrace();

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
            String salt = "51fabbeicuoshou";
            byte[] pd = DigestUtil.digestString(data.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if(!sign1.equals(sign))return false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat sdf  =   new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    AfRepaymentDo afRepaymentDo = new AfRepaymentDo();
                    afRepaymentDo.setStatus("A");
                    afRepaymentDo.setRepayNo(jsonObject.get("repay_no").toString());
                    afRepaymentDo.setUserId(Long.parseLong( jsonObject.get("consumer_no").toString()));
                    try {
                        afRepaymentDo.setGmtCreate(sdf.parse(jsonObject.get("repay_time").toString()));
                    }catch (Exception e){

                    }
                    afRepaymentDo.setGmtModified(afRepaymentDo.getGmtCreate());

                    BigDecimal amount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(jsonObject.get("repay_amount").toString()), BigDecimal.ZERO);
                    if(amount.compareTo(BigDecimal.ZERO) ==0) return;
                    afRepaymentDo.setActualAmount(amount);
                    afRepaymentDo.setBillIds(jsonObject.get("bill_id").toString());
                    afRepaymentDo.setTradeNo(jsonObject.get("trade_no").toString());
                    afRepaymentDo.setUserCouponId(0l);

                    afRepaymentDo.setCardName("");
                    afRepaymentDo.setPayTradeNo(jsonObject.get("trade_no").toString());
                    afRepaymentDo.setCardNumber("");

                    afRepaymentDo.setName("分期催收还款");
                    afRepaymentDo.setRebateAmount(BigDecimal.ZERO);
                    afRepaymentDo.setJfbAmount(BigDecimal.ZERO);
                    afRepaymentDo.setCouponAmount(BigDecimal.ZERO);


                    //String bill
                    //afBorrowBillService.getBorrowBillByIds()
                    //afRepaymentDo.setRepaymentAmount();
                }
            }).start();
            return  true;

        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
