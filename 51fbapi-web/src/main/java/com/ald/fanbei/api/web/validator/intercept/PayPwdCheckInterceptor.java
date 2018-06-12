package com.ald.fanbei.api.web.validator.intercept;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public void intercept(RequestDataVo reqData, FanbeiContext context, HttpServletRequest request) {
		String payPwd = ObjectUtils.toString(reqData.getParams().get("payPwd"), "").toString();
		if (StringUtils.isNotBlank(payPwd) && !StringUtils.equals("null", payPwd)) {
			if (StringUtils.isNotBlank(payPwd)) {
				Long userId = context.getUserId();
				// need version check
				Integer version = context.getAppVersion();
				checkPayPwd(userId, payPwd, version);
			}
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

		String log = String.format("PayPwdCheckInterceptor.checkPayPwd() the params r %d,%s,%d", userId,payPwd,version);
		logger.info(log);

		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);

		log = log + String.format("the middle params r :userAccountInfo = %s ", userAccountInfo.toString());
		logger.info(log);

		String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());

		log = log + String.format("inputOldPwd =  %s", inputOldPwd);
		logger.info(log);

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

		log = log + String.format("key =  %s,key1==  %s", key,key1);
		logger.info(log);

		AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("PAY_PASSWORD_IDENTIFY",
				"INPUT_TIMES_AND_FROZEN_TIME");

		log = log + String.format("resourceDo  %s", resourceDo.toString());
		logger.info(log);

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

		// get the times and the last time
		Integer times = (Integer) bizCacheUtil.getObject(key);
		Date previousDate = (Date) bizCacheUtil.getObject(key1);

		log = log + String.format("times = %d ,previousDate = %tc", times,previousDate);
		logger.info(log);

		//if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {

			// times > specific times
			if (times != null && times >= specificTimes) {

				// if in frozen states

				Date compareDate = DateUtil.addHoures(new Date(), -specificTime);

				log = log + String.format("compareDate = %tc", compareDate);
				logger.info(log);

				if (previousDate == null || (previousDate != null && previousDate.after(compareDate))) {

					// never frozen before or still in frozen time then update
					// the times
					times = times + 1;
					bizCacheUtil.saveObjectForever(key, times);
					//bizCacheUtil.saveObjectForever(key1, new Date());

					if (previousDate == null) {
						// the setting time
						FanbeiExceptionCode exceptionCode = getErrorCodeByHoursAndMinute(specificTime, 0);
						throw new FanbeiException(exceptionCode);

					} else {
						// get the time diff
						int seconds = DateUtil.getTimeDiff(previousDate, compareDate).intValue();
						int hours = seconds / (60 * 60);
						int minutes = seconds / 60 - hours * 60;

						log = log + String.format("hours = %d, minutes = %d", hours,minutes);
						logger.info(log);

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

				     		if (previousDate != null) {
								bizCacheUtil.delCache(key);
							}
				     		return;
						}

					}

					// or not .
					times = 1;
					bizCacheUtil.saveObjectForever(key, times);

					times = specificTimes - 1;
					bizCacheUtil.saveObjectForever(key1, new Date());;

					log = log + String.format("times = %d", times);
					logger.info(log);

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

			     		if (previousDate != null) {
							bizCacheUtil.delCache(key);
						}
			     		return;
					}

				}

				// or not .

				//judge if the last_time is yesterday if yes then times=1 ,and last_tims = now .
				//previousDate
				if(previousDate != null && DateUtil.addDays(new Date(), -1).getDate() == previousDate.getDate()){

					bizCacheUtil.saveObjectForever(key, 1);
					bizCacheUtil.saveObjectForever(key1, new Date());
					times = specificTimes - 1;

					log = log + String.format("times = %d", times);
					logger.info(log);

					FanbeiExceptionCode exceptionCode = getErrorByKeyAndTimes(key, times);
					throw new FanbeiException(exceptionCode);

				}

				times = times==null ?0:times;

				times = times + 1;

				log = log + String.format("times = %d", times);
				logger.info(log);

				bizCacheUtil.saveObjectForever(key, times);
				bizCacheUtil.saveObjectForever(key1, new Date());

				if (times == specificTimes) {
					//this time is the fifth time

					FanbeiExceptionCode exceptionCode = getErrorCodeByHoursAndMinute(specificTime, 0);
					throw new FanbeiException(exceptionCode);
				}

				//this time + 1 < specific time
				times = specificTimes - times;
				FanbeiExceptionCode exceptionCode = getErrorByKeyAndTimes(key, times);
				throw new FanbeiException(exceptionCode);

			}

		//}
	}

	private FanbeiExceptionCode getErrorByKeyAndTimes(String key, Integer times) {
		FanbeiExceptionCode exceptionCode = FanbeiExceptionCode.PAYPWD_ERROR_LESS_THAN_SPECIFIC_TIMES;
		String mString = "支付密码错误，您还有x次机会！".replace("x", times + "");
		exceptionCode.setDesc(mString);
		return exceptionCode;
	}

	private FanbeiExceptionCode getErrorCodeByHoursAndMinute(Integer hours, Integer minutes) {
		FanbeiExceptionCode exceptionCode = FanbeiExceptionCode.PAYPWD_ERROR_MORE_THAN_SPECIFIC_TIMES;
		String mString = "您已多次尝试失败，暂时被锁定，请x小时y分后再试或更改密码！".replace("x", hours + "").replace("y", minutes + "");
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
