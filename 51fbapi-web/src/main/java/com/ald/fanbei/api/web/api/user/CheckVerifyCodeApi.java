package com.ald.fanbei.api.web.api.user;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
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
	
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
        String userName = context.getUserName();
        int type = NumberUtil.objToIntDefault(requestDataVo.getParams().get("type"), -1);
        if(StringUtil.isBlank(verifyCode) || type < 0){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR); 
        }
        
        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(userName, type);
        
        if(smsDo == null){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_NOTEXIST);
        }
        
        //判断验证码是否一致
        String realCode = smsDo.getParams().split(",")[0];
        if(!StringUtils.equals(verifyCode, realCode)){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_ERROR);
        }
        //判断验证码是否过期
        if(DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_2HOURS))){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE);
        }
        //更新为已经验证
        afSmsRecordService.updateSmsIsCheck(smsDo.getRid());
        
        return resp;
    }
    

}
