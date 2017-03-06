/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfCashRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfCashRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月23日上午11:33:38
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("withdrawCashApi")
public class WithdrawCashApi implements ApiHandle {

	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfCashRecordService afCashRecordService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String account = ObjectUtils.toString(requestDataVo.getParams().get("account"));
		String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
		BigDecimal amount = new BigDecimal(ObjectUtils.toString(requestDataVo.getParams().get("amount")));
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();

		if (userId == null || StringUtils.isEmpty(account) || StringUtils.isEmpty(type)) {
			throw new FanbeiException("user id or account or type is  empty", FanbeiExceptionCode.PARAM_ERROR);
		}
		if (!StringUtils.equals(type, UserAccountLogType.REBATE_JFB.getCode())
				&& !StringUtils.equals(type, UserAccountLogType.REBATE_CASH.getCode())) {
			throw new FanbeiException(" type is error", FanbeiExceptionCode.PARAM_ERROR);
		}

		AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (userAccountDo == null) {
			throw new FanbeiException("account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		if (StringUtils.equals(type, UserAccountLogType.REBATE_CASH.getCode())
				&& userAccountDo.getRebateAmount().compareTo(amount) < 0) {
			throw new FanbeiException("apply cash amount more than account money",
					FanbeiExceptionCode.APPLY_CASHED_AMOUNT_MORE_ACCOUNT);

		} else if (StringUtils.equals(type, UserAccountLogType.REBATE_JFB.getCode())
				&& userAccountDo.getJfbAmount().compareTo(amount) < 0) {
			throw new FanbeiException("apply cash amount more than account money",
					FanbeiExceptionCode.APPLY_CASHED_AMOUNT_MORE_ACCOUNT);
		}
		
		String inputOldPwd = UserUtil.getPassword(payPwd, userAccountDo.getSalt());
		if (!StringUtils.equals(inputOldPwd, userAccountDo.getPassword())) {

			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}
		AfCashRecordDo afCashRecordDo = new AfCashRecordDo();
		afCashRecordDo.setType(type);
		afCashRecordDo.setAccount(account);
		afCashRecordDo.setAmount(amount);
		afCashRecordDo.setUserId(userId);
		afCashRecordDo.setStatus("TRANSED");

		if (afCashRecordService.addCashRecord(afCashRecordDo) > 0) {
			return resp;
		}

		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
