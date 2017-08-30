package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
 * 
 * @类描述： 获取是人脸识别SDK类型 或者为依图或者为face++
 * @author xiaotianjian 2017年7月24日上午11:06:44
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getFaceTypeApi")
public class GetFaceTypeApi implements ApiHandle {

	private static final String FACE_PLUS="FACE_PLUS";
	private static final String YITU="YITU";
	
	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.FACE_TYPE);
		String type = YITU;
		if (resourceInfo != null) {
			if (YesNoStatus.NO.getCode().equals(resourceInfo.getValue())) {
				type = FACE_PLUS;
			}
		}
		resp.addResponseData("type", type);
		return resp;
	}

}
