package com.ald.fanbei.api.web.api.user;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：获取能否分期状态
 * @author xiaotianjian 2017年3月31日下午8:39:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAllowConsumeApi")
public class GetAllowConsumeApi implements ApiHandle {

	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserAccountService afUserAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		AfUserAuthDo autDo = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
		if (autDo == null) {
			throw new FanbeiException("authDo id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		
		if (StringUtil.equals("N", autDo.getRiskStatus())) {
			long between = DateUtil.getNumberOfDatesBetween(autDo.getGmtRisk(), new Date(System.currentTimeMillis()));
			
			if (between == 1) {
				throw new FanbeiException("available credit not enough one", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_ONE);
			} else if (between == 2) {
				throw new FanbeiException("available credit not enough two", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_TWO);
			} else if (between == 3) {
				throw new FanbeiException("available credit not enough three", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_THREE);
			} else if (between == 4) {
				throw new FanbeiException("available credit not enough four", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_FOUR);
			} else if (between == 5) {
				throw new FanbeiException("available credit not enough five", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_FIVE);
			} else if (between == 6) {
				throw new FanbeiException("available credit not enough six", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_SIX);
			} else if (between == 7) {
				throw new FanbeiException("available credit not enough seven", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_SEVEN);
			} else if (between == 8) {
				throw new FanbeiException("available credit not enough eight", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_EIGHT);
			} else if (between == 9) {
				throw new FanbeiException("available credit not enough nine", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_NINE);
			} else if (between == 10) {
				throw new FanbeiException("available credit not enough ten", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH_TEN);
			} else {
				throw new FanbeiException("available credit not enough", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH);
			}
		}
		
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(context.getUserId());
		BigDecimal usableAmount = BigDecimalUtil.subtract(accountDo.getAuAmount(), accountDo.getUsedAmount());
		if (StringUtil.equals(autDo.getRiskStatus(), RiskStatus.YES.getCode()) && usableAmount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new FanbeiException("available credit not enough", FanbeiExceptionCode.AVAILABLE_CREDIT_NOT_ENOUGH);
		}
		
		resp.addResponseData("allowConsume", afUserAuthService.getConsumeStatus(context.getUserId(), context.getAppVersion()));
		resp.addResponseData("bindCardStatus", autDo.getBankcardStatus());
		resp.addResponseData("realNameStatus", autDo.getRealnameStatus());
		if (StringUtil.equals(autDo.getRiskStatus(), RiskStatus.SECTOR.getCode())) {
			resp.addResponseData("riskStatus", RiskStatus.A.getCode());
		} else {
			resp.addResponseData("riskStatus", autDo.getRiskStatus());
		}
		resp.addResponseData("faceStatus", autDo.getFacesStatus());

		resp.addResponseData("idNumber", Base64.encodeString(accountDo.getIdNumber()));
		resp.addResponseData("realName", accountDo.getRealName());

		return resp;
	}
}
