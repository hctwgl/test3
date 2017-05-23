/**
 * 
 */
package com.ald.fanbei.api.web.api.auth;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserApiCallLimitService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ApiCallType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.AfUserApiCallLimitDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：获取有盾信息
 * @author suweili 2017年3月13日上午11:08:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authYdInfoApi")
public class AuthYdInfoApi implements ApiHandle {

	@Resource
	AfUserApiCallLimitService afUserApiCallLimitService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
//		if (context.getAppVersion() >= 343) {
//			AfUserApiCallLimitDo callLimitDo = afUserApiCallLimitService.selectByUserIdAndType(userId, ApiCallType.YOUDUN.getCode());
//			if (callLimitDo == null) {
//				callLimitDo = new AfUserApiCallLimitDo();
//				callLimitDo.setType(ApiCallType.YOUDUN.getCode());
//				callLimitDo.setUserId(userId);
//				afUserApiCallLimitService.addUserApiCallLimit(callLimitDo);
//			}
//			if (callLimitDo.getDisableStatus().equals("Y")) {
//				throw new FanbeiException(FanbeiExceptionCode.API_CALL_NUM_OVERFLOW);
//			}
//		}

		Map<String, Object> data = new HashMap<String, Object>();

		String publicKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_YOUDUN_PUBKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));

		data.put("ydKey", publicKey);
		data.put("ydUrl", ConfigProperties.get(Constants.CONFKEY_YOUDUN_NOTIFY));
		resp.setResponseData(data);

		return resp;

	}

}
