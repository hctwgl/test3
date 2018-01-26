package com.ald.fanbei.api.web.api.profile;


import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 支付宝线下转账查询绑定支付宝账号验证码接口
 *
 * @author xieqiang
 * @create 2018-01-25 13:25
 **/
@Component("zhiBalanceGetVerifyCodeApi")
public class ZhiBalanceGetVerifyCodeApi implements ApiHandle {
   @Resource
    SmsUtil smsUtil;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String account = ObjectUtils.toString(requestDataVo.getParams().get("account"),"");
        if ((!CommonUtil.isMobile(account) && !CommonUtil.isEmail(account))||userId == null){
            throw new FanbeiException(FanbeiExceptionCode.ZHI_BALANCE_INVALID_ERROR);
        }
        if (CommonUtil.isMobile(account)){
            smsUtil.sendMobileBindVerifyCode(account, SmsType.ZHI_BIND,userId);
        }else{
            smsUtil.sendEmailVerifyCode(account, SmsType.ZHI_BIND,userId);
        }
        return resp;
    }
}
