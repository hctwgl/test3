package com.ald.fanbei.api.web.validator.intercept;

import java.util.Calendar;
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
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

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
		if (StringUtils.isBlank(payPwd)) {
			payPwd = ObjectUtils.toString(reqData.getParams().get("pwd"), "").toString();
		}
		if (StringUtils.isNotBlank(payPwd)) {
			Long userId = context.getUserId();

			// need version check
			Integer version = context.getAppVersion();
			checkPayPwd(userId, payPwd, version);
		}
	}

	@Override
	public void intercept(Context context) {
		String payPwd = ObjectUtils.toString(context.getData("payPwd"));
		if (StringUtils.isNotBlank(payPwd)) {
			Long userId = context.getUserId();

			// need version check
			Integer version = context.getAppVersion();
			checkPayPwd(userId, payPwd, version);
		}
	}

	/**
	 * 
	 * @Title: checkPayPwd @author qiao @date 2018年3月1日 下午2:35:39 @Description:
	 * 验证密码错误业务逻辑 @param userId @param payPwd @param version @return
	 * void @throws
	 */
	private void checkPayPwd(Long userId, String payPwd, Integer version) {
		if (userId == null) {
			throw new FanbeiException("请登录后再支付！", true);
		}
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
		if (version < 408) {
			// the old version
			if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
				throw new FanbeiException(FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
			return;
		}

		// the new version

		// the times
		String key = Constants.CACHKEY_WRONG_INPUT_PAYPWD_TIMES + userId;
		// the previous time
		String key1 = Constants.CACHKEY_THE_LAST_WRONG_PAYPWD_TIME + userId;

		AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("PAY_PASSWORD_IDENTIFY",
				"INPUT_TIMES_AND_FROZEN_TIME");
		if (resourceDo == null) {
			// PAYPWD_ERROR_SETTING_EMPTY
			throw new FanbeiException(FanbeiExceptionCode.PAYPWD_ERROR_SETTING_EMPTY);
		}

		String strSpecificTimes = resourceDo.getValue();
		String strSpecificTime = resourceDo.getValue1();

		if (StringUtils.isBlank(strSpecificTime) && StringUtils.isBlank(strSpecificTimes)) {
			throw new FanbeiException(FanbeiExceptionCode.PAYPWD_ERROR_SETTING_EMPTY);
		}

		Integer specificTimes = Integer.parseInt(resourceDo.getValue());
		Integer specificTime = Integer.parseInt(resourceDo.getValue1());

		// get the times
		Integer times = (Integer) bizCacheUtil.getObject(key);

		if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {

			// times > specific times
			if (times != null && times >= specificTime) {

				// if in frozen states
				Date previousDate = (Date) bizCacheUtil.getObject(key1);
				Date compareDate = DateUtil.addHoures(new Date(), -specificTime);

				if (previousDate == null || (previousDate != null && previousDate.after(compareDate))) {

					// never frozen before or still in frozen time then update
					// the times and time
					times = times + 1;
					bizCacheUtil.saveObject(key, times);
					bizCacheUtil.saveObject(key1, new Date());

					if (previousDate == null) {
						// the setting time
						FanbeiExceptionCode exceptionCode = getErrorCodeByHoursAndMinute(specificTime, 0);
						throw new FanbeiException(exceptionCode);

					} else {
						// get the time diff
						int seconds = DateUtil.getTimeDiff(new Date(), previousDate).intValue();
						int hours = seconds / (60 * 60);
						int minutes = seconds / 60 - hours * 60;
						FanbeiExceptionCode exception = getErrorCodeByHoursAndMinute(hours, minutes);
						throw new FanbeiException(exception);

					}

				} else {
					// is more than specific (such as 5) times but it has past
					// the frozen time:

					// judge if the paypwd is right
					if (StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
						if (userId != null) {
							if (times != null) {
								bizCacheUtil.delCache(key);
							}
						}
						return;
					}

					// or not .
					times = 1;
					times = specificTimes - 1;
					FanbeiExceptionCode exceptionCode = getErrorByKeyAndTimes(key, times);
					throw new FanbeiException(exceptionCode);

				}

			} else {
				// times is null or times < specificTime
				
				// judge if the paypwd is right
				if (StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
					if (userId != null) {
						if (times != null) {
							bizCacheUtil.delCache(key);
						}
					}
					return;
				}
				
				// or not .
				times = times==null ?0:times;
				
				times = times + 1; 
				
				bizCacheUtil.saveObject(key, times);
				
				if (times == specificTime) {
					//this time is the fifth time
					
					// the setting time
					bizCacheUtil.saveObject(key1, new Date());
					
					FanbeiExceptionCode exceptionCode = getErrorCodeByHoursAndMinute(specificTime, 0);
					throw new FanbeiException(exceptionCode);
				}
				
				//this time + 1 < specific time
				times = specificTimes - 1;
				FanbeiExceptionCode exceptionCode = getErrorByKeyAndTimes(key, times);
				throw new FanbeiException(exceptionCode);

			}

		}
	}

	private FanbeiExceptionCode getErrorByKeyAndTimes(String key, Integer times) {
		bizCacheUtil.saveObject(key, times);
		FanbeiExceptionCode exceptionCode = FanbeiExceptionCode.PAYPWD_ERROR_LESS_THAN_SPECIFIC_TIMES;
		String mString = exceptionCode.getErrorMsg().replace("x", times + "");
		exceptionCode.setDesc(mString);
		return exceptionCode;
	}

	private FanbeiExceptionCode getErrorCodeByHoursAndMinute(Integer hours, Integer minutes) {
		FanbeiExceptionCode exceptionCode = FanbeiExceptionCode.PAYPWD_ERROR_MORE_THAN_SPECIFIC_TIMES;
		String mString = exceptionCode.getErrorMsg().replace("x", hours + "").replace("y", minutes + "");
		exceptionCode.setDesc(mString);
		return exceptionCode;
	}

	public static void main(String[] args) {
		int seconds = 10000;
		int hours = seconds / (60 * 60);
		int minutes = seconds / 60 - hours * 60;
		System.out.println("小时" + hours + ";分钟为" + minutes);
	}

}
