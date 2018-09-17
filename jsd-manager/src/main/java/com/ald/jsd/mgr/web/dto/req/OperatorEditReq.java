package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class OperatorEditReq extends BaseReq{
	public Long id;
	
	@NotNull
	public String username;
	
	@NotNull
	public String phone;
	
	public String roleId;
	
	@NotNull
	public String passwd;
	
	@NotNull
	public String realName;
}
