package com.ald.fanbei.api.web.api.fenbei;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类描述：ApplyCashApi
 *@author 何鑫 2017年2月07日  11:01:26
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyCashApi")
public class ApplyCashApi implements ApiHandle{

	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		BigDecimal money = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("money")), BigDecimal.ZERO);
		Long userId = context.getUserId();
        logger.info("userId=" + userId + ",money=" + money);

		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
//		if(userDto.getCcAmount().compareTo(money)==-1){
//			throw new FanbeiException("user cash money error", FanbeiExceptionCode.USER_CASH_MONEY_ERROR);
//		}
		if(afUserAccountService.dealCashApply(userDto, money)>0){
			return resp;
		}
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
