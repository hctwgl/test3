package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：余额不足提示页面
 * @author fmai 2017年7月10日 14:10:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("insufficientBalanceApi")
public class InsufficientBalanceApi implements ApiHandle {

	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAuthService afUserAuthService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String payAmount = ObjectUtils.toString(requestDataVo.getParams().get("needPayAmount"));
		if (StringUtils.isBlank(payAmount)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		BigDecimal needPayAmount = NumberUtil.objToBigDecimalDefault(payAmount, BigDecimal.ZERO);

		AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);

		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		String isSupplyCertify = "N";
		if (StringUtil.equals(authDo.getFundStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getJinpoStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getCreditStatus(), YesNoStatus.YES.getCode())) {
			isSupplyCertify = "Y";
		}

		HashMap<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("needPayAmount", needPayAmount);
		responseMap.put("totalAmount", accountDo.getAuAmount());
		responseMap.put("useableAmount", accountDo.getUsedAmount());
		responseMap.put("isSupplyCertify", isSupplyCertify);
		resp.setResponseData(responseMap);
		return resp;
	}

}
