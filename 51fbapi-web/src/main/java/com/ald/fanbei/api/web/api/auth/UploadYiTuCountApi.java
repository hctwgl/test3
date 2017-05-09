/**
 * 
 */
package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserApiCallLimitService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ApiCallType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserApiCallLimitDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年5月9日下午4:54:03
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("uploadYiTuCountApi")
public class UploadYiTuCountApi implements ApiHandle {
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserApiCallLimitService afUserApiCallLimitService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String type = (String) requestDataVo.getParams().get("type");
		if(ApiCallType.findRoleTypeByCode(type)==null){
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.PARAM_ERROR);
		}
		
		AfUserApiCallLimitDo callLimitDo =	afUserApiCallLimitService.selectByUserIdAndType(context.getUserId(), type);
		if (callLimitDo == null) {
			callLimitDo = new AfUserApiCallLimitDo();
			callLimitDo.setType(type);
			callLimitDo.setUserId(context.getUserId());
			afUserApiCallLimitService.addUserApiCallLimit(callLimitDo);
		}
		Integer count = callLimitDo.getCallNum()+1;
		Integer maxNum = NumberUtil.objToIntDefault(afResourceService.getConfigByTypesAndSecType(Constants.API_CALL_LIMIT, type).getValue(), 0);
		if (maxNum - count <= 0) {
			callLimitDo.setDisableStatus(YesNoStatus.YES.getCode());
		}
		callLimitDo.setCallNum(count);
		afUserApiCallLimitService.updateUserApiCallLimit(callLimitDo);
		resp.addResponseData("count", count);
		return resp;
	}

}
