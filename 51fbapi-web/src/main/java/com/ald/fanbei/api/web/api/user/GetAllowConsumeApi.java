/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：获取能否分期状态
 * @author xiaotianjian 2017年3月31日下午8:39:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAllowConsumeApi")
public class GetAllowConsumeApi implements ApiHandle {

	@Resource
	AfUserAuthService afUserAuthService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		AfUserAuthDo autDo =afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
		if(autDo==null){
			throw new FanbeiException("authDo id is invalid", FanbeiExceptionCode.PARAM_ERROR);

		}
		resp.addResponseData("allowConsume",afUserAuthService.getConsumeStatus(context.getUserId(), context.getAppVersion()));
		resp.addResponseData("bindCardStatus",autDo.getBankcardStatus());

		
		return resp;
	}
}
