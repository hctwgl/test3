package com.ald.fanbei.api.web.chain;

import java.util.List;

import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.validator.intercept.Interceptor;

/**
 * 拦截器链
 * @author rongbo
 */
public class InterceptorChain{

	List<Interceptor> interceptors ;

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
