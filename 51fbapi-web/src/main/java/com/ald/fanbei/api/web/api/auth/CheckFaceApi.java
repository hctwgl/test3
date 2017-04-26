package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.YituFaceLivingRespBo;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.third.util.yitu.YituUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("checkFaceApi")
public class CheckFaceApi implements ApiHandle {

	@Resource
	YituUtil yituUtil;
	@Resource
	AfIdNumberService afIdNumberService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String packages = (String) requestDataVo.getParams().get("faces");

		AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(context.getUserId());
		if (idNumberDo == null||StringUtils.isBlank(idNumberDo.getIdFrontUrl())) {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_INFO_EXIST_ERROR);
		}
		try {
			YituFaceLivingRespBo bo = yituUtil.checkLiving(packages, idNumberDo.getIdFrontUrl());
			if (bo.getPair_verify_result().equals(YituFaceLivingRespBo.RESULT_TRUE)) {
				return resp;
			} else {
				throw new FanbeiException(FanbeiExceptionCode.USER_FACE_AUTH_ERROR);
			}
		} catch (Exception e) {
			throw new FanbeiException(FanbeiExceptionCode.USER_FACE_AUTH_ERROR);
		}

	}

}
