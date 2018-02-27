package com.ald.fanbei.api.web.api.user;


import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ReplaceMainCardParam;
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

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        final ReplaceMainCardParam param =  (ReplaceMainCardParam)requestDataVo.getParamObj();
        String status = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try{
                   int count =  afUserBankcardService.updateMainBankCard(param.getUserId());
                   int count1 = afUserBankcardService.updateViceBankCard(param.getBackcard(),param.getUserId());
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
