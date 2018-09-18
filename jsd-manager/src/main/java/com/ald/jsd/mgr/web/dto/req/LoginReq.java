package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class LoginReq extends BaseReq{
	@NotNull
	public String username;
	
	@NotNull
	public String passwd;
	
	@NotNull
	public String verifyCode;
}
