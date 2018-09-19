package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class OperatorEditReq extends BaseReq{
	public Long id;
	
	@NotNull
	@Size(min=11)
	public String username;
	
	@NotNull
	@Size(min=11)
	public String phone;
	
	@Size(min=10)
	public String email;
	
	public String roleId;
	
	public String passwd;
	
	@NotNull
	@Size(min=2)
	public String realName;
	
}
