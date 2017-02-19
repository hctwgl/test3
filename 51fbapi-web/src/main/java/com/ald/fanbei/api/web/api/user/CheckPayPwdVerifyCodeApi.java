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
 * @author suweili 2017年2月17日下午6:09:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkPayPwdVerifyCodeApi")
public class CheckPayPwdVerifyCodeApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	SmsUtil smsUtil;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		 ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
	        String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
//	        String userName = context.getUserName();
	        if(context.getUserId()==null){
				throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);

	        }

	        if(StringUtil.isBlank(verifyCode)){
	        	logger.error("verifyCode or type is empty verifyCode = " + verifyCode );
	        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR); 
	        }
	      
			AfUserDo userDo = afUserService.getUserById(context.getUserId());
			
//	        Map<String, Object> map =requestDataVo.getParams();
//	        map.put("type",SmsType.SET_PAY_PWD.getCode() );
//	        map.put("mobile",userDo.getMobile());

	        smsUtil.checkSmsByMobileAndType(userDo.getMobile(), verifyCode,SmsType.SET_PAY_PWD);
	        return resp;
	}

}
