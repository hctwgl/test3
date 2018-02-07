package com.ald.fanbei.api.biz.auth.executor;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
/**
 * 网银认证回调处理类
 * @author rongbo
 *
 */
@Component("bankAuthCallbackExecutor")
public class BankAuthCallbackExecutor implements Executor{

	@Override
	public void execute(AuthCallbackBo authCallbackBo) {
		
	}

}
