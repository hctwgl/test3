package com.ald.fanbei.api.web.api.user;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午7:54:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("userLoginApi")
public class UserLoginApi implements ApiHandle{

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);

        String userName = context.getUserName();
        String appVersion = ObjectUtils.toString(requestDataVo.getParams().get("versionName"));
        String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"));
        String phoneType = ObjectUtils.toString(requestDataVo.getParams().get("phoneType"));
        String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"));
        String ip = CommonUtil.getIpAddr(request);
        String inputPassSrc = ObjectUtils.toString(requestDataVo.getParams().get("password"));
		
        logger.info("userName=" + userName + ",appVersion=" + appVersion + ",osType=" + osType + ",phoneType=" + phoneType + ",uuid=" + uuid + ",ip=" + ip + ",inputPassSrc" + inputPassSrc);
        
		return resp;
	}

}
