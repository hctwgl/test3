package com.ald.fanbei.api.web.validator.intercept;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 支付密码字段转换拦截器
 * 
 * @author rongbo
 *
 */
@Component("payPwdConvertorInterceptor")
public class PayPwdConvertorInterceptor implements Interceptor {

	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public void intercept(RequestDataVo reqData, FanbeiContext context, HttpServletRequest request) {
		
		String[] needConvertorApis = {
				"/borrowCash/applyBorrowCash",
				"/borrowCash/applyBorrowCashV1",
				"/legalborrow/applyLegalBorrowCash",
				"/legalborrowV2/applyLegalBorrowCash"
		};
		List<String> list = Arrays.asList(needConvertorApis);
		String method = reqData.getMethod();
		if(list.contains(method)) {
			reqData.getParams().put("payPwd", reqData.getParams().get("pwd"));
		}
	}

	@Override
	public void intercept(Context context) {
		
	}
	

}
