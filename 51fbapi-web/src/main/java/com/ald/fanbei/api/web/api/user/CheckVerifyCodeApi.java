package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午1:49:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkVerifyCodeApi")
public class CheckVerifyCodeApi implements ApiHandle {

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
        String userName = context.getUserName();
        String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
        if(StringUtil.isBlank(verifyCode) || StringUtil.isBlank(type)){
        	logger.error("verifyCode or type is empty verifyCode = " + verifyCode + " type = " + type);
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR); 
        }

        smsUtil.checkSmsByMobileAndType(userName, requestDataVo.getParams());
        
//        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(userName, type);
//        
//        if(smsDo == null){
//        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_NOTEXIST);
//        }
//        
//        //判断验证码是否一致
//        String realCode = smsDo.getVerifyCode();
//        if(!StringUtils.equals(verifyCode, realCode)){
//        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_ERROR);
//        }
//        //判断验证码是否过期
//        if(DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))){
//        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE);
//        }
//        //更新为已经验证
//        afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
        
        return resp;
    }
    

}
