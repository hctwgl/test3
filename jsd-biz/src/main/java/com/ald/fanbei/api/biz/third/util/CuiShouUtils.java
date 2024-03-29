package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.dal.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RepaymentBo;
import com.ald.fanbei.api.biz.third.cuishou.CuiShouBackMoney;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


@Component("cuiShouUtils")
public class CuiShouUtils {
    protected static final Logger logger = LoggerFactory.getLogger("DSED_THIRD");

    private final String salt = "jsdcuishou";

    @Resource
    JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;

    @Resource
    JsdBorrowCashService jsdBorrowCashService;

    @Resource
    JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
    @Resource
    JsdNoticeRecordDao jsdNoticeRecordDao;
    @Resource
    JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    CollectionNoticeUtil collectionNoticeUtil;
    @Resource
    JsdCollectionBorrowService jsdCollectionBorrowService;
    @Resource
    JsdCollectionRepaymentService jsdCollectionRepaymentService;
    @Resource
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    XgxyUtil xgxyUtil;

    /**
     * 催收逾期还款
     *
     * @param request
     * @return
     */
    public String offlineRepaymentMoney(HttpServletRequest request) {
        try {
            long start = System.currentTimeMillis();
            String sign = request.getParameter("sign");
            String data = request.getParameter("data");
            byte[] pd = DigestUtil.digestString(data.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if (!sign1.equals(sign)) {
                logger.info("sign error!, requestSign = " + sign +", jsdSign = " + sign1);
                return JSONObject.toJSONString(new CuiShouBackMoney(201, "sign error"));
            }
            final JSONObject jsonObject = JSONObject.parseObject(data);
            CuiShouBackMoney result = loanBorrowCashMoney(jsonObject);
            logger.info("offlineRepaymentMoney end , result = " + JSON.toJSONString(result) +" ,TIMES = " + (System.currentTimeMillis() - start));
            return JSONObject.toJSONString(result);//同步反回接收成功
        } catch (Exception e) {
            logger.info("offlineRepaymentMoney error = " , e);
            CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney(500, "error");
            return JSON.toJSONString(cuiShouBackMoney);
        }
    }

    /**
     * dsed还款
     *
     * @param obj
     * @return
     */
    private CuiShouBackMoney loanBorrowCashMoney(JSONObject obj) {
        CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
        try {
            RepaymentBo repaymentBo = JSON.toJavaObject(obj, RepaymentBo.class);
            final String totalAmount = repaymentBo.getTotalAmount();
            final String repaymentNo = repaymentBo.getRepaymentNo();
            final String repayTime = repaymentBo.getRepayTime();
            final String orderNo = repaymentBo.getOrderNo();
            JSONArray detailsArray = obj.getJSONArray("details");
            String dataId = "";
            Long borrowId = 0l;
            Long userId = 0l;
            JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = new JsdBorrowLegalOrderCashDo();
            JsdBorrowCashDo jsdBorrowCashDo = new JsdBorrowCashDo();
            if(detailsArray == null || StringUtil.isEmpty(detailsArray.toJSONString())){
                cuiShouBackMoney.setCode(205);
                logger.info("param is null error orderNo =" + orderNo);
                return cuiShouBackMoney;
            }
            if(detailsArray != null && detailsArray.size()>0){
                dataId = String.valueOf(detailsArray.getJSONObject(0).get("dataId"));
                JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getById(Long.parseLong(dataId));
                if(jsdBorrowLegalOrderDo == null){
                    cuiShouBackMoney.setCode(205);
                    logger.info("param is null error orderNo =" + orderNo);
                    return cuiShouBackMoney;
                }else {
                    jsdBorrowLegalOrderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByOrderId(jsdBorrowLegalOrderDo.getRid());
                    borrowId = jsdBorrowLegalOrderDo.getBorrowId();
                    jsdBorrowCashDo = jsdBorrowCashService.getById(borrowId);
                    userId = jsdBorrowCashDo.getUserId();
                }
            }
            if(StringUtil.isBlank(totalAmount)){
                cuiShouBackMoney.setCode(203);
                logger.info("totalAmount is not exist orderNo =" + orderNo);
                return cuiShouBackMoney;
            }
            if (StringUtil.isAllNotEmpty(orderNo, repaymentNo)) {
                jsdBorrowCashRepaymentService.offlineRepay(jsdBorrowCashDo,jsdBorrowLegalOrderCashDo,totalAmount, repaymentNo, userId, JsdRepayType.COLLECTION,null, DateUtil.stringToDate(repayTime), orderNo,dataId,null);
            } else {
                cuiShouBackMoney.setCode(303);
                logger.info("orderNo and repaymentNo is error orderNo =" + orderNo);
                return cuiShouBackMoney;
            }
            cuiShouBackMoney.setCode(200);
            return cuiShouBackMoney;
        } catch (Exception e) {
            logger.info("offlineLoanRepaymentNotify error = " , e);
            cuiShouBackMoney.setCode(500);
            return cuiShouBackMoney;
        }
    }






    /**
     * 催收平账修改状态
     *
     * @param data
     * @return
     */
    public String collectUpdateStatus(String data,String sign) {
        long start = System.currentTimeMillis();
        try {
            if(StringUtil.isEmpty(data)){
                logger.info("data is null");
                return "false";
            }
            logger.info("offlineRepaymentMoney data = " + data +"  ,sign = " + sign);
            byte[] pd = DigestUtil.digestString(data.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if (!sign1.equals(sign)) {
                logger.info("sign error!, requestSign = " + sign +", jsdSign = " + sign1);
                return "false";
            }
            JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderService.getById(Long.valueOf(data));
            JsdBorrowCashDo cashDo = jsdBorrowCashService.getById(orderDo.getBorrowId());
            JsdBorrowCashDo jsdBorrowCashDo = new JsdBorrowCashDo();
            jsdBorrowCashDo.setStatus(JsdBorrowCashStatus.FINISHED.name());
            jsdBorrowCashDo.setRid(orderDo.getBorrowId());
            int count = jsdBorrowCashService.updateById(jsdBorrowCashDo);
            if(count<1){
                logger.info("update jsdBorrowCash error!");
                return "false";
            }
            JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashDateBeforeToday(orderDo.getBorrowId());
            if(jsdBorrowLegalOrderCashDo != null){
                jsdBorrowLegalOrderCashDo.setStatus(JsdBorrowCashStatus.FINISHED.name());
                int orderCount = jsdBorrowLegalOrderCashService.updateById(jsdBorrowLegalOrderCashDo);
                if(orderCount<1){
                    logger.info("update jsdBorrowLegalOrderCash error!");
                    return "false";
                }
            }
            //催收平账推送西瓜
            HashMap<String, String> map = new HashMap<>();
            map.put("status",YesNoStatus.YES.getCode());
            map.put("isFinish",YesNoStatus.YES.getCode());
            map.put("borrowNo",cashDo.getTradeNoXgxy());
            map.put("period","all");
            map.put("amount",String.valueOf(BigDecimal.ZERO));
            map.put("type",JsdRepayType.COLLECTION.name());
            map.put("realRepayTime",new Date().getTime()+"");
            JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
            noticeRecordDo.setUserId(orderDo.getUserId());
            noticeRecordDo.setType(JsdNoticeType.COLLECT_RECONCILIATION.code);
            noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
            noticeRecordDo.setParams(JSON.toJSONString(map));
            jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
            if (xgxyUtil.repayNoticeRequest(map)) {
                noticeRecordDo.setRid(noticeRecordDo.getRid());
                noticeRecordDo.setGmtModified(new Date());
                jsdNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
            }
            logger.info("collectUpdateStatus end , result = success , TIMES = " + (System.currentTimeMillis() - start));
            return "success";
        } catch (Exception e) {
            logger.info("collectUpdateStatus error = " , e);
            return "false";
        }
    }


    public void  collectionPush(JsdBorrowCashDo borrowCashDo,JsdBorrowLegalOrderCashDo orderCashDo,JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo){
        JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.COLLECT.name(),ResourceSecType.COLLECT_PRODUCT.name());
        List<JsdBorrowLegalOrderDo> orderList =  jsdBorrowLegalOrderService.getBorrowOrdersByBorrowId(borrowCashDo.getRid());
        Map<String, String> buildData = new HashMap<String, String>();
        int count = 0;
        for (int i=0;i<orderList.size();i++){
            if(StringUtil.equals(String.valueOf(orderList.get(i).getRid()),String.valueOf(jsdBorrowLegalOrderDo.getRid()))){
                count = i;
                break;
            }
        }

        if(count>0){
            List<JsdBorrowCashRenewalDo> renewalList =  jsdBorrowCashRenewalService.getJsdRenewalByBorrowIdAndStatus(jsdBorrowLegalOrderDo.getBorrowId());
            logger.info(" count = " + count + " ,renewalList = "+ renewalList);
            buildData.put("overdueDay",String.valueOf(renewalList.get(count-1).getOverdueDay()));
            buildData.put("borrowTime",DateUtil.formatDateTime(renewalList.get(count-1).getGmtCreate()));//借款时间
        }else {
            buildData.put("overdueDay",String.valueOf(DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(borrowCashDo.getGmtPlanRepayment()),DateUtil.formatDateToYYYYMMdd(new Date()))));//逾期天数
            buildData.put("borrowTime",DateUtil.formatDateTime(borrowCashDo.getGmtCreate()));//借款时间
        }
        List<Map<String,String>>  data = new ArrayList<>();
        //--------------------start  催收上报接口需要参数---------------------------
        Long borrowId = borrowCashDo.getRid();
        //搭售商品信息
        JsdBorrowLegalOrderDo jsdBorrowLegalOrder = jsdBorrowLegalOrderDao.getLastValidOrderByBorrowId(borrowId);
        JsdUserDo userDo= jsdUserService.getById(jsdBorrowLegalOrder.getUserId());
        Map<String,String>  param = new HashMap<>();
        param.put("borrowNo",borrowCashDo.getTradeNoXgxy());
        HashMap<String,String> map = xgxyUtil.borrowNoticeRequest(param);
        if(jsdBorrowLegalOrder != null){
            buildData.put("goodsName",jsdBorrowLegalOrder.getGoodsName());//商品名称
            buildData.put("goodsPrice",String.valueOf(jsdBorrowLegalOrder.getPriceAmount()));//商品价格
            buildData.put("orderStatus",map.get("orderStatus"));//订单状态
            buildData.put("expressNo",map.get("shipperNumber"));//快递单号
            buildData.put("expressCompany",map.get("shipperName"));//物流公司
            buildData.put("consigneeName",map.get("consignee"));//收货人姓名
            buildData.put("consigneePhone",map.get("mobile"));//收货人手机号码
            buildData.put("consigneeAddress",map.get("fullAddress"));//收货地址
            buildData.put("deliveryTime",map.get("gmtSended"));//发货时间
            buildData.put("gmtConfirmReceived",map.get("gmtReceived"));//确定收货时间
            buildData.put("logisticsInfo",map.get("traces"));//物流信息
            buildData.put("idNumberAddress",map.get("idNumberAddress")==null?userDo.getAddress():map.get("idNumberAddress").toString());//户籍地址
            buildData.put("company",map.get("company")==null?"":map.get("company").toString());//公司单位
            buildData.put("job",map.get("job")==null?"":map.get("job").toString());//job
            buildData.put("marriageState",map.get("marriageState")==null?"":map.get("marriageState").toString());//N未婚Y已婚P订婚
            buildData.put("companyTelephone",map.get("companyTelephone")==null?"":map.get("companyTelephone").toString());//单位电话
            buildData.put("salary",map.get("salary")==null?"":map.get("salary").toString());//税前收入
            buildData.put("channelName",map.get("channelName")==null?"":map.get("channelName").toString());//渠道名称
            buildData.put("faceUrl",map.get("faceUrl")==null?"":map.get("faceUrl").toString());//人脸识别图片
            buildData.put("idBehindUrl",map.get("idBehindUrl")==null?"":map.get("idBehindUrl").toString());//身份证反面照片
            buildData.put("idFrontUrl",map.get("idFrontUrl")==null?"":map.get("idFrontUrl").toString());//身份证正面照片
            buildData.put("borrowAddress",map.get("borrowAddress")==null?"":String.valueOf(map.get("borrowAddress")));//借款详细地址
            buildData.put("latitude",map.get("latitude")==null?"":String.valueOf(map.get("latitude")));//借款纬度
            buildData.put("longitude",map.get("longitude")==null?"":String.valueOf(map.get("longitude")));//借款经度
        }
        //用户信息

        if(userDo != null){
            buildData.put("userId",String.valueOf(userDo.getRid()));//userId
            buildData.put("realName",userDo.getRealName());//姓名
            buildData.put("userName",userDo.getMobile());//账号
            buildData.put("idNumber",userDo.getIdNumber());//身份证号码
            buildData.put("phoneNumber",userDo.getMobile());//电话号码
            buildData.put("address",userDo.getAddress());//户籍地址
            String gender = userDo.getGender();
            if(StringUtil.equals(gender, GenderType.M.getCode())){
                gender = GenderType.M.getName();
            }else if(StringUtil.equals(gender,GenderType.F.getCode())){
                gender = GenderType.F.getName();
            }else {
                gender = GenderType.U.getName();
            }
            buildData.put("gender",gender);//性别(非必填)
            buildData.put("birthday",userDo.getBirthday());//生日(非必填)
        }
        BigDecimal lateFee = BigDecimal.ZERO;//滞纳金
        //续期信息
        List<Map<String, String>> arrayList = new ArrayList<>();
        List<JsdBorrowCashRenewalDo> list = jsdBorrowCashRenewalService.getJsdRenewalByBorrowIdAndStatus(borrowCashDo.getRid());
        for (JsdBorrowCashRenewalDo renewalDo : list){
            Map<String, String> renewalData = new HashMap<String, String>();
            renewalData.put("tradeNo",renewalDo.getTradeNo());//续期编号
            renewalData.put("renewalAmount",String.valueOf(renewalDo.getRenewalAmount()));//续期本金
            renewalData.put("renewalRepayAmount",String.valueOf(renewalDo.getCapital()));//本期已还本金（
            renewalData.put("priorFee",String.valueOf(BigDecimalUtil.add(renewalDo.getPriorOverdue(),renewalDo.getPriorInterest(),renewalDo.getPriorPoundage())));//上期费用
            renewalData.put("renewalPoundage",String.valueOf(renewalDo.getNextPoundage()));//续期手续费
            renewalData.put("renewalStatus",renewalDo.getStatus());//状态
            renewalData.put("renewalTime",DateUtil.formatDateTime(renewalDo.getGmtCreate()));//续期时间
            lateFee = BigDecimalUtil.add(lateFee,renewalDo.getPriorOverdue());
            arrayList.add(renewalData);
        }
        buildData.put("renewalData",JSON.toJSONString(arrayList));
        //案件信息
        BigDecimal repayAmount = BigDecimal.ZERO;
        BigDecimal currentAmount = BigDecimal.ZERO;//应还本金
        BigDecimal overdueAmount = BigDecimal.ZERO;//逾期金额
        BigDecimal residueAmount = BigDecimal.ZERO;//剩余应还
        //应还本金
        currentAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumRepaidInterest(), borrowCashDo.getSumRepaidPoundage(), borrowCashDo.getSumRepaidOverdue()).subtract(borrowCashDo.getRepayAmount());
        //催收金额
        BigDecimal collectAmount = BigDecimalUtil.add(borrowCashDo.getAmount(),borrowCashDo.getOverdueAmount(),borrowCashDo.getInterestAmount(),borrowCashDo.getPoundageAmount(),borrowCashDo.getSumRepaidInterest(),borrowCashDo.getSumRepaidOverdue(),borrowCashDo.getSumRepaidPoundage());
        //借款费用
        BigDecimal borrowCash = BigDecimalUtil.add(borrowCashDo.getInterestAmount(),borrowCashDo.getPoundageAmount(),borrowCashDo.getSumRepaidPoundage(),borrowCashDo.getSumRepaidInterest());
        //逾期金额
        overdueAmount = borrowCashDo.getOverdueAmount();
        //还款金额
        repayAmount = borrowCashDo.getRepayAmount();
        //借款金额
        BigDecimal borrowAmount = borrowCashDo.getAmount();
        //滞纳金
         lateFee = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(),borrowCashDo.getSumRepaidOverdue());
        if(orderCashDo != null){
            //应还本金
            currentAmount = BigDecimalUtil.add(currentAmount, orderCashDo.getAmount(), orderCashDo.getSumRepaidInterest(), orderCashDo.getSumRepaidPoundage(), orderCashDo.getSumRepaidOverdue()).subtract(orderCashDo.getRepaidAmount());
            //催收金额
            collectAmount = BigDecimalUtil.add(collectAmount,orderCashDo.getAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidOverdue(),orderCashDo.getSumRepaidPoundage(),orderCashDo.getInterestAmount(),orderCashDo.getPoundageAmount(),orderCashDo.getOverdueAmount());
            //借款费用
            borrowCash = BigDecimalUtil.add(borrowCash,orderCashDo.getPoundageAmount(),orderCashDo.getInterestAmount(),orderCashDo.getSumRepaidPoundage(),orderCashDo.getSumRepaidInterest());
            //逾期金额
            overdueAmount = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(), orderCashDo.getOverdueAmount());
            //还款金额
            repayAmount = borrowCashDo.getRepayAmount().add(orderCashDo.getRepaidAmount());
            //借款金额
            borrowAmount = borrowAmount.add(orderCashDo.getAmount());
            //滞纳金
            lateFee = BigDecimalUtil.add(lateFee,orderCashDo.getOverdueAmount(),orderCashDo.getSumRepaidOverdue());
        }
        buildData.put("lateFee", String.valueOf(lateFee));
        buildData.put("productId",resourceDo.getValue2());//产品id
        buildData.put("caseName",resourceDo.getValue()+"_"+borrowCashDo.getType());//案件名称
        buildData.put("caseType",resourceDo.getValue1());//案件类型
        buildData.put("collectAmount",String.valueOf(collectAmount));//催收金额
        buildData.put("repaymentAmount",String.valueOf(repayAmount));//累计还款金额
        if(currentAmount.compareTo(BigDecimal.ZERO) < 0){
            currentAmount = BigDecimal.ZERO;
        }
        if((residueAmount = jsdBorrowCashService.calcuUnrepayAmount(borrowCashDo, orderCashDo)).compareTo(BigDecimal.ZERO) < 0){
            residueAmount = BigDecimal.ZERO;
        }
        buildData.put("residueAmount",String.valueOf(residueAmount));//剩余应还
        buildData.put("currentAmount",String.valueOf(currentAmount));//委案未还金额
        buildData.put("dataId",String.valueOf(jsdBorrowLegalOrderDo.getRid()));//源数据id
        buildData.put("planRepaymenTime",DateUtil.formatDateTime(borrowCashDo.getGmtPlanRepayment()));//计划还款时间
        buildData.put("overdueAmount",String.valueOf(overdueAmount));//逾期金额
        //借款详情
        buildData.put("borrowNo",borrowCashDo.getBorrowNo());//借款编号
        buildData.put("borrowStatus",borrowCashDo.getStatus());//借款状态
        buildData.put("borrowCycle",borrowCashDo.getType());//借款周期
        buildData.put("cardNumber",borrowCashDo.getCardNumber());//收款账号
        buildData.put("borrowAddress",map.get("borrowAddress"));//借款地址
        buildData.put("longitude",map.get("longitude"));//借款经度
        buildData.put("latitude",map.get("latitude"));//借款纬度
        buildData.put("borrowAmount",String.valueOf(borrowAmount));//借款金额(委案金额)
        buildData.put("accountAmount",String.valueOf(borrowCashDo.getArrivalAmount()));//到账金额
        buildData.put("borrowCash",String.valueOf(borrowCash));//借款费用(手续费加利息)
        buildData.put("appName","jsd");//借款app
        buildData.put("contractPdfUrl","");
        buildData.put("payTime",DateUtil.formatDateTime(borrowCashDo.getGmtArrival()));//打款时间

        //--------------------end  催收上报接口需要参数---------------------------
        data.add(buildData);
        collectionNoticeUtil.noticeCollectOverdue(data);
    }


    /**
     * 催收上报接口
     *
     * @param data
     * @return
     */
    public String collectImport(String data) {
        long start = System.currentTimeMillis();
        try {
            if(StringUtil.isEmpty(data)){
                logger.error("data is null");
                return "false";
            }
            //上报
            JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByOrderId(Long.parseLong(data));
            JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getById(Long.parseLong(data));
            JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashService.getById(jsdBorrowLegalOrderDo.getBorrowId());
            collectionPush(jsdBorrowCashDo,jsdBorrowLegalOrderCashDo,jsdBorrowLegalOrderDo);
            logger.info("collectImport end  success !  ,TIMES = " + (System.currentTimeMillis() - start));
            return "success";
        } catch (Exception e) {
            logger.error("collectImport error = " + e.getMessage(), e);
            return "false";
        }
    }


    /**
     * 催收更新数据
     *
     * @param data
     * @return
     */
    public String collectData(String data) {
        long start = System.currentTimeMillis();
        HashMap<String,String> map = new HashMap<>();
        try {
            if(StringUtil.isEmpty(data)){
                map.put("code","500");
                map.put("info","");
                logger.error("data is null");
                return JSON.toJSONString(map);
            }
            List<HashMap<String,String>> list = new ArrayList<>();
            String arr[] =  data.split(",");
            for(int i=0;i<arr.length;i++){
                JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getById(Long.parseLong(arr[i]));
                JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByOrderId(Long.parseLong(arr[i]));
                JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getById(jsdBorrowLegalOrderDo.getBorrowId());
                List<JsdBorrowLegalOrderDo> orderList =  jsdBorrowLegalOrderService.getBorrowOrdersByBorrowId(borrowCashDo.getRid());
                HashMap<String, String> buildData = new HashMap<String, String>();
                int count = 0;
                for (int x=0;x<orderList.size();x++){
                    if(StringUtil.equals(String.valueOf(orderList.get(x).getRid()),String.valueOf(jsdBorrowLegalOrderDo.getRid()))){
                        count = x;
                        break;
                    }
                }
                if(count>0){
                    List<JsdBorrowCashRenewalDo> renewalList =  jsdBorrowCashRenewalService.getJsdRenewalByBorrowId(jsdBorrowLegalOrderDo.getBorrowId());
                    buildData.put("overdueDay",String.valueOf(renewalList.get(count-1).getOverdueDay()));
                }else {
                    buildData.put("overdueDay",String.valueOf(DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(borrowCashDo.getGmtPlanRepayment()),DateUtil.formatDateToYYYYMMdd(new Date()))));//逾期天数
                }
                //案件信息
                BigDecimal overdueAmount = BigDecimal.ZERO;//逾期金额
                //催收金额
                BigDecimal collectAmount = BigDecimalUtil.add(borrowCashDo.getAmount(),borrowCashDo.getOverdueAmount(),borrowCashDo.getInterestRate(),borrowCashDo.getPoundageAmount(),borrowCashDo.getSumRepaidInterest(),borrowCashDo.getSumRepaidOverdue(),borrowCashDo.getSumRepaidPoundage());
                //逾期金额
                overdueAmount = borrowCashDo.getOverdueAmount();
                //委案本金
                BigDecimal borrowAmount = borrowCashDo.getAmount();
                //还款金额
                BigDecimal repayAmount = borrowCashDo.getRepayAmount();
                if(orderCashDo != null){
                    //应还金额
                    collectAmount = BigDecimalUtil.add(collectAmount,orderCashDo.getAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidOverdue(),orderCashDo.getSumRepaidPoundage(),orderCashDo.getInterestAmount(),orderCashDo.getPoundageAmount(),orderCashDo.getOverdueAmount());
                    //逾期金额
                    overdueAmount = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(), orderCashDo.getOverdueAmount());
                    //委案本金
                    borrowAmount = borrowAmount.add(orderCashDo.getAmount());
                    //还款金额
                    repayAmount = borrowCashDo.getRepayAmount().add(orderCashDo.getRepaidAmount());
                }
                buildData.put("borrowAmount",String.valueOf(borrowAmount));//委案本金
                buildData.put("collectAmount",String.valueOf(collectAmount));//催收金额
                buildData.put("overdueAmount",String.valueOf(overdueAmount));//滞纳金
                BigDecimal residueAmount = BigDecimal.ZERO;
                if((residueAmount = jsdBorrowCashService.calcuUnrepayAmount(borrowCashDo, orderCashDo)).compareTo(BigDecimal.ZERO) < 0){
                    residueAmount = BigDecimal.ZERO;
                }
                buildData.put("residueAmount",String.valueOf(residueAmount));//剩余应还
                buildData.put("repayAmount",String.valueOf(repayAmount));//已还金额
                buildData.put("status",borrowCashDo.getStatus());//状态
                buildData.put("dataId",arr[i]);//状态
                list.add(buildData);
            }

            map.put("code","200");
            map.put("info",JSON.toJSONString(list));
            logger.info("collectData end  success ! , result = "+ JSON.toJSONString(map) +"TIMES = " + (System.currentTimeMillis() - start));
            return JSON.toJSONString(map);
        } catch (Exception e) {
            map.put("code","500");
            map.put("info","");
            logger.info("collectData error = " , e);
            return JSON.toJSONString(map);
        }
    }


    /**
     * 催收平账申请(plus)
     *
     * @param request
     * @return
     */
    public String collectReconciliate(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        try {
            String requester = request.getParameter("requester");//发起平账操作者
            String requestReason = request.getParameter("requestReason");//发起平账操作者
            String dataId = request.getParameter("dataId");//唯一交互数据
            String sign = request.getParameter("sign");
            byte[] pd = DigestUtil.digestString(dataId.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if (!sign1.equals(sign)) {
                logger.info("jsdSign = " + sign1 + "requestSign  = " + sign);
                return "false";
            }
            if(StringUtil.isEmpty(dataId)){
                logger.info("param is error");
                return "false";
            }
            JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getById(Long.parseLong(dataId));
            Long borrowId = jsdBorrowLegalOrderDo.getBorrowId();
            JsdCollectionBorrowDo jsdCollectionBorrowDo = jsdCollectionBorrowService.selectByBorrowId(borrowId);
            JsdCollectionBorrowDo borrowDo = new JsdCollectionBorrowDo();
            borrowDo.setBorrowId(borrowId);
            borrowDo.setRequester(requester);
            borrowDo.setRequestReason(requestReason);
            borrowDo.setReviewStatus(CommonReviewStatus.WAIT.name());
            borrowDo.setStatus(CollectionBorrowStatus.WAIT_FINISH.name());
            borrowDo.setRid(jsdCollectionBorrowDo.getRid());
            int count = jsdCollectionBorrowService.updateById(borrowDo);
            logger.info("count = " + count + "borrowDo  = " + borrowDo);
            if (count<1){
                logger.info("save is error");
                return "false";
            }
            logger.info("collectReconciliate end  success !  TIMES = " + (System.currentTimeMillis() - start));
            return "success";
        } catch (Exception e) {
            logger.info("collectReconciliate error = " , e);
            return "false";
        }
    }


    /**
     * 催收还款申请(plus)
     *
     * @param request
     * @return
     */
    public String collectRepay(HttpServletRequest request) {
        long start = System.currentTimeMillis();
        try {
            String requester = request.getParameter("requester");//发起还款操作者
            String repayCert = request.getParameter("repaymentPic");//图片
            String tradeNo = request.getParameter("tradeNo");//还款编号
            String realName = request.getParameter("realName");//用户真实姓名
            String gmtRepay = request.getParameter("repayTime");//还款时间
            String repayAmount = request.getParameter("repaymentAmount");//还款金额
            String repayWay = request.getParameter("repayWay");//还款方式
            String dataId = request.getParameter("dataId");//唯一交互数据
            String payInAccount = request.getParameter("payInAccount");//收款账户
            String payOutAccount = request.getParameter("payOutAccount");//打款账户
            String sign = request.getParameter("sign");
            byte[] pd = DigestUtil.digestString(tradeNo.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if (!sign1.equals(sign)){
                logger.info("jsdSign = " + sign1 + " ,requestSign = " +sign);
                return "false";
            }
            if(StringUtil.isEmpty(dataId)){
                logger.info("param is error");
                return "false";
            }
            JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getById(Long.parseLong(dataId));
            Long borrowId = jsdBorrowLegalOrderDo.getBorrowId();
            JsdCollectionRepaymentDo jsdCollectionRepaymentDo = jsdCollectionRepaymentService.getByRepayNo(tradeNo);
            if(jsdCollectionRepaymentDo != null){
                logger.info("jsdCollectionRepaymentDo is exist");
                return "false";
            }
            JsdUserDo jsdUserDo = jsdUserService.getById(jsdBorrowLegalOrderDo.getUserId());
            JsdCollectionRepaymentDo repaymentDo = new JsdCollectionRepaymentDo();
            repaymentDo.setBorrowId(borrowId);
            repaymentDo.setRequester(requester);
            repaymentDo.setGmtRepay(DateUtil.parseDateyyyyMMddHHmmss(gmtRepay));
            repaymentDo.setRepayWay(repayWay);
            repaymentDo.setRealName(realName);
            repaymentDo.setBorrowId(borrowId);
            repaymentDo.setRepayAmount(new BigDecimal(repayAmount));
            repaymentDo.setRepayCert(repayCert);
            repaymentDo.setPayInAccount(payInAccount);
            repaymentDo.setPayOutAccount(payOutAccount);
            repaymentDo.setTradeNo(tradeNo);
            repaymentDo.setReviewStatus(CommonReviewStatus.WAIT.name());
            repaymentDo.setUserId(jsdBorrowLegalOrderDo.getUserId());
            repaymentDo.setAccount(jsdUserDo.getMobile());
            int count = jsdCollectionRepaymentService.saveRecord(repaymentDo);
            if (count<1){
                logger.info("save is error");
                return "false";
            }
            logger.info("collectRepay end  success !  , TIMES = " + (System.currentTimeMillis() - start));
            return "success";
        } catch (Exception e) {
            logger.error("collectRepay error  " , e);
            return "false";
        }
    }



}
