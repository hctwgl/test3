/**
 * 
 */
package com.ald.fanbei.api.web.api.system;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

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
        AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.RES_IS_FOR_AUTH);
        if (resourceInfo == null) {
        	resp.addResponseData("isForAuth", YesNoStatus.NO.getCode());
        } 
        //需要打开为了审核的相关版本
        List<String> needAuthVersion = Arrays.asList(resourceInfo.getValue().split(","));
        if (needAuthVersion.contains(context.getAppVersion() + StringUtils.EMPTY)) {
        	resp.addResponseData("isForAuth" , YesNoStatus.YES.getCode());
        } else {
        	resp.addResponseData("isForAuth" , YesNoStatus.NO.getCode());
        }
        return resp;
	}

}
