package com.ald.fanbei.api.web.common.util;

import com.ald.fanbei.api.web.common.RequestDataVo;

public abstract class RequestUtil {

	public static String getAppName(RequestDataVo requestDataVo) {
		String appName = requestDataVo.getId().substring(requestDataVo.getId().lastIndexOf("_") + 1,
				requestDataVo.getId().length());
		return appName;
	}
}
