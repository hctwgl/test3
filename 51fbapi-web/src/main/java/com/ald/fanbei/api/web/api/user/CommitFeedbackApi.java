/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfFeedBackService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfFeedBackDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月4日下午1:52:49
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("commitFeedbackApi")
public class CommitFeedbackApi implements ApiHandle {

	@Resource
	AfFeedBackService afFeedBackService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> params = requestDataVo.getParams();
		String content = ObjectUtils.toString(params.get("content"), "").toString();
		AfFeedBackDo feedBackDo = new AfFeedBackDo();
		feedBackDo.setUserId(userId);
		feedBackDo.setContent(content);
		if (afFeedBackService.addFeedBack(feedBackDo) > 0) {
			return resp;
		}

		throw new FanbeiException(FanbeiExceptionCode.FAILED);

	}

}
