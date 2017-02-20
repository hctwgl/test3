package com.ald.fanbei.api.web.api.user;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午1:48:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("registerSetPwdApi")
public class RegisterSetPwdApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	AfUserAccountService afUserAccountService;
	
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        String userName = context.getUserName();
        String passwordSrc = ObjectUtils.toString(requestDataVo.getParams().get("password"));
        String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
        String nick = ObjectUtils.toString(requestDataVo.getParams().get("nick"), null);
//        String recommendCode = ObjectUtils.toString(requestDataVo.getParams().get("recommendCode"), null);
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(passwordSrc)){
        	logger.error("verifyCode or type is empty userName = " + userName + " password = " + passwordSrc);
        	return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.PARAM_ERROR);
        }
        AfUserDo eUserDo = afUserService.getUserByUserName(userName);
        if(eUserDo != null){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST);

        }
        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(userName, SmsType.REGIST.getCode());
        if(smsDo == null){
        	logger.error("sms record is empty");
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        //判断验证码是否一致并且验证码是否已经做过验证
        String realCode = smsDo.getVerifyCode();
        if(!StringUtils.equals(verifyCode, realCode) || smsDo.getIsCheck() == 0){
        	logger.error("verifyCode is invalid");
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        //判断验证码是否过期
        if(DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE);
        }
        
        String salt = UserUtil.getSalt();
        String password = UserUtil.getPassword(passwordSrc, salt);
        
        AfUserDo userDo = new AfUserDo();
        userDo.setSalt(salt);
        userDo.setUserName(userName);
        userDo.setMobile(userName);
        userDo.setNick(nick);
        userDo.setPassword(password);
        afUserService.addUser(userDo);
        
        Long invteLong = Constants.INVITE_START_VALUE + userDo.getRid();
        //TODO 优化邀请码规则
        String inviteCode = Long.toString(invteLong, 36);
        userDo.setRecommendCode(inviteCode);
        afUserService.updateUser(userDo);
        
        AfUserAccountDo account = new AfUserAccountDo();
        account.setUserId(userDo.getRid());
        account.setUserName(userDo.getUserName());
        afUserAccountService.addUserAccount(account);
        return resp;
    }
    
}
