package com.ald.fanbei.api.web.validator.intercept;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 支付密码校验拦截器
 * 
 * @author rongbo
 *
 */
@Component("payPwdCheckInterceptor")
public class PayPwdCheckInterceptor implements Interceptor {

	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public void intercept(RequestDataVo reqData, FanbeiContext context, HttpServletRequest request) {
		String payPwd = ObjectUtils.toString(reqData.getParams().get("payPwd"), "").toString();
		if (StringUtils.isNotBlank(payPwd)) {
			Long userId = context.getUserId();
			if(userId == null) {
				throw new FanbeiException("请登录后再支付！", true);
			}
			
			AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
			String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
			if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
				throw new FanbeiException(FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}
	}

	@Override
	public void intercept(Context context) {
		String payPwd = ObjectUtils.toString(context.getData("payPwd"));
		if (StringUtils.isNotBlank(payPwd)) {
			Long userId = context.getUserId();
			if(userId == null) {
				throw new FanbeiException("请登录后再支付！", true);
			}
			AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
			String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
			if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
				throw new FanbeiException(FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}
	}

}
