package com.ald.fanbei.api.web.api.user;


import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetConfirmBorrowLegalInfoParam;
import com.ald.fanbei.api.web.validator.bean.RepalceMainCardParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component("repalceMainCardApi")
@Validator("repalceMainCardParam")
public class RepalceMainCardApi implements ApiHandle {

    @Resource
    AfUserBankcardService afUserBankcardService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        RepalceMainCardParam param =  (RepalceMainCardParam)requestDataVo.getParamObj();
        int count = afUserBankcardService.updateMainBankCard(param.getUserId());
        if(count<=0){
            throw new FanbeiException("repalceMainCard is fail", FanbeiExceptionCode.REPLACE_MAIN_CARD_FAIL);
        }
        int num = afUserBankcardService.updateViceBankCard(param.getBackcard(),param.getUserId());
        if(num<=0){
            throw new FanbeiException("repalceMainCard is fail", FanbeiExceptionCode.REPLACE_MAIN_CARD_FAIL);
        }
        return resp;
    }
}
