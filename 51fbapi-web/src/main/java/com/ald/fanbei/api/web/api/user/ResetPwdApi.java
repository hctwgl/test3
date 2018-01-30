package com.ald.fanbei.api.web.api.user;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
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
@Component("resetPwdApi")
public class ResetPwdApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;

	@Resource
	RiskUtil riskUtil;
	
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        String userName = context.getUserName();
        String passwordSrc = ObjectUtils.toString(requestDataVo.getParams().get("password"));
        String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
        
        String ip = CommonUtil.getIpAddr(request);
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
        String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"));
		String phoneType = ObjectUtils.toString(requestDataVo.getParams().get("phoneType"));
		String networkType = ObjectUtils.toString(requestDataVo.getParams().get("networkType"));
		String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"));
		
        if(StringUtil.isBlank(passwordSrc)){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(context.getUserName(), SmsType.FORGET_PASS.getCode());
        if(smsDo == null){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        //判断验证码是否一致并且验证码是否已经做过验证
        String realCode = smsDo.getVerifyCode();
        if(!StringUtils.equals(verifyCode, realCode)){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
        }
        //判断验证码是否过期
        if(DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE);
        }
        
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        if(afUserDo == null){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
        }
        
        String salt = UserUtil.getSalt();
        String password = UserUtil.getPassword(passwordSrc, salt);
        AfUserDo userDo = new AfUserDo();
        userDo.setRid(afUserDo.getRid());
        userDo.setSalt(salt);
        userDo.setPassword(password);
        userDo.setFailCount(0);
        userDo.setUserName(userName);
        afUserService.updateUser(userDo);
        // 添加风控可信
        if (context.getAppVersion() >= 381) {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	String registerTime = sdf.format(new Date(System.currentTimeMillis()));
        	try{
        		riskUtil.verifyASyRegister(ObjectUtils.toString(afUserDo.getRid(), ""), userName, blackBox, uuid,
						registerTime, ip, phoneType, networkType, osType,Constants.EVENT_RIGISTER_ASY,bqsBlackBox);
        	} catch (Exception e){
        		logger.error(e.getMessage());
        	}
		}
        return resp;
    }
    

}
