package com.ald.fanbei.api.web.api.profile;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserProfileService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserProfileDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 支付宝线下转账绑定支付宝账号接口
 *
 * @author xieqiang
 * @create 2018-01-25 13:25
 **/
@Component("zhiBalanceBindApi")
public class ZhiBalanceBindApi implements ApiHandle {
    @Resource
    private AfUserProfileService afUserProfileService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String account = ObjectUtils.toString(requestDataVo.getParams().get("account"),"");
        String verifycode = ObjectUtils.toString(requestDataVo.getParams().get("verifycode"),"");
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
        userProfileDo.setUserId(userId);
        userProfileDo.setAccount(null);
        AfUserProfileDo userprofilemy = afUserProfileService.getUserProfileByCommonCondition(userProfileDo);
        if (userprofilemy != null){
            afUserProfileService.updateDeleteUserProfileById(userprofilemy.getRid());
        }
        //绑定新账号
        userProfileDo.setAccount(account);
        afUserProfileService.saveUserProfile(userProfileDo);

        return resp;
    }
}
