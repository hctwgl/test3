/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @类描述：
 * @author Jiang Rongbo 2017年9月7日下午6:09:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkMobileRegisteredApi")
public class CheckMobileRegisteredApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		Map<String, Object> params = requestDataVo.getParams();
		Map<String, Object> data = new HashMap<String, Object>();
		String mobile = ObjectUtils.toString(params.get("mobile"), "");
		Pattern numPattern = Pattern.compile("^1[3|4|5|6|7|8|9][0-9]{9}$");
		Matcher matcher = numPattern.matcher(mobile);
		if(StringUtils.isEmpty(mobile)){
			throw new FanbeiException("mobile can't be null", FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		if(!matcher.matches()) {
			throw new FanbeiException("mobile not allowed",FanbeiExceptionCode.REQUEST_PARAM_ILLEGAL);
		}
		
		data.put("isMember", "N"); // 默认未注册
		// 查询手机号是否已注册
		Long userId = afUserService.getUserIdByMobile(mobile);
		if(userId !=  null) {
			data.put("isMember", "Y");
		}
		resp.setResponseData(data);
		return resp;
	}

}
