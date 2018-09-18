package com.ald.jsd.mgr.dal.query;

import java.util.List;

import com.ald.fanbei.api.common.page.Page;

public class MgrCommonQuery<T> extends Page<T>{
	private static final long serialVersionUID = 1L;
	
	public String status;
	public String searchContent;
	
	public List<T> list;
}
