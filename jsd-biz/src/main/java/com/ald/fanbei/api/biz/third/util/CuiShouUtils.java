package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.bo.RepaymentBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.cuishou.CuiShouBackMoney;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.GenderType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;


@Component("cuiShouUtils")
public class CuiShouUtils {
    protected static final Logger thirdLog = LoggerFactory.getLogger("DSED_THIRD");

    protected static final Logger logger = LoggerFactory.getLogger("DSED_THIRD");

    private final String salt = "dsedcuishou";

    @Resource
    JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;

    @Resource
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;

    @Resource
    JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;

    @Resource
    JsdBorrowCashRenewalService jsdBorrowCashRenewalService;

    @Resource
    JsdUserService jsdUserService;

    @Resource
    CollectionSystemUtil collectionSystemUtil;

    @Resource
    XgxyUtil xgxyUtil;

    private static String token = "eyJhbGciOiJIUzI1NiIsImNvbXBhbnlJZCI6Nn0.eyJhdWQiOiI2IiwiaXNzIjoiQUxEIiwiaWF0IjoxNTM2NjMyODQxfQ.NPLQiwpOsS1FPnCaIal2X9AaRk3R_fRFkCFfbRbNvIQ";


    /**
     * 催收逾期还款
     *
     * @param request
     * @return
     */
    public String offlineRepaymentMoney(HttpServletRequest request) {
        try {
            String sign = request.getParameter("sign");
            String data = request.getParameter("data");
            logger.info("offlineRepaymentMoney data = " + data);
            byte[] pd = DigestUtil.digestString(data.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if (!sign1.equals(sign)) return JSONObject.toJSONString(new CuiShouBackMoney(201, "sign error"));
            final JSONObject jsonObject = JSONObject.parseObject(data);
            CuiShouBackMoney result = loanBorrowCashMoney(jsonObject);
            return JSONObject.toJSONString(result);//同步反回接收成功
        } catch (Exception e) {
            thirdLog.error("offlineRepaymentMoney error = " + e);
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
            final String type = repaymentBo.getType();
            final String repayTime = repaymentBo.getRepayTime();
            final String orderNo = repaymentBo.getOrderNo();
            Date time = DateUtil.stringToDate(repayTime);
            JSONArray detailsArray = obj.getJSONArray("details");
            String dataId = "";
            Long borrowId = 0l;
            Long userId = 0l;
            JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = new JsdBorrowLegalOrderCashDo();
            JsdBorrowCashDo jsdBorrowCashDo = new JsdBorrowCashDo();
            if(detailsArray == null || StringUtil.isEmpty(detailsArray.toJSONString())){
                cuiShouBackMoney.setCode(205);
                thirdLog.error("param is null error orderNo =" + orderNo);
                return cuiShouBackMoney;
            }
            if(detailsArray != null && detailsArray.size()>0){
                dataId = String.valueOf(detailsArray.getJSONObject(0).get("dataId"));
                JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getById(Long.parseLong(dataId));
                if(jsdBorrowLegalOrderDo == null){
                    cuiShouBackMoney.setCode(205);
                    thirdLog.error("param is null error orderNo =" + orderNo);
                    return cuiShouBackMoney;
                }else {
                    jsdBorrowLegalOrderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByOrderId(jsdBorrowLegalOrderDo.getRid());
                    borrowId = jsdBorrowLegalOrderDo.getBorrowId();
                    jsdBorrowCashDo = jsdBorrowCashService.getById(borrowId);
                    userId = jsdBorrowCashDo.getUserId();
                }
            }
            if(jsdBorrowCashDo == null){
                cuiShouBackMoney.setCode(205);
                thirdLog.error("dsedLoanPeriodsDo is null error orderNo =" + orderNo);
                return cuiShouBackMoney;
            }
            if(DateUtil.afterDay(jsdBorrowCashDo.getGmtPlanRepayment(),time)){
                cuiShouBackMoney.setCode(203);
                thirdLog.error("time error loanNo =" + orderNo);
                return cuiShouBackMoney;
            }
            if(StringUtil.isBlank(totalAmount)){
                cuiShouBackMoney.setCode(203);
                thirdLog.error("totalAmount is not exist orderNo =" + orderNo);
                return cuiShouBackMoney;
            }
            if (StringUtil.isAllNotEmpty(orderNo, repaymentNo)) {
                jsdBorrowCashRepaymentService.offlineRepay(jsdBorrowCashDo,jsdBorrowLegalOrderCashDo,totalAmount, repaymentNo, userId, type, repayTime, orderNo,dataId);
            } else {
                cuiShouBackMoney.setCode(303);
                thirdLog.error("orderNo and repaymentNo is error orderNo =" + orderNo);
                return cuiShouBackMoney;
            }
            cuiShouBackMoney.setCode(200);
            return cuiShouBackMoney;
        } catch (Exception e) {
            thirdLog.error("offlineLoanRepaymentNotify error = " + e);
            cuiShouBackMoney.setCode(500);
            return cuiShouBackMoney;
        }
    }






    /**
     * 催收上报接口
     *
     * @param data
     * @return
     */
    public String collectImport(String data) {
        try {
            if(StringUtil.isEmpty(data)){
                thirdLog.error("data is null");
                return "false";
            }
            //上报
            JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = new JsdBorrowLegalOrderDo();
            JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByOrderId(Long.parseLong(data));
            if(jsdBorrowLegalOrderCashDo != null){
                jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getById(Long.parseLong(data));
            }
            JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashService.getById(jsdBorrowLegalOrderDo.getBorrowId());
            collectionPush(jsdBorrowCashDo,jsdBorrowLegalOrderCashDo,jsdBorrowLegalOrderDo);
            return "success";
        } catch (Exception e) {
            thirdLog.error("collectImport error = " + e);
            return "false";
        }
    }


    public void  collectionPush(JsdBorrowCashDo borrowCashDo,JsdBorrowLegalOrderCashDo orderCashDo,JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo){
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
            List<JsdBorrowCashRenewalDo> renewalList =  jsdBorrowCashRenewalService.getJsdRenewalByBorrowId(jsdBorrowLegalOrderDo.getBorrowId());
            buildData.put("overdueDay",String.valueOf(renewalList.get(count-1).getOverdueDay()));
            buildData.put("borrowTime",DateUtil.formatDateTime(renewalList.get(count).getGmtCreate()));//借款时间
        }else {
            buildData.put("overdueDay",String.valueOf(DateUtil.getNumberOfDatesBetween(borrowCashDo.getGmtPlanRepayment(),new Date())));//逾期天数
            buildData.put("borrowTime",DateUtil.formatDateTime(borrowCashDo.getGmtCreate()));//借款时间
        }
        List<Map<String,String>>  data = new ArrayList<>();
        //--------------------start  催收上报接口需要参数---------------------------
        Long borrowId = borrowCashDo.getRid();
        //搭售商品信息
        JsdBorrowLegalOrderDo jsdBorrowLegalOrder = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(borrowId);
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
        }
        //用户信息
        JsdUserDo userDo= jsdUserService.getById(jsdBorrowLegalOrder.getUserId());
        if(userDo != null){
            buildData.put("userId",String.valueOf(userDo.getRid()));//userId
            buildData.put("realName",userDo.getRealName());//姓名
            buildData.put("userName",userDo.getUserName());//账号
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
            buildData.put("workAddress","");//工作单位(非必填)
            buildData.put("workPost","");//工作岗位(非必填)
            buildData.put("income","");//税前收入(非必填)
            buildData.put("workTelephone","");//单位联系方式(非必填)
            buildData.put("marry","");//婚恋情况(非必填)
        }
        //续期信息
        List<Map<String, String>> arrayList = new ArrayList<>();
        List<JsdBorrowCashRenewalDo> list = jsdBorrowCashRenewalService.getJsdRenewalByBorrowId(borrowCashDo.getRid());
        for (JsdBorrowCashRenewalDo renewalDo : list){
            Map<String, String> renewalData = new HashMap<String, String>();
            renewalData.put("tradeNo",renewalDo.getTradeNo());//续期编号
            renewalData.put("renewalAmount",String.valueOf(renewalDo.getRenewalAmount()));//续期本金
            renewalData.put("renewalRepayAmount",String.valueOf(renewalDo.getCapital()));//本期已还本金（
            renewalData.put("priorFee",String.valueOf(BigDecimalUtil.add(renewalDo.getPriorOverdue(),renewalDo.getPriorInterest(),renewalDo.getPriorPoundage())));//上期费用
            renewalData.put("renewalPoundage",String.valueOf(renewalDo.getNextPoundage()));//续期手续费
            renewalData.put("renewalStatus",renewalDo.getStatus());//状态
            renewalData.put("renewalTime",DateUtil.formatDateTime(renewalDo.getGmtCreate()));//续期时间
            arrayList.add(renewalData);
        }
        buildData.put("renewalData",JSON.toJSONString(arrayList));
        //案件信息
        BigDecimal repayAmount = BigDecimal.ZERO;
        BigDecimal residueAmount = BigDecimal.ZERO;//应还金额
        BigDecimal currentAmount = BigDecimal.ZERO;//应还本金
        BigDecimal overdueAmount = BigDecimal.ZERO;//逾期金额
        //应还本金
        currentAmount = borrowCashDo.getAmount().subtract(borrowCashDo.getRepayPrinciple());
        currentAmount = BigDecimalUtil.add(currentAmount, orderCashDo.getAmount(), orderCashDo.getSumRepaidInterest(), orderCashDo.getSumRepaidPoundage(), orderCashDo.getSumRepaidInterest()).subtract(orderCashDo.getRepaidAmount());
        //应还金额
        residueAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getOverdueAmount(), borrowCashDo.getPoundageAmount(), borrowCashDo.getInterestAmount()).subtract(borrowCashDo.getRepayPrinciple());
        residueAmount = BigDecimalUtil.add(residueAmount, orderCashDo.getAmount(), orderCashDo.getOverdueAmount(), orderCashDo.getPoundageAmount(), orderCashDo.getInterestAmount()).subtract(orderCashDo.getRepaidAmount());
        //催收金额
        BigDecimal collectAmount = BigDecimalUtil.add(borrowCashDo.getAmount(),borrowCashDo.getOverdueAmount(),borrowCashDo.getInterestRate(),borrowCashDo.getPoundageAmount(),borrowCashDo.getSumRepaidInterest(),borrowCashDo.getSumRepaidOverdue(),borrowCashDo.getSumRepaidPoundage());
        collectAmount = BigDecimalUtil.add(collectAmount,orderCashDo.getAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidOverdue(),orderCashDo.getSumRepaidPoundage(),orderCashDo.getInterestAmount(),orderCashDo.getPoundageAmount(),orderCashDo.getOverdueAmount());
        //借款费用
        BigDecimal borrowCash = BigDecimalUtil.add(borrowCashDo.getInterestAmount(),borrowCashDo.getPoundageAmount(),borrowCashDo.getSumRepaidPoundage(),borrowCashDo.getSumRepaidInterest(),orderCashDo.getPoundageAmount(),orderCashDo.getInterestAmount(),orderCashDo.getSumRepaidPoundage(),orderCashDo.getSumRepaidInterest());
        //逾期金额
        overdueAmount = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(), orderCashDo.getOverdueAmount());
        repayAmount = borrowCashDo.getRepayAmount().add(orderCashDo.getRepaidAmount());

        buildData.put("productId","1");//产品id
        buildData.put("caseName","jsd");//案件名称
        buildData.put("caseType","jsd");//案件类型
        buildData.put("collectAmount",String.valueOf(collectAmount));//催收金额
        buildData.put("repaymentAmount",String.valueOf(repayAmount));//累计还款金额
        buildData.put("residueAmount",String.valueOf(residueAmount));//剩余应还
        buildData.put("currentAmount",String.valueOf(currentAmount));//委案未还金额
        buildData.put("dataId",String.valueOf(orderCashDo.getRid()));//源数据id
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
        buildData.put("borrowAmount",String.valueOf(BigDecimalUtil.add(borrowCashDo.getAmount(),orderCashDo.getAmount())));//借款金额(委案金额)
        buildData.put("accountAmount",String.valueOf(borrowCashDo.getAmount()));//到账金额
        buildData.put("borrowCash",String.valueOf(borrowCash));//借款费用(手续费加利息)
        buildData.put("appName","jsd");//借款app
        buildData.put("contractPdfUrl","");
        buildData.put("payTime",DateUtil.formatDateTime(borrowCashDo.getGmtArrival()));//打款时间

        //--------------------end  催收上报接口需要参数---------------------------
        data.add(buildData);
        collectionSystemUtil.noticeCollect(data);
    }













}
