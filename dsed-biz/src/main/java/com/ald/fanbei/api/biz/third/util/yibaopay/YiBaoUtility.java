package com.ald.fanbei.api.biz.third.util.yibaopay;

import com.ald.fanbei.api.biz.bo.thirdpay.ResulitCheck;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.third.util.huichaopay.HuichaoUtility;
import com.ald.fanbei.api.biz.third.util.pay.ThirdInterface;
import com.ald.fanbei.api.biz.third.util.pay.ThirdPayUtility;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.RET;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author honghzengpei 2017/9/7 13:53
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("yiBaoUtility")
public class YiBaoUtility  implements ThirdInterface{

    @Resource
    private AfYibaoOrderDao afYibaoOrderDao;

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
    /**
     * 新增易宝订单
     * @return
     */
    public  Map<String,String> createOrder( BigDecimal orderAmount, String orderId, long userId, PayOrderSource payOrderSource){

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



        String baseUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);

        String merchantNo ="";  //商户编号
        String redirectUrl ="http://www.baidu.com";         //同步回调地止
        String notifyUrl =baseUrl+"/third/ups/yibaoback";   //异步
        HashMap<String,String> goods = new HashMap<>();
        goods.put("goodsName","爱上街");
        goods.put("goodsDesc","");
        String goodsParamExt= JSON.toJSONString(goods);
        String csUr =baseUrl+"/third/ups/yibaoqsback";        //清算成功回调地止
        String fundProcessType ="REAL_TIME";

        Map<String,String> ret = new HashMap();
        ret.put("parentMerchantNo",merchantNo);
        ret.put("merchantNo",merchantNo);
        ret.put("orderId",orderId);

//        if(baseUrl.contains("http://testapp.51fanbei.com")){
//            ret.put("orderAmount","0.01");
//        }
//        else{
            ret.put("orderAmount",String.valueOf( orderAmount));
//        }
        ret.put("redirectUrl",redirectUrl);
        ret.put("notifyUrl",notifyUrl);
        ret.put("goodsParamExt",goodsParamExt);
        ret.put("csUr",csUr);
        ret.put("fundProcessType",fundProcessType);
        ret.put("timeoutExpress","1"); //有效时间

        String uri = YeepayService.getUrl(YeepayService.TRADEORDER_URL);
        Map<String,String> yeeRet =  YeepayService.requestYOP(ret, uri, YeepayService.TRADEORDER);



        AfYibaoOrderDo afYibaoOrderDo = new AfYibaoOrderDo();
        afYibaoOrderDo.setOrderNo(orderId);
        afYibaoOrderDo.setPayType(payOrderSource.getCode());
        afYibaoOrderDo.setStatus(0);
        afYibaoOrderDo.setYibaoNo(yeeRet.get("uniqueOrderNo"));
        afYibaoOrderDo.setUserId(userId);
        afYibaoOrderDo.setoType(0);
        if(payOrderSource.getCode().equals(PayOrderSource.RENEWAL_PAY.getCode())){
            afYibaoOrderDo.setoType(1);
        }
        else if(payOrderSource.getCode().equals(PayOrderSource.REPAYMENT.getCode())){
            afYibaoOrderDo.setoType(2);
        }

