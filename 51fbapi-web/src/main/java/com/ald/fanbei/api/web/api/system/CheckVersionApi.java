/**
 * 
 */
package com.ald.fanbei.api.web.api.system;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CheckVersionBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;

/**
 * 提供给验证版本使用
 * @类描述：
 * @author xiaotianjian 2017年4月16日下午3:51:13
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkVersionApi")
public class CheckVersionApi implements ApiHandle {
	
	@Resource
	AfResourceService afResourceService;
 	

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        Map<String, Object> params = requestDataVo.getParams();
        AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.RES_IS_FOR_AUTH);
        if (resourceInfo == null) {
        	resp.addResponseData("isForAuth", YesNoStatus.NO.getCode());
        } 
        //需要打开为了审核的相关版本
        //VALUE是为了IOS审核
        if(requestDataVo.getId().startsWith("i")) {
        	List<String> needAuthVersion = Arrays.asList(resourceInfo.getValue().split(","));
        	if (needAuthVersion.contains(context.getAppVersion() + StringUtils.EMPTY)) {
        		resp.addResponseData("isForAuth" , YesNoStatus.YES.getCode());
        	} else {
        		resp.addResponseData("isForAuth" , YesNoStatus.NO.getCode());
        	}
        } else {
        //VALUE1是为了Android审核
        	String androidCheckVersion = resourceInfo.getValue1();
        	if (StringUtils.isBlank(androidCheckVersion)) {
        		resp.addResponseData("isForAuth", YesNoStatus.NO.getCode());
        	} else {
        		List<CheckVersionBo> array = JSONArray.parseArray(androidCheckVersion, CheckVersionBo.class);
        		String channelCode = ObjectUtils.toString(params.get("channelCode"), null);
        		CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
        		if (array.contains(desVersion)) {
        			resp.addResponseData("isForAuth", YesNoStatus.YES.getCode());
        		} else {
        			resp.addResponseData("isForAuth", YesNoStatus.NO.getCode());
        		}
        	}
        }
        return resp;
	}

}
