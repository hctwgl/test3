package com.ald.fanbei.api.web.api.user;


import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component("repalceMainCard")
@Validator("repalceMainCardParam")
public class RepalceMainCard implements ApiHandle {

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

        return resp;
    }
}
