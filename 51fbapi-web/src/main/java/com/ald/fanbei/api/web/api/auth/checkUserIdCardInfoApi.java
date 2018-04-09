package com.ald.fanbei.api.web.api.auth;

import com.ald.fanbei.api.biz.service.*;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component("checkUserIdCardInfoApi")
public class checkUserIdCardInfoApi implements ApiHandle {

    @Resource
    private AfUserAccountService afUserAccountService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        if (userId == null) {
            throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
        }
        Map<String,Object> params=requestDataVo.getParams();
        int result=afUserAccountService.getCountByIdNumer(ObjectUtils.identityToString(params.get("idNumber")),userId);
        if(result>0){
            return  new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
        }
        return resp;
    }

}

