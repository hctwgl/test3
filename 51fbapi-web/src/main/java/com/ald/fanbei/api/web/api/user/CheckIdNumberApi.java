/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年2月17日下午6:09:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkIdNumberApi")
public class CheckIdNumberApi implements ApiHandle {

	@Resource
	AfUserAccountService afUserAccountService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		
		Map<String, Object> params = requestDataVo.getParams();
		String idNumber = ObjectUtils.toString(params.get("idNumber"), "").toString();
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		if(afUserAccountDo==null){
			throw new FanbeiException("account id is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);

		}
		if (StringUtils.equals(idNumber, afUserAccountDo.getIdNumber())) {
			return resp;

		}
		throw new FanbeiException("idNumber id is invalid", FanbeiExceptionCode.USER_ACCOUNT_IDNUMBER_INVALID_ERROR);

		
	}

}
