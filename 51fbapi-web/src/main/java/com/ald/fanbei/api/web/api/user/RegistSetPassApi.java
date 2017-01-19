package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
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
@Component("registSetPassApi")
public class RegistSetPassApi implements ApiHandle {

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
        if(StringUtil.isBlank(passwordSrc)){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(context.getUserName(), SmsType.REGIST.getCode());
        if(smsDo == null){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        //判断验证码是否一致并且验证码是否已经做过验证
        String realCode = smsDo.getParams().split(",")[0];
        if(!StringUtils.equals(verifyCode, realCode) || smsDo.getIsCheck() == 0){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        
        String salt = UserUtil.getSalt();
        String password = UserUtil.getPassword(passwordSrc, salt);
        
        AfUserDo userDo = new AfUserDo();
        userDo.setSalt(salt);
        userDo.setUserName(userName);
        userDo.setMobile(userName);
        userDo.setPassword(password);
        userDo.setNick(userName);
        
        afUserService.addUser(userDo);
//        Long invteLong = Constants.INVITE_START_VALUE + userDo.getRid();
        //TODO 优化邀请码规则
//        String inviteCode = Long.toString(invteLong, 36);
//        AfUserDo afUserDo = new AfUserDo();
//        afUserDo.setRid(userDo.getRid());
//        afUserDo.setRecommendCode("");
//        afUserService.updateUser(afUserDo);
        
        AfUserAccountDo account = new AfUserAccountDo();
        account.setUserId(userDo.getRid());
        account.setUserName(userDo.getUserName());
        afUserAccountService.addUserAccount(account);
        return resp;
    }
}
