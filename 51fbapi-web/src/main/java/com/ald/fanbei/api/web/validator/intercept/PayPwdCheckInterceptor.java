package com.ald.fanbei.api.web.validator.intercept;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.context.Context;
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
				
				
				//TODO: add the wrong times and update the time.
				Integer times = 0;
				times = (Integer)bizCacheUtil.getObject(key);
				if (times != null) {
					if (times >= 5) {
						//TODO:is more than specific (such as 5) times return the times and time make a specific exception code to remind the front side 
					}
	             	times = times +1;
	             	//TODO:if the times is less than specific (such as 5) times then return the times 
	             	
	             	
	     		}else{
	     			times = 1;
	     			bizCacheUtil.saveObject(key, times);
	     			//TODO:if the times is less than specific (such as 5) times then return the times 
	     			
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
