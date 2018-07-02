package com.ald.fanbei.api.web.api.goods;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserSearchService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @date 2017-9-7 17:35:31
 * @author qiaopan
 * @description 删除用户搜索历史
 *
 */
@Component("deleteHistoryApi")
public class DeleteHistoryApi implements ApiHandle {

	@Resource
	AfUserSearchService afUserSearchService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		try {
			ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

			Long userId = context.getUserId();
			if (userId == null) {
				throw new FanbeiException("without login error", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
			}
			afUserSearchService.deleteHistory(userId);
			return resp;
		} catch (FanbeiException e) {
			logger.error(e.getMessage().toString());
			throw new FanbeiException(e.getMessage(), e.getErrorCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}

	}

}
