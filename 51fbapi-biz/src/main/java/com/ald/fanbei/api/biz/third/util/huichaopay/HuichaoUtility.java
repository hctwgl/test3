package com.ald.fanbei.api.biz.third.util.huichaopay;

import com.ald.fanbei.api.biz.bo.thirdpay.ResulitCheck;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.pay.ThirdInterface;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfUserAmountProcessStatus;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfHuicaoOrderDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author honghzengpei 2017/10/25 13:45
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Component("huichaoUtility")
public class HuichaoUtility implements ThirdInterface {
    //测试用参数值
    static String merid = "yft2017102300002";
    static String noncestr="test2";
    static String key = "rof7L97aPmhNO6jILbGaMLadVG0x3xGp";
    //static int payResult = 0;
    static String notifyUrl="ceshi";//以接口文档为准
    static String quickUrl="http://jh.yizhibank.com/api/createQuickOrder";//以接口文档为准


    @Resource
    AfHuicaoOrderDao afHuicaoOrderDao;


    @Resource
    private AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;

    @Resource
    private AfRenewalDetailDao afRenewalDetailDao;

    @Resource
    private AfRenewalDetailService afRenewalDetailService;
    @Resource
    private AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private AfRepaymentDao afRepaymentDao;

    @Resource
    private AfBorrowBillService afBorrowBillService;

    @Resource
    private AfRepaymentService afRepaymentService;

    @Resource
    private AfUserAmountService afUserAmountService;


    public HashMap<String,String > createOrderZFB(String orderNo, String orderMoney, long userId, PayOrderSource payOrderSource){

        BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(orderMoney), BigDecimal.ZERO);
        if(repaymentAmount.compareTo(BigDecimal.ONE)<0){
            throw new FanbeiException(FanbeiExceptionCode.BACK_MONEY_CHECK);
        }

        int checkType = 2;
        if(payOrderSource.getCode().equals(PayOrderSource.RENEWAL_PAY.getCode())){
            checkType =1;
        }
        else if(payOrderSource.getCode().equals(PayOrderSource.REPAYMENTCASH.getCode())){
            checkType =0;
        }

        if(!checkCanNext(userId,payOrderSource.getCode(),checkType)) {
            throw new FanbeiException(FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
        }





        String thirdNo =String.valueOf( new Date().getTime());
        HashMap<String,String> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String sdate=sdf.format(new Date());
        String stringA=step1(thirdNo,orderMoney,sdate);
        //String stringA=step1(thirdNo,"1",sdate);
        
        String stringsignTemp=stringA+"&key="+key;
        String sign= getMD5(stringsignTemp);
        String stringB = stringA+"&sign=" +sign;
//        String ret= "http://jh.yizhibank.com/api/createOrder?"+stringB;
        String ret = "https://alipay.3c-buy.com/api/createOrder?"+stringB;
        map.put("urlscheme",ret);

//        String firstStr = getFirstString(thirdNo,orderMoney,String.valueOf( new Date().getTime()));
//        String stringA = firstStr + "&key="+key;
//        String sign= getMD5(stringA);
//        String lastUrl = "http://jh.yizhibank.com/api/createOrder?"+firstStr+"&sign="+sign;

        AfHuicaoOrderDo afHuicaoOrderDo = new AfHuicaoOrderDo();
        afHuicaoOrderDo.setOrderNo(orderNo);
        afHuicaoOrderDo.setPayType(payOrderSource.getCode());
        afHuicaoOrderDo.setStatus(0);
        afHuicaoOrderDo.setUserId(userId);
        afHuicaoOrderDo.setWxZfb("zfb");
        afHuicaoOrderDo.setThirdOrderNo(thirdNo);
        afHuicaoOrderDao.addHuicaoOrder(afHuicaoOrderDo);

        map.put("orderId",orderNo);
        return  map;
    }


    private String getFirstString(String orderNo,String orderMoney,String orderTime){
        String ret ="merchantOutOrderNo="+orderNo;
        ret +="&merid="+merid;
        ret +="&noncestr="+noncestr;
        ret +="&notifyUrl="+notifyUrl;
        ret +="&orderMoney="+orderMoney;
        ret +="&orderTime="+orderTime;
        return ret;
    }

    protected static final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");

    private  String step1(String orderNo,String orderMoney,String orderTime)  {
        String baseUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);

        Map<String,String> paraMap = new HashMap<String,String>();
        paraMap.put("merchantOutOrderNo",orderNo);
        paraMap.put("merid", merid);
        paraMap.put("noncestr", noncestr);
        paraMap.put("notifyUrl",baseUrl+"/third/ups/huicaoback");

        paraMap.put("orderMoney",orderMoney);
        paraMap.put("orderTime", orderTime);

        thirdLog.info("start huicao order:"+ JSON.toJSONString(paraMap));

