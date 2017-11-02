/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月17日下午5:59:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getPayPwdVerifyCodeApi")
public class GetPayPwdVerifyCodeApi implements ApiHandle {
	@Resource
	AfUserService afUserService;
	@Resource
	SmsUtil smsUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
        AfUserDo userInfo = afUserService.getUserById(userId);

		if (userInfo == null) {
			throw new FanbeiException("userInfo id   is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}

		boolean resultReg = smsUtil.sendSetPayPwdVerifyCode(userInfo.getMobile(), userId);
		if (!resultReg) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_SEND_SMS_ERROR);
		}

		return resp;
	}

}
