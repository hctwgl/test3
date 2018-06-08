package com.ald.fanbei.api.web.chain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.RequestDataVo;

public interface Chain {
	
	public void execute(RequestDataVo reqData, FanbeiContext context, HttpServletRequest request);
	
	public void execute(Context context);


}