        String stringA = formatUrlMap(paraMap, true, false);
        try {
            stringA = URLDecoder.decode(stringA, "utf-8");
        }
        catch (Exception e){

        }
        return stringA;
    }


    /**
     *
     * @param msg
     * @return
     */
    public String getSign(String msg){
        msg +="&key="+key;
        return getMD5(msg);
    }




    /**
     *
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap   要排序的Map对象
     * @param urlEncode   是否需要URLENCODE
     * @param keyToLower    是否需要将Key转换为全小写
     *            true:key转化成小写，false:不转化
     * @return
     */
    public  String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower)
    {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try
        {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>()
            {

                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
                {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds)
            {
                if (StringUtils.isNotBlank(item.getKey()))
                {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode)
                    {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower)
                    {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else
                    {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false)
            {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e)
        {
            return null;
        }
        return buff;
    }

    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     */
    public String getMD5(String str)  {
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    public int getBizType(String payType){
        int checkType = 2;
        if(payType.equals(PayOrderSource.RENEWAL_PAY.getCode())){
            checkType =1;
        }
        else if(payType.equals(PayOrderSource.REPAYMENTCASH.getCode())){
            checkType =0;
        }

        return checkType;
    }



    public Map<String,String> getOrderStatus(String orderNo){
        Map<String,String> ret = new HashMap<String,String>();

        AfHuicaoOrderDo afHuicaoOrderDo = afHuicaoOrderDao.getOrderByOrderNo(orderNo);
        if(afHuicaoOrderDo == null){
            return null;
        }

        ret.put("type",afHuicaoOrderDo.getPayType());
        String sendStatus = HuiCaoOrderStatus.PROCESSING.getCode();
        if(afHuicaoOrderDo.getStatus().intValue() == 0 || afHuicaoOrderDo.getStatus().intValue() == 3){
            Map<String, String> result  = getHuiCaoOrder(afHuicaoOrderDo.getThirdOrderNo());

            if(result.containsKey("code")){
                afHuicaoOrderDao.updateHuicaoOrderStatusLock(5,afHuicaoOrderDo.getId(),afHuicaoOrderDo.getGmtModified());
                AfRepaymentDo afRepaymentDo = afRepaymentDao.getRepaymentByPayTradeNo(afHuicaoOrderDo.getOrderNo());
                if(afRepaymentDo != null){
                    afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.FAIL,afRepaymentDo);
                }
                return null;
            }
            else{

                String _payResult = String .valueOf( result.get("payResult"));
                if(_payResult.equals("0")){
                    if(DateUtil.addMins( afHuicaoOrderDo.getGmtCreate(),10).compareTo(new Date())>0) {
                        //处理中
                    }else{
                        sendStatus = HuiCaoOrderStatus.FAIL.getCode();
                    }
                }
                else{
                    sendStatus = HuiCaoOrderStatus.SUCCESS.getCode();
                }

                //要获取处理中状态
                proessUpdate(afHuicaoOrderDo,sendStatus,getBizType(afHuicaoOrderDo.getPayType()));
            }



//            String _payResult = String .valueOf( result.get("payResult"));
//
//            proessUpdate(afHuicaoOrderDo,_payResult,getBizType(afHuicaoOrderDo.getPayType()));


            if (sendStatus.equals(HuiCaoOrderStatus.SUCCESS.getCode())){
                ret.put("status","Y");
                return ret;
            }
            else {
                ret.put("status","P");
                return ret;
            }
            //ret.put("status","N");
            //return ret;
        }
        int status = afHuicaoOrderDo.getStatus();
        if(status ==1){
            ret.put("status","Y");
            return ret;
        }
        ret.put("status","N");
        return ret;
    }




    /**
     * 支付成功回调check
     * @param tradeNo
     * @return
     */
    public ResulitCheck<Boolean> checkSuccess(String tradeNo){
        ResulitCheck<Boolean> ret = new ResulitCheck<Boolean>();

        AfHuicaoOrderDo afHuicaoOrderDo = afHuicaoOrderDao.getOrderByOrderNo(tradeNo);
        if(afHuicaoOrderDo !=null){
            ret.setResulit(true);
            if(afHuicaoOrderDo.getStatus().intValue() == 1){
                ret.setSuccess(true);
            }
            else{
                afHuicaoOrderDao.updateOrderStatus(afHuicaoOrderDo.getId(),1);
                ret.setSuccess(false);
            }
        }
        else{
            ret.setResulit(false);
            ret.setSuccess(false);
        }
        return  ret;
    }


    /**
     * 支付失败回调check
     * @param tradeNo
     * @return
     */
    public ResulitCheck<Boolean> checkFail(String tradeNo){
        ResulitCheck<Boolean> ret = new ResulitCheck<Boolean>();

        AfHuicaoOrderDo afHuicaoOrderDo = afHuicaoOrderDao.getOrderByOrderNo(tradeNo);
        if(afHuicaoOrderDo !=null){
            ret.setResulit(true);
            if(afHuicaoOrderDo.getStatus().intValue() == 1){
                ret.setSuccess(true);
            }
            else{
                afHuicaoOrderDao.updateOrderStatus(afHuicaoOrderDo.getId(),2);
                ret.setSuccess(false);
            }
        }
        else{
            ret.setResulit(false);
            ret.setSuccess(false);
        }
        return  ret;
    }







    public Map<String, String> getHuiCaoOrder(String thirdOrderNo){
        String baseUrl ="http://jh.yizhibank.com/api/queryOrder";
        Map<String,String> params = new HashMap<String,String>();
        params.put("merchantOutOrderNo",thirdOrderNo);
        params.put("merid ",merid );
        params.put("noncestr",thirdOrderNo);
        String stringA = "merchantOutOrderNo="+thirdOrderNo+"&merid="+merid+"&noncestr="+thirdOrderNo;
        String signA = getMD5(stringA +"&key="+key);
        params.put("sign",signA);

        String ret = HttpUtil.post(baseUrl+"?"+stringA+"&sign="+signA,new HashMap<String,String>());
        JSONObject myJson = JSONObject.parseObject(ret);
        Map retMap = myJson;
        return  retMap;
    }


    public boolean checkCanNext(long userId,String type,int bizType){
        List<AfHuicaoOrderDo> list =afHuicaoOrderDao.getUnFinishOrder(userId,type);
        if(list ==null || list.size() ==0)return true;
        boolean ret =true;
        for(AfHuicaoOrderDo afHuicaoOrderDo:list){
            Map<String, String> result  = getHuiCaoOrder(afHuicaoOrderDo.getThirdOrderNo());
            if(result.containsKey("code")){
                afHuicaoOrderDao.updateHuicaoOrderStatusLock(5,afHuicaoOrderDo.getId(),afHuicaoOrderDo.getGmtModified());
                AfRepaymentDo afRepaymentDo = afRepaymentDao.getRepaymentByPayTradeNo(afHuicaoOrderDo.getOrderNo());
                if(afRepaymentDo != null){
                    afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.FAIL,afRepaymentDo);
                }
                continue;
            }
            else{
                String sendStatus = HuiCaoOrderStatus.PROCESSING.getCode();
                String _payResult = String .valueOf( result.get("payResult"));
                if(_payResult.equals("0")){
                    if(DateUtil.addMins( afHuicaoOrderDo.getGmtCreate(),10).compareTo(new Date())>0) {
                        //处理中
                        ret = false;
                    }else{
                        sendStatus = HuiCaoOrderStatus.FAIL.getCode();
                    }
                }
                else{
                    sendStatus = HuiCaoOrderStatus.SUCCESS.getCode();
                }

                //要获取处理中状态
                proessUpdate(afHuicaoOrderDo,sendStatus,bizType);
            }
        }
        return ret;
    }


    /**
     * 进度更新
     * @param afYibaoOrderDo
     * @param lstatus
     * @param type
     */
    private  void proessUpdate(final AfHuicaoOrderDo  afHuicaoOrderDo ,final String lsstatus,final int type){
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    switch (type) {
                        case 0:
                            type0Proess(afHuicaoOrderDo.getId(),afHuicaoOrderDo.getGmtModified(),afHuicaoOrderDo.getOrderNo(),afHuicaoOrderDo.getThirdOrderNo(), lsstatus,afHuicaoOrderDo.getStatus().intValue());
                            break;
                        case 1:
                            type1Proess(afHuicaoOrderDo.getId(),afHuicaoOrderDo.getGmtModified(),afHuicaoOrderDo.getOrderNo(),afHuicaoOrderDo.getThirdOrderNo(), lsstatus,afHuicaoOrderDo.getStatus().intValue());
                            break;
                        case 2:
                            type2Proess(afHuicaoOrderDo.getId(),afHuicaoOrderDo.getGmtModified(),afHuicaoOrderDo.getOrderNo(),afHuicaoOrderDo.getThirdOrderNo(), lsstatus,afHuicaoOrderDo.getStatus().intValue());
                            break;
                    }
                }
                catch (Exception e){
                    status.setRollbackOnly();
                }
            }
        });
    }


    /**
     * 现金贷
     * @param id
     * @param updateTime
     * @param orderNo
     * @param thirdOrderNo
     * @param resultStatus
     * @param orderStatus
     */
    @Override
    public void type0Proess(long id, Date updateTime, String orderNo, String thirdOrderNo, String resultStatus, int orderStatus) {
        AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(orderNo);
        if(resultStatus.equals(HuiCaoOrderStatus.PROCESSING.getCode())){
            //处理中
            if(orderStatus == 3) {
                return;
            }
            int ret = afHuicaoOrderDao.updateHuicaoOrderStatusLock(3,id,updateTime);
            if(ret >0) {
                repayment.setStatus("P");
                afRepaymentBorrowCashDao.updateRepaymentBorrowCash(repayment);
            }
        }
        else if(resultStatus.equals(HuiCaoOrderStatus.SUCCESS.getCode())){
            afRepaymentBorrowCashService.dealRepaymentSucess(orderNo,thirdOrderNo,"");
            //成功
        }
        else{
            //关闭
            afRepaymentBorrowCashService.dealRepaymentFail(orderNo,thirdOrderNo,false,"",null,"");
        }
    }

    /**
     * 续期
     * @param id
     * @param updateTime
     * @param orderNo
     * @param thirdOrderNo
     * @param resultStatus
     * @param orderStatus
     */
    @Override
    public void type1Proess(long id, Date updateTime, String orderNo, String thirdOrderNo, String resultStatus, int orderStatus) {
        if(resultStatus.equals(HuiCaoOrderStatus.PROCESSING.getCode())){

            if(orderStatus == 3) {
                return;
            }
            //处理中
            int ret = afHuicaoOrderDao.updateHuicaoOrderStatusLock(3,id,updateTime);
            if(ret >0) {
                AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(orderNo);
                afRenewalDetailDo.setStatus("P");
                afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);
            }

        }
        else if(resultStatus.equals(HuiCaoOrderStatus.SUCCESS.getCode())){
            afRenewalDetailService.dealRenewalSucess(orderNo,thirdOrderNo);
            //成功
        }
        else{
            //关闭
            afRenewalDetailService.dealRenewalFail(orderNo,thirdOrderNo,"");
        }
    }

    /**
     * 分期
     * @param id
     * @param updateTime
     * @param orderNo
     * @param thirdOrderNo
     * @param resultStatus
     * @param orderStatus
     */
    @Override
    public void type2Proess(long id, Date updateTime, String orderNo, String thirdOrderNo, String resultStatus, int orderStatus) {
        if(resultStatus.equals(HuiCaoOrderStatus.PROCESSING.getCode())){
            //处理中
            if(orderStatus == 3) {
                return;
            }
            int ret = afHuicaoOrderDao.updateHuicaoOrderStatusLock(3,id,updateTime);
            if(ret >0) {
                AfRepaymentDo repayment = afRepaymentDao.getRepaymentByPayTradeNo(orderNo);
                repayment.setStatus("P");
                afRepaymentDao.updateRepayment("P",null,repayment.getRid());
                afBorrowBillService.updateBorrowBillStatusByIds(repayment.getBillIds(), BorrowBillStatus.DEALING.getCode(), repayment.getRid(),
                        repayment.getCouponAmount(), repayment.getJfbAmount(), repayment.getRebateAmount());
            }
        }
        else if(resultStatus.equals(HuiCaoOrderStatus.SUCCESS.getCode())){
            afRepaymentService.dealRepaymentSucess(orderNo, thirdOrderNo,false,null);
        }
        else{
            //关闭
            afRepaymentService.dealRepaymentFail(orderNo, thirdOrderNo,false,"");
        }
    }


    /**
     *
     */
    public void updateAllNotCheck(){
        List<AfHuicaoOrderDo> list = afHuicaoOrderDao.getHuicaoUnFinishOrderAll();
        for(AfHuicaoOrderDo afHuicaoOrderDo:list){
            try {
                Map<String, String> result = getHuiCaoOrder(afHuicaoOrderDo.getThirdOrderNo());
                String sendStatus = HuiCaoOrderStatus.PROCESSING.getCode();
                if (result.containsKey("code")) {
                    afHuicaoOrderDao.updateHuicaoOrderStatusLock(5, afHuicaoOrderDo.getId(), afHuicaoOrderDo.getGmtModified());
                    AfRepaymentDo afRepaymentDo = afRepaymentDao.getRepaymentByPayTradeNo(afHuicaoOrderDo.getOrderNo());
                    if(afRepaymentDo != null){
                        afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.FAIL,afRepaymentDo);
                    }
                } else {

                    String _payResult = String.valueOf(result.get("payResult"));
                    if (_payResult.equals("0")) {
                        if (DateUtil.addMins(afHuicaoOrderDo.getGmtCreate(), 10).compareTo(new Date()) > 0) {
                            //处理中
                        } else {
                            sendStatus = HuiCaoOrderStatus.FAIL.getCode();
                        }
                    } else {
                        sendStatus = HuiCaoOrderStatus.SUCCESS.getCode();
                    }

                    //要获取处理中状态
                    proessUpdate(afHuicaoOrderDo, sendStatus, getBizType(afHuicaoOrderDo.getPayType()));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}
