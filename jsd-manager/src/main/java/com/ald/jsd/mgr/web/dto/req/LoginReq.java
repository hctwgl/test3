package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class LoginReq extends BaseReq{
	@NotNull
	public String username;
	
	public String passwd;
	
	public String verifyCode;
}
