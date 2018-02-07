package com.ald.fanbei.api.biz.auth.executor;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
/**
 * 支付宝认证回调处理类
 * @author rongbo
 *
 */
@Component("alipayAuthCallbackExecutor")
public class AlipayAuthCallbackExecutor implements Executor{

	@Override
	public void execute(AuthCallbackBo authCallbackBo) {
		
	}

}
