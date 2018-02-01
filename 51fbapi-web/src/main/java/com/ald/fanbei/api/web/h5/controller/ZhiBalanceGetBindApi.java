package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserProfileService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserProfileDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
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
@RequestMapping(value = "/userprofile", produces = "application/json;charset=UTF-8")
public class ZhiBalanceGetBindApi implements ApiHandle {
    @Resource
    private AfUserProfileService afUserProfileService;
    @Resource
    AfUserService afUserService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();

        AfUserProfileDo userProfileDo = new AfUserProfileDo();
        userProfileDo.setType("Z");
        userProfileDo.setUserId(userId);
        AfUserProfileDo userprofile = afUserProfileService.getUserProfileByCommonCondition(userProfileDo);
        resp.addResponseData("zhiBind",userprofile);
        return resp;
    }
    @RequestMapping(value = "/zhiBalanceGetBindApi", method = RequestMethod.POST)
    public String zhiBalanceGetBindApi(HttpServletRequest request, HttpServletResponse response){
        String resultStr = "获取支付宝绑定信息失败";
        try{
            String userName = ObjectUtils.toString(request.getParameter("userName"), null);
            if(userName == null) {
                resultStr = "用户不存在";
                throw new FanbeiException(resultStr);
            }
            AfUserDo user = afUserService.getUserByUserName(userName);
            if (user == null){
                resultStr = "用户不存在";
                throw new FanbeiException(resultStr);
            }
            AfUserProfileDo userProfileDo = new AfUserProfileDo();
            userProfileDo.setType("Z");
            userProfileDo.setUserId(user.getRid());
            AfUserProfileDo userprofile = afUserProfileService.getUserProfileByCommonCondition(userProfileDo);
            return H5CommonResponse.getNewInstance(true, "获取支付宝绑定信息成功", null, userprofile).toString();
        }catch (Exception e){
            logger.info("获取支付宝绑定信息失败"+e);
        }
        return H5CommonResponse.getNewInstance(true, resultStr, null, null).toString();
    }
    @RequestMapping(value = "/zhiBalanceBindApi", method = RequestMethod.POST)
    public String zhiBalanceBindApi(HttpServletRequest request, HttpServletResponse response){
        String resultStr = "支付宝绑定失败";
        try{
            String userName = ObjectUtils.toString(request.getParameter("userName"), null);
            String account = ObjectUtils.toString(request.getParameter("account"), null);
            String verifycode = ObjectUtils.toString(request.getParameter("verifycode"), null);
            if(userName == null || account==null || verifycode==null) {
                resultStr = "信息有误";
                throw new FanbeiException(resultStr);
            }
            AfUserDo user = afUserService.getUserByUserName(userName);
            if (user == null){
                resultStr = "用户不存在";
                throw new FanbeiException(resultStr);
            }

            AfUserProfileDo userProfileDo = new AfUserProfileDo();
            userProfileDo.setType("Z");
            userProfileDo.setAccount(account);
            //账号已被别人绑定
            AfUserProfileDo userprofileOther = afUserProfileService.getUserProfileByCommonCondition(userProfileDo);
            if (userprofileOther != null){
                throw new FanbeiException(FanbeiExceptionCode.ZHI_BALANCE_EXITS_ERROR);
            }
            //验证码错误
            AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(account, SmsType.ZHI_BIND.getCode());
            if(smsDo == null || !verifycode.equals(smsDo.getVerifyCode())){
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
            return H5CommonResponse.getNewInstance(true, "支付宝绑定失败成功", null, null).toString();
        }catch (Exception e){
            logger.info("支付宝绑定失败"+e);
        }
        return H5CommonResponse.getNewInstance(true, resultStr, null, null).toString();
    }
}
