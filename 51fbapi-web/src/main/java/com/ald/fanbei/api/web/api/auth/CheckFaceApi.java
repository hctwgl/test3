package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.AfTaskType;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.YituFaceLivingRespBo;

import com.ald.fanbei.api.biz.third.util.yitu.YituUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ApiCallType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfUserApiCallLimitDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("checkFaceApi")
public class CheckFaceApi implements ApiHandle {

	@Resource
	YituUtil yituUtil;
	@Resource
	AfIdNumberService afIdNumberService;

	@Resource
	AfUserApiCallLimitService afUserApiCallLimitService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfTaskUserService afTaskUserService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String packages = (String) requestDataVo.getParams().get("faces");

		AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(context.getUserId());
		if (idNumberDo == null||StringUtils.isBlank(idNumberDo.getIdFrontUrl())) {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_INFO_EXIST_ERROR);
		}

		AfUserApiCallLimitDo callLimitDo = afUserApiCallLimitService.selectByUserIdAndType(context.getUserId(), ApiCallType.YITU_FACE.getCode());
		if (callLimitDo == null) {
			callLimitDo = new AfUserApiCallLimitDo();
			callLimitDo.setType(ApiCallType.YITU_FACE.getCode());
			callLimitDo.setUserId(context.getUserId());
			afUserApiCallLimitService.addUserApiCallLimit(callLimitDo);
		}
		if (callLimitDo.getDisableStatus().equals(YesNoStatus.YES.getCode())) {
			throw new FanbeiException(FanbeiExceptionCode.API_CALL_NUM_OVERFLOW);
		}
		try {
			YituFaceLivingRespBo bo = yituUtil.checkLiving(packages, idNumberDo.getIdFrontUrl());
			afUserApiCallLimitService.addVisitNum(context.getUserId(), ApiCallType.YITU_FACE.getCode());
			Integer maxNum = NumberUtil.objToIntDefault(afResourceService.getConfigByTypesAndSecType(Constants.API_CALL_LIMIT, ApiCallType.YITU_FACE.getCode()).getValue(), 0);
			if (maxNum - callLimitDo.getCallNum() <= 0) {
				callLimitDo.setDisableStatus(YesNoStatus.YES.getCode());
				afUserApiCallLimitService.updateUserApiCallLimit(callLimitDo);
			}
			if (bo.getPair_verify_result().equals(YituFaceLivingRespBo.RESULT_TRUE)) {
				AfUserAuthDo auth = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
				auth.setFacesStatus(YesNoStatus.YES.getCode());
				auth.setYdStatus(YesNoStatus.YES.getCode());
				afUserAuthService.updateUserAuth(auth);

				// add by luoxiao 边逛边赚，实名认证通过送奖励
				afTaskUserService.taskHandler(context.getUserId(), AfTaskType.VERIFIED.getCode(), null);
				// end by luoxiao
				return resp;
			} else {
				throw new FanbeiException(FanbeiExceptionCode.USER_FACE_AUTH_ERROR);
			}
		} catch (Exception e) {
			throw new FanbeiException(FanbeiExceptionCode.USER_FACE_AUTH_ERROR);
		}

	}

}
