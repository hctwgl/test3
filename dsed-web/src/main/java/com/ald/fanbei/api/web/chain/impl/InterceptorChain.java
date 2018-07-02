package com.ald.fanbei.api.web.chain.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.chain.Chain;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.intercept.Interceptor;
/**
 * 拦截器链
 * @author rongbo
 *
 */
public class InterceptorChain  implements Chain{

	List<Interceptor> interceptors ;

	@Override
	public void execute(RequestDataVo reqData, FanbeiContext context, HttpServletRequest request) {
		for(Interceptor interceptor : interceptors) {
			interceptor.intercept(reqData, context, request);
		}

	}

	@Override
	public void execute(Context context) {
		for(Interceptor interceptor : interceptors) {
			interceptor.intercept(context);
		}
	}


	public List<Interceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<Interceptor> interceptors) {
		this.interceptors = interceptors;
	}
	

}
