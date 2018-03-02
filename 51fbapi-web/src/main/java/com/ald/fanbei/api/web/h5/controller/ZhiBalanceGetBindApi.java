package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserProfileService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserProfileDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝线下转账查询绑定支付宝账号接口
 *
 * @author xieqiang
 * @create 2018-01-25 13:25
 **/
@RestController
@RequestMapping(value = "h5userprofile", produces = "application/json;charset=UTF-8")
public class ZhiBalanceGetBindApi extends H5Controller {
    @Resource
    private AfUserProfileService afUserProfileService;
    @Resource
    AfUserService afUserService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Resource
    SmsUtil smsUtil;

    @RequestMapping(value = "/zhiBalanceGetBind", method = RequestMethod.POST)
    public String zhiBalanceGetBindApi(HttpServletRequest request, HttpServletResponse response){
        String resultStr = "获取支付宝绑定信息失败";
        try{
            FanbeiWebContext context = new FanbeiWebContext();
            context = doWebCheck(request, true);
            String userName = context.getUserName();
            if(userName == null || "".equals(userName)) {
                resultStr = "用户不存在";
                throw new FanbeiException(resultStr);
            }
            AfUserDo user = afUserService.getUserByUserName(userName);
            if (user== null){
                resultStr = "用户不存在";
                throw new FanbeiException(resultStr);
            }
            AfUserProfileDo userProfileDo = new AfUserProfileDo();
            userProfileDo.setType("Z");
            userProfileDo.setUserId(user.getRid());
            AfUserProfileDo userprofile = afUserProfileService.getUserProfileByCommonCondition(userProfileDo);
            return H5CommonResponse.getNewInstance(true, "获取支付宝绑定信息成功", null, userprofile==null?new HashMap<>():userprofile).toString();
        }catch (Exception e){
            logger.info("获取支付宝绑定信息失败"+e);
        }
        return H5CommonResponse.getNewInstance(false, resultStr, null, null).toString();
    }
    @RequestMapping(value = "/zhiBalanceBind", method = RequestMethod.POST)
    public String zhiBalanceBindApi(HttpServletRequest request, HttpServletResponse response){
        String resultStr = "支付宝绑定失败";
        String code = "";
        Map<String,Object> data = new HashMap<>();
        try{
            FanbeiWebContext context = new FanbeiWebContext();
            context = doWebCheck(request, true);
            String userName = context.getUserName();
            if(userName == null || "".equals(userName)) {
                resultStr = "用户不存在";
                code = "100";
                throw new FanbeiException(resultStr);
            }
            AfUserDo user = afUserService.getUserByUserName(userName);
            if (user== null){
                resultStr = "用户不存在";
                code = "100";
                throw new FanbeiException(resultStr);
            }
            String account = ObjectUtils.toString(request.getParameter("account"), null);
            String verifycode = ObjectUtils.toString(request.getParameter("verifycode"), null);
            if(account==null || verifycode==null) {
                resultStr = "信息有误";
                code = "101";
                throw new FanbeiException(resultStr);
            }
            AfUserProfileDo userProfileDo = new AfUserProfileDo();
            userProfileDo.setType("Z");
            userProfileDo.setAccount(account);
            //账号已被别人绑定
            AfUserProfileDo userprofileOther = afUserProfileService.getUserProfileByCommonCondition(userProfileDo);
            if (userprofileOther != null){
                if (user.getRid().longValue() == userprofileOther.getUserId().longValue()){
                    code = "104";
                    resultStr = "您已绑定该账号，无需重新绑定";
                }else{
                    code = "102";
                    data.put("mobile",userprofileOther.getAccount());
                    resultStr = "支付宝账号已被"+userprofileOther.getAccount()+"绑定，请联系客服解决";
                }

                throw new FanbeiException(FanbeiExceptionCode.ZHI_BALANCE_EXITS_ERROR);
            }
            //验证码错误
            AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(account, SmsType.ZHI_BIND.getCode());
            if(smsDo == null || !verifycode.equals(smsDo.getVerifyCode())){
                code = "103";
                resultStr = "验证码有误";
                throw new FanbeiException(FanbeiExceptionCode.ZHI_BALANCE_CODE_INVALID_ERROR);
            }
            //删除已有绑定账号
            userProfileDo.setUserId(user.getRid());
            userProfileDo.setAccount(null);
            AfUserProfileDo userprofilemy = afUserProfileService.getUserProfileByCommonCondition(userProfileDo);
            if (userprofilemy != null){
                afUserProfileService.updateDeleteUserProfileById(userprofilemy.getRid());
            }
            //绑定新账号
            userProfileDo.setAccount(account);
            afUserProfileService.saveUserProfile(userProfileDo);
            return H5CommonResponse.getNewInstance(true, "支付宝绑定成功", null, null).toString();
        }catch (Exception e){
            logger.info("支付宝绑定失败"+e);
        }
        data.put("code",code);
        return H5CommonResponse.getNewInstance(false, resultStr, null, data).toString();
    }
    @RequestMapping(value = "/zhiBalanceGetVerifyCode", method = RequestMethod.POST)
public String zhiBalanceGetVerifyCodeApi(HttpServletRequest request, HttpServletResponse response){
        String resultStr = "发送绑定验证码失败";
        try {
            String account = ObjectUtils.toString(request.getParameter("account"), null);
            FanbeiWebContext context = new FanbeiWebContext();
            context = doWebCheck(request, true);
            String userName = context.getUserName();
            if(userName == null || "".equals(userName)) {
                resultStr = "用户不存在";
                throw new FanbeiException(resultStr);
            }
            AfUserDo user = afUserService.getUserByUserName(userName);
            if (user== null){
                resultStr = "用户不存在";
                throw new FanbeiException(resultStr);
            }
            if ((!CommonUtil.isMobile(account) && !CommonUtil.isEmail(account)) ){
                resultStr = "支付宝账号有误";
                throw new FanbeiException(FanbeiExceptionCode.ZHI_BALANCE_INVALID_ERROR);
            }

            if (CommonUtil.isMobile(account)){
                smsUtil.sendMobileBindVerifyCode(account, SmsType.ZHI_BIND,user.getRid());
            }else{
                smsUtil.sendEmailVerifyCode(account, SmsType.ZHI_BIND,user.getRid());
            }
            return H5CommonResponse.getNewInstance(true, "支付宝绑定成功", null, null).toString();
        }catch (Exception e){
            resultStr = "发送绑定验证码失败";
            logger.info("支付宝绑定失败"+e);

        }
    return H5CommonResponse.getNewInstance(false, resultStr, null, null).toString();

}
    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        // TODO Auto-generated method stub
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
}
