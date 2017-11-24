package com.ald.fanbei.api.web.api.auth;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 实名认证-用户手动修改扫描后不正确的姓名
 * @author zhujiangfeng
 */
@Component("updateRealnameManualApi")
public class UpdateRealnameManualApi implements ApiHandle {

	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		Map<String, Object> params = requestDataVo.getParams();
        String realname = (String)params.get("realname");
        logger.info("UpdateRealnameManualApi realname=>" + realname);
        if(StringUtils.isBlank(realname)) {
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
        
        bizCacheUtil.saveObjectForever(Constants.CACHEKEY_REAL_AUTH_REAL_NAME_PREFFIX + context.getUserName(), realname);
        
        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
    }
	
}
