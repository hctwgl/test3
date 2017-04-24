package com.ald.fanbei.api.web.api.auth;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.YituFaceRespBo;
import com.ald.fanbei.api.biz.third.util.yitu.YituUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("checkIdCardApi")
public class CheckIdCardApi implements ApiHandle {

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String front = (String) requestDataVo.getParams().get("front");
		String back = (String) requestDataVo.getParams().get("back");
		if(StringUtils.isBlank(front)||StringUtils.isBlank(back)){
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_AUTH_ERROR);
		}
		try {
			YituFaceRespBo bo = YituUtil.checkCard(front, back);
			resp.setResponseData(bo);   
		} catch (Exception e) {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_AUTH_ERROR);
		}
		return resp;
	}

}
