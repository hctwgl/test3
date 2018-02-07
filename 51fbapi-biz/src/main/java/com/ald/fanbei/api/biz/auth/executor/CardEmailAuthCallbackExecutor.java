package com.ald.fanbei.api.biz.auth.executor;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
/**
 * 信用卡认证回调处理类
 * @author rongbo
 *
 */
@Component("cardEmailAuthCallbackExecutor")
public class CardEmailAuthCallbackExecutor implements Executor{

	@Override
	public void execute(AuthCallbackBo authCallbackBo) {
		
	}

}
