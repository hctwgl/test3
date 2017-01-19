package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.SmsSendUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午7:51:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getVerifyCodeApi")
public class GetVerifyCodeApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	SmsSendUtil smsSendUtil;
	
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"));
        int type = NumberUtil.objToIntDefault(requestDataVo.getParams().get("type"), -1);
        if(StringUtils.isBlank(mobile) || type < 0){
        	return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.PARAM_ERROR);
        }
        if(!CommonUtil.isMobile(mobile)){
        	return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_INVALID_MOBILE_NO);
        }
        
        AfUserDo afUserDo = afUserService.getUserByUserName(mobile);
        
        switch (type) {
		case 1://注册短信
	        if(afUserDo != null){
	        	return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_HAS_REGIST_ERROR);
	        }
	        boolean result = smsSendUtil.sendVerifyCodeReg(mobile, CommonUtil.getRandomNumber(6));
	        if(!result){
	        	return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_SEND_SMS_ERROR);
	        }
			break;
		case 2://忘记密码
			if(afUserDo == null){
				return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
			}
			boolean result1 = smsSendUtil.sendVerifyCodeFindPass(mobile, CommonUtil.getRandomNumber(6));
			if(!result1){
	        	return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_SEND_SMS_ERROR);
	        }
			break;
		default:
			break;
		}
        
        

        

        
        return resp;
    }
    

}
