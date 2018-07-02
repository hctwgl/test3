package com.ald.fanbei.api.biz.auth.executor;

import com.ald.fanbei.api.biz.bo.AuthCallbackBo;

public interface Executor {
	
	public void execute(AuthCallbackBo authCallbackBo);
	
}
