package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import jodd.util.ObjectUtil;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author luoxiao @date 2018/5/17 10:23
 * @类描述：钱包
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping(value = "/vallet/")
public class H5WalletController extends BaseController{
    @Resource
    private AfUserAccountService afUserAccountService;
    @Resource
    private AfTaskUserService afTaskUserService;
    @Resource
    private AfCashRecordService afCashRecordService;
    @Resource
    private AfTaskCoinChangeProportionService afTaskCoinChangeProportionService;
    @Resource
    AfUserService afUserService;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    AfIdNumberService afIdNumberService;

    @ResponseBody
    @RequestMapping(value = "valletPage", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String valletPage(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> data = Maps.newHashMap();
        try{
            String userName = ObjectUtils.toString(request.getParameter("userName"),null);
//            FanbeiWebContext context = doWebCheck(request, true);
//            String userName = context.getUserName();
            AfUserDo afUserDo = afUserService.getUserByUserName(userName);
            if(null == afUserDo){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc()).toString();
            }
            Long userId = afUserDo.getRid();
            // 累计收益
            AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(userId);
            BigDecimal rebateAmount = userAccountDo.getRebateAmount();
            BigDecimal ucAmount = userAccountDo.getUcAmount();
            BigDecimal totalAmount = rebateAmount.add(ucAmount);
            // 可用余额
            data.put("rebateAmount", rebateAmount);
            // 累计收益
            data.put("totalAmount", totalAmount);

            // 我的金币
            Long availableCoinAmount = afTaskUserService.getAvailableCoinAmount(userId);
            data.put("availableCoinAmount", availableCoinAmount ==null?0.00:availableCoinAmount);

            // 我的金币兑换，是否昨天已经兑换过
            AfTaskUserDo taskUserDo = afTaskUserService.getTodayTaskUserDoByTaskName(Constants.TASK_COIN_CHANGE_TO_CASH_NAME, userId);
            if(null == taskUserDo){
                data.put("changeCoinFlag", false);
            }
            else{
                BigDecimal yesterdayProportion = afTaskCoinChangeProportionService.getYesterdayProportion();
                Long changedCoinAmount = afTaskUserService.getYestadayChangedCoinAmount(userId);

                data.put("changeCoinFlag", true);
                data.put("yesterdayProportion", yesterdayProportion);
                data.put("changedCoinAmount", changedCoinAmount);
            }

            // 近七天的收益情况
            List<Map<String, Object>> IncomeList = afTaskUserService.getIncomeOfNearlySevenDays(userId);
            data.put("IncomeList", IncomeList);

            return H5CommonResponse.getNewInstance(true,"","", data).toString();
        }catch(Exception e){
            logger.error("unknown error", e);
        }

        return H5CommonResponse.getNewInstance(false,"").toString();
    }

    @ResponseBody
    @RequestMapping(value = "getIncomeDetails", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getIncomeDetails(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> data = Maps.newHashMap();
        try{
//            FanbeiWebContext context = doWebCheck(request, true);
//            String userName = context.getUserName();
            String userName = ObjectUtils.toString(request.getParameter("userName"),null);
            AfUserDo afUserDo = afUserService.getUserByUserName(userName);
            if(null == afUserDo){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc()).toString();
            }
            Long userId = afUserDo.getRid();

            String rewardType = request.getParameter("rewardType");
            List<AfTaskUserDo> taskUserList = afTaskUserService.getDetailsByUserId(userId, Integer.parseInt(rewardType));
            data.put("taskUserList",taskUserList);

            return H5CommonResponse.getNewInstance(true,"", "", data).toString();
        } catch (Exception e){
            logger.error("unknown error", e);
        }

        return H5CommonResponse.getNewInstance(false,"").toString();
    }

    @ResponseBody
    @RequestMapping(value = "getWithDrawDetail", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getWithDrawDetail(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> data = Maps.newHashMap();
        try{
//            FanbeiWebContext context = doWebCheck(request, true);
            String withdrawId = request.getParameter("withdrawId");
            AfCashRecordDo cashRecordDo = afCashRecordService.getCashRecordById(Long.parseLong(withdrawId));
            data.put("cashRecord",cashRecordDo);

            return H5CommonResponse.getNewInstance(true,"", "", data).toString();
        } catch (Exception e){
            logger.error("unknown error", e);
        }

        return H5CommonResponse.getNewInstance(false,"").toString();
    }


    @ResponseBody
    @RequestMapping(value = "getBindBackStatus", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getBindBackStatus(HttpServletRequest request, HttpServletResponse response ){
        Map<String, Object> data = Maps.newHashMap();
        try{
//            FanbeiWebContext context = doWebCheck(request, true);
//            String userName = context.getUserName();
            String userName = ObjectUtils.toString(request.getParameter("userName"),null);
            AfUserDo afUserDo = afUserService.getUserByUserName(userName);
            if(null == afUserDo){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc()).toString();
            }
            Long userId = afUserDo.getRid();
            int count = afUserBankcardService.getUserBankcardCountByUserId(userId);
            data.put("count",count);
            AfIdNumberDo afIdNumberDo = afIdNumberService.getIdNumberInfoByUserId(userId);
            if(null == afIdNumberDo){
                data.put("idNumber","");
                data.put("realName","");
            }else {
                data.put("idNumber",afIdNumberDo.getName());
                data.put("realName",afIdNumberDo.getCitizenId());
            }
            return H5CommonResponse.getNewInstance(true,"", "", data).toString();
        } catch (Exception e){
            logger.error("unknown error", e);
        }

        return H5CommonResponse.getNewInstance(false,"").toString();
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
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
        return null;
    }

}
