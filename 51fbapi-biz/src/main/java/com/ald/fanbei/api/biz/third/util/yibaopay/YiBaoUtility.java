package com.ald.fanbei.api.biz.third.util.yibaopay;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.RET;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author honghzengpei 2017/9/7 13:53
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("yiBaoUtility")
public class YiBaoUtility {

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
    public  Map<String,String> createOrder(BigDecimal orderAmount,String orderId){

        String baseUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);

        String merchantNo ="";  //商户编号
        String redirectUrl ="http://www.baidu.com";         //同步回调地止
        String notifyUrl =baseUrl+"/third/ups/yibaoback";   //异步
        HashMap<String,String> goods = new HashMap<>();
        goods.put("goodsName","51返呗");
        goods.put("goodsDesc","");
        String goodsParamExt= JSON.toJSONString(goods);
        String csUr =baseUrl+"/third/ups/yibaoqsback";        //清算成功回调地止
        String fundProcessType ="REAL_TIME";

        Map<String,String> ret = new HashMap();
        ret.put("parentMerchantNo",merchantNo);
        ret.put("merchantNo",merchantNo);
        ret.put("orderId",orderId);

        if(baseUrl.contains("http://testapp.51fanbei.com")){
            ret.put("orderAmount","0.01");
        }
        else{
            ret.put("orderAmount",String.valueOf( orderAmount));
        }
        ret.put("redirectUrl",redirectUrl);
        ret.put("notifyUrl",notifyUrl);
        ret.put("goodsParamExt",goodsParamExt);
        ret.put("csUr",csUr);
        ret.put("fundProcessType",fundProcessType);
        ret.put("timeoutExpress","1"); //有效时间

        String uri = YeepayService.getUrl(YeepayService.TRADEORDER_URL);
        Map<String,String> yeeRet =  YeepayService.requestYOP(ret, uri, YeepayService.TRADEORDER);
        return  yeeRet;

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


    public boolean checkCanNext(long userId,int type){
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
            Map<String, String> result  = getYiBaoOrder(afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo());
            if(!result.get("code").equals("OPR00000")){
                continue;
            }
            String status = result.get("status");
            if(status.equals("PROCESSING")){
                //处理中
            }
            proessUpdate(afYibaoOrderDo,status,afYibaoOrderDo.getoType());
        }
    }





    public Map<String,String> getOrderByYiBao(String orderNo){
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
                            type0Proess(afYibaoOrderDo, lstatus);
                            break;
                        case 1:
                            type1Proess(afYibaoOrderDo, lstatus);
                            break;
                        case 2:
                            type2Proess(afYibaoOrderDo,lstatus);
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
     * @param afYibaoOrderDo
     * @param status
     */
    private void type0Proess(AfYibaoOrderDo afYibaoOrderDo ,String status){
        if(status.equals("PROCESSING")){
            //处理中
            if(afYibaoOrderDo.getStatus().intValue() == 3) {
                return;
            }
            int ret = afYibaoOrderDao.updateYiBaoOrderStatusLock(3,afYibaoOrderDo.getId(),afYibaoOrderDo.getGtmUpdate());
            if(ret >0) {
                AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(afYibaoOrderDo.getOrderNo());
                repayment.setStatus("P");
                afRepaymentBorrowCashDao.updateRepaymentBorrowCash(repayment);
            }
        }
        else if(status.equals("SUCCESS")){
            afRepaymentBorrowCashService.dealRepaymentSucess(afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo());
            //成功
        }
        else if(status.equals("REJECT")){

        }
        else{
            //关闭
            afRepaymentBorrowCashService.dealRepaymentFail(afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo());
        }
    }

    /**
     * 续期付款
     * @param afYibaoOrderDo
     * @param status
     */
    private void type1Proess(AfYibaoOrderDo afYibaoOrderDo,String status){
        if(status.equals("PROCESSING")){

            if(afYibaoOrderDo.getStatus().intValue() == 3) {
                return;
            }
            //处理中
            int ret = afYibaoOrderDao.updateYiBaoOrderStatusLock(3,afYibaoOrderDo.getId(),afYibaoOrderDo.getGtmUpdate());
            if(ret >0) {
                AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(afYibaoOrderDo.getOrderNo());
                afRenewalDetailDo.setStatus("P");
                afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);
            }

        }
        else if(status.equals("SUCCESS")){
            afRenewalDetailService.dealRenewalSucess(afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo());
            //成功
        }
        else if(status.equals("REJECT")){

        }
        else{
            //关闭
            afRenewalDetailService.dealRenewalFail(afYibaoOrderDo.getOrderNo(),afYibaoOrderDo.getYibaoNo());
        }
    }

    /**
     * 分期还款
     * @param afYibaoOrderDo
     * @param status
     */
    private void type2Proess(AfYibaoOrderDo afYibaoOrderDo,String status){
        if(status.equals("PROCESSING")){
            //处理中
            if(afYibaoOrderDo.getStatus().intValue() == 3) {
                return;
            }
            int ret = afYibaoOrderDao.updateYiBaoOrderStatusLock(3,afYibaoOrderDo.getId(),afYibaoOrderDo.getGtmUpdate());
            if(ret >0) {
                AfRepaymentDo repayment = afRepaymentDao.getRepaymentByPayTradeNo(afYibaoOrderDo.getOrderNo());
                repayment.setStatus("P");
                afRepaymentDao.updateRepayment("P",null,repayment.getRid());
                afBorrowBillService.updateBorrowBillStatusByIds(repayment.getBillIds(), BorrowBillStatus.DEALING.getCode(), repayment.getRid(),
                        repayment.getCouponAmount(), repayment.getJfbAmount(), repayment.getRebateAmount());
            }
        }
        else if(status.equals("SUCCESS")){
            afRepaymentService.dealRepaymentSucess(afYibaoOrderDo.getOrderNo(), afYibaoOrderDo.getYibaoNo());


        }
        else if(status.equals("REJECT")){

        }
        else{
            //关闭
            afRepaymentService.dealRepaymentFail(afYibaoOrderDo.getOrderNo(), afYibaoOrderDo.getYibaoNo());
        }
    }
}
