package com.ald.fanbei.api.web.api.borrow;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：现金借款申请
 * @author 何鑫 2017年2月07日 11:01:26
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyCashApi")
public class ApplyCashApi implements ApiHandle {

	@Resource
	private AfUserAccountService afUserAccountService;

	@Resource
	private AfUserBankcardService afUserBankcardService;

	@Resource
	private AfBorrowService afBorrowService;

	@Resource
	AfResourceService afResourceService;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	UpsUtil upsUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		BigDecimal money = NumberUtil
				.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("money")), BigDecimal.ZERO);

		Long userId = context.getUserId();
		logger.info("userId=" + userId + ",money=" + money);
		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(userId);
	
		BigDecimal useableAmount = userDto.getAuAmount()
				.divide(new BigDecimal(Constants.DEFAULT_CASH_DEVIDE), 2, BigDecimal.ROUND_HALF_UP)
				.subtract(userDto.getUcAmount());
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		String inputOldPwd = UserUtil.getPassword(payPwd, userDto.getSalt());
		if (!StringUtils.equals(inputOldPwd, userDto.getPassword())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}
		if ((userDto.getAuAmount().subtract(userDto.getUsedAmount())).compareTo(useableAmount) == -1) {
			useableAmount = userDto.getAuAmount().subtract(userDto.getUsedAmount());
		}
		if (useableAmount.compareTo(money) == -1) {
			throw new FanbeiException("user cash money error", FanbeiExceptionCode.USER_CASH_MONEY_ERROR);
		}

		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
		if (null == card) {
			throw new FanbeiException(FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
		}
		long result = afBorrowService.dealCashApply(userDto, money, card.getRid());

		if (result > 0) {
			// 信用分大于指定值
			AfResourceDo resourceInfo = afResourceService
					.getSingleResourceBytype(Constants.RES_DIRECT_TRANS_CREDIT_SCORE);
			// 直接打款
			if (userDto.getCreditScore() >= Integer.valueOf(resourceInfo.getValue())) {
				UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(money, userDto.getRealName(), card.getCardNumber(),
						userId + "", card.getMobile(), card.getBankName(), card.getBankCode(),
						Constants.DEFAULT_BORROW_PURPOSE, "02", UserAccountLogType.CASH.getCode(), result + "");
				if (!upsResult.isSuccess()) {
					// 代付失败处理
					afUserAccountService.dealUserDelegatePayError(UserAccountLogType.CASH.getCode(), result);
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BANK_CARD_PAY_ERR);
				}

				resp.addResponseData("directTrans", "T");
			} else {
				resp.addResponseData("directTrans", "F");
			}
			resp.addResponseData("refId", result);
			resp.addResponseData("type", UserAccountLogType.CASH.getCode());
			return resp;
		}
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
