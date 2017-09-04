package com.ald.fanbei.api.web.api.system;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAppUpgradeService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAppUpgradeDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月20日下午6:19:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("appUpgradeApi")
public class AppUpgradeApi implements ApiHandle {
	
	@Resource
	AfAppUpgradeService afAppUpgradeService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Integer versionCode =  NumberUtil.objToIntDefault(requestDataVo.getSystem().get(Constants.REQ_SYS_NODE_VERSION), -1);
		JSONObject data = new JSONObject();
		String idName = requestDataVo.getId();
		String name =idName.substring(idName.lastIndexOf("_")+1);
	    Pattern pattern = Pattern.compile("^[0-9]*$");
	    Matcher matcher = pattern.matcher(name);
		   
		if(matcher.matches()){
			name ="www";
		}
		String type = idName.substring(0, 1).toUpperCase();
		AfAppUpgradeDo appInfo = afAppUpgradeService.getNewestAppUpgradeVersion(versionCode,name,type);

		if (appInfo != null) {
			data.put("version", appInfo.getVersionCode());
			data.put("appUrl", appInfo.getApkUrl());
			data.put("description", appInfo.getVersionDesc());
			data.put("versionName", appInfo.getVersionName());
			data.put("apkMd5", appInfo.getApkMd5());
			data.put("isForce", appInfo.getIsForce());
		}
		resp.setResponseData(data);
		return resp;
	}

}