        afYibaoOrderDao.addYibaoOrder(afYibaoOrderDo);
        yeeRet.put("getopenusrl",notifyUrl+"/oauth2/callback");
        return  yeeRet;

    }


    @Resource
    HuichaoUtility huichaoUtility;
    /**
     *获取收银台
     * @param token
     * @param userId
     * @return
     */
    public String getCashier(String token,long userId){
        Map<String,String> ret = new HashMap();
        ret.put("merchantNo",YeepayService.getMerchantNo());
        ret.put("token",token);
        ret.put("timestamp", String.valueOf(new Date().getTime()/1000));
        ret.put("directPayType","WECHAT");
        ret.put("userNo",String.valueOf(userId));
        ret.put("userType","PHONE");
        String uri = YeepayService.getUrl(YeepayService.CASHIER_URL);
        try {
            return YeepayService.getUrl("CASHIER", ret);
        }
        catch (Exception e){
            throw new FanbeiException(FanbeiExceptionCode.GET_CASHER_ERROR);
        }

//        String signkey = "merchantNo="+ret.get("merchantNo");
//
//        for (int i = 0; i < YeepayService.CASHIER.length; i ++) {
//            String key = YeepayService.CASHIER[i];
//            if(key.equals("merchantNo") ){
//                continue;
//            }
//            signkey = signkey +"&"+key +"=" ;
//            if(ret.get(key) != null){
//                signkey = signkey+ret.get(key);
//            }
//        }
//        //String singKey = huichaoUtility.formatUrlMap(mm,true,false);
//        String sign = huichaoUtility.getMD5(signkey);
//        ret.put("sign",sign);
//        String key = HttpUtil.post(uri,ret);
//
//        return uri +"?"+signkey+"&sign="+sign;
    }




    /**
     *查易宝订单
     * @return
     */
    public  Map<String,String> getYiBaoOrder(String orderId,String uniqueOrderNo){
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("uniqueOrderNo", uniqueOrderNo);
        String uri = YeepayService.getUrl(YeepayService.ORDERQUERY_URL);
        Map<String, String> result = new HashMap<>();
        result = YeepayService.requestYOP(params, uri, YeepayService.ORDERQUERY);
        return result;
    }


    public boolean checkCanNext(long userId,String bizType,int type){
        List<AfYibaoOrderDo> list =afYibaoOrderDao.getYiBaoUnFinishOrderByUserId(userId,type);
        if(list ==null || list.size() ==0)return true;
        boolean ret =true;
        for(AfYibaoOrderDo afYibaoOrderDo:list){
            Map<String, String> result  = getYiBaoOrder(afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo());
            if(!result.get("code").equals("OPR00000")){
                continue;
            }
            String status = result.get("status");
            if(status.equals("PROCESSING")){
                //处理中
                ret =false;
            }
            proessUpdate(afYibaoOrderDo,status,type);
        }
        return ret;
    }


    public void updateYiBaoAllNotCheck(){
        List<AfYibaoOrderDo> list = afYibaoOrderDao.getYiBaoUnFinishOrderAll();
        for(AfYibaoOrderDo afYibaoOrderDo:list){
            try {
                Map<String, String> result = getYiBaoOrder(afYibaoOrderDo.getOrderNo(), afYibaoOrderDo.getYibaoNo());
                if (!result.get("code").equals("OPR00000")) {
                    continue;
                }
                String status = result.get("status");
                if (status.equals("PROCESSING")) {
                    //处理中
                }
                proessUpdate(afYibaoOrderDo, status, afYibaoOrderDo.getoType());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }





    public Map<String,String> getOrderStatus(String orderNo){
        Map<String,String> ret = new HashMap<String,String>();

        AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(orderNo);
        if(afYibaoOrderDo == null){
            return null;
        }

        ret.put("type",afYibaoOrderDo.getPayType());
        if(afYibaoOrderDo.getStatus().intValue() == 0 || afYibaoOrderDo.getStatus().intValue() == 3){
            Map<String, String> result = getYiBaoOrder(orderNo,afYibaoOrderDo.getYibaoNo());
            if(!result.get("code").equals("OPR00000")){
                return ret;
            }

            String status = result.get("status");
            proessUpdate(afYibaoOrderDo,status,afYibaoOrderDo.getoType());
            if(status.equals("PROCESSING")){
                ret.put("status","P");
                return ret;
            }
            else if (status.equals("SUCCESS")){
                ret.put("status","Y");
                return ret;
            }
            ret.put("status","N");
            return ret;
        }
        int status = afYibaoOrderDo.getStatus();
        if(status ==1){
            ret.put("status","Y");
            return ret;
        }
        ret.put("status","N");
        return ret;
    }




    /**
     * 进度更新
     * @param afYibaoOrderDo
     * @param lstatus
     * @param type
     */
    private  void proessUpdate(final AfYibaoOrderDo  afYibaoOrderDo ,final String lstatus,final int type){
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    switch (type) {
                        case 0:
                            type0Proess(afYibaoOrderDo.getId(),afYibaoOrderDo.getGtmUpdate(),afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo(), lstatus,afYibaoOrderDo.getStatus().intValue());
                            break;
                        case 1:
                            type1Proess(afYibaoOrderDo.getId(),afYibaoOrderDo.getGtmUpdate(),afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo(), lstatus,afYibaoOrderDo.getStatus().intValue());
                            break;
                        case 2:
                            type2Proess(afYibaoOrderDo.getId(),afYibaoOrderDo.getGtmUpdate(),afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo(), lstatus,afYibaoOrderDo.getStatus().intValue());
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
     * 现金借还款
     * @param id         afYibaoOrderDo 主键
     * @param updateTime  afYibaoOrderDo 更新时间
     * @param orderNo      afYibaoOrderDo 订单ID   getOrderNo
     * @param thirdOrderNo afYibaoOrderDo yibao订单
     * @param resultStatus        易宝状态
     * @param orderStatus  现在状态
     */
    public void type0Proess(long id,Date updateTime,String orderNo,String thirdOrderNo,String resultStatus,int orderStatus){
//        AfYibaoOrderDo afYibaoOrderDo ,String status
        AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(orderNo);
        if(resultStatus.equals("PROCESSING")){
            //处理中
            if(orderStatus == 3) {
                return;
            }
            int ret = afYibaoOrderDao.updateYiBaoOrderStatusLock(3,id,updateTime);
            if(ret >0) {

                repayment.setStatus("P");
                afRepaymentBorrowCashDao.updateRepaymentBorrowCash(repayment);
            }
        }
        else if(resultStatus.equals("SUCCESS")){
            afRepaymentBorrowCashService.dealRepaymentSucess(orderNo,thirdOrderNo);
            //成功
        }
        else if(resultStatus.equals("REJECT")){

        }
        else{
            //关闭
            afRepaymentBorrowCashService.dealRepaymentFail(orderNo,thirdOrderNo,false,"",null);
        }
    }

    /**
     * 续期付款
     * @param afYibaoOrderDo
     * @param status
     */
    public void type1Proess(long id,Date updateTime,String orderNo,String thirdOrderNo,String resultStatus,int orderStatus){
        if(resultStatus.equals("PROCESSING")){

            if(orderStatus == 3) {
                return;
            }
            //处理中
            int ret = afYibaoOrderDao.updateYiBaoOrderStatusLock(3,id,updateTime);
            if(ret >0) {
                AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(orderNo);
                afRenewalDetailDo.setStatus("P");
                afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);
            }

        }
        else if(resultStatus.equals("SUCCESS")){
            afRenewalDetailService.dealRenewalSucess(orderNo,thirdOrderNo);
            //成功
        }
        else if(resultStatus.equals("REJECT")){

        }
        else{
            //关闭
            afRenewalDetailService.dealRenewalFail(orderNo,thirdOrderNo,"");
        }
    }

    /**
     * 分期还款
     * @param afYibaoOrderDo
     * @param status
     */
    public void type2Proess(long id,Date updateTime,String orderNo,String thirdOrderNo,String resultStatus,int orderStatus){
        if(resultStatus.equals("PROCESSING")){
            //处理中
            if(orderStatus == 3) {
                return;
            }
            int ret = afYibaoOrderDao.updateYiBaoOrderStatusLock(3,id,updateTime);
            if(ret >0) {
                AfRepaymentDo repayment = afRepaymentDao.getRepaymentByPayTradeNo(orderNo);
                repayment.setStatus("P");
                afRepaymentDao.updateRepayment("P",null,repayment.getRid());
                afBorrowBillService.updateBorrowBillStatusByIds(repayment.getBillIds(), BorrowBillStatus.DEALING.getCode(), repayment.getRid(),
                        repayment.getCouponAmount(), repayment.getJfbAmount(), repayment.getRebateAmount());
            }
        }
        else if(resultStatus.equals("SUCCESS")){
            afRepaymentService.dealRepaymentSucess(orderNo, thirdOrderNo,false,null);


        }
        else if(resultStatus.equals("REJECT")){

        }
        else{
            //关闭
            afRepaymentService.dealRepaymentFail(orderNo, thirdOrderNo,false,"");
        }
    }






    public ResulitCheck<Boolean> checkSuccess(String tradeNo){
        ResulitCheck<Boolean> ret = new ResulitCheck<Boolean>();

        AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(tradeNo);
        if(afYibaoOrderDo !=null){
            ret.setResulit(true);
            if(afYibaoOrderDo.getStatus().intValue() == 1){
               ret.setSuccess(true);
            }
            else{
                afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(),1);
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

        AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(tradeNo);
        if(afYibaoOrderDo !=null){
            ret.setResulit(true);
            if(afYibaoOrderDo.getStatus().intValue() == 1){
                ret.setSuccess(true);
            }
            else{
                afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(),2);
                ret.setSuccess(false);
            }
        }
        else{
            ret.setResulit(false);
            ret.setSuccess(false);
        }

        return  ret;
    }
}
