package com.ald.fanbei.api.web.validator.intercept;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 支付密码校验拦截器
 * 
 * @author rongbo
 *
 */
@Component("payPwdCheckInterceptor")
public class PayPwdCheckInterceptor implements Interceptor {

	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfResourceService afResourceService;
	
	@Override
	public void intercept(RequestDataVo reqData, FanbeiContext context, HttpServletRequest request) {
		String payPwd = ObjectUtils.toString(reqData.getParams().get("payPwd"), "").toString();
		if(StringUtils.isBlank(payPwd)) {
			payPwd = ObjectUtils.toString(reqData.getParams().get("pwd"), "").toString();
		}
		if (StringUtils.isNotBlank(payPwd)) {
			Long userId = context.getUserId();
			
			//need version check
			Integer version = context.getAppVersion();
			checkPayPwd(userId, payPwd,version);
		}
	}

	@Override
	public void intercept(Context context) {
		String payPwd = ObjectUtils.toString(context.getData("payPwd"));
		if (StringUtils.isNotBlank(payPwd)) {
			Long userId = context.getUserId();
			
			//need version check
			Integer version = context.getAppVersion();
			checkPayPwd(userId, payPwd,version);
		}
	}
	
	private void  checkPayPwd(Long userId, String payPwd,Integer version) {
		if(userId == null) {
			throw new FanbeiException("请登录后再支付！", true);
		}
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
		if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
			
			if (version >= 408) {
				//the times 
				String key = Constants.CACHKEY_WRONG_INPUT_PAYPWD_TIMES + userId;
				//the previous time
				String key1 = Constants.CACHKEY_THE_LAST_WRONG_PAYPWD_TIME + userId;
				AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("PAY_PASSWORD_IDENTIFY", "INPUT_TIMES_AND_FROZEN_TIME");
				if (resourceDo == null ) {
					//PAYPWD_ERROR_SETTING_EMPTY
					throw new FanbeiException(FanbeiExceptionCode.PAYPWD_ERROR_SETTING_EMPTY);
				}
				String strSpecificTimes = resourceDo.getValue();
				String strSpecificTime = resourceDo.getValue1();
				if (StringUtils.isBlank(strSpecificTime) && StringUtils.isBlank(strSpecificTimes)) {
					throw new FanbeiException(FanbeiExceptionCode.PAYPWD_ERROR_SETTING_EMPTY);
				}
				Integer specificTimes = Integer.parseInt(resourceDo.getValue());
				Integer specificTime = Integer.parseInt(resourceDo.getValue1());
				
				//TODO: add the wrong times and update the time.
				Integer times = 0;
				times = (Integer)bizCacheUtil.getObject(key);
				if (times != null) {
					if (times >= specificTimes) {
						//TODO:is more than specific (such as 5) times return the times and time make a specific exception code to remind the front side 
						Date date = (Date)bizCacheUtil.getObject(key1);
						
						//TODO:is more than specific (such as 5) times but it has past the frozen time
						times = 1;
						bizCacheUtil.saveObject(key, times);
					}
	             	times = times +1;
	             	//TODO:if the times is less than specific (such as 5) times then return the times 
	      	
	             	
	             	
	     		}else{
	     			//if the times is less than specific (such as 5) times then return the times 
	     			times = 1;
	     			bizCacheUtil.saveObject(key, times);
	     			times = specificTimes - 1;
	     			FanbeiExceptionCode exceptionCode = FanbeiExceptionCode.PAYPWD_ERROR_LESS_THAN_SPECIFIC_TIMES;
	     			String mString = exceptionCode.getErrorMsg().replace("x", times+"");
	     			exceptionCode.setDesc(mString);
	     			throw new FanbeiException(exceptionCode);
	     			
	     		}
				
				
				
				
				
				
				throw new FanbeiException(FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}
        //----------------------mqp clear password times (if the pwd is right )-------------
		  if (userId != null) {
	        	 String key = Constants.CACHKEY_WRONG_INPUT_PAYPWD_TIMES + userId;
	             Integer times = (Integer)bizCacheUtil.getObject(key);
	             if (times != null) {
	             	bizCacheUtil.delCache(key);
	     		}
			}
        //----------------------mqp clear password times ------------------------------------
	}
	
}
