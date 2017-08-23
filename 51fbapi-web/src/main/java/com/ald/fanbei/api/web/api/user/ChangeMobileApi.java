/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年2月16日下午7:09:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("changeMobileApi")
public class ChangeMobileApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	
	@Resource
	SmsUtil smsUtil;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		  ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
			Long userId = context.getUserId();

	        String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
	        String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"));
	        String userName = ObjectUtils.toString(requestDataVo.getSystem().get("userName"));
	        if(StringUtil.isBlank(verifyCode) || StringUtil.isBlank(mobile)){
	        	logger.error("changeMobile verifyCode or mobile is empty verifyCode = " + verifyCode + " mobile = " + mobile);
	        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR); 
	        }
	        AfUserDo userDo = afUserService.getUserByUserName(userName);
	        //验证原手机验证码
	        smsUtil.checkSmsByMobileAndType(userDo.getMobile(),verifyCode, SmsType.MOBILE_BIND);
	        AfUserDo afUserDo = new AfUserDo();
			afUserDo.setRid(userId);
			afUserDo.setMobile(mobile);
			afUserDo.setUserName(context.getUserName());
			if (afUserService.updateUser(afUserDo) > 0) {
				return resp;
			}
			
			throw new FanbeiException("change Mobile  info failed", FanbeiExceptionCode.SYSTEM_ERROR);

	}

}
