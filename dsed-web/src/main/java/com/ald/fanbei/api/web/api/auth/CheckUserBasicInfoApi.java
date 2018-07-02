package com.ald.fanbei.api.web.api.auth;


import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.enums.UserAuthSceneStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import org.springframework.stereotype.Component;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：用户信息基础验证（是否认证，设置密码，用户身份信息）
 *@author  2017年2月28日 下午4:03:21
 *@since 4.1.2以后替换类{@link }
 *@version
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkUserBasicInfoApi")
public class CheckUserBasicInfoApi implements ApiHandle {

    @Resource
    private AfUserAuthService afUserAuthService;
    @Resource
    private AfUserAccountService afUserAccountService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        if (userId == null) {
            throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
        }
        Map<String,Boolean> checkResult = new HashMap<String,Boolean>();
        AfUserAuthDo afUserAuthDo= afUserAuthService.getUserAuthInfoByUserId(userId);
        if (StringUtil.equals(afUserAuthDo.getBasicStatus(), UserAuthSceneStatus.YES.getCode())) {
            checkResult.put("isRealNameAuth", true);
        } else {
            checkResult.put("isRealNameAuth", false);
        }
        AfUserAccountDo afUserAccountDo= afUserAccountService.getUserAccountByUserId(userId);
        if(afUserAccountDo==null || StringUtil.isEmpty(afUserAccountDo.getPassword()) ){
            checkResult.put("isPayPwd", false);
        }else {
            checkResult.put("isPayPwd", true);
        }
        if(afUserAccountDo==null || StringUtil.isEmpty(afUserAccountDo.getIdNumber())
                || StringUtil.isEmpty(afUserAccountDo.getRealName())){
            checkResult.put("isIdCard", false);
        }else {
            checkResult.put("isIdCard", true);
        }
        if(afUserAccountDo==null || !StringUtil.equals(afUserAccountDo.getBindCard(),YesNoStatus.YES.getCode())){
            checkResult.put("isBindIcCard", false);
        }else {
            checkResult.put("isBindIcCard", true);
        }
        resp.setResponseData(checkResult);
        return resp;
    }
}
