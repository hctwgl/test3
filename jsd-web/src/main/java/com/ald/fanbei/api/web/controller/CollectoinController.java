package com.ald.fanbei.api.web.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.third.util.CuiShouUtils;


@Controller
@RequestMapping("/third/collection/")
public class CollectoinController {

    @Resource
    CuiShouUtils cuiShouUtils;

    private final Logger logger = LoggerFactory.getLogger(CollectoinController.class);
    /**
     * 线下还款
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/offLineBackMoney"}, method = RequestMethod.POST)
    public String offLineBackMoney(HttpServletRequest request){
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
        logger.info("start collectRepay , request = " + JSON.toJSONString(request.getParameterMap()));
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

}
