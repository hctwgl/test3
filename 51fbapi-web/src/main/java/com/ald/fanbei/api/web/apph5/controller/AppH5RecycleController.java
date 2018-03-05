package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.query.AfRecycleViewQuery;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author :weiqingeng
 * @version ：2018年3月3日 下午5:02:28
 * @类描述： 提现金额 转换成 券信息
 */
@Controller
@RequestMapping("/recycle")
public class AppH5RecycleController extends BaseController {

    @Autowired
    private AfUserService afUserService;
    @Autowired
    private AfUserAccountService afUserAccountService;
    @Autowired
    private AfRecycleService afRecycleService;
    @Autowired
    private AfRecycleViewService afRecycleViewService;

    private String URL = "http://51fanbei.youdemai.com";

    /**
     * @说明：兑换
     */
    @RequestMapping(value = "/exchange", method = RequestMethod.POST)
    @ResponseBody
    public String exchange(HttpServletRequest request) {
        Calendar calStart = Calendar.getInstance();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        FanbeiWebContext context = new FanbeiWebContext();
        try {
            Integer amount = NumberUtil.objToIntDefault(request.getParameter("amount"), null);
            Long userId = -1l;
            AfUserDo afUser = null;
            // 和登录有关的
            context = doWebCheck(request, true);
            if (context.isLogin()) {
                afUser = afUserService.getUserByUserName(context.getUserName());
                if (afUser != null) {
                    userId = afUser.getRid();
                    try {
                        if (null == amount) {
                            throw new FanbeiException("参数错误", true);
                        }
                        if (amount < 50) {
                            throw new FanbeiException("兑换金额不能小于50", true);
                        }
                        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
                        if (null == afUserAccountDo || (null != afUserAccountDo && afUserAccountDo.getRebateAmount().intValue() < 50)) {
                            throw new FanbeiException("账户余额不足50元,无法兑换", true);
                        }
                        if (amount.compareTo(afUserAccountDo.getRebateAmount().intValue()) > 0) {
                            throw new FanbeiException("账户余额小于兑换金额", true);
                        }
                        // 账户关联信息
                        AfUserAccountDto userAccountInfo = afUserAccountService.getUserAndAccountByUserId(userId);
                        if(null != userAccountInfo){
                            Map<String,Object> map = afRecycleService.addExchange(userId, amount, afUserAccountDo.getRebateAmount());
                            map.put("rebateAmount",userAccountInfo.getRebateAmount());
                            resp = H5CommonResponse.getNewInstance(true, "兑换成功", "", map);
                        }else{
                            resp = H5CommonResponse.getNewInstance(false, "获取用户提现金额错误", "", null);
                        }
                    } catch (Exception e) {
                        logger.error("exchangeApi,error=", e);
                        throw new FanbeiException(FanbeiExceptionCode.FAILED);
                    }
                }
            }
        } catch (Exception e) {
            resp = H5CommonResponse.getNewInstance(false, "兑换失败", "", e.getMessage());
            logger.error("兑换失败" + context, e);
        } finally {
            Calendar calEnd = Calendar.getInstance();
            doLog(request, resp, context.getAppInfo(), calEnd.getTimeInMillis() - calStart.getTimeInMillis(), context.getUserName());
        }
        return resp.toString();

    }


    /**
     * @说明：获取跳转到有得卖H5页面地址
     */
    @RequestMapping(value = "/getRecycleUrl", method = RequestMethod.POST)
    @ResponseBody
    public String getRecycleUrl(HttpServletRequest request) {
        Calendar calStart = Calendar.getInstance();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        FanbeiWebContext context = new FanbeiWebContext();
        try {
            Integer amount = NumberUtil.objToIntDefault(request.getParameter("amount"), null);
            Long userId = -1l;
            AfUserDo afUser = null;
            // 和登录有关的
            context = doWebCheck(request, true);
            if (context.isLogin()) {
                afUser = afUserService.getUserByUserName(context.getUserName());
                if (afUser != null) {
                    userId = afUser.getRid();
                    try {
                        String recycleUrl = URL + "?userId=" + userId;
                        //添加页面访问记录
                        AfRecycleViewQuery afRecycleViewQuery = new AfRecycleViewQuery(userId, 1);
                        afRecycleViewService.getRecycleViewByUid(afRecycleViewQuery);
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put("recycleUrl",recycleUrl);
                        resp = H5CommonResponse.getNewInstance(true, "获取数据成功", "", map);
                    } catch (Exception e) {
                        logger.error("exchangeApi,error=", e);
                        throw new FanbeiException(FanbeiExceptionCode.FAILED);
                    }
                }
            }
        } catch (Exception e) {
            resp = H5CommonResponse.getNewInstance(false, "获取地址失败", "", e.getMessage());
            logger.error("获取地址失败" + context, e);
        } finally {
            Calendar calEnd = Calendar.getInstance();
            doLog(request, resp, context.getAppInfo(), calEnd.getTimeInMillis() - calStart.getTimeInMillis(), context.getUserName());
        }
        return resp.toString();

    }


    /**
     * @说明：获取跳转返现H5页面地址
     */
    @RequestMapping(value = "/getReturnCash", method = RequestMethod.POST)
    @ResponseBody
    public String getReturnCash (HttpServletRequest request) {
        Calendar calStart = Calendar.getInstance();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        FanbeiWebContext context = new FanbeiWebContext();
        try {
            Long userId = -1l;
            AfUserDo afUser = null;
            // 和登录有关的
            context = doWebCheck(request, true);
            if (context.isLogin()) {
                afUser = afUserService.getUserByUserName(context.getUserName());
                if (afUser != null) {
                    userId = afUser.getRid();
                }
            }
            // 账户关联信息
            AfUserAccountDto userAccountInfo = afUserAccountService.getUserAndAccountByUserId(userId);

            //添加页面访问记录 '访问类型 1：回收 2：返现 3.运营活动位置1 4.运营活动位置2'
            AfRecycleViewQuery afRecycleViewQuery = new AfRecycleViewQuery(userId, 2);
            afRecycleViewService.getRecycleViewByUid(afRecycleViewQuery);

            Map<String,Object> map = new HashMap<String,Object>();
            if(null != userAccountInfo){
                map.put("rebateAmount",userAccountInfo.getRebateAmount());
                resp = H5CommonResponse.getNewInstance(true, "获取数据成功", "", map);
            }else{
                resp = H5CommonResponse.getNewInstance(false, "获取用户提现金额错误", "", map);
            }
        } catch (Exception e) {
            resp = H5CommonResponse.getNewInstance(false, "获取信息失败", "", e.getMessage());
            logger.error("获取信息失败" + context, e);
        } finally {
            Calendar calEnd = Calendar.getInstance();
            doLog(request, resp, context.getAppInfo(), calEnd.getTimeInMillis() - calStart.getTimeInMillis(), context.getUserName());
        }
        return resp.toString();

    }


    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setId(jsonObj.getString("id"));
            reqVo.setMethod(request.getRequestURI());
            reqVo.setSystem(jsonObj);

            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        // TODO Auto-generated method stub
        return null;
    }

}
