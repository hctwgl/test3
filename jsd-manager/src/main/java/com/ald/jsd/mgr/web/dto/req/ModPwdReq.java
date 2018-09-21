package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ModPwdReq extends BaseReq{
	@NotNull
	@Size(min=6)
	public String oriPasswd;
	@NotNull
	@Size(min=6)
	public String newPasswd;
}
