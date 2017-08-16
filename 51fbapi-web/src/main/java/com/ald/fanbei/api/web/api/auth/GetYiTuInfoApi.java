/**
 * 
 */
package com.ald.fanbei.api.web.api.auth;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserApiCallLimitService;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.RSAHelper.PublicKeyException;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ApiCallType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserApiCallLimitDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年5月9日下午3:36:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getYiTuInfoApi")
public class GetYiTuInfoApi implements ApiHandle {
	@Resource
	AfUserApiCallLimitService afUserApiCallLimitService;
	@Resource
	AfResourceService afResourceService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Map<String, Object> data = new HashMap<String, Object>();

		AfUserApiCallLimitDo faceDo = afUserApiCallLimitService.selectByUserIdAndType(userId, ApiCallType.YITU_FACE.getCode());
		AfUserApiCallLimitDo cardDo = afUserApiCallLimitService.selectByUserIdAndType(userId, ApiCallType.YITU_CARD.getCode());
		Integer maxNum = NumberUtil.objToIntDefault(afResourceService.getConfigByTypesAndSecType(Constants.API_CALL_LIMIT, ApiCallType.YITU_CARD.getCode()).getValue(), 0);
		Integer maxfaceNum = NumberUtil.objToIntDefault(afResourceService.getConfigByTypesAndSecType(Constants.API_CALL_LIMIT, ApiCallType.YITU_FACE.getCode()).getValue(), 0);

		data.put("cardMaxNum", maxNum);
		data.put("faceMaxNum", maxfaceNum);

		if(faceDo ==null){
			data.put("faceCount", 0);
		}else{
			data.put("faceCount", faceDo.getCallNum());
		}
		if(cardDo==null){
			data.put("cardCount", 0);
		}else{
			data.put("cardCount", cardDo.getCallNum());
		}
		try {
			PublicKey publicKey = EncryptionHelper.RSAHelper.loadPublicKey(ConfigProperties.get(Constants.CONFKEY_YITU_PEM_PATH));
			data.put("publicKey", publicKey);

		} catch (PublicKeyException e) {
			e.printStackTrace();
			return new  ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
		}

		data.put("accessId", ConfigProperties.get(Constants.CONFKEY_YITU_ID));
		data.put("accessKey", ConfigProperties.get(Constants.CONFKEY_YITU_KEY));
		data.put("url", ConfigProperties.get(Constants.CONFKEY_YITU_URL));
		resp.setResponseData(data);
		return resp;
	}

}
