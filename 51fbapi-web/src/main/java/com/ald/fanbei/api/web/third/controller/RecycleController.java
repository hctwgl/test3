package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RecycleUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.ald.fanbei.api.dal.domain.query.AfRecycleViewQuery;
import com.ald.fanbei.api.web.util.AppRecycleControllerUtil;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @author weiqingeng
 * @Description: 有得卖三方 回收业务
 * @date 2018年2月26日 下午4:14:31
 */
@Controller
@RequestMapping(value = "/fanbei/ydm")
public class RecycleController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AfRecycleService afRecycleService;
    @Autowired
    private AfRecycleViewService afRecycleViewService;
    @Autowired
    private  AfUserAccountService afUserAccountService;
    @Autowired
    private BizCacheUtil bizCacheUtil;

    /**
     * 创建订单 有得卖 三方 推送过来的订单数据(发券)
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    @ResponseBody
    public String addOrder(HttpServletRequest request) {
        String result = "";
        String key = "";
        try {
            AfRecycleQuery afRecycleQuery = AppRecycleControllerUtil.buildParam(request);
            if (RecycleUtil.PARTNER_ID.equals(afRecycleQuery.getPartnerId())) {
                logger.info("/fanbei/ydm/addOrder,params ={}", afRecycleQuery.toString());
                String refOrderId = afRecycleQuery.getRefOrderId();
                Long uid = afRecycleQuery.getUid();
                key = Constants.CACHKEY_GET_COUPON_LOCK + ":" + refOrderId + ":" + uid;
                boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
                if (isNotLock) {
                    AfRecycleDo afRecycleDo = afRecycleService.getRecycleOrder(afRecycleQuery);
                    if (null == afRecycleDo) {//订单不存在，新增一条订单
                        afRecycleService.addRecycleOrder(afRecycleQuery);//新增一条订单
                    } else {
                        result = H5CommonResponse.getNewInstance(true, "订单已存在", null, "").toString();
                    }
                }
            } else {
                logger.error("/fanbei/ydm/addOrder, partnerId error,partnerId={}", afRecycleQuery.getPartnerId());
                return H5CommonResponse.getNewInstance(false, "合作商id错误", null, "").toString();
            }
        } catch (Exception e) {
            logger.error("/fanbei/ydm/addOrder, error = {}", e.getStackTrace());
            return H5CommonResponse.getNewInstance(false, "订单生成失败", null, "").toString();
        } finally {
            bizCacheUtil.delCache(key);
        }
        return result;
    }


    /**
     * 增加页面访问记录   访问类型 1：回收 2：返现
     * @return
     * @author weiqingeng
     */
    @RequestMapping(value = "/addPageView", method = RequestMethod.POST)
    @ResponseBody
    public String addPageView(@RequestParam("uid") Long uid, @RequestParam("type") Integer type) {
        String result = "";
        String key = "";
        try {
            if(null == uid || null == type){
                return H5CommonResponse.getNewInstance(false, "参数错误", null, "").toString();
            }
            AfRecycleViewQuery afRecycleViewQuery = new AfRecycleViewQuery(uid,type);
            logger.info("/fanbei/ydm/addPageView,params ={}", afRecycleViewQuery.toString());
            key = Constants.CACHKEY_GET_COUPON_LOCK + ":" + afRecycleViewQuery.getUid();
            boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
            if (isNotLock) {
                AfRecycleViewDo afRecycleDo = afRecycleViewService.getRecycleViewByUid(afRecycleViewQuery);
                if (null == afRecycleDo) {//访问不存在，新增一条访问记录
                    afRecycleViewService.addRecycleView(afRecycleViewQuery);
                } else {//修改访问记录
                    afRecycleViewService.updateRecycleView(afRecycleViewQuery);
                }
            }
        } catch (Exception e) {
            logger.error("/fanbei/ydm/addPageView, error = {}", e.getStackTrace());
            return H5CommonResponse.getNewInstance(false, "增加页面访问记录失败", null, "").toString();
        } finally {
            bizCacheUtil.delCache(key);
        }
        return result;
    }


    /**
     * 兑换 余额兑换成 满减卷
     *  amount 兑换的金额
     * @return
     * @author weiqingeng
     */
    @RequestMapping(value = "/exchange", method = RequestMethod.POST)
    @ResponseBody
    public String exchange(@RequestParam("uid") Long uid, @RequestParam("amount") Integer amount) {
        String result = "";
        try {
            if(null == uid || null == amount){
                return H5CommonResponse.getNewInstance(false, "参数错误", null, "").toString();
            }
            if(amount < 50){
                return H5CommonResponse.getNewInstance(false, "兑换金额不能小于50", null, "").toString();
            }
            AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(uid);
            if(null == afUserAccountDo || (null != afUserAccountDo && afUserAccountDo.getRebateAmount().intValue() < 50)){
                return H5CommonResponse.getNewInstance(false, "账户余额不足50元,无法兑换", null, "").toString();
            }
            if(amount.compareTo(afUserAccountDo.getRebateAmount().intValue()) > 0){
                return H5CommonResponse.getNewInstance(false, "账户余额小于兑换金额", null, "").toString();
            }
            afRecycleService.addExchange(uid, amount, afUserAccountDo.getRebateAmount());
        } catch (Exception e) {
            logger.error("/fanbei/ydm/exchange, error = {}", e.getStackTrace());
            return H5CommonResponse.getNewInstance(false, "兑换失败", null, "").toString();
        }
        return result;
    }




}
