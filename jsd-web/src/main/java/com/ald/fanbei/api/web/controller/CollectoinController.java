package com.ald.fanbei.api.web.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdCollectionService;
import com.ald.fanbei.api.biz.service.JsdUserContactsService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRepaymentDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.third.util.CuiShouUtils;

import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping("/third/collection/")
public class CollectoinController {

    @Resource
    CuiShouUtils cuiShouUtils;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    XgxyUtil xgxyUtil;
    @Resource
    JsdUserContactsService jsdUserContactsService;
    @Resource
    JsdBorrowCashRepaymentDao jsdBorrowCashRepaymentDao;
    @Resource
    JsdBorrowLegalOrderRepaymentDao jsdBorrowLegalOrderRepaymentDao;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
    @Resource
    JsdCollectionService jsdCollectionService;

    private final Logger logger = LoggerFactory.getLogger(CollectoinController.class);
    /**
     * 线下还款
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/offLineBackMoney"}, method = RequestMethod.POST)
    public String offLineBackMoney(HttpServletRequest request){
        logger.info("offlineRepaymentMoney start , request = " + JSON.toJSONString(request.getParameterMap()));
        return cuiShouUtils.offlineRepaymentMoney(request);
    }

    /**
     * 催收上报
     * @param data
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/collectImport"}, method = RequestMethod.POST)
    public String collectImport(String data){
    	logger.info(" -------------- collectImport request from cuishou, start, request data=" + data);
        return cuiShouUtils.collectImport(data);
    }

    /**
     * 催收平账申请(plus)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/collectReconciliate"}, method = RequestMethod.POST)
    public String collectReconciliate(HttpServletRequest request){
        logger.info("start collectReconciliate , request = " + JSON.toJSONString(request.getParameterMap()));
        return cuiShouUtils.collectReconciliate(request);
    }

    /**
     * 催收还款申请(plus)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/collectRepay"}, method = RequestMethod.POST)
    public String collectRepay(HttpServletRequest request){
        logger.info("start collectRepay " );
        return cuiShouUtils.collectRepay(request);
    }

    /**
     * 催收更新数据(第二版)
     * @param data
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/collectData"}, method = RequestMethod.POST)
    public String collectData(String data){
        logger.info("start collectData , data = " + data);
        return cuiShouUtils.collectData(data);
    }

    /**
     * 催收平账修改状态(第二版)
     * @param data
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/collectUpdateStatus"}, method = RequestMethod.POST)
    public String collectUpdateStatus(String data,String sign){
        logger.info("start collectUpdateStatus ------------ data = " + data + ",sign = " + sign);
        return cuiShouUtils.collectUpdateStatus(data,sign);
    }
    /**
     * 从西瓜获取通讯录
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/addUserContancts"}, method = RequestMethod.POST)
    public void addUserContancts(Long userId){
        try {
            JsdUserDo userDo = jsdUserService.getById(userId);
            String contacts=xgxyUtil.getUserContactsInfo(userDo.getOpenId());
            if(StringUtils.isNotBlank(contacts)){
                List<JsdUserContactsDo> userContactsDo= jsdUserContactsService.getUserContactsByUserId(userId);
                JsdUserContactsDo contactsDo=new JsdUserContactsDo();
                contactsDo.setUserId(userId);
                contactsDo.setContactsMobile(StringUtil.filterEmoji(contacts));
                if(userContactsDo.size()==0){
                    jsdUserContactsService.saveRecord(contactsDo);
                }else {
                    jsdUserContactsService.updateByUserId(contactsDo);
                }
            }
        }catch (Exception e) {
            logger.error("calcuOverdueRecords.addUserContancts error, userId = "+ userId, e);
        }
    }

    /**
     * 手动推送还款给催收
     * @param data
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/manualPushRepayment"}, method = RequestMethod.POST)
    public String manualPushRepayment(@RequestBody JSONObject data){
        logger.info("start manualPushRepayment ------------ data = " + data.toJSONString());
        Boolean isPush = data.getBoolean("isPush");
        String reviewReuslt = data.getString("reviewReuslt");
        JsdBorrowCashRepaymentDo jsdBorrowCashRepaymentDo = jsdBorrowCashRepaymentDao.getByTradeNo(data.getString("tradeNo"));
        JsdBorrowLegalOrderRepaymentDo jsdBorrowLegalOrderRepaymentDo = jsdBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByTradeNo(data.getString("tradeNo"));
        JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashService.getBorrowByRid(jsdBorrowCashRepaymentDo.getBorrowId());
        JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderDao.getLastValidOrderByBorrowId(jsdBorrowCashRepaymentDo.getBorrowId());
        BigDecimal repayAmount = jsdBorrowCashRepaymentDo.getRepaymentAmount();
        if(jsdBorrowLegalOrderRepaymentDo != null){
            repayAmount = jsdBorrowCashRepaymentDo.getRepaymentAmount().add(jsdBorrowLegalOrderRepaymentDo.getRepayAmount());
        }
        String outTradeNo = jsdBorrowCashRepaymentDo.getTradeNoUps();
        String borrowNo = jsdBorrowCashDo.getBorrowNo();
        Long orderId = jsdBorrowLegalOrderDo.getRid();
        Long userId = jsdBorrowCashRepaymentDo.getUserId();
        logger.info("manualPushRepayment ------------ repayAmount = " + repayAmount+", outTradeNo = "+outTradeNo+", borrowNo = "+borrowNo+", orderId = "+orderId+", userId = "+userId);
        if(isPush){
            jsdCollectionService.nofityRepayment(repayAmount, outTradeNo, borrowNo, orderId, userId, JsdRepayType.OFFLINE, reviewReuslt);
        }
        return "true";
    }

}
