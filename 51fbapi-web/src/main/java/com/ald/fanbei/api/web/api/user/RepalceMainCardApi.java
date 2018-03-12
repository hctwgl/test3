package com.ald.fanbei.api.web.api.user;


import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ReplaceMainCardParam;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component("replaceMainCardApi")
@Validator("replaceMainCardParam")
public class RepalceMainCardApi implements ApiHandle {

    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfUserAccountService afUserAccountService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo,final FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        final ReplaceMainCardParam param =  (ReplaceMainCardParam)requestDataVo.getParamObj();
        String payPwd = param.getPwd();
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(context.getUserId());
        if (afUserAccountDo == null) {
            throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
        if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
        }
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                   afUserBankcardService.updateMainBankCard(context.getUserId());
                   afUserBankcardService.updateViceBankCard(param.getCardNumber(),context.getUserId());
                   return "success";
                }catch (Exception e){
                    status.setRollbackOnly();
                    return "fail";
                }

            }
        });
        if(StringUtils.equals("fail",status)){
            throw new FanbeiException("repalceMainCard is fail", FanbeiExceptionCode.REPLACE_MAIN_CARD_FAIL);
        }
        return resp;
    }
}
