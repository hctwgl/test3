package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.auth.executor.AuthCallbackManager;
import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.RiskAuthStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类现描述：认证提额接口
 * @author Jiang Rongbo 2017年7月10日 16:18:42
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authRaiseQuotaApi")
public class AuthRaiseQuotaApi implements ApiHandle {

    @Resource
    AuthCallbackManager authCallbackManager;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	Long userId = context.getUserId();
	String authItem = ObjectUtils.toString(requestDataVo.getParams().get("authItem"));
	// 复用认证回调提额逻辑
	AuthCallbackBo authCallbackBo = new AuthCallbackBo("", ObjectUtils.toString(userId), authItem, RiskAuthStatus.SUCCESS.getCode(), requestDataVo.getParams().get("authItem").toString());
	authCallbackManager.execute(authCallbackBo);
	return resp;
    }

}
