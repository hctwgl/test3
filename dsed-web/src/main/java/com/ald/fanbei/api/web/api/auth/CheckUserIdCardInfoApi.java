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

/**
 *@类现描述：身份证信息是否已经被绑定过
 *@author  2017年2月28日 下午4:03:21
 *@since 4.1.2以后替换类{@link }
 *@version
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkUserIdCardInfoApi")
public class CheckUserIdCardInfoApi implements ApiHandle {

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
        if(afUserAccountService.getCountByIdNumer(ObjectUtils.identityToString(params.get("idNumber")),userId)>0){
            return  new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_CARD_IS_EXIST);
        }
        return resp;
    }

}

