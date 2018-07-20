package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.bo.RepaymentBo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.third.cuishou.CuiShouBackMoney;
import com.ald.fanbei.api.biz.third.cuishou.CuiShouType;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Component("cuiShouUtils")
public class CuiShouUtils {
    protected static final Logger thirdLog = LoggerFactory.getLogger("DSED_THIRD");
    private final String salt = "dsedcuishou";
    @Resource
    DsedLoanService dsedLoanService;

    @Resource
    DsedLoanRepaymentService dsedLoanRepaymentService;

    @Resource
    DsedLoanPeriodsService dsedLoanPeriodsService;




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
            byte[] pd = DigestUtil.digestString(data.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign1 = DigestUtil.encodeHex(pd);
            if (!sign1.equals(sign)) return JSONObject.toJSONString(new CuiShouBackMoney(201, "sign error"));
            final JSONObject jsonObject = JSONObject.parseObject(data);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CuiShouUtils.setIsXianXiaHuangKuang(true);
                    loanBorrowCashMoney(jsonObject);
                }
            }).start();
            return JSONObject.toJSONString(new CuiShouBackMoney(200, "success"));//同步反回接收成功
        } catch (Exception e) {
            thirdLog.error("offlineRepaymentMoney error", e);
            CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney(500, "error");
            return JSON.toJSONString(cuiShouBackMoney);
        }
    }

    /**
     * 白领贷还款
     *
     * @param obj
     * @return
     */
    private CuiShouBackMoney loanBorrowCashMoney(JSONObject obj) {
        CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
        try {
            RepaymentBo repaymentBo = JSON.toJavaObject(obj, RepaymentBo.class);
            List<DsedLoanPeriodsDo> list = new ArrayList<>();
            String totalAmount = repaymentBo.getTotalAmount();
            String repaymentNo = repaymentBo.getRepaymentNo();
            String userId = repaymentBo.getRepaymentAcc();
            String type = repaymentBo.getType();
            String repayTime = repaymentBo.getRepayTime();
            String orderNo = repaymentBo.getOrderNo();
            Date time = DateUtil.stringToDate(repayTime);
            JSONArray detailsArray = obj.getJSONArray("details");
            Long loanId = 0l;
            String loanNo = "";

            if(detailsArray != null && detailsArray.size()>0){
                String id = String.valueOf(detailsArray.getJSONObject(0).get("dataId"));
                DsedLoanPeriodsDo dsedLoanPeriodsDo = dsedLoanPeriodsService.getById(Long.parseLong(id));
                loanId = dsedLoanPeriodsDo.getLoanId();
                loanNo = dsedLoanPeriodsDo.getLoanNo();
                DsedLoanDo dsedLoanDo = dsedLoanService.getByLoanNo(loanNo);
                if(!StringUtil.equals(dsedLoanDo.getUserId()+"",userId+"")){
                    cuiShouBackMoney.setCode(305);
                    thirdLog.error("offlineLoanRepaymentNotify error loanNo =" + orderNo);
                    return cuiShouBackMoney;
                }
            }
            for(int i=0;detailsArray.size()>i;i++){
                String id = String.valueOf(detailsArray.getJSONObject(i).get("dataId"));
                DsedLoanPeriodsDo dsedLoanPeriodsDo = dsedLoanPeriodsService.getById(Long.parseLong(id));

                if(dsedLoanPeriodsDo == null){
                    cuiShouBackMoney.setCode(205);
                    thirdLog.error("offlineLoanRepaymentNotify error loanNo =" + orderNo);
                    return cuiShouBackMoney;
                }
                if(StringUtil.equals(dsedLoanPeriodsDo.getOverdueStatus(),YesNoStatus.NO.getCode())){
                    cuiShouBackMoney.setCode(205);
                    thirdLog.error("offlineLoanRepaymentNotify error loanNo =" + orderNo);
                    return cuiShouBackMoney;
                }
                if(DateUtil.afterDay(dsedLoanPeriodsDo.getGmtPlanRepay(),time)){
                    cuiShouBackMoney.setCode(203);
                    thirdLog.error("offlineLoanRepaymentNotify error loanNo =" + orderNo);
                    return cuiShouBackMoney;
                }
                list.add(dsedLoanPeriodsDo);
            }
            if(StringUtil.isBlank(totalAmount)){
                cuiShouBackMoney.setCode(203);
                thirdLog.error("offlineLoanRepaymentNotify error loanNo =" + orderNo);
                return cuiShouBackMoney;
            }
            if (StringUtil.isAllNotEmpty(orderNo, repaymentNo, userId)) {
                dsedLoanRepaymentService.offlineRepay(loanNo,loanId,totalAmount, repaymentNo, Long.parseLong(userId), type, repayTime, orderNo, list);
            } else {
                cuiShouBackMoney.setCode(303);
                thirdLog.error("offlineLoanRepaymentNotify error loanNo =" + orderNo);
            }
            cuiShouBackMoney.setCode(200);
            return cuiShouBackMoney;
        } catch (Exception e) {
            thirdLog.error("offlineLoanRepaymentNotify error", e);
            cuiShouBackMoney.setCode(500);
            return cuiShouBackMoney;
        }
    }

    /**
     * 白领贷
     *
     * @param dsedLoanRepaymentDo
     */
    public void syncCuiShou(DsedLoanRepaymentDo dsedLoanRepaymentDo) {
        try {
            thirdLog.info("cuishouhuankuan bailingdai:" + dsedLoanRepaymentDo.toString());

            setDsedLoanRepaymentDo(dsedLoanRepaymentDo);
            CuiShouBackMoney cuiShouBackMoney = new CuiShouBackMoney();
            cuiShouBackMoney.setCode(200);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ref_id", dsedLoanRepaymentDo.getRid());
            jsonObject.put("id", dsedLoanRepaymentDo.getRemark());
            jsonObject.put("type", CuiShouType.WITH_BORROW.getCode());
            cuiShouBackMoney.setData(jsonObject);
            if (CuiShouUtils.getIsXianXiaHuangKuang() != null && CuiShouUtils.getIsXianXiaHuangKuang()) {
                return;
            }
            sycnSuccessAndError(cuiShouBackMoney, 1);
        } catch (Exception e) {
            thirdLog.error("cuishouhuankuan xianjinjie error:", e);
        }
    }


    /**
     * 还款成功或失败同步回去
     *
     * @param cuiShouBackMoney
     * @param type             1  主动还款，0 线下还款
     */
    public void sycnSuccessAndError(CuiShouBackMoney cuiShouBackMoney, Integer type) {
//        String  url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL)+"/api/getway/callback/nperRepay";
        String url = ConfigProperties.get("dsed.cuishou.sycnurl") + "/report/repayment";
//        String url ="http://192.168.96.38:8003/report/repayment";
        try {
            String mm = JSON.toJSONString(cuiShouBackMoney);
            byte[] pd = DigestUtil.digestString(mm.getBytes("UTF-8"), salt.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String sign = DigestUtil.encodeHex(pd);
            HashMap<String, String> mp = new HashMap<String, String>();
            mp.put("sign", sign);
            mp.put("data", mm);
            mp.put("type", String.valueOf(type));
            thirdLog.info("cuishouhuankuan  postChuiSohiu {sign:" + sign + ",data:" + mm + "}");
            thirdLog.info("cuishouhuankuan  postChuiSohiu url:" + url);
            String e1 = "";
            if (url.toLowerCase().startsWith("https")) {
                thirdLog.info("cuishouhuankuan  postChuiSohiu https");
                e1 = HttpUtil.doHttpsPost(url, mp, "utf-8");
            } else {
                e1 = HttpUtil.post(url, mp);
            }
            thirdLog.info("cuishouhuankuan  postChuiSohiu back" + e1);
        } catch (Exception e) {
            thirdLog.error("cuishouhuankuan  postChuiSohiu error", e);
        }

    }



    private static ThreadLocal<Boolean> isXianXiaHuangKuang = new ThreadLocal<Boolean>();

    public static void setIsXianXiaHuangKuang(Boolean a) {
        isXianXiaHuangKuang.set(a);
    }

    public static Boolean getIsXianXiaHuangKuang() {
        return isXianXiaHuangKuang.get();
    }


    private static ThreadLocal<DsedLoanRepaymentDo> dsedLoanRepaymentDoThreadLocal = new ThreadLocal<DsedLoanRepaymentDo>();

    public static void setDsedLoanRepaymentDo(DsedLoanRepaymentDo dsedLoanRepaymentDo) {
        dsedLoanRepaymentDoThreadLocal.set(dsedLoanRepaymentDo);
    }

    public static DsedLoanRepaymentDo getDsedLoanRepaymentDo() {
        return dsedLoanRepaymentDoThreadLocal.get();
    }







}
