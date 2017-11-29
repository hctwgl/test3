package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Map<String,String> data = new HashMap<String,String>();
		AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.FACE_TYPE);
		AfResourceDo resourceInfoV1 = afResourceService.getSingleResourceBytype(Constants.SWITCH);
		String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"));
		String type = YITU;
		String switch_type = "N";
		if(resourceInfoV1 != null){
			if (YesNoStatus.NO.getCode().equals(resourceInfoV1.getValue())) {
				switch_type = "N";
			}else if(YesNoStatus.YES.getCode().equals(resourceInfoV1.getValue())){
				switch_type = "Y";
			}
		}else{
			switch_type = "N";
		}
		if (resourceInfo != null) {
			if (YesNoStatus.NO.getCode().equals(resourceInfo.getValue())) {
				type = FACE_PLUS;
			}
		}
		if(context.getAppVersion()==392&&requestDataVo.getId().startsWith("i")){
			type=YITU;
		}
		data.put("type",type);
		data.put("switchType",switch_type);
		resp.setResponseData(data);
		return resp;
	}

}
